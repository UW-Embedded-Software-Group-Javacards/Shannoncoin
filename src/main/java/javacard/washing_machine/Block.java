package javacard.washing_machine;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class Block {

    private long timestamp; // unix time
    private String data; // for now string, will need to implement transaction class later
    private String prevHash;
    private double dummy; // dummy value can be modified for mining
    private String hash; // byte array casted to string

    // constructors
    // generate timestamp at time of creation
    public Block(String data, String prevHash, double dummy) {
        this.timestamp = Instant.now().getEpochSecond(); // unix time in seconds. only set once, at creation
        this.data = data;
        this.prevHash = prevHash;
        this.dummy = dummy;
        // set hash value upon creation
        this.hash = "";
        this.updateHash();
    }

    public Block(String data) {
        this(data, "", 0);
    }

    public Block() {
        this("", "", 0);
    }

    // some getters and setters

    public String getHash() {
        return this.hash;
    }

    public void setPrevHash(String prev) {
        this.prevHash = prev;
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
            buffer.putDouble(dummy);
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

}
