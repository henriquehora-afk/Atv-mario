"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { AuthLayout } from "@/components/auth-layout";
import { FormField } from "@/components/form-field";
import { Notice } from "@/components/notice";
import { useAuth } from "@/components/auth-provider";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const { signIn } = useAuth();
  const router = useRouter();

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError(""); setBusy(true);
    try { await signIn(email, password); router.replace("/"); }
    catch (err) { setError(err instanceof Error ? err.message : "Não foi possível entrar."); }
    finally { setBusy(false); }
  }

  return (
    <AuthLayout heading="Bem-vindo de volta" subtitle="Entre na sua conta para acessar sua biblioteca acadêmica.">
      <form onSubmit={handleSubmit} className="space-y-5">
        {error && <Notice>{error}</Notice>}
        <FormField label="E-mail" name="email" type="email" value={email} onChange={setEmail} placeholder="voce@exemplo.com" autoComplete="email" />
        <FormField label="Senha" name="password" type="password" value={password} onChange={setPassword} placeholder="Digite sua senha" autoComplete="current-password" />
        <div className="text-right"><Link className="text-sm font-medium text-emerald-700 hover:underline" href="/recuperar-senha">Esqueci minha senha</Link></div>
        <button disabled={busy} className="w-full rounded-md bg-emerald-700 px-4 py-3 font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">{busy ? "Entrando..." : "Entrar"}</button>
      </form>
      <p className="mt-6 text-center text-sm text-slate-600">Ainda não tem conta? <Link className="font-semibold text-emerald-700 hover:underline" href="/cadastro">Criar conta</Link></p>
    </AuthLayout>
  );
}
