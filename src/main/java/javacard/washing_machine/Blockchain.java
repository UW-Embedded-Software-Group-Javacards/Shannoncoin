package javacard.washing_machine;

import java.util.ArrayList;


// defines a blockchain object. this is the object being passed between clients.
// clients will have the ability to verify the legitimacy of a blockchain
public class Blockchain {
    private ArrayList<Block> chain;
    private String name; // currency name

    public Blockchain(String name) {
        this.chain = new ArrayList<Block>();
        this.name = name;
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
        if (newBlock == null) {
            throw new RuntimeException("Error adding block to chain: given null");
        } else if (!newBlock.isBlockValid()) {
            throw new RuntimeException("Error adding block to chain: invalid block");
        }

        // to add new block to chain: set the prev hash
        newBlock.setPrevHash(getLatestBlock().getHash());
        // re-hash since we changed value
        newBlock.updateHash();
        this.chain.add(newBlock);
    }

    // checks the validity of the chain by ensuring all blocks are linked correctly
    public boolean isChainValid() {
        // do not check the genesis block (index 0) - it does not point to a previous block
        for (int i = this.chain.size() - 1; i > 0; --i) {
            Block current = this.chain.get(i);
            Block prev = this.chain.get(i - 1);
            // block is null, block is not valid, or block does not point to correct block
            if (current == null || ! current.isBlockValid() || ! current.getPrevHash().equals(prev.getHash())) {
                return false;
            }
        }
        return true;
    }



}

