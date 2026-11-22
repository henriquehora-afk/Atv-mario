"use client";

import { useEffect, useState } from "react";
import { AppHeader } from "@/components/app-header";
import { ProtectedPage } from "@/components/protected-page";
import { FormField } from "@/components/form-field";
import { Notice } from "@/components/notice";
import { useAuth } from "@/components/auth-provider";
import { updateCurrentUser, changePassword } from "@/lib/auth";

function PasswordSection() {
  const [current, setCurrent] = useState("");
  const [next, setNext] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState(""); const [success, setSuccess] = useState("");
  const [busy, setBusy] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError(""); setSuccess("");
    if (next.length < 8) { setError("A nova senha deve ter pelo menos 8 caracteres."); return; }
    if (next !== confirm) { setError("A confirmação não é igual à nova senha."); return; }
    if (next === current) { setError("A nova senha deve ser diferente da atual."); return; }
    setBusy(true);
    try {
      await changePassword(current, next);
      setSuccess("Senha alterada com sucesso.");
      setCurrent(""); setNext(""); setConfirm("");
    } catch (err) { setError(err instanceof Error ? err.message : "Não foi possível alterar a senha."); }
    finally { setBusy(false); }
  }

  return (
    <section className="mt-8 rounded-lg border border-slate-200 bg-white p-6 sm:p-8">
      <h2 className="mb-6 text-xl font-semibold">Alterar senha</h2>
      <form onSubmit={handleSubmit} className="space-y-5">
        {error && <Notice>{error}</Notice>}{success && <Notice kind="success">{success}</Notice>}
        <FormField label="Senha atual" name="currentPassword" type="password" value={current} onChange={setCurrent} autoComplete="current-password" />
        <FormField label="Nova senha" name="newPassword" type="password" value={next} onChange={setNext} autoComplete="new-password" />
        <FormField label="Confirmar nova senha" name="confirmPassword" type="password" value={confirm} onChange={setConfirm} autoComplete="new-password" />
        <button disabled={busy} className="rounded-md bg-emerald-700 px-5 py-3 text-sm font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">{busy ? "Alterando..." : "Alterar senha"}</button>
      </form>
    </section>
  );
}

function ProfileContent() {
  const { user, reloadUser } = useAuth();
  const [name, setName] = useState(user?.name ?? "");
  const [email, setEmail] = useState(user?.email ?? "");
  const [error, setError] = useState(""); const [success, setSuccess] = useState("");
  const [busy, setBusy] = useState(false);

  useEffect(() => { setName(user?.name ?? ""); setEmail(user?.email ?? ""); }, [user]);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError(""); setSuccess(""); setBusy(true);
    try {
      await updateCurrentUser({ name, email });
      await reloadUser();
      setSuccess("Perfil atualizado com sucesso.");
    } catch (err) { setError(err instanceof Error ? err.message : "Não foi possível atualizar o perfil."); }
    finally { setBusy(false); }
  }

  return (
    <>
      <AppHeader />
      <main className="mx-auto max-w-3xl px-5 py-10">
        <h1 className="text-3xl font-bold">Meu perfil</h1>
        <p className="mt-2 text-slate-600">Consulte e atualize as informações da sua conta.</p>
        <section className="mt-8 rounded-lg border border-slate-200 bg-white p-6 sm:p-8">
          <div className="mb-6 flex items-center gap-4">
            <div className="grid h-14 w-14 place-items-center rounded-full bg-emerald-100 text-xl font-bold text-emerald-800">{user?.name?.charAt(0).toUpperCase()}</div>
            <div><h2 className="font-semibold">{user?.name}</h2><p className="text-sm text-slate-500">{user?.email}</p></div>
          </div>
          <form onSubmit={handleSubmit} className="space-y-5">
            {error && <Notice>{error}</Notice>}{success && <Notice kind="success">{success}</Notice>}
            <FormField label="Nome completo" name="name" value={name} onChange={setName} autoComplete="name" />
            <FormField label="E-mail" name="email" type="email" value={email} onChange={setEmail} autoComplete="email" />
            <button disabled={busy} className="rounded-md bg-emerald-700 px-5 py-3 text-sm font-semibold text-white hover:bg-emerald-800 disabled:opacity-60">{busy ? "Salvando..." : "Salvar alterações"}</button>
          </form>
        </section>
        <PasswordSection />
      </main>
    </>
  );
}

export default function ProfilePage() {
  return <ProtectedPage><ProfileContent /></ProtectedPage>;
}
