package br.com.bytebank.domain.infra.cliente;

import br.com.bytebank.domain.cliente.Cliente;
import br.com.bytebank.domain.cliente.RepositorioCliente;

import java.util.*;

public class RepositorioClienteEmMemoria implements RepositorioCliente {

    private final Map<UUID, Cliente> banco = new LinkedHashMap<>();

    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório.");
        }
        banco.put(cliente.getId(), cliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID é obrigadtório.");
        }
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(banco.values());
    }
}
