package br.com.bytebank.domain.infra.conta;

import br.com.bytebank.domain.conta.Conta;
import br.com.bytebank.domain.conta.RepositorioConta;

import java.util.*;

public class RepositorioContaEmMemoria implements RepositorioConta {

    private final Map<UUID, Conta> banco = new LinkedHashMap<>();

    @Override
    public void salvar(Conta conta) {
        if (conta == null) throw new IllegalArgumentException("Conta é obrigatória");
        banco.put(conta.getId(), conta);
    }

    @Override
    public Optional<Conta> buscarPorId(UUID id) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Conta> listarTodas() {
        return new ArrayList<>(banco.values());
    }
}
