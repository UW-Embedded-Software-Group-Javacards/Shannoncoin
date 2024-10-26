package javacard.washing_machine;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class Block {

    private long timestamp; // unix time in seconds
    private String data; // for now string, will need to implement transaction class later
    private String prevHash;
    private long dummy; // dummy value can be modified for mining
    private String hash; // byte array casted to string

    // constructors

    // all params, only use for making blocks that already exist on the chain (deserializing)
    // re-hashes, does NOT generate timestamp
    public Block(long timestamp, String data, String prevHash, long dummy) {
        this.timestamp = timestamp;
        this.data = data;
        this.prevHash = prevHash;
        this.dummy = dummy;
        this.hash = "";
        this.updateHash();
    }

    // generate timestamp at time of creation, set dummy value to 0
    public Block(String data, String prevHash) {
        this.timestamp = Instant.now().getEpochSecond();
        this.data = data;
        this.prevHash = prevHash;
        this.dummy = 0;
        // set hash value upon creation
        this.hash = "";
        this.updateHash();
    }

    // generates timestamp when created
    public Block(String data) {
        this(data, "");
    }

    // generates timestamp when created
    public Block() {
        this("", "");
    }

    // some getters and setters

    public String getHash() {
        return this.hash;
    }

    public String getPrevHash() {
        return this.prevHash;
    }

    public void setPrevHash(String prev) {
        this.prevHash = prev;
    }

    public void setDummy(long val) {
        this.dummy = val;
    }

    // hashes the block using SHA-256
    // returns a string of hex digits (bytes)
    public String calculateHash() {
        try {
            MessageDigest hashDigest = MessageDigest.getInstance("SHA-256");
            ByteBuffer buffer = ByteBuffer.allocate(10240); // 10kb buffer (may need to increase)
            // put in all the info we want to hash in buffer and convert to byte array
            buffer.putLong(timestamp);
            buffer.put(data.getBytes(StandardCharsets.UTF_8));
            buffer.put(prevHash.getBytes(StandardCharsets.UTF_8));
            buffer.putLong(dummy);
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

    // determines if a block and its data is valid
    public boolean isBlockValid() {
        // confirm: this block is what it claims to be
        return this.hash.equals(this.calculateHash());

        // soon: will also check validity of block's transactions
    }

}
