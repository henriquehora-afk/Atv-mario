"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import { Notice } from "@/components/notice";
import { PdfReader } from "@/components/pdf-reader";
import { AddToCollection, FavoriteButton } from "@/components/work-card";
import { getWork, registerVisit } from "@/lib/obras";
import type { AcademicWork } from "@/lib/types";

function WorkDetails({ id }: { id: string }) {
  const [work, setWork] = useState<AcademicWork | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    setWork(null); setError("");
    getWork(id)
      .then((w) => {
        if (!active) return;
        setWork(w);
        // Guarda no histórico do usuário. Se falhar, a página continua funcionando.
        registerVisit(id).catch(() => {});
      })
      .catch((err) => { if (active) setError(err instanceof Error ? err.message : "Não foi possível carregar a obra."); });
    return () => { active = false; };
  }, [id]);

  if (error) return <div className="mt-6"><Notice>{error}</Notice></div>;
  if (!work) return <p className="mt-6 text-sm text-slate-500">Carregando...</p>;

  return (
    <article className="mt-4 rounded-lg border border-slate-200 bg-white p-6 sm:p-8">
      <div className="flex flex-wrap items-center gap-2 text-xs">
        <span className="rounded-full bg-emerald-100 px-2.5 py-1 font-semibold text-emerald-800">{work.type}</span>
        <span className="text-slate-500">{work.year} · Fonte: {work.source}</span>
      </div>
      <h1 className="mt-4 text-2xl font-bold leading-snug">{work.title}</h1>
      <p className="mt-2 text-slate-600">{work.authors.join(", ")}</p>

      <h2 className="mt-6 text-sm font-semibold uppercase tracking-wide text-slate-500">Resumo</h2>
      <p className="mt-2 leading-7 text-slate-700">
        {work.abstract ?? <span className="italic text-slate-400">Resumo não disponível nesta fonte.</span>}
      </p>

      {work.keywords.length > 0 && (
        <>
          <h2 className="mt-6 text-sm font-semibold uppercase tracking-wide text-slate-500">Palavras-chave</h2>
          <div className="mt-2 flex flex-wrap gap-2">
            {work.keywords.map((k) => <span key={k} className="rounded-full bg-slate-100 px-3 py-1 text-xs text-slate-700">{k}</span>)}
          </div>
        </>
      )}

      {work.doi && <p className="mt-6 text-sm text-slate-600">DOI: {work.doi}</p>}

      <div className="mt-8 flex flex-wrap items-center gap-3">
        <a href={work.url} target="_blank" rel="noopener noreferrer" className="rounded-md border border-slate-300 px-4 py-3 text-sm font-semibold hover:bg-slate-50">Abrir na fonte original</a>
        <FavoriteButton workId={work.id} />
        <AddToCollection workId={work.id} />
      </div>

      <h2 className="mt-8 text-sm font-semibold uppercase tracking-wide text-slate-500">Leitura</h2>
      <div className="mt-3"><PdfReader work={work} /></div>
    </article>
  );
}

export default function WorkDetailsPage() {
  const params = useParams<{ id: string }>();
  return (
    <ProtectedPage>
      <AppHeader />
      <main className="mx-auto max-w-4xl px-5 py-10">
        <Link href="/buscar" className="text-sm text-emerald-700 hover:underline">← Voltar à busca</Link>
        <WorkDetails id={String(params.id)} />
      </main>
    </ProtectedPage>
  );
}