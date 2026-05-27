package br.com.alunonline.api.dtos;

import java.util.List;

public record RelatorioRequestDTO(String tabela, List<String> campos, String termo) {
}
