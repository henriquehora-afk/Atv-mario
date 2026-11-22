"use client";

import { useMemo, useState } from "react";
import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import { WorkCard } from "@/components/work-card";
import { useLibrary } from "@/components/library-provider";
import { PAGE_SIZE, PERIODS, WORK_TYPES, searchWorks } from "@/lib/mock-data";

export default function SearchPage() {
  const { addHistory } = useLibrary();
  const [text, setText] = useState("");
  const [type, setType] = useState("Todos");
  const [period, setPeriod] = useState("Qualquer período");
  const [applied, setApplied] = useState<{ q: string; type: string; period: string } | null>(null);
  const [page, setPage] = useState(1);

  const results = useMemo(
    () => (applied ? searchWorks(applied.q, applied.type, applied.period) : []),
    [applied],
  );
  const totalPages = Math.max(1, Math.ceil(results.length / PAGE_SIZE));
  const visible = results.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setApplied({ q: text, type, period });
    setPage(1);
    addHistory(text, { type, period });
  }

  return (
    <ProtectedPage>
      <AppHeader />
      <main className="mx-auto max-w-6xl px-5 py-10">
        <h1 className="text-3xl font-bold tracking-tight">Buscar publicações</h1>
        <p className="mt-2 text-sm text-slate-600">Pesquise por palavra-chave, título ou autor.</p>
        <p className="mt-2 rounded-lg bg-amber-50 px-3 py-2 text-xs text-amber-800">Modo demonstração: os resultados são dados fictícios, até a integração com as fontes (OpenAlex, Crossref, Google Books) ficar pronta.</p>

        <form onSubmit={handleSubmit} className="mt-6 grid gap-3 rounded-lg border border-slate-200 bg-white p-5 md:grid-cols-[1fr_auto_auto_auto]">
          <input
            aria-label="Termo de busca"
            className="rounded-md border border-slate-300 px-4 py-3 text-sm outline-none focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100"
            placeholder="Ex.: aprendizagem de máquina, autor, título..."
            value={text} onChange={(e) => setText(e.target.value)}
          />
          <select aria-label="Tipo" className="rounded-md border border-slate-300 bg-white px-3 py-3 text-sm" value={type} onChange={(e) => setType(e.target.value)}>
            {WORK_TYPES.map((t) => <option key={t}>{t}</option>)}
          </select>
          <select aria-label="Período" className="rounded-md border border-slate-300 bg-white px-3 py-3 text-sm" value={period} onChange={(e) => setPeriod(e.target.value)}>
            {PERIODS.map((p) => <option key={p}>{p}</option>)}
          </select>
          <button className="rounded-md bg-emerald-700 px-6 py-3 text-sm font-semibold text-white hover:bg-emerald-800">Buscar</button>
        </form>

        <section className="mt-8" aria-live="polite">
          {!applied && <p className="text-sm text-slate-500">Faça uma busca para ver os resultados.</p>}
          {applied && results.length === 0 && (
            <p className="rounded-md bg-white p-6 text-sm text-slate-600">Nenhum resultado encontrado. Tente outros termos ou remova os filtros.</p>
          )}
          {applied && results.length > 0 && (
            <>
              <p className="mb-4 text-sm text-slate-600">{results.length} resultado(s) · página {page} de {totalPages}</p>
              <div className="grid gap-4 md:grid-cols-2">
                {visible.map((w) => <WorkCard key={w.id} work={w} />)}
              </div>
              {totalPages > 1 && (
                <div className="mt-6 flex items-center justify-center gap-3">
                  <button disabled={page === 1} onClick={() => setPage(page - 1)} className="rounded-lg border border-slate-300 px-4 py-2 text-sm disabled:opacity-40">Anterior</button>
                  <span className="text-sm text-slate-600">{page} / {totalPages}</span>
                  <button disabled={page === totalPages} onClick={() => setPage(page + 1)} className="rounded-lg border border-slate-300 px-4 py-2 text-sm disabled:opacity-40">Próxima</button>
                </div>
              )}
            </>
          )}
        </section>
      </main>
    </ProtectedPage>
  );
}
