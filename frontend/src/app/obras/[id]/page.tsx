"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import { AddToCollection, FavoriteButton } from "@/components/work-card";
import { getWork } from "@/lib/mock-data";

export default function WorkDetailsPage() {
  const params = useParams<{ id: string }>();
  const work = getWork(String(params.id));

  return (
    <ProtectedPage>
      <AppHeader />
      <main className="mx-auto max-w-3xl px-5 py-10">
        <Link href="/buscar" className="text-sm text-emerald-700 hover:underline">← Voltar à busca</Link>
        {!work ? (
          <p className="mt-6 rounded-md bg-white p-6 text-sm text-slate-600">Publicação não encontrada.</p>
        ) : (
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

            <h2 className="mt-6 text-sm font-semibold uppercase tracking-wide text-slate-500">Palavras-chave</h2>
            <div className="mt-2 flex flex-wrap gap-2">
              {work.keywords.map((k) => <span key={k} className="rounded-full bg-slate-100 px-3 py-1 text-xs text-slate-700">{k}</span>)}
            </div>

            {work.doi && <p className="mt-6 text-sm text-slate-600">DOI: {work.doi}</p>}

            <div className="mt-8 flex flex-wrap items-center gap-3">
              <a href={work.url} target="_blank" rel="noopener noreferrer" className="rounded-md bg-emerald-700 px-4 py-3 text-sm font-semibold text-white hover:bg-emerald-800">Abrir na fonte original</a>
              <FavoriteButton workId={work.id} />
              <AddToCollection workId={work.id} />
            </div>
          </article>
        )}
      </main>
    </ProtectedPage>
  );
}
