package br.com.alunonline.api.service;

import java.util.Map;

final class SqlCrudFormatter {

    private SqlCrudFormatter() {
    }

    static String insert(String tabela, Map<String, Object> valores) {
        return "INSERT INTO " + tabela + " (" + String.join(", ", valores.keySet()) + ")\n"
                + "VALUES (" + valores.values().stream()
                .map(SqlCrudFormatter::formatarValor)
                .reduce((atual, proximo) -> atual + ", " + proximo)
                .orElse("") + ");";
    }

    static String update(String tabela, Map<String, Object> valores, Long id) {
        return "UPDATE " + tabela + "\n"
                + "SET " + valores.entrySet().stream()
                .map(entrada -> entrada.getKey() + " = " + formatarValor(entrada.getValue()))
                .reduce((atual, proximo) -> atual + ",\n    " + proximo)
                .orElse("") + "\n"
                + "WHERE id = " + id + ";";
    }

    static String delete(String tabela, Long id) {
        return "DELETE FROM " + tabela + "\n"
                + "WHERE id = " + id + ";";
    }

    private static String formatarValor(Object valor) {
        if (valor == null) {
            return "NULL";
        }
        if (valor instanceof Number || valor instanceof Boolean) {
            return valor.toString();
        }
        if (valor instanceof Enum<?> enumValor) {
            return "'" + enumValor.name() + "'";
        }
        return "'" + valor.toString().replace("'", "''") + "'";
    }
}
