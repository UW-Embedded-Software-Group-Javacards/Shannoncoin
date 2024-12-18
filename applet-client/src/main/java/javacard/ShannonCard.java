package javacard;

import javacard.framework.*;
import javacard.security.*;

public class ShannonCard extends Applet {
    private static final short dataLength = 32;
    private static final short keyLength = 256;
    private KeyPair keypair;
    private Signature signer;
    // runs once
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new ShannonCard();
    }

    protected ShannonCard() {
        // register the applet instance
        // persistent keypair
        this.keypair = generateKeyPair((short) (this.keyLength * 8));
        this.signer = Signature.getInstance(Signature.ALG_RSA_SHA_256_PKCS1, false);
        this.signer.init(this.keypair.getPrivate(), Signature.MODE_SIGN);
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
        // should be protocol constants
        byte cla = buffer[ISO7816.OFFSET_CLA];
        byte ins = buffer[ISO7816.OFFSET_INS];

        // divert from 2 specified commands to respond public key and sign data
        if (cla == (byte) 0x00) {  // Only handle commands with CLA 0x00
            if (ins == (byte) 0x01) {
                // respond with the public key exponent
                respondPublicModulus(apdu);
            } else if (ins == (byte) 0x02) {
                // respond with public key modulus
                respondPublicModulus(apdu);
            } else if (ins == (byte) 0x03) {
                // sign data with private key
                //byte[] dataToSign = new byte[dataLength];  // Create a byte array for the data
                //System.arraycopy(buffer, 0, dataToSign, 0, 32);
                signData(apdu);
            } else {
                // error for unsupported instruction
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        } else {
            // error for unsupported command type
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
    }

    public void respondPublicExponent(APDU apdu) {
        RSAPublicKey pub = (RSAPublicKey) this.keypair.getPublic();
        byte[] buffer = apdu.getBuffer();
        pub.getExponent(buffer, (short) 0);

        // setup apdu response
        apdu.setOutgoingAndSend((short) 0, this.keyLength);
    }

    public void respondPublicModulus(APDU apdu) {
        RSAPublicKey pub = (RSAPublicKey) this.keypair.getPublic();
        byte[] buffer = apdu.getBuffer();
        pub.getModulus(buffer, (short) 0);

        // setup apdu response
        apdu.setOutgoingAndSend((short) 0, this.keyLength);
    }

    /*
     * Sign data with private key for validation
     */
    public void signData(APDU apdu) {
        // NOTE: data is 32 bytes
        byte[] buffer = apdu.getBuffer();
        short signatureLength = (short) (signer.sign(buffer, (short) 0, this.dataLength, buffer, (short) 0));

        // setup apdu response
        apdu.setOutgoingAndSend((short) 0, signatureLength);
    }

}
