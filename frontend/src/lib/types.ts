export type User = {
  id?: number | string;
  name: string;
  email: string;
};

export type AuthResponse = {
  accessToken?: string;
  token?: string;
  user?: User;
  message?: string;
};

export type ApiError = {
  message: string;
  status?: number;
};

export type WorkType = "Artigo" | "Livro" | "Tese";

export type AcademicWork = {
  id: string;
  source: string;
  title: string;
  authors: string[];
  year: number;
  type: WorkType;
  abstract?: string;
  doi?: string;
  url: string;
  keywords: string[];
};

export type Collection = {
  id: string;
  name: string;
  description: string;
  workIds: string[];
};

export type SearchFilters = { type: string; period: string };

export type HistoryItem = {
  id: string;
  query: string;
  filters: SearchFilters;
  searchedAt: string;
};

