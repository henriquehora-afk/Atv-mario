# Biblioteca

Projeto organizado em um unico repositorio, com a API Spring Boot e espaco reservado para o front-end.

## Estrutura

```text
api/       API REST em Java e Spring Boot, gerenciada pelo Maven
frontend/  Aplicacao front-end (a tecnologia sera definida ao iniciar essa parte)
```

## Requisitos

- JDK 25

O Maven Wrapper esta incluido; nao e necessario instalar o Maven separadamente.

## Executar a API

Na raiz do repositorio, no PowerShell:

```powershell
.\api\mvnw.cmd -f api\pom.xml spring-boot:run
```

## Executar os testes

```powershell
.\api\mvnw.cmd -f api\pom.xml test
```

## GitHub

O repositorio local fica na raiz deste projeto (`F:\api`). Nenhum endereco remoto e configurado aqui. Depois de criar um repositorio vazio no GitHub, execute na raiz:

```powershell
git add .
git commit -m "Initial project setup"
git remote add origin https://github.com/SEU_USUARIO/NOME_DO_REPOSITORIO.git
git push -u origin main
```
