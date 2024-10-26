package javacard.washing_machine;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.ArrayList;

public class Block {
    private static final int BUFFER_SIZE = 10240; // number of bytes to buffer when hashing. here: 10kb

    private long timestamp; // unix time in seconds
    private ArrayList<Transaction> transactions; // block data represented as list of transactions
    private String prevHash;
    private long nonce; // nonce value can be modified for mining
    private String hash; // byte array casted to string
    private static HashMap<Character, String> hexMap = new HashMap<>(); // for efficiently checking pow
    private String minerAddress;
    // constructors

    // workhorse constructor: only explicitly use for blocks that already exist on the chain (deserializing)
    // re-hashes, does NOT generate timestamp on its own
    public Block(long timestamp, ArrayList<Transaction> transactions, String prevHash, long nonce,
                 String minerAddress) {
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.prevHash = prevHash;
        this.nonce = nonce;
        this.minerAddress = minerAddress;
        this.hash = "";
        this.updateHash();
        this.fillHexMap();
    }

    // generate timestamp at time of creation, set nonce value to 0, miner address to null
    public Block(ArrayList<Transaction> transactions, String prevHash) {
        this(Instant.now().getEpochSecond(), transactions, prevHash, 0, null);
    }

    // generates timestamp when created
    public Block(ArrayList<Transaction> transactions) {
        this(transactions, "");
    }

    // generates timestamp when created
    public Block() {
        this(new ArrayList<>(), "");
    }

    // some getters and setters

    public String getMinerAddress() {
        return this.minerAddress;
    }

    public ArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public String getHash() {
        return this.hash;
    }

    public String getPrevHash() {
        return this.prevHash;
    }

    public void setPrevHash(String prev) {
        this.prevHash = prev;
    }

    public void setNonce(long val) {
        this.nonce = val;
    }

    // hashes the block using SHA-256
    // returns a string of hex digits (bytes)
    public String calculateHash() {
        try {
            MessageDigest hashDigest = MessageDigest.getInstance("SHA-256");
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            // put in all the info we want to hash in buffer and convert to byte array
            buffer.putLong(timestamp);
            // put all transactions
            for (Transaction t : this.transactions) {
                buffer.put(t.toString().getBytes(StandardCharsets.UTF_8));
            }
            buffer.put(prevHash.getBytes(StandardCharsets.UTF_8));
            buffer.putLong(nonce);
            if (minerAddress != null) {
                buffer.put(minerAddress.getBytes(StandardCharsets.UTF_8));
            }
            byte[] raw_data = buffer.array();
            // hash it all
            byte[] raw_hash = hashDigest.digest(raw_data);
            // cast hash to string of hex chars (readable)
            StringBuilder output = new StringBuilder();
            for (byte b : raw_hash) {
                output.append(String.format("%02X", b));
            }
            return output.toString(); // return as string
        } catch (NoSuchAlgorithmException e) { // java makes you catch
            throw new RuntimeException(e);
        }
    }

    // update the hash (ex. after changing data)
    public void updateHash() {
        this.hash = this.calculateHash();
    }

    private void fillHexMap() {
        hexMap.put('0', "0000");
        hexMap.put('1', "0001");
        hexMap.put('2', "0010");
        hexMap.put('3', "0011");
        hexMap.put('4', "0100");
        hexMap.put('5', "0101");
        hexMap.put('6', "0110");
        hexMap.put('7', "0111");
        hexMap.put('8', "1000");
        hexMap.put('9', "1001");
        hexMap.put('A', "1010");
        hexMap.put('B', "1011");
        hexMap.put('C', "1100");
        hexMap.put('D', "1101");
        hexMap.put('E', "1110");
        hexMap.put('F', "1111");
    }

    // determines if a (mined) block and its data is valid
    public boolean isBlockValid(int difficulty) {
        // confirm: this block is what it claims to be and has proof of work
        return this.hash.equals(this.calculateHash()) && this.hashHexIsZeroes(difficulty);

        // soon: will also check validity of block's transactions
    }

    // overload: checks validity while ignoring proof of work
    public boolean isBlockValid() {
        return isBlockValid(0);
    }

    // helper function: takes hex string and converts to binary,
    // returns true if the first (difficulty) binary digits are 0s
    private boolean hashHexIsZeroes(int difficulty) {
        char digit;
        int i = 0;
        while (difficulty > 0) {
            digit = this.hash.charAt(i);
            if (difficulty >= 4) {
                // current hex digit MUST be a 0
                if (digit == '0') {
                    difficulty -= 4;
                } else {
                    return false;
                }
            } else {
                // between 1 and 3 chars needed: convert last char to binary using map then count
                String binary = hexMap.get(digit);
                int j = 0;
                while (difficulty > 0) {
                    if (binary.charAt(j) == '0') {
                        --difficulty;
                    } else {
                        return false;
                    }
                    ++j;
                }
            }
            ++i;
        }

        return true;
    }

    // rudimentary proof of work system:
    // takes an unmined block and mines it by adjusting the nonce value
    // until the first (difficulty) number of bits in the hash are 0s.
    // difficulty MUST be [0, 256] (hash output is 256 bits).
    // each difficulty increment doubles the amount of average computation required
    // miner address required to payout mining reward
    public void mine(int difficulty, String minerAddress) {
        assert 0 <= difficulty && difficulty <= 256;
        this.minerAddress = minerAddress;
        long start = Instant.now().getEpochSecond();
        long calculations = 0;
        while (! hashHexIsZeroes(difficulty)) { // won't mine already mined block
            ++this.nonce;
            this.updateHash();
            ++calculations;
        }
        long end = Instant.now().getEpochSecond();
        System.out.println("Mined Block at Difficulty " + difficulty + ": " + this.hash);
        System.out.println("Time taken: " + (end - start) + " seconds");
        System.out.println("Calculations made: " + calculations);
        System.out.println("Calculations per second: " + (calculations / (Math.max(end - start, 1))));
    }

}
