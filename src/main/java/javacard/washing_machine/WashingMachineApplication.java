package javacard.washing_machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.ArrayList;

@SpringBootApplication

// washing machine system client
public class WashingMachineApplication {
    public static void main(String[] args) {
        System.out.println("Testing Blockchain validity");
        Blockchain myCoin = new Blockchain("MyCoin");
        myCoin.addBlock(new Block("block a"));
        myCoin.addBlock(new Block("block b"));
        myCoin.addBlock(new Block("block c"));
        System.out.println("Initial chain valid? " + myCoin.isChainValid());
        System.out.println("attempting to tamper with chain:");
        Block target = myCoin.getChain().get(2);
        target.setDummy(1000);
        System.out.println("chain valid before hashing modified block? " + myCoin.isChainValid());
        target.updateHash();
        System.out.println("chain valid evan after hashing modified block? " + myCoin.isChainValid());



        // SpringApplication.run(WashingMachineApplication.class, args);
    }

}
