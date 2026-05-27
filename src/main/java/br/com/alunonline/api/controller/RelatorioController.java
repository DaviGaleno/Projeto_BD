package br.com.alunonline.api.controller;

import br.com.alunonline.api.dtos.RelatorioRequestDTO;
import br.com.alunonline.api.dtos.RelatorioResponseDTO;
import br.com.alunonline.api.dtos.RelatorioTabelaDTO;
import br.com.alunonline.api.service.RelatorioService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/metadados")
    public List<RelatorioTabelaDTO> listarMetadados() {
        return relatorioService.listarMetadados();
    }

    @PostMapping("/gerar")
    public RelatorioResponseDTO gerarRelatorio(@RequestBody RelatorioRequestDTO request) {
        return relatorioService.gerarRelatorio(request);
    }

    @PostMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv(@RequestBody RelatorioRequestDTO request) {
        byte[] arquivo = relatorioService.gerarCsv(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("relatorio-personalizado.csv")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(arquivo);
    }
}
