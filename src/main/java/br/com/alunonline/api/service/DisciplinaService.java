package br.com.alunonline.api.service;

import br.com.alunonline.api.dtos.OperacaoSqlResponseDTO;
import br.com.alunonline.api.model.Disciplina;
import br.com.alunonline.api.repository.DisciplinaReposiitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DisciplinaService {

    @Autowired
    DisciplinaReposiitory disciplinaReposiitory;

    public OperacaoSqlResponseDTO criarDisciplina(Disciplina disciplina){
        disciplinaReposiitory.save(disciplina);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.insert("disciplina", valoresDisciplina(disciplina)));
    }

    public List<Disciplina> listarTodasDisciplinas() {
        return disciplinaReposiitory.findAll();
    }

    public Optional<Disciplina> disciplinaPorId(Long id) {
        return disciplinaReposiitory.findById(id);
    }

    public OperacaoSqlResponseDTO deletarDisciplinaPorId(Long id){
        disciplinaReposiitory.deleteById(id);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.delete("disciplina", id));
    }

    public OperacaoSqlResponseDTO atualizarDisciplinaPorId(Long id, Disciplina disciplinaEditada){
        disciplinaEditada.setId(id);
        disciplinaReposiitory.save(disciplinaEditada);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.update("disciplina", valoresDisciplina(disciplinaEditada), id));
    }

    private Map<String, Object> valoresDisciplina(Disciplina disciplina) {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("nome", disciplina.getNome());
        valores.put("carga_horaria", disciplina.getCargaHoraria());
        valores.put("professor_id", disciplina.getProfessor() == null ? null : disciplina.getProfessor().getId());
        return valores;
    }
}
