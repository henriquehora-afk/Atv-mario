import type { AcademicWork } from "@/lib/types";

// DADOS FICTÍCIOS DE DEMONSTRAÇÃO.
// Servem só para montar e testar a interface enquanto o backend (D1/D2) não existe.
// Quando a API de busca estiver pronta, troque a função searchWorks por uma chamada real.
export const MOCK_WORKS: AcademicWork[] = [
  {
    id: "w1", source: "OpenAlex", type: "Artigo", year: 2023,
    title: "Aprendizagem de máquina aplicada à evasão no ensino superior",
    authors: ["Marina Alves", "Rafael Duarte"],
    abstract: "Este estudo compara modelos de classificação para prever a evasão de estudantes de graduação a partir de dados acadêmicos. Os resultados indicam que modelos baseados em árvores têm melhor desempenho e que a frequência nas primeiras semanas é o fator mais relevante.",
    doi: "10.0000/exemplo.2023.001", url: "https://example.org/obras/w1",
    keywords: ["aprendizagem de máquina", "evasão", "ensino superior"],
  },
  {
    id: "w2", source: "Crossref", type: "Artigo", year: 2021,
    title: "Usabilidade em sistemas de bibliotecas digitais universitárias",
    authors: ["Camila Ribeiro"],
    abstract: "Avalia a usabilidade de três bibliotecas digitais universitárias com testes com usuários. Conclui que a busca avançada e a organização de resultados são os principais pontos de dificuldade.",
    doi: "10.0000/exemplo.2021.014", url: "https://example.org/obras/w2",
    keywords: ["usabilidade", "biblioteca digital", "interface"],
  },
  {
    id: "w3", source: "Google Books", type: "Livro", year: 2019,
    title: "Engenharia de software: princípios e práticas modernas",
    authors: ["Eduardo Pires", "Luciana Matos"],
    abstract: "Apresenta processos de desenvolvimento, requisitos, arquitetura e testes, com exemplos de equipes ágeis em projetos acadêmicos e de mercado.",
    url: "https://example.org/obras/w3",
    keywords: ["engenharia de software", "metodologias ágeis", "arquitetura"],
  },
  {
    id: "w4", source: "OpenAlex", type: "Tese", year: 2022,
    title: "Recuperação de informação em português: avaliação de modelos de busca semântica",
    authors: ["Beatriz Nogueira"],
    abstract: "Tese que avalia modelos de busca semântica em coleções de textos em português e propõe uma métrica de qualidade para resultados de pesquisa acadêmica.",
    doi: "10.0000/exemplo.2022.077", url: "https://example.org/obras/w4",
    keywords: ["recuperação de informação", "busca semântica", "português"],
  },
  {
    id: "w5", source: "Crossref", type: "Artigo", year: 2024,
    title: "Inteligência artificial generativa e integridade acadêmica",
    authors: ["Thiago Mendes", "Paula Farias"],
    abstract: "Discute riscos e boas práticas no uso de assistentes de IA na pesquisa, com ênfase em citação de fontes e verificação de informações geradas.",
    doi: "10.0000/exemplo.2024.032", url: "https://example.org/obras/w5",
    keywords: ["inteligência artificial", "integridade acadêmica", "citação"],
  },
  {
    id: "w6", source: "Google Books", type: "Livro", year: 2017,
    title: "Banco de dados relacionais com PostgreSQL",
    authors: ["Henrique Lacerda"],
    url: "https://example.org/obras/w6",
    keywords: ["banco de dados", "PostgreSQL", "modelagem"],
  },
  {
    id: "w7", source: "OpenAlex", type: "Artigo", year: 2020,
    title: "Segurança em APIs REST: autenticação com tokens JWT",
    authors: ["Vinícius Castro", "Aline Souza"],
    abstract: "Analisa vulnerabilidades comuns em autenticação baseada em tokens e compara estratégias de armazenamento de tokens no navegador, como cookies httpOnly e memória.",
    doi: "10.0000/exemplo.2020.058", url: "https://example.org/obras/w7",
    keywords: ["segurança", "JWT", "API REST", "autenticação"],
  },
  {
    id: "w8", source: "Crossref", type: "Artigo", year: 2018,
    title: "Acessibilidade web em plataformas educacionais brasileiras",
    authors: ["Juliana Prado"],
    url: "https://example.org/obras/w8",
    keywords: ["acessibilidade", "educação", "web"],
  },
];

export const PAGE_SIZE = 4;
export const WORK_TYPES = ["Todos", "Artigo", "Livro", "Tese"];
export const PERIODS = ["Qualquer período", "Últimos 3 anos", "Últimos 5 anos", "Antes de 2020"];

export function getWork(id: string) {
  return MOCK_WORKS.find((w) => w.id === id);
}

export function searchWorks(query: string, type: string, period: string) {
  const q = query.trim().toLowerCase();
  const thisYear = new Date().getFullYear();
  return MOCK_WORKS.filter((w) => {
    if (type !== "Todos" && w.type !== type) return false;
    if (period === "Últimos 3 anos" && w.year < thisYear - 3) return false;
    if (period === "Últimos 5 anos" && w.year < thisYear - 5) return false;
    if (period === "Antes de 2020" && w.year >= 2020) return false;
    if (!q) return true;
    const text = [w.title, w.authors.join(" "), w.abstract ?? "", w.keywords.join(" ")].join(" ").toLowerCase();
    return q.split(/\s+/).every((term) => text.includes(term));
  });
}
