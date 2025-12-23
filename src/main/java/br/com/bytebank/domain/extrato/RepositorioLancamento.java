package br.com.bytebank.domain.extrato;

import java.util.List;
import java.util.UUID;

public interface RepositorioLancamento {
    void salvar(Lancamento lancamento);

    List<Lancamento> listarPorConta(UUID contaId);
}
