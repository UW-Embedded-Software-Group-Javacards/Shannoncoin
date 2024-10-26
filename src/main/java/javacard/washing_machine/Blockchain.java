package javacard.washing_machine;

import java.util.ArrayList;


// defines a blockchain object. this is the object being passed between clients.
// clients will have the ability to verify the legitimacy of a blockchain
public class Blockchain {
    private ArrayList<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<Block>();
        this.chain.add(this.createGenesisBlock());
    }

    // creates a genesis block for a blockchain (starting block)
    private Block createGenesisBlock() {
        return new Block("Genesis Block");
    }

    public Block getLatestBlock() {
        return this.chain.get(this.chain.size() - 1);
    }

    public void addBlock(Block newBlock) {
        // to add new block to chain: set the prev hash
        newBlock.setPrevHash(getLatestBlock().getHash());
        // re-hash since we changed value
        newBlock.updateHash();
        this.chain.add(newBlock);
    }

}

