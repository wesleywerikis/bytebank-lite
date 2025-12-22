package br.com.bytebank.app;

import br.com.bytebank.domain.infra.cliente.RepositorioClienteEmMemoria;
import br.com.bytebank.domain.infra.conta.RepositorioContaEmMemoria;

public class Main {
    public static void main(String[] args) {

        var repoCliente = new RepositorioClienteEmMemoria();
        var repoConta = new RepositorioContaEmMemoria();

        new AppConsole(repoCliente, repoConta).run();
    }
}
