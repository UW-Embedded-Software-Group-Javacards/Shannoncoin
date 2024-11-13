package javacard.washing_machine;

import java.lang.reflect.Array;
import java.util.ArrayList;


// defines a blockchain object. this is the object being passed between clients.
// clients will have the ability to verify the legitimacy of a blockchain
public class Blockchain {

    private ArrayList<Block> chain;
    private String name; // currency name
    private int difficulty;
    private ArrayList<Transaction> pendingTransactions; // mempool of pending transactions
    private int miningReward;

    public Blockchain(String name, int difficulty, int miningReward) {
        this.chain = new ArrayList<Block>();
        this.name = name;
        this.difficulty = difficulty;
        this.pendingTransactions = new ArrayList<>();
        this.miningReward = miningReward;
        this.chain.add(this.createGenesisBlock());
    }

    public ArrayList<Block> getChain() {
        return this.chain;
    }

    public ArrayList<Transaction> getPendingTransactions() {
        return this.pendingTransactions;
    }

    public String getName() {
        return this.name;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    // creates a genesis block for a blockchain (starting block)
    private Block createGenesisBlock() {
        return new Block();
    }

    public Block getLatestBlock() {
        return this.chain.get(this.chain.size() - 1);
    }

    // adds a block to chain and clears pending transactions
    public void addBlockAndClear(Block b) {
        if (b.isBlockValid(this.difficulty)) {
            this.chain.add(b);
            this.pendingTransactions.clear();
        }
    }

    // creates a new block using all pending transactions and mines it
    // adds it to the chain, then returns the mined block to be broadcast
    // will soon replace this with CPU and GPU mining (choose number of threads)
    public Block minePendingTransactions(String minerAddress) {
        // new block pointing to current latest
        // shallow copy transactions
        Block newBlock = new Block(new ArrayList<Transaction>(this.pendingTransactions), this.getLatestBlock().getHash());
        newBlock.mine(this.difficulty, minerAddress);
        this.chain.add(newBlock);
        System.out.println("Congratulations " + minerAddress +
                ", you mined a block! (transactions: " + newBlock.getTransactions().size() + ")");
        // clear everything that was mined
        this.pendingTransactions.clear();
        return newBlock;
    }

    // adds a transaction to the mempool
    public void addTransaction(Transaction t) {
        this.pendingTransactions.add(t);
        System.out.println("Added pending transaction:\nSender: " + t.getFromAddress() +
                "\nRecipient: " + t.getToAddress() + "\nAmount: " + t.getAmount());
    }

    // gets the balance of an address by going through the whole chain
    // checks transactions and mining rewards
    public int getBalance(String address) {
        int balance = 0;
        for (int i = this.chain.size() - 1; i > 0; --i) {
            Block b = this.chain.get(i);
            // check mining reward of block
            if (b.getMinerAddress().equals(address)) {
                balance += this.miningReward;
            }
            // check all transactions: subtract from, add to
            for (Transaction t : b.getTransactions()) {
                if (t.getFromAddress().equals(address)) {
                    balance -= t.getAmount();
                }
                if (t.getToAddress().equals(address)) {
                    balance += t.getAmount();
                }

            }
        }


        return balance;
    }

    // checks the validity of the chain by ensuring all blocks are linked correctly
    public boolean isChainValid() {
        // do not check the genesis block (index 0) - it does not point to a previous block
        for (int i = this.chain.size() - 1; i > 0; --i) {
            Block current = this.chain.get(i);
            Block prev = this.chain.get(i - 1);
            // block is null, block is not valid, or block does not point to correct block
            if (current == null) {
                System.out.println("INVALID CHAIN: NULL BLOCK");
                return false;
            } else if (! current.isBlockValid(this.difficulty)) {
                System.out.println("INVALID CHAIN: BLOCK WITH INVALID PROOF OF WORK OR INACCURATE HASH VALUE");
                return false;
            } else if (! current.getPrevHash().equals(prev.getHash())) {
                System.out.println("INVALID CHAIN: BLOCK WITH INVALID PREVIOUS POINTER");
                return false;
            }
        }
        return true;
    }

}

