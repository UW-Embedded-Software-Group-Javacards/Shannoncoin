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

    // process method - runs when receives apdu
    public void process(APDU apdu) {
        // divert from 2 specified commands to respond public key and sign data
    }

    public void respondPublicKey() {
        PublicKey pub = this.keypair.getPublic();
    }

    // data is 32 bytes
    public void signData(byte[] data) {
        PrivateKey priv = this.keypair.getPrivate();
        short signatureLength = signer.sign(data, 0, this.dataLength, this.signatureBuffer, 0);

    }

}
