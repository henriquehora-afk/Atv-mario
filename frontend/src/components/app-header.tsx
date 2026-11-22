"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAuth } from "@/components/auth-provider";


const LINKS = [
  { href: "/", label: "Início" },
  { href: "/buscar", label: "Buscar" },
  { href: "/obras/nova", label: "Adicionar obra" },
  { href: "/biblioteca", label: "Biblioteca" },
  { href: "/perfil", label: "Meu perfil" },
];

// Versão B: menu lateral (em telas grandes) ou barra no topo (celular).
export function AppHeader() {
  const { user, signOut } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  async function handleLogout() {
    await signOut();
    router.replace("/login");
  }

  return (
    <aside className="app-sidebar bg-emerald-950 text-emerald-50 lg:fixed lg:inset-y-0 lg:left-0 lg:z-10 lg:w-64">
      <div className="flex flex-col gap-4 p-5 lg:h-full lg:gap-8">
        <Link href="/" className="flex items-center gap-3" aria-label="Biblioteca Acadêmica Inteligente - início">
          <span className="grid h-10 w-10 place-items-center rounded-full bg-emerald-500 text-lg font-bold text-emerald-950">B</span>
          <span className="font-serif text-base leading-tight">Biblioteca<br />Acadêmica</span>
        </Link>

        <nav className="flex flex-wrap gap-2 text-sm lg:flex-col lg:gap-1">
          {LINKS.map((l) => {
            const active = l.href === "/" ? pathname === "/" : pathname.startsWith(l.href);
            return (
              <Link
                key={l.href} href={l.href} aria-current={active ? "page" : undefined}
                className={`rounded-md px-3 py-2 ${active ? "bg-emerald-800 font-semibold text-white" : "text-emerald-100 hover:bg-emerald-900"}`}
              >{l.label}</Link>
            );
          })}
        </nav>

        <div className="flex items-center justify-between gap-3 text-xs lg:mt-auto lg:flex-col lg:items-start">
          <span className="text-emerald-200">Olá, {user?.name}</span>
          <button onClick={handleLogout} className="rounded-md border border-emerald-700 px-3 py-2 font-medium hover:bg-emerald-900">Sair</button>
        </div>
      </div>
    </aside>
  );
}
