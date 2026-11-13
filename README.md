

Problemas encontrados: 
1. Ao instalar o vitejs, dava problema de versão por ser a versão mais recente não carregando os módulos ao testar, a solução encontrada foi dar um downgrade de versão nos remotes para 7.3.0 do vite e para 5.1.4 do react. 
2. Ao tentar utilizar o TypeScript como linguagem, havia conflito com o module federation, assim ao usar o federation, o código não entendia o que era o método.

Comandos para atualizar e rodar após alterar:

npm run build:remotes
npm run start

## Backend - configuração no Azure

### Variáveis de ambiente (App Service > Configuração)

| Variável | aluno | chamada | turma | Observação |
|---|:-:|:-:|:-:|---|
| `JWT_SECRET` | x | x | x | **Mesmo valor do API Gateway** (32+ caracteres) |
| `SPRING_DATA_MONGODB_URI` | x | | | Primary Connection String do Cosmos DB |
| `AZURE_SQL_URL`, `AZURE_SQL_USERNAME`, `AZURE_SQL_PASSWORD` | | x | x | Cada serviço com o seu banco |
| `AZURE_SERVICEBUS_CONNECTION_STRING` | x | x | x | Obrigatória (ou `AZURE_SERVICEBUS_ENABLED=false`) |
| `TURMA_SERVICE_URL` | x | x | | URL do turma-service |
| `ALUNO_SERVICE_URL`, `CHAMADA_SERVICE_URL` | | | x | URLs do aluno-service e chamada-service |

### Segurança

Todos os serviços validam o JWT emitido pelo API Gateway (HS256, mesmo `JWT_SECRET`) e aplicam a regra por perfil:

- **ADMIN**: tudo.
- **PROFESSOR**: vê apenas as suas turmas, os alunos matriculados nelas e as chamadas delas; abre, registra e finaliza chamadas.
- **ALUNO**: vê apenas os próprios dados (`/api/alunos/{seuId}`, `/api/turmas/aluno/{seuId}`, `/api/presencas/aluno/{seuId}`).

Chamadas entre serviços repassam o token do usuário (o perfil é respeitado em toda a cadeia).

### Novos endpoints

- `GET /api/alunos?ids=1,2,3` - filtra alunos (ex.: os da turma)
- `GET|POST /api/turmas/{id}/alunos`, `DELETE /api/turmas/{id}/alunos/{alunoId}` - matrículas (POST/DELETE só ADMIN)
- `GET /api/turmas/aluno/{alunoId}` - turmas do aluno
- `GET /api/turmas/professor/{professorId}/alunos` - ids dos alunos do professor
- `PUT|DELETE /api/turmas/{id}` - editar/excluir turma (ADMIN)
- `POST /api/chamadas/{id}/presencas/lote` - `{ "presencas": [{"alunoId":1,"presente":true}], "finalizar": true }` (tudo ou nada)
- `GET /api/chamadas/{id}/presencas` - presenças registradas na chamada
- `GET /api/presencas/aluno/{alunoId}` - frequência do aluno em cada turma

### Banco de dados (Flyway)

O esquema é criado/alterado pelas migrações em `src/main/resources/db/migration` (chamada e turma).
Bancos que já existiam são reconhecidos automaticamente (baseline na versão 1) e recebem só as mudanças novas.

### Service Bus

- chamada-service grava o evento na tabela `outbox_eventos` junto com a finalização e envia a cada 5 s.
- Consumidores confirmam a mensagem só depois de processar; falhas voltam para a fila e, após o limite de entregas, vão para a Dead-Letter Queue.
- Recomendado: ligar **Duplicate detection** no tópico `chamadas-topic`.

### Erros

Todos os erros retornam JSON com `status`, `mensagem`, `servico` e `caminho` (404 para não encontrado, 409 para conflito/regra, 403 para acesso negado, 502 quando outro serviço falhou). Erros 500 aparecem com stack trace no Log stream do App Service.
