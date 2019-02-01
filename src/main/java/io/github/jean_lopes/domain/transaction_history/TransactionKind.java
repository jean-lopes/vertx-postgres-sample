package io.github.jean_lopes.domain.transaction_history;

public enum TransactionKind {
    CASH("CA"),
    DEBT("DE"),
    CREDIT("CR");
    
    private final String abbreviation;
    
    private TransactionKind(String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    public String getAbbreviation() {
        return abbreviation;
    }
    
    public static TransactionKind from(String abbreviation) {
        switch (abbreviation) {
            case "CA": return CASH;
            case "CR" : return CREDIT;
            case "DE" : return DEBT;
            default: return null;
        }
    }
}
