"use client";

import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { getCurrentUser, login as loginRequest, logout as logoutRequest, refreshSession } from "@/lib/auth";
import type { User } from "@/lib/types";

type AuthContextValue = {
  user: User | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  reloadUser: () => Promise<void>;
};

// Modo de teste: só para ver as telas sem o backend. Ligue com NEXT_PUBLIC_DEMO_MODE=true no .env.local.
const DEMO_MODE = process.env.NEXT_PUBLIC_DEMO_MODE === "true";
const DEMO_USER: User = { id: "demo", name: "Usuário de Teste", email: "teste@exemplo.com" };

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const reloadUser = useCallback(async () => {
    const current = await getCurrentUser();
    setUser(current);
  }, []);

  useEffect(() => {
    let active = true;
    async function restore() {
      if (DEMO_MODE) {
        if (active) { setUser(DEMO_USER); setLoading(false); }
        return;
      }
      try {
        await refreshSession();
        const current = await getCurrentUser();
        if (active) setUser(current);
      } catch {
        if (active) setUser(null);
      } finally {
        if (active) setLoading(false);
      }
    }
    restore();
    return () => { active = false; };
  }, []);

  const signIn = async (email: string, password: string) => {
    const result = await loginRequest(email, password);
    if (result.user) setUser(result.user);
    else await reloadUser();
  };

  const signOut = async () => {
    if (!DEMO_MODE) await logoutRequest();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, signIn, signOut, reloadUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth deve ser usado dentro de AuthProvider.");
  return context;
}
