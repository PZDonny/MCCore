package MCCore.utils;

import MCCore.Core;
import MCCore.events.TimedBarEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BossBarTools {
//BossBars
    private static KeyedBossBar createBossBar(Player p, String message, String barID, BarColor color, BarStyle style){
        KeyedBossBar bar = Bukkit.createBossBar(new NamespacedKey(Core.getInstance(), barID), message, color, style);
        if (p != null) bar.addPlayer(p);
        bar.setVisible(true);
        return bar;
    }

    public static void removeFromBar(Player p, String barID){
        Iterator<KeyedBossBar> bossBars = Bukkit.getBossBars();
        while (bossBars.hasNext()){
            KeyedBossBar bar = bossBars.next();
            if (bar.getKey().getKey().equals(barID)){
                bar.removePlayer(p);
                return;
            }
        }
    }

    public static void removeAllBossBar(String barID){
        Iterator<KeyedBossBar> bossBars = Bukkit.getBossBars();
        while (bossBars.hasNext()){
            KeyedBossBar bar = bossBars.next();
            if (bar.getKey().getKey().equals(barID)){
                bar.getPlayers().clear();
                return;
            }
        }
    }


    //Solid BossBars
    //Single Player
    public static void sendBossBar( Player p, String barID, String message, BarColor color, BarStyle style){
        KeyedBossBar bar = createBossBar(p, message, barID, color, style);
    }
    //List of Players
    public static void sendBossBar(List<Player> players, String barID, String message, BarColor color, BarStyle style){
        KeyedBossBar bar = createBossBar(null, message, barID, color, style);
        for (Player p : players){
            if (p.isOnline()){
                bar.addPlayer(p);
            }
        }
    }




    //Timed BossBars
    //Single Player
    public static void sendBossBarTimed(Player p, String barID, String message, BarColor color, BarStyle style, long duration){
        KeyedBossBar bar = createBossBar(p, message, barID, color, style);
        new BukkitRunnable(){
            double i = duration*10;
            final double totaltime = i;
            public void run(){
                if (i == 0 && !bar.getPlayers().isEmpty()){
                    cancel();
                    Bukkit.getServer().getPluginManager().callEvent(new TimedBarEndEvent(bar, bar.getPlayers()));
                    bar.removePlayer(p);
                    return;
                }
                else if (!p.isOnline()){
                    bar.removePlayer(p);
                    cancel();
                    return;
                }
                else if (bar.getPlayers().isEmpty()){
                    cancel();
                    return;
                }
                double prog = i/(totaltime);
                bar.setProgress(prog);
                i--;

            }
        }.runTaskTimer(Core.getInstance(), 1, 2);
    }

    //Multiple Players
    public static void sendBossBarTimed(Set<Player> players, String barID, String message, BarColor color, BarStyle style, long duration){
        KeyedBossBar bar = createBossBar(null, message, barID, color, style);
        for (Player p : players){
            if (p.isOnline()) bar.addPlayer(p);
        }
        new BukkitRunnable(){
            double i = duration*10;
            final double totaltime = i;
            public void run(){
                if (i == 0 && !bar.getPlayers().isEmpty()){
                    cancel();
                    Bukkit.getServer().getPluginManager().callEvent(new TimedBarEndEvent(bar, bar.getPlayers()));
                    bar.removeAll();
                    return;
                }
                if (bar.getPlayers().isEmpty()){
                    cancel();
                    return;
                }
                for (Player p : bar.getPlayers()){
                    if (!p.isOnline()){
                        bar.removePlayer(p);
                    }
                }
                double prog = i/(totaltime);
                bar.setProgress(prog);
                i--;

            }
        }.runTaskTimer(Core.getInstance(), 1, 2);
    }
}
