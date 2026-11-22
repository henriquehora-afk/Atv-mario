import { Brand } from "@/components/brand";

export function AuthLayout({ children, heading, subtitle }: {
  children: React.ReactNode; heading: string; subtitle: string;
}) {
  return (
    <main className="flex min-h-screen items-center justify-center px-4 py-10">
      <div className="w-full max-w-md">
        <div className="mb-8 flex justify-center"><Brand /></div>
        <section className="rounded-xl border border-slate-200 bg-white p-7 shadow-sm sm:p-9">
          <h1 className="text-2xl font-bold tracking-tight">{heading}</h1>
          <p className="mt-2 text-sm leading-6 text-slate-500">{subtitle}</p>
          <div className="mt-7">{children}</div>
        </section>
        <p className="mt-6 text-center text-xs text-slate-400">Biblioteca Acadêmica Inteligente · Projeto acadêmico</p>
      </div>
    </main>
  );
}
