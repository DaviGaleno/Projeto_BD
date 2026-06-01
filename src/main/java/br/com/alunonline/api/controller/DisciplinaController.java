package br.com.alunonline.api.controller;

import br.com.alunonline.api.dtos.OperacaoSqlResponseDTO;
import br.com.alunonline.api.model.Disciplina;
import br.com.alunonline.api.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    DisciplinaService disciplinaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperacaoSqlResponseDTO criarDisciplina(@RequestBody Disciplina disciplina){
        return disciplinaService.criarDisciplina(disciplina);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Disciplina> listarTodasDisciplinas(){
        return disciplinaService.listarTodasDisciplinas();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Disciplina> disciplinaPorId(@PathVariable Long id){
        return disciplinaService.disciplinaPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OperacaoSqlResponseDTO deletarDisciplinaPorId(@PathVariable Long id){
        return disciplinaService.deletarDisciplinaPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OperacaoSqlResponseDTO atualizarDisciplinaPorId(@PathVariable Long id, @RequestBody Disciplina disciplinaEditada){
        return disciplinaService.atualizarDisciplinaPorId(id, disciplinaEditada);
    }
}
