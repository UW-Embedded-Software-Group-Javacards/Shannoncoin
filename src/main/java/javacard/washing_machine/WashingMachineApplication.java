package javacard.washing_machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.ArrayList;

@SpringBootApplication

// washing machine system client
public class WashingMachineApplication {

    public static void testChain() {
        System.out.println("Testing Blockchain validity");
        Blockchain myCoin = new Blockchain("MyCoin", 0, 1);
        myCoin.addBlock(new Block());
        myCoin.addBlock(new Block());
        myCoin.addBlock(new Block());
        System.out.println("Initial chain valid? " + myCoin.isChainValid());
        System.out.println("attempting to tamper with chain:");
        Block target = myCoin.getChain().get(2);
        target.setNonce(1000);
        System.out.println("chain valid before hashing modified block? " + myCoin.isChainValid());
        target.updateHash();
        System.out.println("chain valid evan after hashing modified block? " + myCoin.isChainValid());
    }

    public static void testMine() {
        System.out.println("Testing Block mining");
        Block myBlock = new Block();
        System.out.println("Block created");
        myBlock.mine(0, null);
        myBlock.mine(1, null);
        myBlock.mine(5, null);
        myBlock.mine(16, null);
        myBlock.mine(20, null);
        myBlock.mine(21, null);
        myBlock.mine(22, null);
        myBlock.mine(23, null);
        System.out.println("Done mining all blocks!");
    }

    public static void main(String[] args) {
        testMine();
        // SpringApplication.run(WashingMachineApplication.class, args);
    }

}
