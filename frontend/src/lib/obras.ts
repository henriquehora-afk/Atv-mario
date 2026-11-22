import { apiBlob, apiRequest } from "@/lib/api";
import { periodToYears } from "@/lib/constants";
import type { AcademicWork, NewWork, VisitedWork, WorkType } from "@/lib/types";

// Formato que a API devolve (campos em português). Convertido para AcademicWork,
// que é o tipo usado pelos componentes.
type ObraApi = {
  id: number;
  fonte: string;
  titulo: string;
  autores: string[];
  ano: number;
  tipo: WorkType;
  resumo: string | null;
  doi: string | null;
  url: string;
  palavrasChave: string[];
  temPdf: boolean;
};

type VisitaApi = { obra: ObraApi; visitadoEm: string };

function toWork(o: ObraApi): AcademicWork {
  return {
    id: String(o.id),
    source: o.fonte,
    title: o.titulo,
    authors: o.autores,
    year: o.ano,
    type: o.tipo,
    abstract: o.resumo ?? undefined,
    doi: o.doi ?? undefined,
    url: o.url,
    keywords: o.palavrasChave,
    hasPdf: o.temPdf,
  };
}

export async function searchWorks(query: string, type: string, period: string): Promise<AcademicWork[]> {
  const params = new URLSearchParams();
  if (query.trim()) params.set("q", query.trim());
  if (type && type !== "Todos") params.set("tipo", type);
  const { from, to } = periodToYears(period);
  if (from) params.set("anoDe", String(from));
  if (to) params.set("anoAte", String(to));
  const qs = params.toString();
  const obras = await apiRequest<ObraApi[]>(`/api/v1/obras${qs ? `?${qs}` : ""}`);
  return obras.map(toWork);
}

export async function getWork(id: string): Promise<AcademicWork> {
  return toWork(await apiRequest<ObraApi>(`/api/v1/obras/${encodeURIComponent(id)}`));
}

export async function createWork(work: NewWork): Promise<AcademicWork> {
  const created = await apiRequest<ObraApi>("/api/v1/obras", {
    method: "POST",
    body: JSON.stringify({
      titulo: work.title,
      autores: work.authors,
      ano: work.year,
      tipo: work.type,
      resumo: work.abstract || null,
      doi: work.doi || null,
      url: work.url || null,
      palavrasChave: work.keywords,
      pdfUrl: work.pdfUrl,
    }),
  });
  return toWork(created);
}

/** Baixa o PDF pela API (que busca na fonte original). */
export async function fetchWorkPdf(id: string): Promise<Blob> {
  const blob = await apiBlob(`/api/v1/obras/${encodeURIComponent(id)}/pdf`);
  return blob.type === "application/pdf" ? blob : new Blob([blob], { type: "application/pdf" });
}

/** Ex.: "Attention Is All You Need" (2017) -> attention-is-all-you-need-2017.pdf */
export function pdfFileName(work: AcademicWork): string {
  const base = work.title
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/(^-|-$)/g, "")
    .slice(0, 80)
    .replace(/-$/, "");
  return `${base || "obra"}-${work.year}.pdf`;
}

export async function registerVisit(id: string): Promise<void> {
  await apiRequest<void>(`/api/v1/users/me/historico/${encodeURIComponent(id)}`, { method: "POST" });
}

export async function getVisitHistory(): Promise<VisitedWork[]> {
  const visitas = await apiRequest<VisitaApi[]>("/api/v1/users/me/historico");
  return visitas.map((v) => ({ work: toWork(v.obra), visitedAt: v.visitadoEm }));
}

export async function removeVisit(id: string): Promise<void> {
  await apiRequest<void>(`/api/v1/users/me/historico/${encodeURIComponent(id)}`, { method: "DELETE" });
}

export async function clearVisitHistory(): Promise<void> {
  await apiRequest<void>("/api/v1/users/me/historico", { method: "DELETE" });
}