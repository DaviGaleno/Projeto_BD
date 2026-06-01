package br.com.alunonline.api.service;

import br.com.alunonline.api.dtos.AtualizarNotasRequestDTO;
import br.com.alunonline.api.dtos.DisciplinasAlunoResponseDTO;
import br.com.alunonline.api.dtos.HistoricoAlunoResponseDTO;
import br.com.alunonline.api.dtos.OperacaoSqlResponseDTO;
import br.com.alunonline.api.enums.MatriculaAlunoStatusEnum;
import br.com.alunonline.api.model.Aluno;
import br.com.alunonline.api.model.MatriculaAluno;
import br.com.alunonline.api.repository.MatriculaAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MatriculaAlunoService {

    private static final Double MEDIA_PARA_APROVACAO = 7.0;

    @Autowired
    MatriculaAlunoRepository matriculaAlunoRepository;

    public List<MatriculaAluno> listarTodasMatriculas() {
        return matriculaAlunoRepository.findAll();
    }

    public Optional<MatriculaAluno> matriculaPorId(Long id) {
        return matriculaAlunoRepository.findById(id);
    }

    public OperacaoSqlResponseDTO criarMatricula(MatriculaAluno matriculaAluno) {
        matriculaAluno.setStatus(MatriculaAlunoStatusEnum.MATRICULADO);
        matriculaAlunoRepository.save(matriculaAluno);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.insert("matricula_aluno", valoresMatricula(matriculaAluno)));
    }

    public OperacaoSqlResponseDTO atualizarMatriculaPorId(Long id, MatriculaAluno matriculaEditada) {
        MatriculaAluno matriculaAtual = matriculaAlunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Matricula nao encontrada"));

        matriculaEditada.setId(id);
        if (matriculaEditada.getStatus() == null) {
            matriculaEditada.setStatus(matriculaAtual.getStatus());
        }
        matriculaAlunoRepository.save(matriculaEditada);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.update("matricula_aluno", valoresMatricula(matriculaEditada), id));
    }

    public OperacaoSqlResponseDTO deletarMatriculaPorId(Long id) {
        matriculaAlunoRepository.deleteById(id);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.delete("matricula_aluno", id));
    }

    public void trancarMatricula(Long id) {
        MatriculaAluno matricula = matriculaAlunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Matricula não encontrada"));

        if (matricula.getStatus().equals(MatriculaAlunoStatusEnum.MATRICULADO)) {
            matricula.setStatus(MatriculaAlunoStatusEnum.TRANCADO);
            matriculaAlunoRepository.save(matricula);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Só é possível trancar com status MATRICULADO");
        }
    }

    public void atualizarNotas(Long id, AtualizarNotasRequestDTO dto) {
        MatriculaAluno matricula = matriculaAlunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Matricula não encontrada"));

        if (dto.getNota1() != null) matricula.setNota1(dto.getNota1());
        if (dto.getNota2() != null) matricula.setNota2(dto.getNota2());

        if (matricula.getNota1() != null && matricula.getNota2() != null) {
            Double media = (matricula.getNota1() + matricula.getNota2()) / 2;
            matricula.setStatus(media >= MEDIA_PARA_APROVACAO
                    ? MatriculaAlunoStatusEnum.APROVADO
                    : MatriculaAlunoStatusEnum.REPROVADO);
        }

        matriculaAlunoRepository.save(matricula);
    }

    public HistoricoAlunoResponseDTO emitirHistorico(Long alunoId) {
        List<MatriculaAluno> matriculas =
                matriculaAlunoRepository.findByAlunoId(alunoId);

        if (matriculas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma matrícula encontrada para esse aluno");
        }

        Aluno aluno = matriculas.get(0).getAluno();
        List<DisciplinasAlunoResponseDTO> disciplinas = new ArrayList<>();

        for (MatriculaAluno matricula : matriculas) {
            DisciplinasAlunoResponseDTO disc = new DisciplinasAlunoResponseDTO();
            disc.setNomeDisciplina(matricula.getDisciplina().getNome());
            disc.setNomeProfessor(matricula.getDisciplina().getProfessor().getNomeCompleto());
            disc.setNota1(matricula.getNota1());
               disc.setNota2(matricula.getNota2());
            if (matricula.getNota1() != null && matricula.getNota2() != null) {
                disc.setMedia((matricula.getNota1() + matricula.getNota2()) / 2);
            }
            disc.setStatus(matricula.getStatus());
            disciplinas.add(disc);
        }

        HistoricoAlunoResponseDTO historico = new HistoricoAlunoResponseDTO();
        historico.setNomeAluno(aluno.getNomeCompleto());
        historico.setEmailAluno(aluno.getEmail());
        historico.setCpfAluno(aluno.getCpf());
        historico.setDisciplinas(disciplinas);
        return historico;
    }

    private Map<String, Object> valoresMatricula(MatriculaAluno matricula) {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("aluno_id", matricula.getAluno() == null ? null : matricula.getAluno().getId());
        valores.put("disciplina_id", matricula.getDisciplina() == null ? null : matricula.getDisciplina().getId());
        valores.put("nota1", matricula.getNota1());
        valores.put("nota2", matricula.getNota2());
        valores.put("status", matricula.getStatus());
        return valores;
    }
}
