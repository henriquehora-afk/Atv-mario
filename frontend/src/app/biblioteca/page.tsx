"use client";

import Link from "next/link";
import { useState } from "react";
import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import { WorkCard } from "@/components/work-card";
import { useLibrary } from "@/components/library-provider";
import { getWork } from "@/lib/mock-data";
import type { AcademicWork } from "@/lib/types";

type Tab = "favoritos" | "colecoes" | "historico";

function Collections() {
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
        const works = c.workIds.map(getWork).filter((w): w is AcademicWork => Boolean(w));
        return (
          <section key={c.id} className="rounded-lg border border-slate-200 bg-white p-5">
            <div className="flex items-start justify-between gap-3">
              <div>
                <h3 className="text-lg font-semibold">{c.name}</h3>
                {c.description && <p className="text-sm text-slate-600">{c.description}</p>}
              </div>
              <button onClick={() => { if (window.confirm(`Excluir a coleção "${c.name}"?`)) deleteCollection(c.id); }} className="rounded-lg border border-red-200 px-3 py-2 text-xs font-medium text-red-700 hover:bg-red-50">Excluir</button>
            </div>
            {works.length === 0 ? (
              <p className="mt-4 text-sm text-slate-500">Coleção vazia. Adicione publicações pela busca.</p>
            ) : (
              <ul className="mt-4 divide-y divide-slate-100">
                {works.map((w) => (
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

function History() {
  const { history, removeHistory, clearHistory } = useLibrary();
  if (history.length === 0) return <p className="text-sm text-slate-500">Seu histórico de pesquisas está vazio.</p>;
  return (
    <div>
      <div className="mb-3 flex justify-end">
        <button onClick={() => { if (window.confirm("Limpar todo o histórico?")) clearHistory(); }} className="text-sm text-red-700 hover:underline">Limpar histórico</button>
      </div>
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
    </div>
  );
}

export default function LibraryPage() {
  const { favorites } = useLibrary();
  const [tab, setTab] = useState<Tab>("favoritos");
  const favWorks = favorites.map(getWork).filter((w): w is AcademicWork => Boolean(w));
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
            favWorks.length === 0
              ? <p className="text-sm text-slate-500">Você ainda não favoritou nenhuma publicação. <Link href="/buscar" className="text-emerald-700 underline">Fazer uma busca</Link></p>
              : <div className="grid gap-4 md:grid-cols-2">{favWorks.map((w) => <WorkCard key={w.id} work={w} />)}</div>
          )}
          {tab === "colecoes" && <Collections />}
          {tab === "historico" && <History />}
        </div>
      </main>
    </ProtectedPage>
  );
}
