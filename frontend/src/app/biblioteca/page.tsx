"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import { WorkCard } from "@/components/work-card";
import { Notice } from "@/components/notice";
import { useLibrary } from "@/components/library-provider";
import { useWorks } from "@/components/use-works";
import { clearVisitHistory, getVisitHistory, removeVisit } from "@/lib/obras";
import type { AcademicWork, VisitedWork } from "@/lib/types";

type Tab = "favoritos" | "colecoes" | "historico";
type WorksMap = Record<string, AcademicWork>;

function Collections({ works }: { works: WorksMap }) {
  const { collections, createCollection, deleteCollection, removeFromCollection } = useLibrary();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");

  function handleCreate(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!name.trim()) return;
    createCollection(name.trim(), description.trim());
    setName(""); setDescription("");
  }

  return (
    <div className="space-y-6">
      <form onSubmit={handleCreate} className="grid gap-3 rounded-lg border border-slate-200 bg-white p-5 md:grid-cols-[1fr_1fr_auto]">
        <input aria-label="Nome da coleção" required placeholder="Nome da coleção" value={name} onChange={(e) => setName(e.target.value)} className="rounded-md border border-slate-300 px-4 py-3 text-sm outline-none focus:border-emerald-600" />
        <input aria-label="Descrição" placeholder="Descrição (opcional)" value={description} onChange={(e) => setDescription(e.target.value)} className="rounded-md border border-slate-300 px-4 py-3 text-sm outline-none focus:border-emerald-600" />
        <button className="rounded-md bg-emerald-700 px-5 py-3 text-sm font-semibold text-white hover:bg-emerald-800">Criar coleção</button>
      </form>
      {collections.length === 0 && <p className="text-sm text-slate-500">Você ainda não tem coleções.</p>}
      {collections.map((c) => {
        const items = c.workIds.map((id) => works[id]).filter((w): w is AcademicWork => Boolean(w));
        return (
          <section key={c.id} className="rounded-lg border border-slate-200 bg-white p-5">
            <div className="flex items-start justify-between gap-3">
              <div>
                <h3 className="text-lg font-semibold">{c.name}</h3>
                {c.description && <p className="text-sm text-slate-600">{c.description}</p>}
              </div>
              <button onClick={() => { if (window.confirm(`Excluir a coleção "${c.name}"?`)) deleteCollection(c.id); }} className="rounded-lg border border-red-200 px-3 py-2 text-xs font-medium text-red-700 hover:bg-red-50">Excluir</button>
            </div>
            {items.length === 0 ? (
              <p className="mt-4 text-sm text-slate-500">Coleção vazia. Adicione publicações pela busca.</p>
            ) : (
              <ul className="mt-4 divide-y divide-slate-100">
                {items.map((w) => (
                  <li key={w.id} className="flex items-center justify-between gap-3 py-3">
                    <Link href={`/obras/${w.id}`} className="text-sm font-medium hover:text-emerald-700">{w.title}</Link>
                    <button onClick={() => removeFromCollection(c.id, w.id)} className="shrink-0 text-xs text-slate-500 hover:text-red-700">Remover</button>
                  </li>
                ))}
              </ul>
            )}
          </section>
        );
      })}
    </div>
  );
}

/** Obras abertas pelo usuário, salvas no banco (aparecem em qualquer computador). */
function VisitedHistory() {
  const [items, setItems] = useState<VisitedWork[] | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    getVisitHistory()
      .then((list) => { if (active) setItems(list); })
      .catch((err) => { if (active) setError(err instanceof Error ? err.message : "Não foi possível carregar o histórico."); });
    return () => { active = false; };
  }, []);

  async function handleRemove(id: string) {
    setError("");
    try {
      await removeVisit(id);
      setItems((list) => (list ? list.filter((i) => i.work.id !== id) : list));
    } catch (err) { setError(err instanceof Error ? err.message : "Não foi possível remover."); }
  }

  async function handleClear() {
    if (!window.confirm("Limpar todo o histórico de obras visitadas?")) return;
    setError("");
    try { await clearVisitHistory(); setItems([]); }
    catch (err) { setError(err instanceof Error ? err.message : "Não foi possível limpar o histórico."); }
  }

  return (
    <section>
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-lg font-semibold">Obras visitadas</h2>
        {items && items.length > 0 && (
          <button onClick={handleClear} className="text-sm text-red-700 hover:underline">Limpar</button>
        )}
      </div>
      {error && <div className="mb-3"><Notice>{error}</Notice></div>}
      {!items && !error && <p className="text-sm text-slate-500">Carregando...</p>}
      {items && items.length === 0 && (
        <p className="text-sm text-slate-500">Você ainda não abriu nenhuma obra. <Link href="/buscar" className="text-emerald-700 underline">Fazer uma busca</Link></p>
      )}
      {items && items.length > 0 && (
        <ul className="divide-y divide-slate-100 rounded-lg border border-slate-200 bg-white">
          {items.map(({ work, visitedAt }) => (
            <li key={work.id} className="flex items-center justify-between gap-3 px-5 py-4">
              <div>
                <Link href={`/obras/${work.id}`} className="text-sm font-medium hover:text-emerald-700">{work.title}</Link>
                <p className="text-xs text-slate-500">
                  {work.authors.join(", ")} · {work.year} · visitado em {new Date(visitedAt).toLocaleString("pt-BR")}
                  {work.hasPdf && " · PDF disponível"}
                </p>
              </div>
              <div className="flex shrink-0 items-center gap-3">
                <Link href={`/obras/${work.id}`} className="text-xs font-semibold text-emerald-700 hover:underline">Abrir</Link>
                <button onClick={() => handleRemove(work.id)} className="text-xs text-slate-500 hover:text-red-700">Remover</button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

/** Termos pesquisados (guardados só neste navegador). */
function SearchHistory() {
  const { history, removeHistory, clearHistory } = useLibrary();
  return (
    <section className="mt-10">
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-lg font-semibold">Pesquisas recentes</h2>
        {history.length > 0 && (
          <button onClick={() => { if (window.confirm("Limpar as pesquisas recentes?")) clearHistory(); }} className="text-sm text-red-700 hover:underline">Limpar</button>
        )}
      </div>
      {history.length === 0 ? (
        <p className="text-sm text-slate-500">Nenhuma pesquisa recente.</p>
      ) : (
        <ul className="divide-y divide-slate-100 rounded-lg border border-slate-200 bg-white">
          {history.map((h) => (
            <li key={h.id} className="flex items-center justify-between gap-3 px-5 py-4">
              <div>
                <p className="text-sm font-medium">{h.query}</p>
                <p className="text-xs text-slate-500">
                  {h.filters.type} · {h.filters.period} · {new Date(h.searchedAt).toLocaleString("pt-BR")}
                </p>
              </div>
              <button onClick={() => removeHistory(h.id)} className="shrink-0 text-xs text-slate-500 hover:text-red-700">Remover</button>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

export default function LibraryPage() {
  const { favorites, collections } = useLibrary();
  const [tab, setTab] = useState<Tab>("favoritos");
  const { works, loading } = useWorks([...favorites, ...collections.flatMap((c) => c.workIds)]);
  const favWorks = favorites.map((id) => works[id]).filter((w): w is AcademicWork => Boolean(w));
  const tabs: { id: Tab; label: string }[] = [
    { id: "favoritos", label: "Favoritos" },
    { id: "colecoes", label: "Coleções" },
    { id: "historico", label: "Histórico" },
  ];

  return (
    <ProtectedPage>
      <AppHeader />
      <main className="mx-auto max-w-6xl px-5 py-10">
        <h1 className="text-3xl font-bold tracking-tight">Minha biblioteca</h1>
        <div role="tablist" className="mt-6 flex gap-2 border-b border-slate-200">
          {tabs.map((t) => (
            <button
              key={t.id} role="tab" aria-selected={tab === t.id} onClick={() => setTab(t.id)}
              className={`-mb-px border-b-2 px-4 py-3 text-sm font-medium ${tab === t.id ? "border-emerald-700 text-emerald-700" : "border-transparent text-slate-600 hover:text-slate-900"}`}
            >{t.label}</button>
          ))}
        </div>
        <div className="mt-6">
          {tab === "favoritos" && (
            loading && favorites.length > 0
              ? <p className="text-sm text-slate-500">Carregando...</p>
              : favWorks.length === 0
                ? <p className="text-sm text-slate-500">Você ainda não favoritou nenhuma publicação. <Link href="/buscar" className="text-emerald-700 underline">Fazer uma busca</Link></p>
                : <div className="grid gap-4 md:grid-cols-2">{favWorks.map((w) => <WorkCard key={w.id} work={w} />)}</div>
          )}
          {tab === "colecoes" && <Collections works={works} />}
          {tab === "historico" && (<><VisitedHistory /><SearchHistory /></>)}
        </div>
      </main>
    </ProtectedPage>
  );
}