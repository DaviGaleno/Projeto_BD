package br.com.alunonline.api.service;

import br.com.alunonline.api.dtos.RelatorioCampoDTO;
import br.com.alunonline.api.dtos.RelatorioRequestDTO;
import br.com.alunonline.api.dtos.RelatorioResponseDTO;
import br.com.alunonline.api.dtos.RelatorioTabelaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

@Service
public class RelatorioService {

    private static final int LIMITE_RESULTADOS = 500;

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, TabelaRelatorio> tabelas;

    public RelatorioService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.tabelas = criarTabelasPermitidas();
    }

    public List<RelatorioTabelaDTO> listarMetadados() {
        return tabelas.values().stream()
                .map(tabela -> new RelatorioTabelaDTO(
                        tabela.nome,
                        tabela.rotulo,
                        tabela.campos.values().stream()
                                .map(campo -> new RelatorioCampoDTO(campo.nome, campo.rotulo))
                                .toList()))
                .toList();
    }

    public RelatorioResponseDTO gerarRelatorio(RelatorioRequestDTO request) {
        TabelaRelatorio tabela = buscarTabela(request.tabela());
        List<CampoRelatorio> camposSelecionados = buscarCampos(tabela, request.campos());

        String select = montarSelect(camposSelecionados);
        StringBuilder sql = new StringBuilder()
                .append("SELECT\n    ")
                .append(select)
                .append("\nFROM ")
                .append(tabela.from);

        List<Object> parametros = new ArrayList<>();
        String termo = request.termo();
        if (termo != null && !termo.isBlank()) {
            sql.append("\nWHERE\n    ");
            List<String> filtros = tabela.campos.values().stream()
                    .filter(CampoRelatorio::pesquisavel)
                    .map(campo -> "LOWER(CAST(" + campo.expressaoSql + " AS TEXT)) LIKE ?")
                    .toList();
            sql.append(String.join("\n    OR ", filtros));
            String termoLike = "%" + termo.toLowerCase(Locale.ROOT).trim() + "%";
            for (int i = 0; i < filtros.size(); i++) {
                parametros.add(termoLike);
            }
        }

        sql.append("\nORDER BY 1\nLIMIT ").append(LIMITE_RESULTADOS);

        String consultaSql = sql.toString();
        List<Map<String, Object>> linhas = jdbcTemplate.queryForList(consultaSql, parametros.toArray());
        List<String> cabecalhos = camposSelecionados.stream()
                .map(CampoRelatorio::rotulo)
                .toList();

        return new RelatorioResponseDTO(formatarSqlParaExibicao(consultaSql, parametros), cabecalhos, linhas);
    }

    public byte[] gerarCsv(RelatorioRequestDTO request) {
        RelatorioResponseDTO relatorio = gerarRelatorio(request);
        StringBuilder csv = new StringBuilder();
        csv.append(String.join(";", relatorio.cabecalhos())).append("\n");

        for (Map<String, Object> linha : relatorio.linhas()) {
            List<String> valores = linha.values().stream()
                    .map(this::formatarValorCsv)
                    .toList();
            csv.append(String.join(";", valores)).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private TabelaRelatorio buscarTabela(String nomeTabela) {
        TabelaRelatorio tabela = tabelas.get(nomeTabela);
        if (tabela == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tabela invalida para relatorio");
        }
        return tabela;
    }

    private List<CampoRelatorio> buscarCampos(TabelaRelatorio tabela, List<String> nomesCampos) {
        if (nomesCampos == null || nomesCampos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione pelo menos um campo");
        }

        List<CampoRelatorio> campos = new ArrayList<>();
        for (String nomeCampo : nomesCampos) {
            CampoRelatorio campo = tabela.campos.get(nomeCampo);
            if (campo == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo invalido para relatorio");
            }
            campos.add(campo);
        }
        return campos;
    }

    private String montarSelect(List<CampoRelatorio> camposSelecionados) {
        return camposSelecionados.stream()
                .map(campo -> campo.expressaoSql + " AS " + campo.alias)
                .reduce((atual, proximo) -> atual + ",\n    " + proximo)
                .orElseThrow();
    }

    private String formatarSqlParaExibicao(String sql, List<Object> parametros) {
        String sqlFormatado = sql;
        for (Object parametro : parametros) {
            sqlFormatado = sqlFormatado.replaceFirst("\\?", Matcher.quoteReplacement(formatarParametroSql(parametro)));
        }
        return sqlFormatado + ";";
    }

    private String formatarParametroSql(Object parametro) {
        if (parametro == null) {
            return "NULL";
        }
        if (parametro instanceof Number || parametro instanceof Boolean) {
            return parametro.toString();
        }
        return "'" + parametro.toString().replace("'", "''") + "'";
    }

    private String formatarValorCsv(Object valor) {
        if (valor == null) {
            return "";
        }

        String texto = valor.toString().replace("\"", "\"\"");
        if (texto.contains(";") || texto.contains("\n") || texto.contains("\"")) {
            return "\"" + texto + "\"";
        }
        return texto;
    }

    private Map<String, TabelaRelatorio> criarTabelasPermitidas() {
        Map<String, TabelaRelatorio> mapa = new LinkedHashMap<>();

        mapa.put("alunos", new TabelaRelatorio(
                "alunos",
                "Alunos",
                "aluno a",
                List.of(
                        campo("id", "a.id", "id", "ID", true),
                        campo("nomeCompleto", "a.nome_completo", "nome_completo", "Nome completo", true),
                        campo("email", "a.email", "email", "E-mail", true),
                        campo("cpf", "a.cpf", "cpf", "CPF", true)
                )));

        mapa.put("professores", new TabelaRelatorio(
                "professores",
                "Professores",
                "professor p",
                List.of(
                        campo("id", "p.id", "id", "ID", true),
                        campo("nomeCompleto", "p.nome_completo", "nome_completo", "Nome completo", true),
                        campo("email", "p.email", "email", "E-mail", true),
                        campo("cpf", "p.cpf", "cpf", "CPF", true)
                )));

        mapa.put("disciplinas", new TabelaRelatorio(
                "disciplinas",
                "Disciplinas",
                "disciplina d LEFT JOIN professor p ON p.id = d.professor_id",
                List.of(
                        campo("id", "d.id", "id", "ID", true),
                        campo("nome", "d.nome", "nome", "Disciplina", true),
                        campo("cargaHoraria", "d.carga_horaria", "carga_horaria", "Carga horaria", true),
                        campo("professor", "p.nome_completo", "professor", "Professor", true)
                )));

        mapa.put("matriculas", new TabelaRelatorio(
                "matriculas",
                "Matriculas",
                "matricula_aluno m " +
                        "LEFT JOIN aluno a ON a.id = m.aluno_id " +
                        "LEFT JOIN disciplina d ON d.id = m.disciplina_id " +
                        "LEFT JOIN professor p ON p.id = d.professor_id",
                List.of(
                        campo("id", "m.id", "id", "ID", true),
                        campo("aluno", "a.nome_completo", "aluno", "Aluno", true),
                        campo("disciplina", "d.nome", "disciplina", "Disciplina", true),
                        campo("professor", "p.nome_completo", "professor", "Professor", true),
                        campo("nota1", "m.nota1", "nota1", "Nota 1", true),
                        campo("nota2", "m.nota2", "nota2", "Nota 2", true),
                        campo("media", "(m.nota1 + m.nota2) / 2", "media", "Media", true),
                        campo("status", "m.status", "status", "Status", true)
                )));

        mapa.put("historico_aluno", new TabelaRelatorio(
                "historico_aluno",
                "Histórico do Aluno",
                "vw_historico_aluno_completo",
                List.of(
                        campo("aluno_id", "aluno_id", "aluno_id", "ID Aluno", true),
                        campo("nome_completo", "nome_completo", "nome_completo", "Nome do Aluno", true),
                        campo("disciplina", "disciplina", "disciplina", "Disciplina", true),
                        campo("professor", "professor", "professor", "Professor", true),
                        campo("nota1", "nota1", "nota1", "Nota 1", true),
                        campo("nota2", "nota2", "nota2", "Nota 2", true),
                        campo("media", "media", "media", "Média", true),
                        campo("status", "status", "status", "Status", true)
                )));

        mapa.put("matriculas_aprovadas", new TabelaRelatorio(
                "matriculas_aprovadas",
                "Matrículas Aprovadas",
                "vw_matriculas_aprovadas",
                List.of(
                        campo("aluno_nome", "aluno_nome", "aluno_nome", "Aluno", true),
                        campo("disciplina_nome", "disciplina_nome", "disciplina_nome", "Disciplina", true),
                        campo("professor_nome", "professor_nome", "professor_nome", "Professor", true),
                        campo("media", "(nota1 + nota2) / 2", "media", "Média", true),
                        campo("status", "status", "status", "Status", true)
                )));

        mapa.put("desempenho_disciplinas", new TabelaRelatorio(
                "desempenho_disciplinas",
                "Desempenho por Disciplina",
                "vw_relatorio_desempenho_disciplinas",
                List.of(
                        campo("disciplina", "disciplina", "disciplina", "Disciplina", true),
                        campo("professor", "professor", "professor", "Professor", true),
                        campo("total_matriculas", "total_matriculas", "total_matriculas", "Total de Alunos", true),
                        campo("aprovados", "aprovados", "aprovados", "Aprovados", true),
                        campo("reprovados", "reprovados", "reprovados", "Reprovados", true),
                        campo("media_disciplina", "media_disciplina", "media_disciplina", "Média da Disciplina", true)
                )));

        return mapa;
    }

    private CampoRelatorio campo(String nome, String expressaoSql, String alias, String rotulo, boolean pesquisavel) {
        return new CampoRelatorio(nome, expressaoSql, alias, rotulo, pesquisavel);
    }

    private record TabelaRelatorio(
            String nome,
            String rotulo,
            String from,
            Map<String, CampoRelatorio> campos) {

        TabelaRelatorio(String nome, String rotulo, String from, List<CampoRelatorio> campos) {
            this(nome, rotulo, from, new LinkedHashMap<>());
            campos.forEach(campo -> this.campos.put(campo.nome, campo));
        }
    }

    private record CampoRelatorio(
            String nome,
            String expressaoSql,
            String alias,
            String rotulo,
            boolean pesquisavel) {
    }
}
