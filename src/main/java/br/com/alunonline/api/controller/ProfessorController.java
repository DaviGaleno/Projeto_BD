package br.com.alunonline.api.controller;

import br.com.alunonline.api.dtos.OperacaoSqlResponseDTO;
import br.com.alunonline.api.model.Professor;
import br.com.alunonline.api.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    ProfessorService professorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperacaoSqlResponseDTO criarProfessor(@RequestBody Professor professor) {
        return professorService.criarProfessor(professor);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Professor> listarTodosProfessores(){
        return professorService.listarTodosProfessores();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Professor> professorPorId(@PathVariable Long id){
        return professorService.professorPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OperacaoSqlResponseDTO deletarProfessorPorId(@PathVariable Long id){
        return professorService.deletarProfessorPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OperacaoSqlResponseDTO atualizarProfessorPorId(@PathVariable Long id, @RequestBody Professor professorEditado){
        return professorService.atualizarProfessorPorId(id, professorEditado);
    }
}
