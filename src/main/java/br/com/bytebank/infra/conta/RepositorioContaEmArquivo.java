package br.com.bytebank.infra.conta;

import br.com.bytebank.domain.cliente.RepositorioCliente;
import br.com.bytebank.domain.conta.Conta;
import br.com.bytebank.domain.conta.RepositorioConta;
import br.com.bytebank.infra.persistencia.CsvIO;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class RepositorioContaEmArquivo implements RepositorioConta {

    private final Path file;
    private final RepositorioCliente repositorioCliente;
    private final Map<UUID, Conta> banco = new LinkedHashMap<>();

    public RepositorioContaEmArquivo(Path file, RepositorioCliente repositorioCliente) {
        this.file = Objects.requireNonNull(file, "file é obrigatório");
        this.repositorioCliente = Objects.requireNonNull(repositorioCliente, "repositorioCliente é obrigatório");
        carregar();
    }

    @Override
    public void salvar(Conta conta) {
        if (conta == null) throw new IllegalArgumentException("conta é obrigatória");
        banco.put(conta.getId(), conta);
        persistir();
    }

    @Override
    public Optional<Conta> buscarPorId(UUID id) {
        if (id == null) throw new IllegalArgumentException("id é obrigatório");
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Conta> listarTodas() {
        return new ArrayList<>(banco.values());
    }

    private void carregar() {
        banco.clear();
        for (String[] row : CsvIO.read(file)) {
            if (row.length < 3) continue;

            UUID idConta = UUID.fromString(row[0]);
            UUID idCliente = UUID.fromString(row[1]);
            BigDecimal saldo = new BigDecimal(row[2]);

            var titular = repositorioCliente.buscarPorId(idCliente)
                    .orElseThrow(() -> new IllegalStateException("Cliente não encontrado para conta: " + idConta));

            Conta conta = Conta.reconstruir(idConta, titular, saldo);
            banco.put(conta.getId(), conta);
        }
    }

    private void persistir() {
        var rows = banco.values().stream()
                .map(c -> new String[]{
                        c.getId().toString(),
                        c.getTitular().getId().toString(),
                        c.getSaldo().toPlainString()
                })
                .collect(Collectors.toList());

        CsvIO.write(file, rows);
    }
}
