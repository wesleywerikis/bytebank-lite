package br.com.bytebank.domain.conta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioConta {

    void salvar(Conta conta);

    Optional<Conta> buscarPorId(UUID id);

    List<Conta> listarTodas();

}
