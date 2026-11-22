"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import type { Collection, HistoryItem, SearchFilters } from "@/lib/types";

// Biblioteca pessoal guardada no navegador (localStorage) enquanto o backend não existe.
// Quando a API de biblioteca (D3) estiver pronta, troque estas funções por chamadas à API.
type LibraryState = { favorites: string[]; collections: Collection[]; history: HistoryItem[] };
type LibraryContextValue = LibraryState & {
  isFavorite: (workId: string) => boolean;
  toggleFavorite: (workId: string) => void;
  createCollection: (name: string, description: string) => void;
  deleteCollection: (id: string) => void;
  addToCollection: (collectionId: string, workId: string) => void;
  removeFromCollection: (collectionId: string, workId: string) => void;
  addHistory: (query: string, filters: SearchFilters) => void;
  removeHistory: (id: string) => void;
  clearHistory: () => void;
};

const STORAGE_KEY = "bai:library:v1";
const EMPTY: LibraryState = { favorites: [], collections: [], history: [] };
const LibraryContext = createContext<LibraryContextValue | null>(null);

function newId() {
  return Date.now().toString(36) + Math.random().toString(36).slice(2, 7);
}

export function LibraryProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<LibraryState>(EMPTY);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) setState({ ...EMPTY, ...JSON.parse(raw) });
    } catch {
      /* sem armazenamento: segue vazio */
    }
    setLoaded(true);
  }, []);

  useEffect(() => {
    if (!loaded) return;
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch {
      /* ignora */
    }
  }, [state, loaded]);

  const isFavorite = useCallback((id: string) => state.favorites.includes(id), [state.favorites]);
  const toggleFavorite = useCallback((id: string) => {
    setState((s) => ({
      ...s,
      favorites: s.favorites.includes(id) ? s.favorites.filter((f) => f !== id) : [...s.favorites, id],
    }));
  }, []);
  const createCollection = useCallback((name: string, description: string) => {
    setState((s) => ({ ...s, collections: [...s.collections, { id: newId(), name, description, workIds: [] }] }));
  }, []);
  const deleteCollection = useCallback((id: string) => {
    setState((s) => ({ ...s, collections: s.collections.filter((c) => c.id !== id) }));
  }, []);
  const addToCollection = useCallback((cid: string, wid: string) => {
    setState((s) => ({
      ...s,
      collections: s.collections.map((c) =>
        c.id === cid && !c.workIds.includes(wid) ? { ...c, workIds: [...c.workIds, wid] } : c,
      ),
    }));
  }, []);
  const removeFromCollection = useCallback((cid: string, wid: string) => {
    setState((s) => ({
      ...s,
      collections: s.collections.map((c) => (c.id === cid ? { ...c, workIds: c.workIds.filter((w) => w !== wid) } : c)),
    }));
  }, []);
  const addHistory = useCallback((query: string, filters: SearchFilters) => {
    const q = query.trim();
    if (!q) return;
    setState((s) => ({
      ...s,
      history: [{ id: newId(), query: q, filters, searchedAt: new Date().toISOString() }, ...s.history].slice(0, 50),
    }));
  }, []);
  const removeHistory = useCallback((id: string) => {
    setState((s) => ({ ...s, history: s.history.filter((h) => h.id !== id) }));
  }, []);
  const clearHistory = useCallback(() => setState((s) => ({ ...s, history: [] })), []);

  const value = useMemo(
    () => ({
      ...state, isFavorite, toggleFavorite, createCollection, deleteCollection,
      addToCollection, removeFromCollection, addHistory, removeHistory, clearHistory,
    }),
    [state, isFavorite, toggleFavorite, createCollection, deleteCollection, addToCollection, removeFromCollection, addHistory, removeHistory, clearHistory],
  );

  return <LibraryContext.Provider value={value}>{children}</LibraryContext.Provider>;
}

export function useLibrary() {
  const ctx = useContext(LibraryContext);
  if (!ctx) throw new Error("useLibrary deve ser usado dentro de LibraryProvider");
  return ctx;
}
