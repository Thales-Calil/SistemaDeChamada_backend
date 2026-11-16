# Sistema de Chamadas - Backend

Documentação de arquitetura (arc42): [docs/arc42.md](docs/arc42.md)

## Serviços

| Serviço | Banco | Responsabilidade |
|---|---|---|
| `aluno-service` (porta 8081) | Azure Cosmos DB (API MongoDB) | Alunos (id `Long` por sequência) |
| `turma-service` (porta 8083) | Azure SQL | Professores, disciplinas, turmas, **matrículas, chamadas e presenças** |

O antigo `chamada-service` foi **incorporado ao `turma-service`**: menos um App Service e um banco Azure SQL, e as
validações que eram feitas por HTTP entre os dois (turma existe? aluno matriculado? turma tem chamadas?) agora são
locais, na mesma transação.

## Subir do zero no Azure

### Recursos

1. **Azure SQL**: um servidor com 2 bancos — `turma-db` (turma-service) e `gateway-db` (usuários do API Gateway).
   Em *Rede*, marque **Permitir que serviços e recursos do Azure acessem este servidor**; TLS mínimo 1.2.
2. **Azure Cosmos DB (API MongoDB)** para o aluno-service.
3. **Service Bus**: tópico `chamadas-topic` com a subscription `aluno-service-sub`.
4. **App Services (Java 17)**: `aluno-service` e `turma-service` (mais o do API Gateway, no outro repositório).
5. Em cada workflow (`.github/workflows`), troque `app-name` e os secrets de publicação pelos dos novos App Services.

### Variáveis de ambiente (App Service > Configurações > Variáveis de ambiente)

| Variável | aluno-service | turma-service | Valor |
|---|:-:|:-:|---|
| `JWT_SECRET` | x | x | **Mesmo valor do API Gateway** (32+ caracteres) |
| `SPRING_DATA_MONGODB_URI` | x | | Primary Connection String do Cosmos DB |
| `AZURE_SQL_URL` | | x | `jdbc:sqlserver://<servidor>.database.windows.net:1433;database=turma-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;` |
| `AZURE_SQL_USERNAME`, `AZURE_SQL_PASSWORD` | | x | Credenciais do Azure SQL |
| `AZURE_SERVICEBUS_CONNECTION_STRING` | x | x | Connection string do namespace do Service Bus |
| `TURMA_SERVICE_URL` | x | | `https://<app do turma-service>.azurewebsites.net` |
| `ALUNO_SERVICE_URL` | | x | `https://<app do aluno-service>.azurewebsites.net` |

Opcionais: `AZURE_SERVICEBUS_TOPIC` (padrão `chamadas-topic`), `AZURE_SERVICEBUS_SUBSCRIPTION` (aluno-service, padrão
`aluno-service-sub`), `AZURE_SERVICEBUS_ENABLED=false` para rodar sem Service Bus.

### Ordem de deploy

API Gateway → turma-service → aluno-service → frontend. O banco do turma-service é criado pelo Flyway na primeira subida
(V1 a V4).

## Segurança

Os serviços validam o JWT emitido pelo API Gateway (HS256, mesmo `JWT_SECRET`) e aplicam a regra por perfil:

- **ADMIN**: tudo (turmas, matrículas, chamadas, presenças e cadastros).
- **PROFESSOR**: leitura geral; abre, registra presença em lote, finaliza e exclui chamadas **apenas das próprias turmas**.
- **ALUNO**: somente `GET` dos próprios dados (`/api/alunos/{seuId}`, `/api/turmas/aluno/{seuId}`, `/api/presencas/aluno/{seuId}`).

## Endpoints do turma-service

| Rota | Quem |
|---|---|
| `GET /api/turmas`, `GET /api/turmas/{id}` | ADMIN; PROFESSOR (as suas); ALUNO (as que cursa) |
| `POST/PUT/DELETE /api/turmas`, `/api/professores`, `/api/disciplinas` | ADMIN |
| `GET /api/turmas/{id}/alunos`; `POST /api/turmas/{id}/alunos`; `DELETE /api/turmas/{id}/alunos/{alunoId}` | leitura: ADMIN e professor da turma; escrita: ADMIN |
| `GET /api/turmas/aluno/{alunoId}`, `GET /api/turmas/professor/{professorId}/alunos` | conforme o perfil |
| `POST /api/chamadas`, `GET /api/chamadas/turma/{turmaId}`, `GET /api/chamadas/{id}` | ADMIN; PROFESSOR da turma |
| `POST /api/chamadas/{id}/presencas/lote` — `{ "presencas": [{"alunoId":1,"presente":true}], "finalizar": true }` | ADMIN; PROFESSOR da turma |
| `PUT /api/chamadas/{id}/finalizar`, `DELETE /api/chamadas/{id}` | ADMIN; PROFESSOR da turma |
| `GET /api/presencas/turma/{turmaId}` | ADMIN; PROFESSOR da turma |
| `GET /api/presencas/aluno/{alunoId}` | ADMIN; PROFESSOR; ALUNO (só o próprio) |

## Banco de dados (Flyway, turma-service)

| Migração | Conteúdo |
|---|---|
| V1 | professores, disciplinas, turmas |
| V2 | matrículas (`turma_alunos`), `aulas_ministradas` |
| V3 | chamadas (FK para turmas, uma por turma/data/horário) e presenças (uma por aluno/chamada) |
| V4 | `outbox_eventos` |

## Service Bus

- Ao finalizar uma chamada, o turma-service grava na **mesma transação**: status FINALIZADA, `aulas_ministradas + 1` e o
  evento na tabela `outbox_eventos`. O envio ao tópico `chamadas-topic` acontece a cada 5 s.
- O **aluno-service** consome o evento; confirma a mensagem só depois de processar, e falhas repetidas vão para a
  Dead-Letter Queue.

## Erros

Todos os erros retornam JSON com `status`, `mensagem`, `servico` e `caminho` (404 não encontrado, 409 conflito/regra,
403 acesso negado, 502 falha ao chamar outro serviço). Erros 500 aparecem com stack trace no Log stream do App Service.

## Testes

`mvn test` em cada serviço. No turma-service: 3 regras de arquitetura (ArchUnit) e 5 testes unitários das regras de
turma, chamada e presença.

Problemas encontrados: 
1. Ao instalar o vitejs, dava problema de versão por ser a versão mais recente não carregando os módulos ao testar, a solução encontrada foi dar um downgrade de versão nos remotes para 7.3.0 do vite e para 5.1.4 do react. 
2. Ao tentar utilizar o TypeScript como linguagem, havia conflito com o module federation, assim ao usar o federation, o código não entendia o que era o método.

Comandos para atualizar e rodar após alterar:

npm run build:remotes
npm run start
