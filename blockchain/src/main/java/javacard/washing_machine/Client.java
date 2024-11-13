package javacard.washing_machine;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// client which uses threading to mine blocks and interact with network
public class Client {
    static AtomicBoolean keepMining = new AtomicBoolean(false);

    public static void stopMining() {
        keepMining.set(false);
    }

    // mines blocks on cpu threads. does NOT pause main thread, call stopMining()
    public static void cpuMinePendingTransactions(Blockchain blockchain, int threads, String minerAddress, boolean await) {
        System.out.println("[MINING WITH " + threads + " CPU THREADS]");
        // object that manages threads
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        // object that we await in main thread. when any thread mines a block, we will
        // countdown the latch which will pause the main thread
        CountDownLatch latch = new CountDownLatch(1);
        // thread-safe integer
        AtomicInteger calculations = new AtomicInteger(0);
        keepMining.set(true);
        // currently are references: need to copy for each thread
        final String prevHash = blockchain.getLatestBlock().getHash();
        final ArrayList<Transaction> pending = new ArrayList<>(blockchain.getPendingTransactions());

        for (int i = 0; i < threads; ++i) {
            final long offset_index = i;
            final long offset = (long) 1e12;
            executor.submit(() -> {
                // actual mining task. SHALLOW COPY ARGS, so each block is distinct
                // offset nonce value for each thread (offset by 1 trillion)
                Block newBlock = new Block(new ArrayList<Transaction>(pending), prevHash);
                newBlock.setNonce(offset_index * offset);
                newBlock.mine(blockchain.getDifficulty(), minerAddress, keepMining,  calculations);
                // done mining: count down latch, then add block
                if (keepMining.getAndSet(false)) {
                    blockchain.addBlockAndClear(newBlock);
                    System.out.println("[TERMINATING MINE]");
                    latch.countDown();
                }
            });
        }
        if (await) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
