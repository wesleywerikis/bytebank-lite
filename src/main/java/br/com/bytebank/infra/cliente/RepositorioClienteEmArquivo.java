package br.com.bytebank.infra.cliente;

import br.com.bytebank.domain.cliente.Cliente;
import br.com.bytebank.domain.cliente.RepositorioCliente;
import br.com.bytebank.infra.persistencia.CsvIO;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RepositorioClienteEmArquivo implements RepositorioCliente {

    private final Path file;
    private final Map<UUID, Cliente> banco = new LinkedHashMap<>();

    public RepositorioClienteEmArquivo(Path file) {
        this.file = Objects.requireNonNull(file, "File é obrigadtório.");
        carregar();
    }

    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("Cliente é obrigatório");
        banco.put(cliente.getId(), cliente);
        persistir();
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(banco.values());
    }

    private void carregar() {
        banco.clear();
        for (String[] row : CsvIO.read(file)) {
            if (row.length < 2) continue;
            UUID id = UUID.fromString(row[0]);
            String nome = row[1];

            Cliente c = new Cliente(id, nome);
            banco.put(c.getId(), c);
        }
    }

    private void persistir() {
        var rows = banco.values().stream()
                .map(c -> new String[]{
                        c.getId().toString(), c.getNome()})
                .collect(Collectors.toList());

        CsvIO.write(file, rows);
    }
}
