package MCCore.minigameAPI;

import MCCore.Core;
import MCCore.sockets.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class RamThresholdManager implements Runnable{
    private final int limit;

    private boolean isStopped = false;

    private boolean isRestartingSoon = false;

    int mb = 1024 * 1024;

    public RamThresholdManager(int ramThreshold){
        this.limit = ramThreshold;
        Core.setRamManager(this);
        String message = Core.prefix+ChatColor.YELLOW+"Ram manager running! ("+ramThreshold+")";
        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000*5);
                Runtime instance = Runtime.getRuntime();
                // available memory
                //System.out.println("Total Memory: " + instance.totalMemory() / mb);
                // free memory
                //System.out.println("Free Memory: " + instance.freeMemory() / mb);
                // used memory
                //System.out.println("Used Memory: "
                        //+ (instance.totalMemory() - instance.freeMemory()) / mb);
                // Maximum available memory
                //System.out.println("Max Memory: " + instance.maxMemory() / mb);
                if ((instance.totalMemory() / (double) mb) >= limit && !isRestartingSoon){
                    isRestartingSoon = true;
                    String message = Core.prefix+ ChatColor.RED+"Ram limit reached server will restart after all game arenas finish their matches!";
                    Bukkit.getConsoleSender().sendMessage(message);
                    String[] out = new String[]{Messages.MINIGAMEAPI_BLOCK.getID()};
                    Core.getClient().sendMessage(out);
                    return;
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void stop(){
        this.isStopped = true;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isRestartingSoon() {
        return isRestartingSoon;
    }
}
