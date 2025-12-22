package br.com.bytebank.domain.cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioCliente {

    void salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    List<Cliente> listarTodos();
}
