package javacard.washing_machine;

import java.lang.reflect.Array;
import java.util.ArrayList;


// defines a blockchain object. this is the object being passed between clients.
// clients will have the ability to verify the legitimacy of a blockchain
public class Blockchain {

    private ArrayList<Block> chain;
    private String name; // currency name
    private int difficulty;
    private ArrayList<Transaction> pendingTransactions;
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

    public String getName() {
        return this.name;
    }

    // creates a genesis block for a blockchain (starting block)
    private Block createGenesisBlock() {
        return new Block();
    }

    public Block getLatestBlock() {
        return this.chain.get(this.chain.size() - 1);
    }

    // adds a mined block to the blockchain
    // caller is responsible for ensuring that it's valid, or it will be ignored
    public void addBlock(Block newBlock) {
        if (newBlock == null) {
            throw new RuntimeException("Error adding block to chain: given null");
        } else if (!newBlock.isBlockValid(this.difficulty)) {
            throw new RuntimeException("Error adding block to chain: invalid block");
        }

        this.chain.add(newBlock);
    }

    // checks the validity of the chain by ensuring all blocks are linked correctly
    public boolean isChainValid() {
        // do not check the genesis block (index 0) - it does not point to a previous block
        for (int i = this.chain.size() - 1; i > 0; --i) {
            Block current = this.chain.get(i);
            Block prev = this.chain.get(i - 1);
            // block is null, block is not valid, or block does not point to correct block
            if (current == null || ! current.isBlockValid(this.difficulty) ||
                    ! current.getPrevHash().equals(prev.getHash())) {
                return false;
            }
        }
        return true;
    }

    // creates a new block using all pending transactions and mines it
    // adds it to the chain, then returns the mined block to be broadcast
    public Block minePendingTransactions(String minerAddress) {
        Block newBlock = new Block(this.pendingTransactions, this.getLatestBlock().getHash());
        newBlock.mine(this.difficulty, minerAddress);
        this.addBlock(newBlock);
        return newBlock;
    }
}

