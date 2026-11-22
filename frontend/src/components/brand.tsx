import Link from "next/link";

export function Brand() {
  return (
    <Link href="/" className="flex items-center gap-3" aria-label="Biblioteca Acadêmica Inteligente - início">
      <span className="grid h-11 w-11 place-items-center rounded-lg bg-emerald-700 text-xl text-white">B</span>
      <span className="leading-tight">
        <span className="block text-sm font-bold text-slate-900">Biblioteca Acadêmica</span>
        <span className="block text-xs text-slate-500">Inteligente</span>
      </span>
    </Link>
  );
}
