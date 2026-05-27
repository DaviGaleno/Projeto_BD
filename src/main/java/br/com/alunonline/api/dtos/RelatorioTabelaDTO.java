package br.com.alunonline.api.dtos;

import java.util.List;

public record RelatorioTabelaDTO(String nome, String rotulo, List<RelatorioCampoDTO> campos) {
}
