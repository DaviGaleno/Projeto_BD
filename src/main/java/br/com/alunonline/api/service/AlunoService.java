package br.com.alunonline.api.service;

import br.com.alunonline.api.dtos.OperacaoSqlResponseDTO;
import br.com.alunonline.api.model.Aluno;
import br.com.alunonline.api.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    AlunoRepository alunoRepository;

    public OperacaoSqlResponseDTO criarAluno(Aluno aluno) {
        alunoRepository.save(aluno);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.insert("aluno", valoresAluno(aluno)));
    }

    public List <Aluno> listarTodosAlunos() {
        return alunoRepository.findAll();
    }

    public Optional<Aluno> buscarAlunoporid(Long id) {
        return alunoRepository.findById(id);
    }

    public OperacaoSqlResponseDTO deletarAlunoPorId(Long id){
        alunoRepository.deleteById(id);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.delete("aluno", id));
    }

    public OperacaoSqlResponseDTO atualizarAlunoPorId(Long id, Aluno alunoEditado){
        alunoEditado.setId(id);
        alunoRepository.save(alunoEditado);
        return new OperacaoSqlResponseDTO(SqlCrudFormatter.update("aluno", valoresAluno(alunoEditado), id));
    }

    private Map<String, Object> valoresAluno(Aluno aluno) {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("nome_completo", aluno.getNomeCompleto());
        valores.put("email", aluno.getEmail());
        valores.put("cpf", aluno.getCpf());
        return valores;
    }

}
