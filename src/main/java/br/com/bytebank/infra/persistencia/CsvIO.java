package br.com.bytebank.infra.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CsvIO {
    private CsvIO() {
    }

    public static List<String[]> read(Path file) {
        try {
            if (!Files.exists(file)) return List.of();

            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<String[]> rows = new ArrayList<>();

            for (String line : lines) {
                if (line == null) continue;
                String trimed = line.trim();
                if (trimed.isEmpty()) continue;
                rows.add(trimed.split(";", -1));
            }
            return rows;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler arquivo: " + file, e);
        }
    }

    public static void write(Path file, List<String[]> rows) {
        try {
            Files.createDirectories((file.getParent()));

            List<String> lines = new ArrayList<>();
            for (String[] row : rows) {
                lines.add(String.join(";", row));
            }

            Files.write(file, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao escrever arquivo: " + file, e);
        }
    }
}
