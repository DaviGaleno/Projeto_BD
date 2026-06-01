# Projeto de Banco de Dados - Aluno Online

Este projeto foi desenvolvido como trabalho academico de Banco de Dados. O foco principal nao e apenas criar uma API CRUD, mas demonstrar na pratica conceitos relacionais usando PostgreSQL, Spring Boot e uma interface web que evidencia as consultas SQL geradas.

O sistema gerencia alunos, professores, disciplinas e matriculas, aplicando operacoes SQL de `SELECT`, `INSERT`, `UPDATE`, `DELETE`, `JOIN`, `WHERE`, `VIEW` e `SUBQUERY`.

## Objetivo do Projeto

Demonstrar como um sistema real utiliza Banco de Dados relacional para:

- Modelar entidades e relacionamentos.
- Criar chaves primarias e estrangeiras.
- Executar CRUD completo.
- Consultar dados com `JOIN`.
- Filtrar dados com `WHERE`.
- Criar relatorios personalizados.
- Utilizar `VIEW` para consultas prontas.
- Utilizar `SUBQUERY` para consultas mais avancadas.
- Exibir no frontend a SQL usada nas operacoes principais.

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- HTML, CSS e JavaScript puro

## Modelo Relacional

O banco possui quatro tabelas principais:

### Tabela `aluno`

Armazena os dados dos alunos.

| Campo | Tipo esperado | Descricao |
|---|---|---|
| `id` | BIGINT | Chave primaria |
| `nome_completo` | VARCHAR | Nome do aluno |
| `email` | VARCHAR | E-mail do aluno |
| `cpf` | VARCHAR | CPF do aluno |

### Tabela `professor`

Armazena os dados dos professores.

| Campo | Tipo esperado | Descricao |
|---|---|---|
| `id` | BIGINT | Chave primaria |
| `nome_completo` | VARCHAR | Nome do professor |
| `email` | VARCHAR | E-mail do professor |
| `cpf` | VARCHAR | CPF do professor |

### Tabela `disciplina`

Armazena as disciplinas e liga cada disciplina a um professor.

| Campo | Tipo esperado | Descricao |
|---|---|---|
| `id` | BIGINT | Chave primaria |
| `nome` | VARCHAR | Nome da disciplina |
| `carga_horaria` | INTEGER | Carga horaria |
| `professor_id` | BIGINT | Chave estrangeira para `professor.id` |

### Tabela `matricula_aluno`

Representa a matricula de um aluno em uma disciplina.

| Campo | Tipo esperado | Descricao |
|---|---|---|
| `id` | BIGINT | Chave primaria |
| `aluno_id` | BIGINT | Chave estrangeira para `aluno.id` |
| `disciplina_id` | BIGINT | Chave estrangeira para `disciplina.id` |
| `nota1` | DOUBLE PRECISION | Primeira nota |
| `nota2` | DOUBLE PRECISION | Segunda nota |
| `status` | VARCHAR | Situacao da matricula |

## Relacionamentos

O projeto utiliza relacionamentos relacionais com chaves estrangeiras:

| Relacionamento | Tipo | Explicacao |
|---|---|---|
| `professor` -> `disciplina` | 1:N | Um professor pode lecionar varias disciplinas |
| `aluno` -> `matricula_aluno` | 1:N | Um aluno pode ter varias matriculas |
| `disciplina` -> `matricula_aluno` | 1:N | Uma disciplina pode ter varios alunos matriculados |

Na pratica, a tabela `matricula_aluno` funciona como a ligacao entre alunos e disciplinas.

## Conceitos de Banco de Dados Implementados

### CRUD com SQL

O sistema executa as operacoes principais de banco:

```sql
INSERT INTO aluno (nome_completo, email, cpf)
VALUES ('Maria Souza', 'maria@email.com', '12345678900');
```

```sql
UPDATE aluno
SET nome_completo = 'Maria Silva',
    email = 'maria@email.com',
    cpf = '12345678900'
WHERE id = 1;
```

```sql
DELETE FROM aluno
WHERE id = 1;
```

```sql
SELECT *
FROM aluno;
```

### JOIN

Os relatorios usam `JOIN` para combinar dados de varias tabelas.

Exemplo:

```sql
SELECT
    a.nome_completo AS aluno,
    d.nome AS disciplina,
    p.nome_completo AS professor,
    m.nota1,
    m.nota2,
    m.status
FROM matricula_aluno m
LEFT JOIN aluno a ON a.id = m.aluno_id
LEFT JOIN disciplina d ON d.id = m.disciplina_id
LEFT JOIN professor p ON p.id = d.professor_id;
```

### WHERE

Os filtros dos relatorios usam `WHERE` para pesquisar valores informados pelo usuario.

Exemplo:

```sql
SELECT
    a.nome_completo,
    a.email
FROM aluno a
WHERE LOWER(CAST(a.nome_completo AS TEXT)) LIKE '%maria%';
```

### VIEW

As views estao em:

```text
src/main/resources/db/views.sql
```

Views implementadas:

| View | Finalidade |
|---|---|
| `vw_historico_aluno_completo` | Mostra historico completo do aluno com disciplina, professor, notas, media e status |
| `vw_matriculas_aprovadas` | Mostra apenas matriculas aprovadas |
| `vw_relatorio_desempenho_disciplinas` | Mostra desempenho por disciplina, com totais, aprovados, reprovados e media |

Exemplo de uso:

```sql
SELECT *
FROM vw_historico_aluno_completo;
```

### SUBQUERY

As subqueries de exemplo estao em:

```text
src/main/resources/db/subqueries.sql
```

Elas demonstram consultas com `IN`, `NOT EXISTS`, `HAVING` e subconsultas dentro de agregacoes.

Exemplo:

```sql
SELECT
    a.id,
    a.nome_completo,
    COUNT(ma.id) AS total_matriculas
FROM aluno a
INNER JOIN matricula_aluno ma ON a.id = ma.aluno_id
WHERE a.id IN (
    SELECT aluno_id
    FROM matricula_aluno
    GROUP BY aluno_id
)
GROUP BY a.id, a.nome_completo;
```

## Relatorios Personalizados

O sistema possui uma tela de relatorios onde o usuario escolhe:

- A origem dos dados.
- Os campos desejados.
- Um termo de pesquisa opcional.

O backend monta uma consulta SQL dinamica e retorna:

```json
{
  "sql": "SELECT ... FROM ... WHERE ...;",
  "cabecalhos": ["Aluno", "Disciplina", "Status"],
  "dados": [
    {
      "aluno": "Maria Souza",
      "disciplina": "Banco de Dados",
      "status": "APROVADO"
    }
  ]
}
```

A tabela de resultados continua sendo exibida normalmente, mas agora o SQL usado para gerar o relatorio tambem aparece acima dos dados.

## Exibicao do SQL no Frontend

Como o projeto e focado em Banco de Dados, a interface exibe a SQL gerada nas operacoes principais.

### Na aba CRUD

Ao criar, editar ou excluir registros, o frontend mostra o SQL equivalente:

- `INSERT` ao criar.
- `UPDATE` ao editar.
- `DELETE` ao excluir.

Exemplo de resposta da API:

```json
{
  "sql": "DELETE FROM aluno\nWHERE id = 1;"
}
```

### Na aba Relatorios

Ao gerar um relatorio, o frontend mostra o `SELECT` completo utilizado, incluindo `JOIN`, `WHERE`, `ORDER BY` e `LIMIT` quando aplicavel.

O bloco aparece como codigo, preservando quebras de linha para facilitar a leitura.

## Endpoints Principais

### CRUD

| Metodo | Rota | Funcao |
|---|---|---|
| `GET` | `/alunos` | Listar alunos |
| `POST` | `/alunos` | Criar aluno e retornar SQL |
| `PUT` | `/alunos/{id}` | Atualizar aluno e retornar SQL |
| `DELETE` | `/alunos/{id}` | Excluir aluno e retornar SQL |
| `GET` | `/professores` | Listar professores |
| `POST` | `/professores` | Criar professor e retornar SQL |
| `PUT` | `/professores/{id}` | Atualizar professor e retornar SQL |
| `DELETE` | `/professores/{id}` | Excluir professor e retornar SQL |
| `GET` | `/disciplinas` | Listar disciplinas |
| `POST` | `/disciplinas` | Criar disciplina e retornar SQL |
| `PUT` | `/disciplinas/{id}` | Atualizar disciplina e retornar SQL |
| `DELETE` | `/disciplinas/{id}` | Excluir disciplina e retornar SQL |
| `GET` | `/matriculas` | Listar matriculas |
| `POST` | `/matriculas` | Criar matricula e retornar SQL |
| `PUT` | `/matriculas/{id}` | Atualizar matricula e retornar SQL |
| `DELETE` | `/matriculas/{id}` | Excluir matricula e retornar SQL |

### Relatorios

| Metodo | Rota | Funcao |
|---|---|---|
| `GET` | `/relatorios/metadados` | Lista tabelas e campos disponiveis para relatorio |
| `POST` | `/relatorios/gerar` | Gera relatorio e retorna SQL + dados |
| `POST` | `/relatorios/exportar-csv` | Exporta relatorio em CSV |

## Estrutura Relevante do Projeto

```text
src/main/java/br/com/alunonline/api/
|-- controller/       Controllers REST
|-- dtos/             Objetos de entrada e resposta
|-- model/            Entidades JPA
|-- repository/       Repositorios Spring Data JPA
|-- service/          Regras de negocio e montagem de SQL

src/main/resources/
|-- application.properties
|-- db/
|   |-- views.sql
|   |-- subqueries.sql
|-- static/
|   |-- index.html
|   |-- app.js
|   |-- styles.css
```

## Como Executar

### 1. Criar o banco PostgreSQL

```sql
CREATE DATABASE aluno_online;
```

### 2. Configurar acesso ao banco

Arquivo:

```text
src/main/resources/application.properties
```

Exemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aluno_online
spring.datasource.username=postgres
spring.datasource.password=123
spring.jpa.hibernate.ddl-auto=update
server.port=8081
```

### 3. Rodar o projeto

Na raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

### 4. Abrir o frontend

```text
http://localhost:8081
```

ou:

```text
http://localhost:8081/index.html
```

## Observacao Sobre o SQL Exibido

As operacoes CRUD usam Spring Data JPA/Hibernate para persistir no banco. O SQL exibido na tela representa a operacao SQL equivalente (`INSERT`, `UPDATE` ou `DELETE`) com os valores informados pelo usuario.

Nos relatorios personalizados, a SQL exibida e a consulta montada pelo `RelatorioService` e enviada ao banco via `JdbcTemplate`.

## Resultado Esperado

Ao usar o sistema, o usuario consegue visualizar:

- Os dados armazenados no PostgreSQL.
- As relacoes entre tabelas.
- Os comandos SQL das operacoes CRUD.
- As consultas SQL dos relatorios.
- O uso pratico de `JOIN`, `WHERE`, `VIEW` e `SUBQUERY`.

Assim, o projeto apresenta a aplicacao de conceitos fundamentais de Banco de Dados em um sistema funcional com backend, frontend e PostgreSQL.
