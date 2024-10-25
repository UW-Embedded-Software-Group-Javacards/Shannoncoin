package javacard.washing_machine;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {

    private long timestamp; // unix time
    private String data; // for now string, will need to implement transaction class later
    private String hash; // byte array casted to string
    private String prevHash;
    private double dummy; // dummy value can be modified for mining

    public Block(long timestamp, String data, String prevHash, double dummy) {
        this.timestamp = timestamp;
        this.data = data;
        this.prevHash = prevHash;
        this.dummy = dummy;
    }

    public String CalculateHash() {
        try {
            MessageDigest hashDigest = MessageDigest.getInstance("SHA-256");
            ByteBuffer buffer = ByteBuffer.allocate(1024); // 1kb buffer (may need to increase)
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
