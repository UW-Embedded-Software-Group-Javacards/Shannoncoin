package javacard.washing_machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

// washing machine system client
public class WashingMachineApplication {
    public static void main(String[] args) {
        System.out.println("Test");
        SpringApplication.run(WashingMachineApplication.class, args);
    }

}
