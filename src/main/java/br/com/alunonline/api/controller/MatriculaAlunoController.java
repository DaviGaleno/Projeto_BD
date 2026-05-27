package br.com.alunonline.api.controller;

import br.com.alunonline.api.dtos.AtualizarNotasRequestDTO;
import br.com.alunonline.api.dtos.HistoricoAlunoResponseDTO;
import br.com.alunonline.api.model.MatriculaAluno;
import br.com.alunonline.api.service.MatriculaAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/matriculas")
public class MatriculaAlunoController {

    @Autowired
    MatriculaAlunoService matriculaAlunoService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MatriculaAluno> listarTodasMatriculas() {
        return matriculaAlunoService.listarTodasMatriculas();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<MatriculaAluno> matriculaPorId(@PathVariable Long id) {
        return matriculaAlunoService.matriculaPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarMatricula(@RequestBody MatriculaAluno m) {
        matriculaAlunoService.criarMatricula(m);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarMatriculaPorId(@PathVariable Long id,
                                         @RequestBody MatriculaAluno matriculaEditada) {
        matriculaAlunoService.atualizarMatriculaPorId(id, matriculaEditada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarMatriculaPorId(@PathVariable Long id) {
        matriculaAlunoService.deletarMatriculaPorId(id);
    }

    @PatchMapping("/trancar/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void trancarMatricula(@PathVariable Long id) {
        matriculaAlunoService.trancarMatricula(id);
    }

    @PatchMapping("/atualizar-notas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarNotas(@PathVariable Long id,
                               @RequestBody AtualizarNotasRequestDTO dto) {
        matriculaAlunoService.atualizarNotas(id, dto);
    }

    @GetMapping("/emitir-historico/{alunoId}")
    @ResponseStatus(HttpStatus.OK)
    public HistoricoAlunoResponseDTO emitirHistorico(
            @PathVariable Long alunoId) {
        return matriculaAlunoService.emitirHistorico(alunoId);
    }
}
