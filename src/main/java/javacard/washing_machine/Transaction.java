package javacard.washing_machine;

public class Transaction {
    private String fromAddress;
    private String toAddress;
    private int amount;

    public Transaction(String fromAddress, String toAddress, int amount) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public String getToAddress() {
        return this.toAddress;
    }

    public int getAmount() {
        return this.amount;
    }

    // used for hashing blocks. for sending over http network, serialize to json instead
    public String toString() {
        return this.fromAddress + " " + this.toAddress + " " + this.amount;
    }
}
