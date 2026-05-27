# PRIMEIRA AVALIACAO - API Java Spring Boot

## Descricao do Projeto

Este projeto consiste no desenvolvimento de uma API REST utilizando Spring Boot para gerenciar dados academicos de **Alunos**, **Professores**, **Disciplinas** e **Matriculas**.

A aplicacao permite realizar operacoes de CRUD, possibilitando o cadastro, consulta, atualizacao e remocao de registros no banco de dados. Alem disso, o sistema tambem possui regras especificas para matriculas, como trancamento, atualizacao de notas, calculo de media, definicao de status e emissao de historico do aluno.

O sistema foi desenvolvido seguindo boas praticas de organizacao em camadas, separando responsabilidades entre Controller, Service, Repository, Model, DTO e Enum.

---

## Tecnologias Utilizadas

* Java 21
* Spring Boot 4.0.4
* Spring Web MVC
* Spring Data JPA
* Maven
* Lombok
* PostgreSQL
* Insomnia para testes de API
* DBeaver para visualizacao do banco de dados

---

## Arquitetura do Projeto

O projeto segue o padrao de arquitetura em camadas:

### Model

Representa as entidades do banco de dados.

Entidades principais:

* `Aluno`
* `Professor`
* `Disciplina`
* `MatriculaAluno`

### Repository

Responsavel pela comunicacao com o banco de dados utilizando Spring Data JPA.

Repositories principais:

* `AlunoRepository`
* `ProfessorRepository`
* `DisciplinaReposiitory`
* `MatriculaAlunoRepository`

### Service

Contem a logica de negocio da aplicacao.

Services principais:

* `AlunoService`
* `ProfessorService`
* `DisciplinaService`
* `MatriculaAlunoService`

### Controller

Responsavel por expor os endpoints da API.

Controllers principais:

* `AlunoController`
* `ProfessorController`
* `DisciplinaController`
* `MatriculaAlunoController`

### DTO

Utilizado para entrada e saida de dados especificos, evitando expor ou receber estruturas desnecessarias.

DTOs implementados:

* `AtualizarNotasRequestDTO`
* `DisciplinasAlunoResponseDTO`
* `HistoricoAlunoResponseDTO`

### Enum

Utilizado para controlar os status possiveis de uma matricula.

Status da matricula:

* `MATRICULADO`
* `APROVADO`
* `REPROVADO`
* `TRANCADO`
* `DESLIGADO`

---

## Detalhamento do Codigo

### Entidade Aluno

A classe `Aluno` representa a tabela `aluno` no banco de dados.

```java
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "aluno")
@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;
    private String email;
    private String cpf;
}
```

### Entidade Professor

A classe `Professor` representa a tabela de professores.

```java
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Professor")
@Entity
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;
    private String email;
    private String cpf;
}
```

### Entidade Disciplina

A classe `Disciplina` representa uma disciplina cadastrada no sistema e possui relacionamento com um professor.

```java
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "disciplina")
@Entity
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer cargaHoraria;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;
}
```

### Entidade MatriculaAluno

A classe `MatriculaAluno` representa a matricula de um aluno em uma disciplina.

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "matricula_aluno")
@Entity
public class MatriculaAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    private Double nota1;
    private Double nota2;

    @Enumerated(EnumType.STRING)
    private MatriculaAlunoStatusEnum status;
}
```

### Regras de Matricula

Ao criar uma matricula, o status inicial e definido automaticamente como `MATRICULADO`.

Tambem foram implementadas as seguintes regras:

* Uma matricula pode ser trancada somente quando estiver com status `MATRICULADO`.
* As notas podem ser atualizadas por meio de um endpoint especifico.
* Quando `nota1` e `nota2` estiverem preenchidas, o sistema calcula a media.
* Se a media for maior ou igual a `7.0`, o status passa para `APROVADO`.
* Se a media for menor que `7.0`, o status passa para `REPROVADO`.
* O historico do aluno retorna seus dados e a lista de disciplinas cursadas.

---

## Endpoints da API

### Interface Web

A aplicacao tambem possui uma tela interativa para executar o CRUD e gerar relatorios personalizados.

| Rota | Descricao |
| ---- | --------- |
| `/` | Tela principal com CRUD e relatorios |

### Alunos

| Metodo | Rota | Descricao |
| ------ | ---- | --------- |
| POST | `/alunos` | Criar aluno |
| GET | `/alunos` | Listar todos os alunos |
| GET | `/alunos/{id}` | Buscar aluno por ID |
| PUT | `/alunos/{id}` | Atualizar aluno |
| DELETE | `/alunos/{id}` | Deletar aluno |

### Professores

| Metodo | Rota | Descricao |
| ------ | ---- | --------- |
| POST | `/professores` | Criar professor |
| GET | `/professores` | Listar todos os professores |
| GET | `/professores/{id}` | Buscar professor por ID |
| PUT | `/professores/{id}` | Atualizar professor |
| DELETE | `/professores/{id}` | Deletar professor |

### Disciplinas

| Metodo | Rota | Descricao |
| ------ | ---- | --------- |
| POST | `/disciplinas` | Criar disciplina |
| GET | `/disciplinas` | Listar todas as disciplinas |
| GET | `/disciplinas/{id}` | Buscar disciplina por ID |
| PUT | `/disciplinas/{id}` | Atualizar disciplina |
| DELETE | `/disciplinas/{id}` | Deletar disciplina |

### Matriculas

| Metodo | Rota | Descricao |
| ------ | ---- | --------- |
| POST | `/matriculas` | Criar matricula |
| GET | `/matriculas` | Listar todas as matriculas |
| GET | `/matriculas/{id}` | Buscar matricula por ID |
| PUT | `/matriculas/{id}` | Atualizar matricula |
| DELETE | `/matriculas/{id}` | Deletar matricula |
| PATCH | `/matriculas/trancar/{id}` | Trancar matricula |
| PATCH | `/matriculas/atualizar-notas/{id}` | Atualizar notas da matricula |
| GET | `/matriculas/emitir-historico/{alunoId}` | Emitir historico do aluno |

### Relatorios Personalizados

| Metodo | Rota | Descricao |
| ------ | ---- | --------- |
| GET | `/relatorios/metadados` | Lista as tabelas e campos disponiveis para relatorio |
| POST | `/relatorios/gerar` | Gera um relatorio personalizado com tabela, campos e termo de busca |
| POST | `/relatorios/exportar-csv` | Exporta o relatorio personalizado em CSV |

Exemplo para gerar relatorio:

```json
{
  "tabela": "matriculas",
  "campos": ["aluno", "disciplina", "nota1", "nota2", "status"],
  "termo": "aprovado"
}
```

Tabelas disponiveis para relatorio:

* `alunos`
* `professores`
* `disciplinas`
* `matriculas`

---

## Exemplos de Requisicoes

### Criar Aluno

```json
{
  "nomeCompleto": "Joao Silva",
  "email": "joao@email.com",
  "cpf": "12345678900"
}
```

### Criar Professor

```json
{
  "nomeCompleto": "Maria Souza",
  "email": "maria@email.com",
  "cpf": "98765432100"
}
```

### Criar Disciplina

```json
{
  "nome": "Programacao Java",
  "cargaHoraria": 80,
  "professor": {
    "id": 1
  }
}
```

### Criar Matricula

```json
{
  "aluno": {
    "id": 1
  },
  "disciplina": {
    "id": 1
  }
}
```

### Atualizar Notas

```json
{
  "nota1": 8.0,
  "nota2": 7.5
}
```

### Exemplo de Historico

```json
{
  "nomeAluno": "Joao Silva",
  "emailAluno": "joao@email.com",
  "cpfAluno": "12345678900",
  "disciplinas": [
    {
      "nomeDisciplina": "Programacao Java",
      "nomeProfessor": "Maria Souza",
      "nota1": 8.0,
      "nota2": 7.5,
      "media": 7.75,
      "status": "APROVADO"
    }
  ]
}
```

---

## Banco de Dados

A aplicacao utiliza PostgreSQL.

Configuracao atual em `application.properties`:

```properties
spring.application.name=Aluno Online

spring.datasource.url=jdbc:postgresql://localhost:5432/aluno_online
spring.datasource.username=postgres
spring.datasource.password=123
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Tabelas principais criadas pela aplicacao:

* `aluno`
* `Professor`
* `disciplina`
* `matricula_aluno`

---

## Testes no Insomnia

Os endpoints podem ser testados no Insomnia utilizando a URL base:

```text
http://localhost:8080
```

### Criar Aluno

<img width="600" height="278" alt="{85C1AAF9-B90A-433D-8333-2FCD8A724EB0}" src="https://github.com/user-attachments/assets/074b37a5-4599-450d-aed9-270e1e3eb021" />

### Listar Alunos

<img width="727" height="581" alt="{1A37BFEA-8F05-4C12-9EB4-8678398F478A}" src="https://github.com/user-attachments/assets/1b2330fc-47d4-426e-8db8-2332940b1764" />

### Editar Alunos

<img width="622" height="237" alt="{BBCBC833-2603-4707-B07B-D0B9E9EEFF9E}" src="https://github.com/user-attachments/assets/8b517247-6c56-4e3c-8858-5468ea622a05" />

### Buscar Aluno Por ID

<img width="726" height="269" alt="{2DD40A44-F6D0-4B0A-969E-915D036405AB}" src="https://github.com/user-attachments/assets/5fd50d97-50bf-4053-9880-f9a6ecb87ca3" />

### Deletar Aluno Por ID

<img width="614" height="274" alt="{176C0858-F381-4E5C-9DB1-118D29FD1505}" src="https://github.com/user-attachments/assets/3b9f1641-7095-491e-b71c-5b956edcba8c" />

### Criar Professor

<img width="617" height="280" alt="{6468324D-045A-49CE-BF04-0001CB3B3BCF}" src="https://github.com/user-attachments/assets/aea8a4a0-b1fe-4361-bc5d-e3eebf7bd238" />

### Listar Professores

<img width="666" height="314" alt="{C1DBD3C7-C0EC-41BC-AF00-B9BA69649ABE}" src="https://github.com/user-attachments/assets/75e68a3d-300b-4535-9996-e2dddad94ac9" />

### Editar Professores

<img width="618" height="250" alt="{3C67B37D-EAB2-49EC-9B14-B880F1AFBBEB}" src="https://github.com/user-attachments/assets/34ce6652-2559-4eae-93de-cf41d4bd20b4" />

### Buscar Professor Por ID

<img width="687" height="261" alt="{2C201424-55AC-4E5B-9684-87405AAE4327}" src="https://github.com/user-attachments/assets/13c152a9-1258-4cdc-b1a9-e552fbbe35a7" />

### Deletar Professor Por ID

<img width="620" height="231" alt="{40765108-139A-42D5-ABE2-BB9071F733AF}" src="https://github.com/user-attachments/assets/f9d3a403-1bee-447e-90a0-4f842533da82" />

---

## Como Executar o Projeto

1. Clonar o repositorio:

```bash
git clone https://github.com/DaviGaleno/PRIMEIRA_AVALIACAO_JavaSpring.git
```

2. Entrar na pasta do projeto:

```bash
cd PRIMEIRA_AVALIACAO_JavaSpring
```

3. Configurar o PostgreSQL:

```sql
CREATE DATABASE aluno_online;
```

4. Conferir usuario e senha em `src/main/resources/application.properties`.

5. Executar o projeto:

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

6. Acessar a API:

```text
http://localhost:8080/alunos
```

7. Acessar a tela interativa:

```text
http://localhost:8080
```

Observacao: o `pom.xml` esta configurado para Java 21. Caso o terminal esteja com Java 17, a compilacao pode ser validada com:

```bash
mvnw.cmd -DskipTests "-Djava.version=17" package
```

---

## Consideracoes Finais

O projeto demonstra a aplicacao pratica de:

* Desenvolvimento de APIs REST com Spring Boot
* Uso do Spring Data JPA
* Organizacao em arquitetura em camadas
* Integracao com PostgreSQL
* Relacionamentos entre entidades
* Regras de negocio para matriculas
* Uso de DTOs para entrada e saida de dados

Alem disso, foram utilizadas ferramentas como Insomnia e DBeaver para testes e validacao dos dados.
