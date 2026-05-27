
SELECT 
    a.id,
    a.nome_completo,
    a.email,
    COUNT(ma.id) as total_matriculas
FROM aluno a
INNER JOIN matricula_aluno ma ON a.id = ma.aluno_id
WHERE a.id IN (
    SELECT aluno_id 
    FROM matricula_aluno 
    GROUP BY aluno_id 
    HAVING COUNT(*) > (
        SELECT AVG(matriculas_por_aluno)
        FROM (
            SELECT COUNT(*) as matriculas_por_aluno
            FROM matricula_aluno
            GROUP BY aluno_id
        ) subquery_media
    )
)
GROUP BY a.id, a.nome_completo, a.email
ORDER BY total_matriculas DESC;

SELECT 
    d.id,
    d.nome as disciplina,
    p.nome_completo as professor,
    d.carga_horaria,
    ROUND(AVG(CASE 
        WHEN ma.nota1 IS NOT NULL AND ma.nota2 IS NOT NULL 
        THEN (ma.nota1 + ma.nota2) / 2.0 
    END), 2) as media_disciplina
FROM disciplina d
LEFT JOIN professor p ON d.professor_id = p.id
LEFT JOIN matricula_aluno ma ON d.id = ma.disciplina_id
GROUP BY d.id, d.nome, d.carga_horaria, p.nome_completo
HAVING ROUND(AVG(CASE 
    WHEN ma.nota1 IS NOT NULL AND ma.nota2 IS NOT NULL 
    THEN (ma.nota1 + ma.nota2) / 2.0 
END), 2) > (
    SELECT ROUND(AVG(media), 2)
    FROM (
        SELECT (ma.nota1 + ma.nota2) / 2.0 as media
        FROM matricula_aluno ma
        WHERE ma.nota1 IS NOT NULL AND ma.nota2 IS NOT NULL
    ) subquery_media_geral
)
ORDER BY media_disciplina DESC;

SELECT 
    p.id,
    p.nome_completo,
    p.email,
    COUNT(d.id) as disciplinas_sem_alunos
FROM professor p
LEFT JOIN disciplina d ON p.id = d.professor_id
WHERE d.id IN (
    SELECT d2.id 
    FROM disciplina d2
    WHERE NOT EXISTS (
        SELECT 1 
        FROM matricula_aluno ma 
        WHERE ma.disciplina_id = d2.id
    )
)
GROUP BY p.id, p.nome_completo, p.email
ORDER BY disciplinas_sem_alunos DESC;

SELECT 
    a.id,
    a.nome_completo,
    a.email,
    COUNT(CASE WHEN ma.status = 'REPROVADO' THEN 1 END) as total_reprovacoes
FROM aluno a
WHERE a.id IN (
    SELECT DISTINCT ma.aluno_id
    FROM matricula_aluno ma
    WHERE ma.status = 'REPROVADO'
)
LEFT JOIN matricula_aluno ma ON a.id = ma.aluno_id
GROUP BY a.id, a.nome_completo, a.email
ORDER BY total_reprovacoes DESC;

SELECT 
    d.id,
    d.nome as disciplina,
    p.nome_completo as professor,
    COUNT(ma.id) as total_alunos,
    COUNT(CASE WHEN ma.status = 'APROVADO' THEN 1 END) as aprovados,
    COUNT(CASE WHEN ma.status = 'REPROVADO' THEN 1 END) as reprovados,
    ROUND(
        (COUNT(CASE WHEN ma.status = 'REPROVADO' THEN 1 END)::FLOAT / COUNT(ma.id)) * 100, 
        2
    ) as taxa_reprovacao_percentual
FROM disciplina d
LEFT JOIN professor p ON d.professor_id = p.id
LEFT JOIN matricula_aluno ma ON d.id = ma.disciplina_id
GROUP BY d.id, d.nome, p.nome_completo
HAVING COUNT(ma.id) > 0 
    AND (COUNT(CASE WHEN ma.status = 'REPROVADO' THEN 1 END)::FLOAT / COUNT(ma.id)) > 0.3
ORDER BY taxa_reprovacao_percentual DESC;
