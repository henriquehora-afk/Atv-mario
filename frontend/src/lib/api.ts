const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

let accessToken: string | null = null;

export function setAccessToken(token: string | null) {
  accessToken = token;
}

export function getAccessToken() {
  return accessToken;
}

type RequestOptions = RequestInit & { auth?: boolean };

// O access token dura 15 minutos. Quando a API responde 401, renovamos a sessão
// uma vez com o refresh token (cookie HttpOnly) e repetimos a requisição.
let refreshing: Promise<boolean> | null = null;

async function doRefresh(): Promise<boolean> {
  try {
    const response = await fetch(`${API_URL}/api/v1/auth/refresh`, { method: "POST", credentials: "include" });
    if (!response.ok) {
      accessToken = null;
      return false;
    }
    const data = (await response.json()) as { accessToken?: string; token?: string };
    accessToken = data.accessToken ?? data.token ?? null;
    return Boolean(accessToken);
  } catch {
    return false;
  }
}

function tryRefresh(): Promise<boolean> {
  if (!refreshing) {
    refreshing = doRefresh().finally(() => {
      refreshing = null;
    });
  }
  return refreshing;
}

async function send(path: string, options: RequestOptions): Promise<Response> {
  const headers = new Headers(options.headers);
  if (options.body !== undefined && !(options.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (options.auth !== false && accessToken) {
    headers.set("Authorization", `Bearer ${accessToken}`);
  }
  try {
    return await fetch(`${API_URL}${path}`, { ...options, headers, credentials: "include" });
  } catch {
    throw new Error("Não foi possível conectar ao servidor. Verifique se o backend está iniciado.");
  }
}

async function sendWithRefresh(path: string, options: RequestOptions): Promise<Response> {
  const response = await send(path, options);
  if (response.status === 401 && options.auth !== false && (await tryRefresh())) {
    return send(path, options);
  }
  return response;
}

async function readError(response: Response): Promise<string> {
  try {
    const data: unknown = await response.json();
    if (typeof data === "object" && data && "message" in data) {
      return String((data as { message: unknown }).message);
    }
  } catch {
    /* resposta sem JSON */
  }
  return `Erro na solicitação (${response.status}).`;
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const response = await sendWithRefresh(path, options);

  if (!response.ok) throw new Error(await readError(response));
  if (response.status === 204) return undefined as T;

  const contentType = response.headers.get("content-type") ?? "";
  const data = contentType.includes("application/json") ? await response.json() : await response.text();
  return data as T;
}

/** Para arquivos (ex.: PDF). Envia o token como as outras chamadas. */
export async function apiBlob(path: string, options: RequestOptions = {}): Promise<Blob> {
  const response = await sendWithRefresh(path, options);
  if (!response.ok) throw new Error(await readError(response));
  return response.blob();
}