"use client";

import { useEffect, useState } from "react";
import { getWork } from "@/lib/obras";
import type { AcademicWork } from "@/lib/types";

/** Carrega da API as obras de uma lista de ids (favoritos, coleções). */
export function useWorks(ids: string[]) {
  const [works, setWorks] = useState<Record<string, AcademicWork>>({});
  const [loading, setLoading] = useState(true);
  const key = Array.from(new Set(ids)).sort().join(",");

  useEffect(() => {
    let active = true;
    const list = key ? key.split(",") : [];
    setLoading(true);
    Promise.allSettled(list.map((id) => getWork(id))).then((results) => {
      if (!active) return;
      const map: Record<string, AcademicWork> = {};
      results.forEach((r) => {
        if (r.status === "fulfilled") map[r.value.id] = r.value;
      });
      setWorks(map);
      setLoading(false);
    });
    return () => {
      active = false;
    };
  }, [key]);

  return { works, loading };
}