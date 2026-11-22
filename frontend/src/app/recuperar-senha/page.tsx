"use client";

import { useState } from "react";
import Link from "next/link";
import { AuthLayout } from "@/components/auth-layout";
import { FormField } from "@/components/form-field";
import { Notice } from "@/components/notice";
import { requestPasswordReset } from "@/lib/auth";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState(""); const [message, setMessage] = useState("");
  const [error, setError] = useState(""); const [busy, setBusy] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError(""); setMessage(""); setBusy(true);
    try {
      const result = await requestPasswordReset(email);
      setMessage(result.message ?? "Se o e-mail estiver cadastrado, você receberá instruções para redefinir a senha.");
    } catch (err) { setError(err instanceof Error ? err.message : "Não foi possível solicitar a recuperação."); }
    finally { setBusy(false); }
  }

  return (
    <AuthLayout heading="Recuperar senha" subtitle="Informe o e-mail da sua conta para solicitar as instruções de recuperação.">
      <form onSubmit={handleSubmit} className="space-y-5">
        {error && <Notice>{error}</Notice>}{message && <Notice kind="success">{message}</Notice>}
        <FormField label="E-mail" name="email" type="email" value={email} onChange={setEmail} placeholder="voce@exemplo.com" autoComplete="email" />
        <button disabled={busy} className="w-full rounded-md bg-emerald-700 px-4 py-3 font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">{busy ? "Enviando..." : "Enviar instruções"}</button>
      </form>
      <p className="mt-6 text-center text-sm"><Link href="/login" className="font-semibold text-emerald-700 hover:underline">Voltar para o login</Link></p>
    </AuthLayout>
  );
}
