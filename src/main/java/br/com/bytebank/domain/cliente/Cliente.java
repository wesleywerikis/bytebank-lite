package br.com.bytebank.domain.cliente;

import java.util.Objects;
import java.util.UUID;

public final class Cliente {

    private final UUID id;
    private final String nome;

    public Cliente(String nome) {
        this(UUID.randomUUID(), nome);
    }

    public Cliente(UUID id, String nome) {
        if (id == null) throw new IllegalArgumentException("ID do Cliente é Obrigadotótio.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do Cliente é Obrigadtório.");
        this.id = id;
        this.nome = nome;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
