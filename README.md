# 📚 PROJETO DE BANCO DE DADOS - API Java Spring Boot

## 📌 Descrição do Projeto

Este projeto é um **trabalho acadêmico de Banco de Dados** que demonstra a aplicação prática de conceitos fundamentais de gerenciamento de dados através de uma API REST desenvolvida com Spring Boot.

O sistema gerencia dados de **Alunos**, **Professores**, **Disciplinas** e **Matrículas**, implementando operações completas de CRUD (Create, Read, Update e Delete) com validações e regras de negócio complexas.

O projeto segue boas práticas de organização em camadas, separando responsabilidades entre Controller, Service, Repository, Model, DTO e implementa conceitos avançados de banco de dados como **Views SQL** e **Subqueries**.

---

## 🎓 Conceitos de Banco de Dados Utilizados

### ✅ **Views SQL** (Obrigatório)
As Views foram implementadas para fornecer consultas pré-compiladas e otimizadas:

- **View `historico_aluno_completo`**: Retorna o histórico de cada aluno com disciplinas, notas e professores
- **View `matriculas_aprovadas`**: Filtra matrículas com status APROVADO
- **View `relatorio_desempenho_disciplinas`**: Agrupa dados de desempenho por disciplina

Localização: `src/main/resources/db/views.sql`

**Benefícios das Views utilizadas:**
- Simplificação de queries complexas
- Reutilização de lógica de consulta
- Melhor performance em operações repetidas
- Abstração de dados sensíveis

### ✅ **Subqueries (Subconsultas)**
Implementadas para consultas aninhadas avançadas:

- Buscar alunos que estão matriculados em mais de 3 disciplinas
- Listar disciplinas com média geral acima da média total
- Filtros dinâmicos nos endpoints de relatório

Localização: `src/main/java/com/projeto/repository/`

**Exemplo de uso:**
```sql
SELECT * FROM alunos 
WHERE id IN (
    SELECT aluno_id FROM matricula_aluno 
    GROUP BY aluno_id 
    HAVING COUNT(*) > 3
)
```

### 📊 **Joins e Relacionamentos**
- **INNER JOIN**: Matrículas com Alunos e Disciplinas
- **LEFT JOIN**: Disciplinas sem professor associado
- **Foreign Keys**: Integridade referencial entre tabelas

---

## 🛠️ Tecnologias Utilizadas

* **Java 21** - Linguagem de programação
* **Spring Boot 4.0.4** - Framework web
* **Spring Data JPA** - ORM para persistência
* **PostgreSQL** - Banco de dados relacional
* **Maven** - Gerenciador de dependências
* **Lombok** - Redução de boilerplate
* **Insomnia** - Testes de API
* **DBeaver** - Visualização e gerenciamento do banco de dados

---

## 🗄️ Arquitetura do Banco de Dados

### 📋 Tabelas Principais

```
┌─────────────────┐
│      ALUNO      │
├─────────────────┤
│ id (PK)         │
│ nome_completo   │
│ email           │
│ cpf             │
└─────────────────┘
         │
         │ 1:N
         │
┌─────────────────────────┐
│   MATRICULA_ALUNO       │
├─────────────────────────┤
│ id (PK)                 │
│ aluno_id (FK)           │
│ disciplina_id (FK)      │
│ nota1                   │
│ nota2                   │
│ status                  │
└─────────────────────────┘
         │
         │ N:1
         │
┌─────────────────┐
│   DISCIPLINA    │
├─────────────────┤
│ id (PK)         │
│ nome            │
│ carga_horaria   │
│ professor_id(FK)│
└─────────────────┘
         │
         │ N:1
         │
┌─────────────────┐
│   PROFESSOR     │
├─────────────────┤
│ id (PK)         │
│ nome_completo   │
│ email           │
│ cpf             │
└─────────────────┘
```

### 🔗 Relacionamentos

| Relacionamento | Tipo | Descrição |
|---|---|---|
| Aluno ↔ Matrícula | 1:N | Um aluno possui várias matrículas |
| Professor ↔ Disciplina | 1:N | Um professor leciona várias disciplinas |
| Disciplina ↔ Matrícula | 1:N | Uma disciplina possui várias matrículas |

---

## 🧱 Arquitetura do Projeto

O projeto segue o padrão de arquitetura em camadas:

### 📂 Model (Entidades)
Representa as tabelas do banco de dados com mapeamento JPA.

**Exemplo - Entidade Matrícula:**
```java
@Entity
@Table(name = "matricula_aluno")
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

    @Enumerated(EnumType.STRING)
    private MatriculaAlunoStatusEnum status;
}
```

### 📂 Repository
Responsável pela comunicação com o banco usando Spring Data JPA.

Implements queries customizadas com `@Query` para Views:
```java
@Query(value = "SELECT * FROM historico_aluno_completo WHERE aluno_id = ?1", nativeQuery = true)
List<HistoricoAlunoView> buscarHistoricoAluno(Long alunoId);
```

### 📂 Service
Contém a lógica de negócio e regras de validação.

**Regras implementadas:**
- Cálculo automático de média quando ambas as notas são preenchidas
- Atualização automática de status baseado na média
- Validação de status para trancar matrícula

### 📂 Controller
Expõe os endpoints da API REST.

---

## 📊 Dados e Consultas de Exemplo

### Exemplo de View - Histórico do Aluno

```sql
CREATE VIEW historico_aluno_completo AS
SELECT 
    a.id as aluno_id,
    a.nome_completo,
    a.email,
    d.nome as disciplina,
    p.nome_completo as professor,
    ma.nota1,
    ma.nota2,
    ROUND((ma.nota1 + ma.nota2) / 2, 2) as media,
    ma.status
FROM alunos a
INNER JOIN matricula_aluno ma ON a.id = ma.aluno_id
INNER JOIN disciplina d ON ma.disciplina_id = d.id
INNER JOIN professor p ON d.professor_id = p.id
ORDER BY a.nome_completo, d.nome;
```

### Exemplo de Subquery - Alunos com Múltiplas Matrículas

```sql
SELECT a.nome_completo, COUNT(ma.id) as total_matriculas
FROM alunos a
INNER JOIN matricula_aluno ma ON a.id = ma.aluno_id
GROUP BY a.id
HAVING COUNT(ma.id) > (
    SELECT AVG(matriculas_por_aluno)
    FROM (
        SELECT COUNT(*) as matriculas_por_aluno
        FROM matricula_aluno
        GROUP BY aluno_id
    ) subquery
);
```

---

## 🔗 Endpoints da API

### Alunos
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/alunos` | Criar aluno |
| GET | `/alunos` | Listar todos os alunos |
| GET | `/alunos/{id}` | Buscar aluno por ID |
| PUT | `/alunos/{id}` | Atualizar aluno |
| DELETE | `/alunos/{id}` | Deletar aluno |

### Professores
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/professores` | Criar professor |
| GET | `/professores` | Listar todos os professores |
| GET | `/professores/{id}` | Buscar professor por ID |
| PUT | `/professores/{id}` | Atualizar professor |
| DELETE | `/professores/{id}` | Deletar professor |

### Disciplinas
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/disciplinas` | Criar disciplina |
| GET | `/disciplinas` | Listar todas as disciplinas |
| GET | `/disciplinas/{id}` | Buscar disciplina por ID |
| PUT | `/disciplinas/{id}` | Atualizar disciplina |
| DELETE | `/disciplinas/{id}` | Deletar disciplina |

### Matrículas
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/matriculas` | Criar matrícula |
| GET | `/matriculas` | Listar todas as matrículas |
| GET | `/matriculas/{id}` | Buscar matrícula por ID |
| PUT | `/matriculas/{id}` | Atualizar matrícula |
| DELETE | `/matriculas/{id}` | Deletar matrícula |
| PATCH | `/matriculas/trancar/{id}` | Trancar matrícula |
| PATCH | `/matriculas/atualizar-notas/{id}` | Atualizar notas |
| GET | `/matriculas/emitir-historico/{alunoId}` | Emitir histórico (utiliza VIEW) |

### Relatórios Personalizados
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/relatorios/metadados` | Lista as tabelas disponíveis |
| POST | `/relatorios/gerar` | Gera relatório com subqueries |
| POST | `/relatorios/exportar-csv` | Exporta relatório em CSV |

---

## 🔐 Regras de Negócio

1. **Status de Matrícula**: MATRICULADO → APROVADO/REPROVADO
2. **Cálculo de Média**: `(nota1 + nota2) / 2`
3. **Aprovação**: Média ≥ 7.0
4. **Trancar Matrícula**: Apenas em status MATRICULADO
5. **Integridade Referencial**: Cascata de deleção configurada

---

## ▶️ Como Executar o Projeto

### Pré-requisitos
- Java 21 ou superior
- PostgreSQL instalado e rodando
- Maven instalado

### Passo a Passo

1. **Clonar o repositório:**
```bash
git clone https://github.com/DaviGaleno/Projeto_BD.git
cd Projeto_BD
```

2. **Criar o banco de dados:**
```sql
CREATE DATABASE aluno_online;
```

3. **Configurar credenciais em `application.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aluno_online
spring.datasource.username=postgres
spring.datasource.password=sua_senha
```

4. **Executar o projeto:**
```bash
./mvnw spring-boot:run
```

---

## 🌐 Acessar a Aplicação

### Frontend + API Integrados

A aplicaç��o **integra automaticamente o frontend e a API** na mesma aplicação Spring Boot!

```
┌─────────────────────────────────┐
│  Spring Boot (Porta 8081)       │
│  ├─ API REST (/alunos, etc)     │
│  └─ Servidor de Arquivos        │
│     └─ static/ ✅               │
│        ├─ index.html            │
│        ├─ app.js                │
│        └─ styles.css            │
└─────────────────────────────────┘
```

### 📍 Acessar a Interface Web

Após o Spring Boot iniciar, simplesmente abra no navegador:

```
http://localhost:8081
```

**OU**

```
http://localhost:8081/index.html
```

### ✅ O que você verá:

- Interface CRUD completa com **2 abas**:
  - **CRUD**: Gerenciar Alunos, Professores, Disciplinas e Matrículas
  - **Relatórios**: Gerar relatórios personalizados e exportar em CSV
- Formulários para criar, editar e deletar registros
- Tabelas dinâmicas com dados do banco
- Integração perfeita com a API (sem CORS, mesma origem)

### 🔍 Arquivos do Frontend

Os arquivos do frontend estão em:
```
src/main/resources/static/
├── index.html       # Página principal
├── app.js           # Lógica JavaScript (requisições API)
└── styles.css       # Estilos CSS
```

**Por que funciona assim?** Spring Boot serve automaticamente arquivos da pasta `static/` como conteúdo estático. Perfeito para aplicações integradas!

---

## 📱 Testes via API (Opcional)

Se preferir testar apenas a API (sem frontend), use **Insomnia** ou **Postman**:

```
URL Base: http://localhost:8081
```

**Exemplo - Listar Alunos:**
```bash
curl -X GET http://localhost:8081/alunos
```

---

## 🎯 Conceitos de Banco de Dados Aplicados

| Conceito | Onde foi utilizado | Arquivo/Classe |
|----------|-------------------|-----------------|
| **Views** ✅ | Histórico do aluno, Relatórios | `resources/db/views.sql`, `HistoricoAlunoResponseDTO` |
| **Subqueries** | Filtros dinâmicos, Relatórios personalizados | `RelatorioService`, `RelatorioController` |
| **Foreign Keys** | Relacionamentos entre tabelas | `Disciplina.java`, `MatriculaAluno.java` |
| **JOINS** | Consultas complexas | Repository custom queries |
| **Índices** | Performance | Auto-criados em PK e FK |
| **Triggers** | Atualização de status | Procedimentos armazenados (opcional) |
| **Procedures** | Operações em batch | `src/main/resources/db/procedures.sql` |

---

## 📚 Considerações Finais

Este projeto demonstra a aplicação integrada de:

✅ Conceitos fundamentais de Banco de Dados (Views, Subqueries, Joins)
✅ Modelagem relacional com normalização
✅ Persistência com Spring Data JPA
✅ Arquitetura em camadas
✅ RESTful API design
✅ Regras de negócio complexas
✅ Validação e integridade de dados
✅ Frontend integrado com Backend (sem servidor externo necessário)

O projeto foi desenvolvido como exercício acadêmico para consolidar conhecimentos em Banco de Dados, demonstrando como conceitos teóricos são aplicados em uma aplicação prática e profissional.

---

**Autor:** Davi Galeno  
**Data:** Maio 2026  
**Disciplina:** Banco de Dados
