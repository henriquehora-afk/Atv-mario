"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { AuthLayout } from "@/components/auth-layout";
import { FormField } from "@/components/form-field";
import { Notice } from "@/components/notice";
import { register } from "@/lib/auth";

export default function RegisterPage() {
  const [name, setName] = useState(""); const [email, setEmail] = useState("");
  const [password, setPassword] = useState(""); const [confirm, setConfirm] = useState("");
  const [error, setError] = useState(""); const [success, setSuccess] = useState("");
  const [busy, setBusy] = useState(false); const router = useRouter();

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError(""); setSuccess("");
    if (password.length < 8) { setError("A senha deve ter pelo menos 8 caracteres."); return; }
    if (password !== confirm) { setError("As senhas não são iguais."); return; }
    setBusy(true);
    try {
      await register(name, email, password);
      setSuccess("Cadastro realizado. Agora você pode entrar na sua conta.");
      setTimeout(() => router.push("/login"), 1200);
    } catch (err) { setError(err instanceof Error ? err.message : "Não foi possível criar a conta."); }
    finally { setBusy(false); }
  }

  return (
    <AuthLayout heading="Crie sua conta" subtitle="Preencha os dados para começar a organizar seus estudos.">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && <Notice>{error}</Notice>}{success && <Notice kind="success">{success}</Notice>}
        <FormField label="Nome completo" name="name" value={name} onChange={setName} placeholder="Seu nome" autoComplete="name" />
        <FormField label="E-mail" name="email" type="email" value={email} onChange={setEmail} placeholder="voce@exemplo.com" autoComplete="email" />
        <FormField label="Senha (mínimo de 8 caracteres)" name="password" type="password" value={password} onChange={setPassword} placeholder="Crie uma senha" autoComplete="new-password" />
        <FormField label="Confirmar senha" name="confirmPassword" type="password" value={confirm} onChange={setConfirm} placeholder="Digite a senha novamente" autoComplete="new-password" />
        <button disabled={busy} className="w-full rounded-md bg-emerald-700 px-4 py-3 font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">{busy ? "Criando..." : "Criar conta"}</button>
      </form>
      <p className="mt-6 text-center text-sm text-slate-600">Já tem conta? <Link className="font-semibold text-emerald-700 hover:underline" href="/login">Entrar</Link></p>
    </AuthLayout>
  );
}
