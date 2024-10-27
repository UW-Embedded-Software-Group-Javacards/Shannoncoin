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
        myCoin.minePendingTransactions(null);
        myCoin.minePendingTransactions(null);
        myCoin.minePendingTransactions(null);
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

    public static void testMineRewards() {
        Blockchain coin = new Blockchain("ShannonCoin", 16, 100);
        // give shannon some mining rewards
        coin.minePendingTransactions("shannon");
        coin.minePendingTransactions("shannon");
        coin.minePendingTransactions("shannon");
        System.out.println("shannon current balance: " + coin.getBalance("shannon"));
        coin.addTransaction(new Transaction("shannon", "graham", 50));
        coin.minePendingTransactions("bart");
        System.out.println("shannon current balance: " + coin.getBalance("shannon"));
        System.out.println("graham current balance: " + coin.getBalance("graham"));
        System.out.println("bart current balance: " + coin.getBalance("bart"));
        System.out.println("Blockchain valid? " + coin.isChainValid());
    }

    public static void testClient() {
        Client myClient = new Client();
        Blockchain coin = new Blockchain("coin1", 22, 1);
        myClient.cpuMinePendingTransactions(coin, 4, null);
        myClient.cpuMinePendingTransactions(coin, 8, null);
        myClient.cpuMinePendingTransactions(coin, 12, null);
        Blockchain coin2 = new Blockchain("coin2", 24, 1);
        myClient.cpuMinePendingTransactions(coin2, 8, null);
    }

    public static void main(String[] args) {
        testClient();
        // testMine();
        // testMineRewards();
        // SpringApplication.run(WashingMachineApplication.class, args);
    }

}
