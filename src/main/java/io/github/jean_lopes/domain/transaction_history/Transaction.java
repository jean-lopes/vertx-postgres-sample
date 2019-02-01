package io.github.jean_lopes.domain.transaction_history;

import java.time.Instant;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private String cpf;
    private TransactionKind kind;
    private Instant processedOn;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public TransactionKind getKind() {
        return kind;
    }

    public void setKind(TransactionKind kind) {
        this.kind = kind;
    }
    
    @Override
    public String toString() {
        return "Transaction [id=" + id + ", cpf=" + cpf + ", kind=" + kind + ", processedOn=" + processedOn + "]";
    }
}
