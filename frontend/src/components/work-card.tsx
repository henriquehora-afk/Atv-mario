"use client";

import Link from "next/link";
import { useLibrary } from "@/components/library-provider";
import type { AcademicWork } from "@/lib/types";

export function AddToCollection({ workId }: { workId: string }) {
  const { collections, addToCollection } = useLibrary();
  if (collections.length === 0) {
    return <Link href="/biblioteca" className="text-xs text-slate-500 underline hover:text-emerald-700">Crie uma coleção na biblioteca</Link>;
  }
  return (
    <select
      aria-label="Adicionar à coleção"
      className="rounded-lg border border-slate-300 bg-white px-2 py-2 text-xs"
      value=""
      onChange={(e) => { if (e.target.value) addToCollection(e.target.value, workId); }}
    >
      <option value="">+ Adicionar à coleção</option>
      {collections.map((c) => (
        <option key={c.id} value={c.id} disabled={c.workIds.includes(workId)}>
          {c.name}{c.workIds.includes(workId) ? " (já adicionada)" : ""}
        </option>
      ))}
    </select>
  );
}

export function FavoriteButton({ workId }: { workId: string }) {
  const { isFavorite, toggleFavorite } = useLibrary();
  const fav = isFavorite(workId);
  return (
    <button
      onClick={() => toggleFavorite(workId)}
      aria-pressed={fav}
      className={`rounded-lg border px-3 py-2 text-xs font-medium ${fav ? "border-amber-300 bg-amber-50 text-amber-800" : "border-slate-300 hover:bg-slate-50"}`}
    >
      {fav ? "★ Favorita" : "☆ Favoritar"}
    </button>
  );
}

export function WorkCard({ work }: { work: AcademicWork }) {
  return (
    <article className="rounded-lg border border-slate-200 bg-white p-5">
      <div className="flex flex-wrap items-center gap-2 text-xs">
        <span className="rounded-full bg-emerald-100 px-2.5 py-1 font-semibold text-emerald-800">{work.type}</span>
        <span className="text-slate-500">{work.year} · {work.source}</span>
      </div>
      <h3 className="mt-3 text-lg font-semibold leading-snug">
        <Link href={`/obras/${work.id}`} className="hover:text-emerald-700">{work.title}</Link>
      </h3>
      <p className="mt-1 text-sm text-slate-600">{work.authors.join(", ")}</p>
      <p className="mt-3 text-sm leading-6 text-slate-600">
        {work.abstract
          ? work.abstract.length > 180 ? work.abstract.slice(0, 180).trimEnd() + "…" : work.abstract
          : <span className="italic text-slate-400">Resumo não disponível nesta fonte.</span>}
      </p>
      <div className="mt-4 flex flex-wrap items-center gap-2">
        <Link href={`/obras/${work.id}`} className="rounded-lg bg-emerald-700 px-3 py-2 text-xs font-semibold text-white hover:bg-emerald-800">Ver detalhes</Link>
        <FavoriteButton workId={work.id} />
        <AddToCollection workId={work.id} />
      </div>
    </article>
  );
}
