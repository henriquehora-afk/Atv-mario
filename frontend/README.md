# Biblioteca Acadêmica Inteligente — Frontend (D4)

Frontend inicial em Next.js + React + TypeScript + Tailwind CSS.

## Requisitos
- Node.js 20.9 ou superior
- npm
- Backend Spring Boot (para testar a integração real)

## Como executar
1. Extraia o ZIP.
2. Abra a pasta `biblioteca-academica-frontend` no VS Code.
3. No terminal, execute:

```bash
npm install
```

4. Copie `.env.example` para `.env.local` e ajuste `NEXT_PUBLIC_API_URL`.
5. Execute:

```bash
npm run dev
```

6. Acesse http://localhost:3000.

## Telas incluídas
- `/login` — entrar
- `/cadastro` — criar conta
- `/recuperar-senha` — solicitar recuperação
- `/redefinir-senha` — definir nova senha usando token
- `/perfil` — visualizar e editar dados do usuário
- `/` — página inicial protegida de demonstração

## Integração com o backend
A camada de comunicação está em `src/lib/api.ts` e `src/lib/auth.ts`.
Os caminhos atualmente previstos são:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/password/forgot`
- `POST /api/v1/auth/password/reset`
- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`

**Importante:** esses endpoints e os formatos dos JSONs são contratos provisórios baseados no plano inicial. Antes da integração final, D1 e D4 precisam confirmar os caminhos e os campos exatos com o backend. Não invente ou altere os contratos sem alinhar com D1.

## Decisão de segurança pendente
O projeto não grava tokens em `localStorage`. O cliente mantém o access token em memória e envia `credentials: "include"` para permitir que o backend gerencie o refresh token em cookie `HttpOnly`, se essa for a decisão aprovada pelo grupo. O backend precisa implementar CORS com credenciais e cookies adequados. Se o grupo escolher outra estratégia, ajuste `src/lib/auth.ts` junto com D1.

## Observações
- O frontend inclui validações básicas de formulário.
- Sem backend disponível, login/cadastro não concluem autenticação real; a aplicação mostra uma mensagem de conexão.
- Não há persistência simulada de usuários.
- A recuperação de senha depende do serviço de e-mail e dos endpoints do backend.
