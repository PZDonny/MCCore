package MCCore.utils;

import MCCore.Core;
import MCCore.events.AbilityReturnedEvent;
import MCCore.events.TimedBossBarEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Iterator;

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
    public static void sendBossBar(Collection<Player> players, String barID, String message, BarColor color, BarStyle style){
        KeyedBossBar bar = createBossBar(null, message, barID, color, style);
        for (Player p : players){
            if (p.isOnline()){
                bar.addPlayer(p);
            }
        }
    }




    //Timed BossBars
    public static KeyedBossBar sendBossBarTimed(Player p, String barID, String message, BarColor color, BarStyle style, long timeInSeconds){
        KeyedBossBar bar = createBossBar(p, message, barID, color, style);
        new BukkitRunnable(){
            double i = timeInSeconds*10;
            final double totaltime = i;
            public void run(){
                if (i == 0 && !bar.getPlayers().isEmpty()){
                    cancel();
                    Bukkit.getServer().getPluginManager().callEvent(new TimedBossBarEndEvent(bar, bar.getPlayers()));
                    if (bar.getKey().getKey().contains(AbilityHandler.CooldownType.PRIMARY.getName())
                            || bar.getKey().getKey().contains(AbilityHandler.CooldownType.SECONDARY.getName())) {
                        AbilityHandler.CooldownType type;
                        if (bar.getKey().getKey().contains(AbilityHandler.CooldownType.PRIMARY.getName())) type = AbilityHandler.CooldownType.PRIMARY;
                        else type = AbilityHandler.CooldownType.SECONDARY;
                        Bukkit.getServer().getPluginManager().callEvent(new AbilityReturnedEvent(bar, p, type));
                    }
                    removePlayersFromBar(bar);
                    return;
                }
                else if (!p.isOnline()){
                    bar.removePlayer(p);
                    Bukkit.removeBossBar(bar.getKey());
                    cancel();
                    return;
                }
                else if (bar.getPlayers().isEmpty() || !bar.isVisible()){
                    Bukkit.removeBossBar(bar.getKey());
                    cancel();
                    return;
                }
                double progress = i/(totaltime);
                bar.setProgress(progress);
                i--;
            }
        }.runTaskTimer(Core.getInstance(), 1, 2);
        return bar;
    }

    public static KeyedBossBar sendBossBarTimed(Collection<Player> players, String barID, String message, BarColor color, BarStyle style, long timeInSeconds){
        KeyedBossBar bar = createBossBar(null, message, barID, color, style);
        for (Player p : players){
            if (p.isOnline()) bar.addPlayer(p);
        }
        new BukkitRunnable(){
            double i = timeInSeconds*10;
            final double totaltime = i;
            public void run(){
                if (i == 0 && !bar.getPlayers().isEmpty()){
                    cancel();
                    Bukkit.getServer().getPluginManager().callEvent(new TimedBossBarEndEvent(bar, bar.getPlayers()));
                    removePlayersFromBar(bar);
                    Bukkit.removeBossBar(bar.getKey());
                    return;
                }
                if (bar.getPlayers().isEmpty() || !bar.isVisible()){
                    Bukkit.removeBossBar(bar.getKey());
                    cancel();
                    return;
                }
                for (Player p : bar.getPlayers()){
                    if (!p.isOnline()){
                        bar.removePlayer(p);
                    }
                }
                double progress= i/(totaltime);
                bar.setProgress(progress);
                i--;
            }
        }.runTaskTimer(Core.getInstance(), 1, 2);
        return bar;
    }

    private static void removePlayersFromBar(BossBar bar){
        new BukkitRunnable(){
            public void run(){
                bar.removeAll();
            }
        }.runTaskLater(Core.getInstance(), 10);
    }
}
