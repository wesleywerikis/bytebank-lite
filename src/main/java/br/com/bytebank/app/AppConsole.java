package br.com.bytebank.app;

import br.com.bytebank.domain.cliente.Cliente;
import br.com.bytebank.domain.cliente.RepositorioCliente;
import br.com.bytebank.domain.conta.Conta;
import br.com.bytebank.domain.conta.RepositorioConta;
import br.com.bytebank.domain.extrato.Lancamento;
import br.com.bytebank.domain.extrato.RepositorioLancamento;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public final class AppConsole {

    private final Scanner in = new Scanner(System.in);

    private final RepositorioCliente repositorioCliente;
    private final RepositorioConta repositorioConta;
    private final RepositorioLancamento repositorioLancamento;
    private Cliente clienteLogado = null;

    public AppConsole(RepositorioCliente repositorioCliente, RepositorioConta repositorioConta, RepositorioLancamento repositorioLancamento) {
        if (repositorioCliente == null) throw new IllegalArgumentException("repositorioCliente é obrigatório.");
        if (repositorioConta == null) throw new IllegalArgumentException("repositorioConta é obrigatório.");
        if (repositorioLancamento == null) throw new IllegalArgumentException("repositorioLancamento é obrigatório.");

        this.repositorioLancamento = repositorioLancamento;
        this.repositorioCliente = repositorioCliente;
        this.repositorioConta = repositorioConta;
    }

    public void run() {
        while (true) {
            try {
                mostrarMenu();
                int opcao = lerInt("Escolha: ");

                switch (opcao) {
                    case 0 -> {
                        System.out.println("Encerrando. Até!");
                        return;
                    }
                    default -> {
                        if (clienteLogado == null) {
                            executarMenuPublico(opcao);
                        } else {
                            executarMenuCliente(opcao);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void executarMenuPublico(int opcao) {
        switch (opcao) {
            case 1 -> loginCliente();
            case 2 -> criarCliente();
            case 3 -> listarClientes();
            default -> System.out.println("Opção inválida.");
        }
    }

    private void executarMenuCliente(int opcao) {
        switch (opcao) {
            case 1 -> criarContaParaClienteLogado();
            case 2 -> listarMinhasContas();
            case 3 -> depositarEmMinhaConta();
            case 4 -> sacarDeMinhaConta();
            case 5 -> transferirDeMinhaConta();
            case 6 -> extratoDaMinhaConta();
            case 7 -> logout();
            default -> System.out.println("Opção inválida.");
        }
    }

    private void loginCliente() {
        var todos = repositorioCliente.listarTodos();

        if (todos.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado. Crie um cliente primeiro.");
            return;
        }

        System.out.println("Selecione um cliente:");
        for (int i = 0; i < todos.size(); i++) {
            Cliente c = todos.get(i);
            System.out.println("[" + (i + 1) + "]" + c.getNome() + " | ID: " + c.getId());
        }

        int escolha = lerInt("Escolha o número: ");
        if (escolha < 1 || escolha > todos.size()) {
            throw new IllegalArgumentException("Número inválido.");
        }

        clienteLogado = todos.get(escolha - 1);
        System.out.println("Logado como: " + clienteLogado.getNome());
    }

    private void logout() {
        System.out.println("Saíndo do cliente: " + clienteLogado.getNome());
        clienteLogado = null;
    }

    private void criarContaParaClienteLogado() {
        if (clienteLogado == null) throw new IllegalArgumentException("Faça login primeiro.");

        Conta conta = new Conta(clienteLogado);
        repositorioConta.salvar(conta);
        System.out.println("Conta criada: " + conta.getId());
    }

    private void listarMinhasContas() {
        if (clienteLogado == null) throw new IllegalArgumentException("Faça login primeiro.");

        var todas = repositorioConta.listarTodas();
        var minhas = todas.stream()
                .filter(c -> c.getTitular().getId().equals(clienteLogado.getId()))
                .toList();

        if (minhas.isEmpty()) {
            System.out.println("Você ainda não tem contas.");
            return;
        }

        System.out.println("Minhas contas:");
        for (int i = 0; i < minhas.size(); i++) {
            Conta c = minhas.get(i);
            System.out.println("[" + (i + 1) + "]" + c.getId() + " | Saldo: " + c.getSaldo());
        }

    }

    private Conta escolherMinhaContaPorNumero(String titulo) {
        if (clienteLogado == null) throw new IllegalArgumentException("Faça login primeiro.");

        var minhas = repositorioConta.listarTodas().stream()
                .filter(c -> c.getTitular().getId().equals(clienteLogado.getId()))
                .toList();

        if (minhas.isEmpty()) {
            throw new IllegalArgumentException("Você ainda não tem contas.");
        }

        System.out.println(titulo);
        for (int i = 0; i < minhas.size(); i++) {
            Conta c = minhas.get(i);
            System.out.println("[" + (i + 1) + "] Saldo: " + c.getSaldo() + " | ID: " + c.getId());
        }

        int escolha = lerInt("Escolha o número: ");
        if (escolha < 1 || escolha > minhas.size()) {
            throw new IllegalArgumentException("Número inválido.");
        }
        return minhas.get(escolha - 1);
    }

    private void depositarEmMinhaConta() {
        Conta conta = escolherMinhaContaPorNumero("Selecione a conta para DEPÓSITO:");
        BigDecimal valor = lerBigDecimal("Valor do depósito: ");
        conta.depositar(valor);
        repositorioConta.salvar(conta);
        repositorioLancamento.salvar(Lancamento.deposito(conta.getId(), valor, conta.getSaldo()));
        System.out.println("Saldo atual: " + conta.getSaldo());
    }

    private void sacarDeMinhaConta() {
        Conta conta = escolherMinhaContaPorNumero("Selecione a conta para SAQUE:");
        BigDecimal valor = lerBigDecimal("Valor do saque: ");
        conta.sacar(valor);
        repositorioConta.salvar(conta);
        repositorioLancamento.salvar(Lancamento.saque(conta.getId(), valor, conta.getSaldo()));
        System.out.println("Saldo atual: " + conta.getSaldo());
    }

    private void transferirDeMinhaConta() {
        Conta origem = escolherMinhaContaPorNumero("Selecione a conta ORIGEM:");

        UUID destinoId = lerUUID("ID da conta DESTINO (UUID): ");
        Conta destino = repositorioConta.buscarPorId(destinoId)
                .orElseThrow(() -> new IllegalArgumentException("Conta destino não encontrada."));

        BigDecimal valor = lerBigDecimal("Valor da transferência: ");
        origem.transferirPara(destino, valor);

        repositorioConta.salvar(origem);
        repositorioConta.salvar(destino);
        repositorioLancamento.salvar(Lancamento.transferenciaSaida(origem.getId(), destino.getId(), valor, origem.getSaldo()));
        repositorioLancamento.salvar(Lancamento.transferenciaEntrada(destino.getId(), origem.getId(), valor, destino.getSaldo()));

        System.out.println("Transferência OK.");
        System.out.println("Saldo origem: " + origem.getSaldo());
    }

    private void extratoDaMinhaConta() {
        Conta conta = escolherMinhaContaPorNumero("Selecione a conta para EXTRATO:");
        var lista = repositorioLancamento.listarPorConta(conta.getId());

        if (lista.isEmpty()) {
            System.out.println("Nenhuma peração encontrada para esta conta.");
            return;
        }

        System.out.println("=== Extrato ===");
        for (Lancamento l : lista) {
            String contra = (l.getContraParteContaId() == null) ? "" : (" | Contra: " + l.getContraParteContaId());
            System.out.println(l.getInstante() + " | " + l.getTipo()
                    + " | Valor: " + l.getValor()
                    + " | Saldo: " + l.getSaldoApos()
                    + conta);
        }
    }

    private void mostrarMenu() {
        System.out.println("=== ByteBank Lite (Console) ===");

        if (clienteLogado == null) {
            System.out.println("1) Login (selecionar cliente)");
            System.out.println("2) Criar cliente");
            System.out.println("3) Listar clientes");
            System.out.println("0) Sair");
        } else {
            System.out.println("Cliente: " + clienteLogado.getNome() + " (" + clienteLogado.getId() + ")");
            System.out.println("1) Criar conta");
            System.out.println("2) Minhas contas");
            System.out.println("3) Depositar");
            System.out.println("4) Sacar");
            System.out.println("5) Transferir");
            System.out.println("6) Extrato");
            System.out.println("7) Trocar cliente (logout)");
            System.out.println("0) Sair");
        }
    }

    private void criarCliente() {
        String nome = lerString("Nome do cliente: ");
        Cliente c = new Cliente(nome);
        repositorioCliente.salvar(c);
        System.out.println("Cliente criado: " + c.getId());
    }

    private void listarClientes() {
        var todos = repositorioCliente.listarTodos();

        if (todos.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("Clientes:");
        for (Cliente c : todos) {
            System.out.println("- " + c.getId() + " | " + c.getNome());
        }
    }

    private String lerString(String label) {
        System.out.print(label);
        String s = in.nextLine();
        if (s == null || s.isBlank()) throw new IllegalArgumentException("Valor obrigatório.");
        return s.trim();
    }

    private int lerInt(String label) {
        String s = lerString(label);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um número inteiro válido.");
        }
    }

    private UUID lerUUID(String label) {
        String s = lerString(label);
        try {
            return UUID.fromString(s);
        } catch (Exception e) {
            throw new IllegalArgumentException("UUID inválido. Ex: 550e8400-e29b-41d4-a716-446655440000");
        }
    }

    private BigDecimal lerBigDecimal(String label) {
        String s = lerString(label).replace(",", ".");
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido. Ex: 10.50");
        }
    }
}
