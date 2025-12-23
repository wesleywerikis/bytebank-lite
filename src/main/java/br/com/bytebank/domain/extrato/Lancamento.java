package br.com.bytebank.domain.extrato;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Lancamento {

    private final UUID id;
    private final UUID contaId;
    private final Instant instante;
    private final TipoOperacao tipo;
    private final BigDecimal valor;
    private final BigDecimal saldoApos;
    private final UUID contraParteContaId;
    private final String descricao;

    public Lancamento(UUID id, UUID contaId, Instant instante, TipoOperacao tipo, BigDecimal valor, BigDecimal saldoApos, UUID contraParteContaId, String descricao) {
        this.id = Objects.requireNonNull(id, "id é orbigatório");
        this.contaId = Objects.requireNonNull(contaId, "contaId é obrigatório");
        this.instante = Objects.requireNonNull(instante, "instante é obrigatório");
        this.tipo = Objects.requireNonNull(tipo, "tipo é obirgatório");
        this.valor = money(valor, "valor");
        this.saldoApos = money(saldoApos, "saldoApos");
        if (this.valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor deve ser > 0");
        }
        this.contraParteContaId = contraParteContaId;
        this.descricao = (descricao == null ? "" : descricao.trim());
    }

    public static Lancamento deposito(UUID contaId, BigDecimal valor, BigDecimal saldoApos) {
        return new Lancamento(UUID.randomUUID(), contaId, Instant.now(),
                TipoOperacao.DEPOSITO, valor, saldoApos, null, "Depósito");
    }

    public static Lancamento saque(UUID contaId, BigDecimal valor, BigDecimal saldoApos) {
        return new Lancamento(UUID.randomUUID(), contaId, Instant.now(),
                TipoOperacao.SAQUE, valor, saldoApos, null, "Saque");
    }

    public static Lancamento transferenciaSaida(UUID contaId, UUID destinoContaId, BigDecimal valor, BigDecimal saldoApos) {
        return new Lancamento(UUID.randomUUID(), contaId, Instant.now(),
                TipoOperacao.TRANSFERENCIA_SAIDA, valor, saldoApos, destinoContaId, "Transferência (saída)");
    }

    public static Lancamento transferenciaEntrada(UUID contaId, UUID origemContaId, BigDecimal valor, BigDecimal saldoApos) {
        return new Lancamento(UUID.randomUUID(), contaId, Instant.now(),
                TipoOperacao.TRANSFERENCIA_ENTRADA, valor, saldoApos, origemContaId, "Transferência (entrada)");
    }

    public static Lancamento reconstruir(UUID id,
                                         UUID contaId,
                                         Instant instante,
                                         TipoOperacao tipo,
                                         BigDecimal valor,
                                         BigDecimal saldoApos,
                                         UUID contraParteContaId,
                                         String descricao) {
        return new Lancamento(id, contaId, instante, tipo, valor, saldoApos, contraParteContaId, descricao);
    }


    private static BigDecimal money(BigDecimal v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " é obrigatório");
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getId() {
        return id;
    }

    public UUID getContaId() {
        return contaId;
    }

    public Instant getInstante() {
        return instante;
    }

    public TipoOperacao getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public BigDecimal getSaldoApos() {
        return saldoApos;
    }

    public UUID getContraParteContaId() {
        return contraParteContaId;
    }

    public String getDescricao() {
        return descricao;
    }
}
