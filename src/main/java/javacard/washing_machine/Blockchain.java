package javacard.washing_machine;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// defines a blockchain object. this is the object being passed between clients.
// clients will have the ability to verify the legitimacy of a blockchain
public class Blockchain {
    private class Block {


        private long timestamp;
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
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                ByteBuffer buffer = ByteBuffer.allocate(64); // 64 byte buffer
                // put in all the info we want to hash
                buffer.putLong(timestamp);
                buffer.put(data.getBytes(StandardCharsets.UTF_8));
                buffer.put(prevHash.getBytes(StandardCharsets.UTF_8));
                buffer.putDouble(dummy);
                byte[] raw_data = buffer.array();
                // hash it all
                byte[] raw_hash = digest.digest(raw_data);
                // cast hash to string (readable)
                return new String(raw_hash, StandardCharsets.UTF_8);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

    }



}

