package br.com.bytebank.infra.extrato;

import br.com.bytebank.domain.extrato.Lancamento;
import br.com.bytebank.domain.extrato.RepositorioLancamento;
import br.com.bytebank.domain.extrato.TipoOperacao;
import br.com.bytebank.infra.persistencia.CsvIO;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public final class RepositorioLancamentoEmArquivo implements RepositorioLancamento {

    private final Path file;
    private final List<Lancamento> banco = new ArrayList<>();

    public RepositorioLancamentoEmArquivo(Path file) {
        this.file = Objects.requireNonNull(file, "file é obrigatório");
        carregar();
    }

    @Override
    public void salvar(Lancamento lancamento) {
        if (lancamento == null) throw new IllegalArgumentException("lancamento é obrigatório");
        banco.add(lancamento);
        persistir();
    }

    @Override
    public List<Lancamento> listarPorConta(UUID contaId) {
        if (contaId == null) throw new IllegalArgumentException("contaId é obrigatório");
        return banco.stream()
                .filter(l -> l.getContaId().equals(contaId))
                .sorted(Comparator.comparing(Lancamento::getInstante))
                .collect(Collectors.toList());
    }

    private void carregar() {
        banco.clear();
        for (String[] row : CsvIO.read(file)) {
            if (row.length < 8) continue;

            UUID id = UUID.fromString(row[0]);
            UUID contaId = UUID.fromString(row[1]);
            Instant instante = Instant.parse(row[2]);
            TipoOperacao tipo = TipoOperacao.valueOf(row[3]);
            BigDecimal valor = new BigDecimal(row[4]);
            BigDecimal saldoApos = new BigDecimal(row[5]);

            UUID contra = row[6].isBlank() ? null : UUID.fromString(row[6]);
            String desc = row[7];

            banco.add(Lancamento.reconstruir(
                    id, contaId, instante, tipo, valor, saldoApos, contra, desc
            ));
        }
    }

    private void persistir() {
        var rows = banco.stream()
                .map(l -> new String[]{
                        l.getId().toString(),
                        l.getContaId().toString(),
                        l.getInstante().toString(),
                        l.getTipo().name(),
                        l.getValor().toPlainString(),
                        l.getSaldoApos().toPlainString(),
                        l.getContraParteContaId() == null ? "" : l.getContraParteContaId().toString(),
                        l.getDescricao()
                })
                .collect(Collectors.toList());
        CsvIO.write(file, rows);
    }
}
