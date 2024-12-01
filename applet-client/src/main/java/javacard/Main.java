package javacard;

import javacard.framework.*;
import javacard.security.*;

public class ShannonCard extends Applet {
    private static final dataLength = 32;
    private static final keyLength = 256;
    private KeyPair keypair;
    private Signature signer;
    private byte[] signatureBuffer;
    // runs once
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new ShannonCard();
    }

    protected ShannonCard() {
        // register the applet instance
        // persistent keypair
        this.keypair = generateKeyPair(this.keyLength * 8);
        this.signer = Signature.getInstance(Signature.ALG_RSA_SHA_256_PKCS1, false);
        this.signer.init(priv, Signature.MODE_SIGN);
        this.signatureBuffer = new byte[this.keyLength]; // public key is 256 bytes
        register();
    }

    public static KeyPair generateKeyPair(short numBits) {
        byte algorithm = KeyPair.ALG_RSA;

        // Create and generate the key pair
        KeyPair keypair = new KeyPair(algorithm, numBits);
        keypair.genKeyPair();

        return keypair;
    }

    /*
     * Handles incoming APDU commands and responds accordingly
     */
    public void process(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte cla = apdu.getCLA();  // class byte to identify type of command
        byte ins = apdu.getINS();  // instruction byte to identify command


        // divert from 2 specified commands to respond public key and sign data
        if (cla == (byte) 0x00) {  // Only handle commands with CLA 0x00
            if (ins == (byte) 0x01) {
                // respond with the public key
                respondPublicKey(pub);
            } else if (ins == (byte) 0x02) {
                // sign data with private key
                //byte[] dataToSign = new byte[dataLength];  // Create a byte array for the data
                //System.arraycopy(buffer, 0, dataToSign, 0, 32);
                signData(buffer);
            } else {
                // error for unsupported instruction
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        } else {
            // error for unsupported command type
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
    }

    public void respondPublicKey() {
        PublicKey pub = this.keypair.getPublic();
        // get the public key as a byte array
        byte[] pubKeyBytes = pub.getEncoded();  
        short pubKeyLength = (short) pubKeyBytes.length; 
        
        // setup apdu response
        APDU.setOutgoing();
        APDU.setOutgoingLength(pubKeyLength);
        APDU.sendBytes((short) 0, pubKeyBytes, (short) 0, pubKeyLength);
    }

    /*
     * Sign data with private key for validation
     */
    public void signData(byte[] data) {
        // NOTE: data is 32 bytes
        PrivateKey priv = this.keypair.getPrivate();
        byte[] signatureBuffer = new byte[256];
        short signatureLength = signer.sign(data, 0, this.dataLength, this.signatureBuffer, 0);

        // setup apdu response
        APDU.setOutgoing();
        APDU.setOutgoingLength(signatureLength); 
        APDU.sendBytes((short) 0, signature, (short) 0, signatureLength);
    }

}
