"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { AuthLayout } from "@/components/auth-layout";
import { FormField } from "@/components/form-field";
import { Notice } from "@/components/notice";
import { resetPassword } from "@/lib/auth";

export default function ResetPasswordPage() {
  const [token, setToken] = useState("");
  useEffect(() => {
    setToken(new URLSearchParams(window.location.search).get("token") ?? "");
  }, []);
  const [password, setPassword] = useState(""); const [confirm, setConfirm] = useState("");
  const [error, setError] = useState(""); const [success, setSuccess] = useState(""); const [busy, setBusy] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError(""); setSuccess("");
    if (!token) { setError("O link está sem token de recuperação. Solicite um novo e-mail."); return; }
    if (password.length < 8) { setError("A senha deve ter pelo menos 8 caracteres."); return; }
    if (password !== confirm) { setError("As senhas não são iguais."); return; }
    setBusy(true);
    try { const result = await resetPassword(token, password); setSuccess(result.message ?? "Senha alterada. Você já pode entrar."); }
    catch (err) { setError(err instanceof Error ? err.message : "Não foi possível redefinir a senha."); }
    finally { setBusy(false); }
  }

  return (
    <AuthLayout heading="Definir nova senha" subtitle="Escolha uma nova senha para sua conta.">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && <Notice>{error}</Notice>}{success && <Notice kind="success">{success}</Notice>}
        <FormField label="Nova senha" name="password" type="password" value={password} onChange={setPassword} placeholder="Mínimo de 8 caracteres" autoComplete="new-password" />
        <FormField label="Confirmar nova senha" name="confirm" type="password" value={confirm} onChange={setConfirm} placeholder="Digite novamente" autoComplete="new-password" />
        <button disabled={busy} className="w-full rounded-md bg-emerald-700 px-4 py-3 font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">{busy ? "Salvando..." : "Redefinir senha"}</button>
      </form>
      <p className="mt-6 text-center text-sm"><Link href="/login" className="font-semibold text-emerald-700 hover:underline">Voltar para o login</Link></p>
    </AuthLayout>
  );
}
