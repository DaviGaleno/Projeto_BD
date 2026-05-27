package br.com.alunonline.api.dtos;

import java.util.List;
import java.util.Map;

public record RelatorioResponseDTO(List<String> cabecalhos, List<Map<String, Object>> linhas) {
}
