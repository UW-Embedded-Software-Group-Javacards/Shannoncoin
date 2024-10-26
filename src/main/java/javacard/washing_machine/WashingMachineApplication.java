package javacard.washing_machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.Instant;

@SpringBootApplication

// washing machine system client
public class WashingMachineApplication {
    public static void main(String[] args) {
        System.out.println("Testing Block Hashing");

        long unixTime = Instant.now().getEpochSecond();
        Block myBlock = new Block(unixTime, "sample", "sample", 0);
        // print twice to make sure it's consistent
        System.out.println(myBlock.calculateHash());
        System.out.println(myBlock.calculateHash());

        SpringApplication.run(WashingMachineApplication.class, args);
    }

}
