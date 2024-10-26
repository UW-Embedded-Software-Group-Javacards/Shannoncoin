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

    // used for hashing blocks. for sending over http network, serialize to json instead
    public String toString() {
        return this.fromAddress + " " + this.toAddress + " " + this.amount;
    }
}
