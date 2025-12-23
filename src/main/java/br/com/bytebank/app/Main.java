package br.com.bytebank.app;

import br.com.bytebank.infra.cliente.RepositorioClienteEmArquivo;
import br.com.bytebank.infra.conta.RepositorioContaEmArquivo;
import br.com.bytebank.infra.extrato.RepositorioLancamentoEmArquivo;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        var repoCliente = new RepositorioClienteEmArquivo(Path.of("data/clientes.csv"));
        var repoConta = new RepositorioContaEmArquivo(Path.of("data/contas.csv"), repoCliente);
        var repoLanc = new RepositorioLancamentoEmArquivo(Path.of("data/lancamentos.csv"));

        new AppConsole(repoCliente, repoConta, repoLanc).run();
    }
}
