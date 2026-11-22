"use client";

import { useEffect, useState } from "react";
import { Notice } from "@/components/notice";
import { fetchWorkPdf, pdfFileName } from "@/lib/obras";
import type { AcademicWork } from "@/lib/types";

/** Botões "Ler PDF" (leitor embutido) e "Baixar PDF" (salva no computador). */
export function PdfReader({ work }: { work: AcademicWork }) {
  const [pdf, setPdf] = useState<Blob | null>(null);
  const [viewerUrl, setViewerUrl] = useState<string | null>(null);
  const [busy, setBusy] = useState<"ler" | "baixar" | null>(null);
  const [error, setError] = useState("");

  // Libera a memória do PDF quando o leitor fecha ou a página sai.
  useEffect(() => () => {
    if (viewerUrl) URL.revokeObjectURL(viewerUrl);
  }, [viewerUrl]);

  async function loadPdf(): Promise<Blob> {
    if (pdf) return pdf;
    const blob = await fetchWorkPdf(work.id);
    setPdf(blob);
    return blob;
  }

  async function handleRead() {
    if (viewerUrl) { setViewerUrl(null); return; }
    setError(""); setBusy("ler");
    try { setViewerUrl(URL.createObjectURL(await loadPdf())); }
    catch (err) { setError(err instanceof Error ? err.message : "Não foi possível abrir o PDF."); }
    finally { setBusy(null); }
  }

  async function handleDownload() {
    setError(""); setBusy("baixar");
    try {
      const href = URL.createObjectURL(await loadPdf());
      const link = document.createElement("a");
      link.href = href;
      link.download = pdfFileName(work);
      document.body.appendChild(link);
      link.click();
      link.remove();
      setTimeout(() => URL.revokeObjectURL(href), 1000);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Não foi possível baixar o PDF.");
    } finally {
      setBusy(null);
    }
  }

  if (!work.hasPdf) {
    return <p className="text-sm italic text-slate-500">Esta obra não tem PDF disponível.</p>;
  }

  return (
    <div className="w-full">
      <div className="flex flex-wrap items-center gap-3">
        <button onClick={handleRead} disabled={busy !== null} className="rounded-md bg-emerald-700 px-4 py-3 text-sm font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">
          {busy === "ler" ? "Carregando PDF..." : viewerUrl ? "Fechar leitor" : "Ler PDF"}
        </button>
        <button onClick={handleDownload} disabled={busy !== null} className="rounded-md border border-slate-300 px-4 py-3 text-sm font-semibold hover:bg-slate-50 disabled:opacity-60">
          {busy === "baixar" ? "Baixando..." : "Baixar PDF"}
        </button>
      </div>
      {error && <div className="mt-4"><Notice>{error}</Notice></div>}
      {viewerUrl && (
        <iframe title={`PDF: ${work.title}`} src={viewerUrl} className="mt-6 h-[80vh] w-full rounded-lg border border-slate-200" />
      )}
    </div>
  );
}