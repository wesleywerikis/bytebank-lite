package br.com.bytebank.domain.conta;

import br.com.bytebank.domain.cliente.Cliente;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public final class Conta {

    private final UUID id;
    private final Cliente titular;

    private BigDecimal saldo;

    public Conta(Cliente titular) {
        this(UUID.randomUUID(), titular, BigDecimal.ZERO);
    }

    public Conta(UUID id, Cliente titular, BigDecimal saldoInicial) {
        if (id == null) throw new IllegalArgumentException("ID da conta é obrigatório.");
        if (titular == null) throw new IllegalArgumentException("Titular é obrigatório.");
        if (saldoInicial == null) throw new IllegalArgumentException("Saldo incial é obrigatório.");
        if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }

        this.id = id;
        this.titular = titular;
        this.saldo = dinheiro(saldoInicial);
    }

    public UUID getId() {
        return id;
    }

    public Cliente getTitular() {
        return titular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void depositar(BigDecimal valor) {
        validarValorPositivo(valor);
        saldo = saldo.add(dinheiro(valor));
    }

    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor);
        BigDecimal v = dinheiro(valor);

        if (saldo.compareTo(v) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
        saldo = saldo.subtract(v);
    }

    public void transferirPara(Conta destino, BigDecimal valor) {
        if (destino == null) throw new IllegalArgumentException("Conta destino é obrigadtória.");
        if (this.id.equals(destino.id))
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta.");

        this.sacar(valor);
        destino.depositar(valor);
    }

    private void validarValorPositivo(BigDecimal valor) {
        if (valor == null) throw new IllegalArgumentException("Valor é obrigadtório.");
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero.");
        }
    }

    private BigDecimal dinheiro(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conta conta)) return false;
        return id.equals(conta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", titular=" + titular +
                ", saldo=" + saldo +
                '}';
    }
}