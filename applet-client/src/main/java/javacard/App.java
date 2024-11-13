package javacard;

import java.security.*;

public class App {
    // atm, setting up functions in this test class so that we can test out all functionality
    // eventually can just be pasted into the actual applet file

    // try out different key pair generation algorithms (provide params to select) - taken care of by generator
    // this Key class in the javacard framework is the main superclass: https://docs.oracle.com/javacard/3.0.5/api/javacard/security/Key.html
    // TODO: add EC support
    public static KeyPair generateKeyPair(String algorithm, int numBits) throws InvalidAlgorithmParameterException {
       KeyPair keypair = null;
       try {
         // generator supports DH, RSA, DSA, but NOT EC
         // docs: https://docs.oracle.com/javase/8/docs/api///?java/security/KeyPairGenerator.html
         KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
         // in our case, numBits can probably be hardcoded - no need for a parameter for this
         generator.initialize(numBits);
         keypair = generator.generateKeyPair();
       } catch (NoSuchAlgorithmException e) {
         throw new InvalidAlgorithmParameterException("Algorithm provided not supported");
       }
      
       return keypair;
    }

    // TODO: write a function to save keypair to memory
    public static void saveKeyPair(KeyPair keypair) {
       // do something eventually
    }

    public static void main(String[] args) {
       // testing out the key pair generation
       try {
         KeyPair test = generateKeyPair("RSA", 2048);
         System.out.println(test);
       } catch(InvalidAlgorithmParameterException e) {
         System.out.println(e);
       }
    }
}
