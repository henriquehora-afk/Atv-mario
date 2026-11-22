"use client";

import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import Link from "next/link";

export default function HomePage() {
  return (
    <ProtectedPage>
      <AppHeader />
      <main className="mx-auto max-w-6xl px-5 py-12">
        <div className="max-w-2xl">
          <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-800">Área do usuário</span>
          <h1 className="mt-5 text-3xl font-bold tracking-tight sm:text-4xl">Sua pesquisa acadêmica começa aqui.</h1>
          <p className="mt-4 leading-7 text-slate-600">Este é o espaço inicial da Biblioteca Acadêmica Inteligente. Pesquise publicações, e organize sua biblioteca. Por enquanto as telas usam dados de demonstração, até o backend ficar pronto.</p>
          <div className="mt-7 flex flex-wrap gap-3">
            <Link href="/buscar" className="inline-flex rounded-md bg-emerald-700 px-5 py-3 text-sm font-semibold text-white hover:bg-emerald-800">Buscar publicações</Link>
            <Link href="/biblioteca" className="inline-flex rounded-md border border-slate-300 px-5 py-3 text-sm font-semibold hover:bg-slate-50">Minha biblioteca</Link>
          </div>
        </div>
      </main>
    </ProtectedPage>
  );
}
