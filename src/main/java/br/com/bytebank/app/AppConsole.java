package br.com.bytebank.app;

import br.com.bytebank.domain.cliente.Cliente;
import br.com.bytebank.domain.cliente.RepositorioCliente;
import br.com.bytebank.domain.conta.Conta;
import br.com.bytebank.domain.conta.RepositorioConta;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public final class AppConsole {

    private final Scanner in = new Scanner(System.in);

    private final RepositorioCliente repositorioCliente;
    private final RepositorioConta repositorioConta;

    public AppConsole(RepositorioCliente repositorioCliente, RepositorioConta repositorioConta) {
        if (repositorioCliente == null) throw new IllegalArgumentException("repositorioCLiente é orbigadtório.");
        if (repositorioConta == null) throw new IllegalArgumentException("repositorioConta é obrigatório.");
        this.repositorioCliente = repositorioCliente;
        this.repositorioConta = repositorioConta;
    }

    public void run() {
        while (true) {
            try {
                mostrarMenu();
                int opcao = lerInt("Escolha: ");

                switch (opcao) {
                    case 1 -> criarCliente();
                    case 2 -> criarConta();
                    case 3 -> listarCliente();
                    case 4 -> listarContas();
                    case 5 -> depositar();
                    case 6 -> sacar();
                    case 7 -> transferir();
                    case 0 -> {
                        System.out.println("Encerrando. Até!");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void mostrarMenu() {
        System.out.println("=== ByteBank Lite (Console) ===");
        System.out.println("1) Criar cliente");
        System.out.println("2) Criar conta");
        System.out.println("3) Listar clientes");
        System.out.println("4) Listar contas");
        System.out.println("5) Depositar");
        System.out.println("6) Sacar");
        System.out.println("7) Transferir");
        System.out.println("0) Sair");
    }

    private void criarCliente() {
        String nome = lerString("Nome do cliente: ");
        Cliente c = new Cliente(nome);
        repositorioCliente.salvar(c);
        System.out.println("Cliente criado: " + c.getId());
    }

    private void criarConta() {
        UUID clienteId = lerUUID("ID do cliente (UUID): ");
        Cliente titular = repositorioCliente.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Conta conta = new Conta(titular);
        repositorioConta.salvar(conta);

        System.out.println("Conta criada: " + conta.getId() + " (Titular: " + titular.getNome() + ")");
    }

    private void listarCliente() {
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

    private void listarContas() {
        var todas = repositorioConta.listarTodas();

        if (todas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
            return;
        }
        System.out.println("Contas:");
        for (Conta c : todas) {
            System.out.println("- " + c.getId()
                    + " | Titular: " + c.getTitular().getNome()
                    + " | Saldo: " + c.getSaldo());
        }
    }

    private void depositar() {
        Conta conta = buscarContaPorId();
        BigDecimal valor = lerBigDecimal("Valor do depósito: ");
        conta.depositar(valor);
        repositorioConta.salvar(conta);
        System.out.println("Depósito realizado. Saldo atual: " + conta.getSaldo());
    }

    private void sacar() {
        Conta conta = buscarContaPorId();
        BigDecimal valor = lerBigDecimal("Valor do saque: ");
        conta.sacar(valor);
        repositorioConta.salvar(conta);
        System.out.println("Saque realizado. Saldo atual: " + conta.getSaldo());
    }

    private void transferir() {
        System.out.println("Conta ORIGEM:");
        Conta origem = buscarContaPorId();

        System.out.println("Conta DESTINO:");
        Conta destino = buscarContaPorId();

        BigDecimal valor = lerBigDecimal("Valor da transferência: ");
        origem.transferirPara(destino, valor);

        repositorioConta.salvar(origem);
        repositorioConta.salvar(destino);

        System.out.println("Transferência OK.");
        System.out.println("Saldo origem: " + origem.getSaldo());
        System.out.println("Saldo destino: " + destino.getSaldo());
    }

    private Conta buscarContaPorId() {
        UUID contaId = lerUUID("ID da conta (UUID): ");
        return repositorioConta.buscarPorId(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

    private String lerString(String label) {
        System.out.println(label);
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