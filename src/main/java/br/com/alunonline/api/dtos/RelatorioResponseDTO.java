package br.com.alunonline.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record RelatorioResponseDTO(
        String sql,
        List<String> cabecalhos,
        List<Map<String, Object>> dados) {

    public RelatorioResponseDTO(List<String> cabecalhos, List<Map<String, Object>> linhas) {
        this("", cabecalhos, linhas);
    }

    @JsonProperty("linhas")
    public List<Map<String, Object>> linhas() {
        return dados;
    }
}
