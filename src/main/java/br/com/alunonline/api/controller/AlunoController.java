package br.com.alunonline.api.controller;

import br.com.alunonline.api.dtos.OperacaoSqlResponseDTO;
import br.com.alunonline.api.model.Aluno;
import br.com.alunonline.api.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    AlunoService alunoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperacaoSqlResponseDTO criarAluno(@RequestBody Aluno aluno) {
        return alunoService.criarAluno(aluno);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Aluno> listarTodosAlunos(){
        return alunoService.listarTodosAlunos();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Aluno> buscarAlunoporid(@PathVariable Long id){
        return alunoService.buscarAlunoporid(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OperacaoSqlResponseDTO deletarAlunoPorId(@PathVariable Long id){
        return alunoService.deletarAlunoPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OperacaoSqlResponseDTO atualizarAlunoPorId(@PathVariable Long id, @RequestBody Aluno alunoEditado){
        return alunoService.atualizarAlunoPorId(id, alunoEditado);
    }
}
