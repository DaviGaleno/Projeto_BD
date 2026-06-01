package br.com.alunonline.api.service;

import br.com.alunonline.api.dtos.OperacaoSqlResponseDTO;
import br.com.alunonline.api.model.Professor;
import br.com.alunonline.api.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    ProfessorRepository professorRepository;

    public OperacaoSqlResponseDTO criarProfessor(Professor professor){
        professorRepository.save(professor);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.insert("professor", valoresProfessor(professor)));
    }

    public List<Professor> listarTodosProfessores() {
        return professorRepository.findAll();
    }

    public Optional<Professor> professorPorId(Long id) {
        return professorRepository.findById(id);
    }

    public OperacaoSqlResponseDTO deletarProfessorPorId(Long id){
        professorRepository.deleteById(id);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.delete("professor", id));
    }

    public OperacaoSqlResponseDTO atualizarProfessorPorId(Long id, Professor professorEditado){
        professorEditado.setId(id);
        professorRepository.save(professorEditado);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.update("professor", valoresProfessor(professorEditado), id));
    }

    private Map<String, Object> valoresProfessor(Professor professor) {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("nome_completo", professor.getNomeCompleto());
        valores.put("email", professor.getEmail());
        valores.put("cpf", professor.getCpf());
        return valores;
    }
}
