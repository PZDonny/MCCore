package net.donnypz.mccore.database;


import org.bukkit.Bukkit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class DBExecutor {

    static final ExecutorService executor = Executors.newCachedThreadPool();
    private static boolean isShuttingDown = false;

    static void run(Runnable action){
        executor.submit(action);
        if (!isShuttingDown){
            isShuttingDown = true;

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Bukkit.getLogger().warning("Shutting down executor");
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        Bukkit.getLogger().severe("Executor failed to terminate in under 10 seconds");
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    Bukkit.getLogger().severe("Executor shutdown interrupted");
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }));
        }
    }
}
