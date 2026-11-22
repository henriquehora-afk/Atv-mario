"use client";

import Link from "next/link";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import { Notice } from "@/components/notice";
import { createWork } from "@/lib/obras";
import type { WorkType } from "@/lib/types";

const inputClass = "w-full rounded-md border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-emerald-600 focus:ring-4 focus:ring-emerald-100";

function Field({ label, hint, children }: { label: string; hint?: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-medium text-slate-700">{label}</span>
      {children}
      {hint && <span className="mt-1 block text-xs text-slate-500">{hint}</span>}
    </label>
  );
}

function splitList(value: string, separator: RegExp) {
  return value.split(separator).map((v) => v.trim()).filter(Boolean);
}

function NewWorkForm() {
  const router = useRouter();
  const [pdfUrl, setPdfUrl] = useState("");
  const [title, setTitle] = useState("");
  const [authors, setAuthors] = useState("");
  const [year, setYear] = useState(String(new Date().getFullYear()));
  const [type, setType] = useState<WorkType>("Artigo");
  const [abstract, setAbstract] = useState("");
  const [keywords, setKeywords] = useState("");
  const [url, setUrl] = useState("");
  const [doi, setDoi] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError("");
    const authorList = splitList(authors, /;|\n/);
    if (authorList.length === 0) { setError("Informe ao menos um autor."); return; }
    setBusy(true);
    try {
      const created = await createWork({
        pdfUrl: pdfUrl.trim(),
        title: title.trim(),
        authors: authorList,
        year: Number(year),
        type,
        abstract: abstract.trim() || undefined,
        keywords: splitList(keywords, /,/),
        url: url.trim() || undefined,
        doi: doi.trim() || undefined,
      });
      router.push(`/obras/${created.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Não foi possível cadastrar a obra.");
      setBusy(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mt-6 space-y-5 rounded-lg border border-slate-200 bg-white p-6 sm:p-8">
      {error && <Notice>{error}</Notice>}
      <Field label="Link do PDF *" hint="Endereço direto do arquivo, por exemplo https://arxiv.org/pdf/1706.03762">
        <input className={inputClass} type="url" required value={pdfUrl} onChange={(e) => setPdfUrl(e.target.value)} placeholder="https://..." />
      </Field>
      <Field label="Título *">
        <input className={inputClass} required maxLength={300} value={title} onChange={(e) => setTitle(e.target.value)} />
      </Field>
      <Field label="Autores *" hint="Separe os autores com ponto e vírgula (;).">
        <input className={inputClass} required value={authors} onChange={(e) => setAuthors(e.target.value)} placeholder="Maria Silva; João Souza" />
      </Field>
      <div className="grid gap-5 sm:grid-cols-2">
        <Field label="Ano *">
          <input className={inputClass} type="number" required min={1000} max={2100} value={year} onChange={(e) => setYear(e.target.value)} />
        </Field>
        <Field label="Tipo *">
          <select className={inputClass} value={type} onChange={(e) => setType(e.target.value as WorkType)}>
            <option>Artigo</option><option>Livro</option><option>Tese</option>
          </select>
        </Field>
      </div>
      <Field label="Resumo">
        <textarea className={inputClass} rows={4} maxLength={5000} value={abstract} onChange={(e) => setAbstract(e.target.value)} />
      </Field>
      <Field label="Palavras-chave" hint="Separe com vírgula.">
        <input className={inputClass} value={keywords} onChange={(e) => setKeywords(e.target.value)} placeholder="banco de dados, PostgreSQL" />
      </Field>
      <div className="grid gap-5 sm:grid-cols-2">
        <Field label="Página da obra (opcional)">
          <input className={inputClass} type="url" value={url} onChange={(e) => setUrl(e.target.value)} placeholder="https://..." />
        </Field>
        <Field label="DOI (opcional)">
          <input className={inputClass} maxLength={100} value={doi} onChange={(e) => setDoi(e.target.value)} placeholder="10.xxxx/xxxxx" />
        </Field>
      </div>
      <button disabled={busy} className="rounded-md bg-emerald-700 px-5 py-3 text-sm font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">{busy ? "Salvando..." : "Cadastrar obra"}</button>
    </form>
  );
}

export default function NewWorkPage() {
  return (
    <ProtectedPage>
      <AppHeader />
      <main className="mx-auto max-w-3xl px-5 py-10">
        <Link href="/buscar" className="text-sm text-emerald-700 hover:underline">← Voltar à busca</Link>
        <h1 className="mt-4 text-3xl font-bold tracking-tight">Adicionar obra por link</h1>
        <p className="mt-2 text-sm text-slate-600">Cole o link de um PDF de acesso aberto. Depois de cadastrada, a obra pode ser lida e baixada pela biblioteca.</p>
        <NewWorkForm />
      </main>
    </ProtectedPage>
  );
}