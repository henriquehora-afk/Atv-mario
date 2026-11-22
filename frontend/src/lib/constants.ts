export const PAGE_SIZE = 6;
export const WORK_TYPES = ["Todos", "Artigo", "Livro", "Tese"];
export const PERIODS = ["Qualquer período", "Últimos 3 anos", "Últimos 5 anos", "Antes de 2020"];

/** Converte o período escolhido em ano inicial/final para a API. */
export function periodToYears(period: string): { from?: number; to?: number } {
  const thisYear = new Date().getFullYear();
  if (period === "Últimos 3 anos") return { from: thisYear - 3 };
  if (period === "Últimos 5 anos") return { from: thisYear - 5 };
  if (period === "Antes de 2020") return { to: 2019 };
  return {};
}