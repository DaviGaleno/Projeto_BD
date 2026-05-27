-- =====================================================
-- VIEWS SQL - PROJETO DE BANCO DE DADOS
-- =====================================================
-- Este arquivo contém as Views utilizadas no projeto
-- Views são consultas pré-compiladas armazenadas no banco
-- =====================================================

-- VIEW 1: Histórico Completo do Aluno
-- Retorna o histórico acadêmico de cada aluno com disciplinas, professores e notas
CREATE OR REPLACE VIEW vw_historico_aluno_completo AS
SELECT 
    a.id as aluno_id,
    a.nome_completo,
    a.email,
    a.cpf,
    d.id as disciplina_id,
    d.nome as disciplina,
    d.carga_horaria,
    p.id as professor_id,
    p.nome_completo as professor,
    ma.nota1,
    ma.nota2,
    ROUND((ma.nota1 + ma.nota2) / 2.0, 2) as media,
    ma.status,
    ma.id as matricula_id
FROM aluno a
LEFT JOIN matricula_aluno ma ON a.id = ma.aluno_id
LEFT JOIN disciplina d ON ma.disciplina_id = d.id
LEFT JOIN professor p ON d.professor_id = p.id
ORDER BY a.nome_completo, d.nome;

-- VIEW 2: Matrículas com Status Aprovado
-- Filtra apenas as matrículas com status APROVADO
CREATE OR REPLACE VIEW vw_matriculas_aprovadas AS
SELECT 
    ma.id,
    ma.aluno_id,
    a.nome_completo as aluno_nome,
    ma.disciplina_id,
    d.nome as disciplina_nome,
    p.nome_completo as professor_nome,
    ma.nota1,
    ma.nota2,
    ROUND((ma.nota1 + ma.nota2) / 2.0, 2) as media,
    ma.status,
    d.carga_horaria
FROM matricula_aluno ma
INNER JOIN aluno a ON ma.aluno_id = a.id
INNER JOIN disciplina d ON ma.disciplina_id = d.id
INNER JOIN professor p ON d.professor_id = p.id
WHERE ma.status = 'APROVADO'
ORDER BY a.nome_completo, d.nome;

-- VIEW 3: Relatório de Desempenho por Disciplina
-- Agrupa dados de desempenho agrupados por disciplina
CREATE OR REPLACE VIEW vw_relatorio_desempenho_disciplinas AS
SELECT 
    d.id,
    d.nome as disciplina,
    d.carga_horaria,
    p.nome_completo as professor,
    COUNT(ma.id) as total_matriculas,
    COUNT(CASE WHEN ma.status = 'APROVADO' THEN 1 END) as aprovados,
    COUNT(CASE WHEN ma.status = 'REPROVADO' THEN 1 END) as reprovados,
    COUNT(CASE WHEN ma.nota1 IS NOT NULL AND ma.nota2 IS NOT NULL THEN 1 END) as com_notas,
    ROUND(AVG(CASE 
        WHEN ma.nota1 IS NOT NULL AND ma.nota2 IS NOT NULL 
        THEN (ma.nota1 + ma.nota2) / 2.0 
    END), 2) as media_disciplina
FROM disciplina d
LEFT JOIN professor p ON d.professor_id = p.id
LEFT JOIN matricula_aluno ma ON d.id = ma.disciplina_id
GROUP BY d.id, d.nome, d.carga_horaria, p.nome_completo
ORDER BY d.nome;
