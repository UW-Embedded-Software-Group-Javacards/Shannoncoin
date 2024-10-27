package javacard.washing_machine;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// client which uses threading to mine blocks and interact with network
public class Client {

    public void cpuMinePendingTransactions(Blockchain blockchain, int threads, String minerAddress) {
        System.out.println("[MINING WITH " + threads + " CPU THREADS]");
        // object that manages threads
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        // object that we await in main thread. when any thread mines a block, we will
        // countdown the latch and then shutdown all other threads
        CountDownLatch latch = new CountDownLatch(1);
        // thread-safe integer
        AtomicInteger calculations = new AtomicInteger(0);
        AtomicBoolean keepMining = new AtomicBoolean(true);
        // currently are references: need to copy for each thread
        final String prevHash = blockchain.getLatestBlock().getHash();
        final ArrayList<Transaction> pending = new ArrayList<>(blockchain.getPendingTransactions());

        for (int i = 0; i < threads; ++i) {
            final long offset_index = i;
            executor.submit(() -> {
                System.out.println("OPENED MINING THREAD");
                // actual mining task. SHALLOW COPY ARGS, so each block is distinct
                // offset nonce value for each thread (offset by 1 billion)
                Block newBlock = new Block(new ArrayList<Transaction>(pending), prevHash);
                newBlock.setNonce(offset_index * 1000000000);
                newBlock.mine(blockchain.getDifficulty(), minerAddress, keepMining,  calculations);
                // done mining: count down latch which will close all other threads, then add block
                if (keepMining.get()) {
                    latch.countDown();
                    blockchain.addBlockAndClear(newBlock);
                    // set flag
                    System.out.println("SETTING FLAG");
                    keepMining.set(false);
                }

            });
        }

        // back in main thread: await latch close, then immediately shutdown all threads
        // gotta fix this but its chill
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt status
        } finally {
            executor.shutdown();
        }
    }
}
