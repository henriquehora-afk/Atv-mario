import { apiRequest, setAccessToken } from "@/lib/api";
import type { AuthResponse, User } from "@/lib/types";

export async function login(email: string, password: string) {
  const result = await apiRequest<AuthResponse>("/api/v1/auth/login", {
    method: "POST",
    auth: false,
    body: JSON.stringify({ email, password }),
  });
  if (result.accessToken || result.token) {
    setAccessToken(result.accessToken ?? result.token ?? null);
  }
  return result;
}

export async function register(name: string, email: string, password: string) {
  return apiRequest<AuthResponse>("/api/v1/auth/register", {
    method: "POST",
    auth: false,
    body: JSON.stringify({ name, email, password }),
  });
}

export async function refreshSession() {
  const result = await apiRequest<AuthResponse>("/api/v1/auth/refresh", {
    method: "POST",
    auth: false,
  });
  if (result.accessToken || result.token) {
    setAccessToken(result.accessToken ?? result.token ?? null);
  }
  return result;
}

export async function logout() {
  try {
    await apiRequest<void>("/api/v1/auth/logout", { method: "POST" });
  } finally {
    setAccessToken(null);
  }
}

export async function getCurrentUser() {
  return apiRequest<User>("/api/v1/users/me");
}

export async function updateCurrentUser(user: Pick<User, "name" | "email">) {
  return apiRequest<User>("/api/v1/users/me", {
    method: "PUT",
    body: JSON.stringify(user),
  });
}

export async function requestPasswordReset(email: string) {
  return apiRequest<{ message?: string }>("/api/v1/auth/password/forgot", {
    method: "POST",
    auth: false,
    body: JSON.stringify({ email }),
  });
}

export async function resetPassword(token: string, newPassword: string) {
  return apiRequest<{ message?: string }>("/api/v1/auth/password/reset", {
    method: "POST",
    auth: false,
    body: JSON.stringify({ token, newPassword }),
  });
}

// ATENÇÃO: endpoint provisório. Confirmar caminho e campos com D1 antes da integração final.
export async function changePassword(currentPassword: string, newPassword: string) {
  return apiRequest<{ message?: string }>("/api/v1/users/me/password", {
    method: "PUT",
    body: JSON.stringify({ currentPassword, newPassword }),
  });
}
