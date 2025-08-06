package net.donnypz.mccore.utils.ui;

import net.donnypz.mccore.events.AbilityReturnedEvent;
import net.donnypz.mccore.events.TimedBossBarEndEvent;
import net.donnypz.mccore.utils.ability.AbilityHandler;
import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class BossBarUtils {

    private BossBarUtils(){}

//BossBars
    private static KeyedBossBar createBossBar(Player p, String message, String barID, BarColor color, BarStyle style){
        KeyedBossBar bar = Bukkit.createBossBar(new NamespacedKey(CoreAPI.getPlugin(), barID), message, color, style);
        if (p != null){
            bar.addPlayer(p);
        }
        bar.setVisible(true);
        return bar;
    }

    public static void removeFromBar(Player p, String barID){
        KeyedBossBar bar = Bukkit.getBossBar(new NamespacedKey(CoreAPI.getPlugin(), barID));
        if (bar == null){
            return;
        }
        bar.removePlayer(p);
    }

    public static void removeAllBossBar(String barID){
        KeyedBossBar bar = Bukkit.getBossBar(new NamespacedKey(CoreAPI.getPlugin(), barID));
        if (bar == null){
            return;
        }
        bar.removeAll();
    }

    public static KeyedBossBar getBossBar(String barID){
        return Bukkit.getBossBar(new NamespacedKey(CoreAPI.getPlugin(), barID));
    }


    public static KeyedBossBar sendHealthBossBar(Player p, String barID, String message, BarColor color, BarStyle style, LivingEntity trackedEntity){
        KeyedBossBar bar = createBossBar(p, message, barID, color, style);
        trackHealth(bar, trackedEntity);
        return bar;
    }

    public static KeyedBossBar sendHealthBossBar(Collection<Player> players, String barID, String message, BarColor color, BarStyle style, LivingEntity trackedEntity){
        KeyedBossBar bar = createBossBar(null, message, barID, color, style);
        for (Player p : players){
            if (p.isOnline()){
                bar.addPlayer(p);
            }
        }
        trackHealth(bar, trackedEntity);
        return bar;
    }

    private static void trackHealth(KeyedBossBar bar, LivingEntity livingEntity){
        new BukkitRunnable(){
            public void run(){
                if (bar.getPlayers().isEmpty() || !bar.isVisible() || livingEntity.isDead() || !livingEntity.isTicking()){
                    removeBossBar(bar);
                    cancel();
                    return;
                }

                for (Player p : bar.getPlayers()){
                    if (!p.isOnline()){
                        bar.removePlayer(p);
                    }
                }

                double progress = livingEntity.getHealth()/(livingEntity.getAttribute(CoreAPI.getVersionHandler().getMaxHealthAttribute()).getValue());
                bar.setProgress(progress);
            }
        }.runTaskTimer(CoreAPI.getPlugin(), 1, 3);
    }

    //Solid BossBars
    //Single Player
    public static KeyedBossBar sendBossBar(Player p, String barID, String message, BarColor color, BarStyle style){
        return createBossBar(p, message, barID, color, style);
    }

    //List of Players
    public static KeyedBossBar sendBossBar(Collection<Player> players, String barID, String message, BarColor color, BarStyle style){
        KeyedBossBar bar = createBossBar(null, message, barID, color, style);
        for (Player p : players){
            if (p.isOnline()){
                bar.addPlayer(p);
            }
        }
        return bar;
    }



    //Timed BossBars
    public static BossBar sendBossBarTimed(Player p, String message, BarColor color, BarStyle style, long timeInSeconds){
        BossBar bar = Bukkit.createBossBar(message, color, style);
        new BukkitRunnable(){
            double i = timeInSeconds*10;
            final double totaltime = i;
            public void run(){
                if (i == 0 && !bar.getPlayers().isEmpty()){
                    cancel();
                    removeBossBar(bar);
                    return;
                }
                else if (!p.isOnline()){
                    removeBossBar(bar);
                    cancel();
                    return;
                }
                else if (bar.getPlayers().isEmpty() || !bar.isVisible()){
                    cancel();
                    return;
                }
                double progress = i/(totaltime);
                bar.setProgress(progress);
                i--;
            }
        }.runTaskTimer(CoreAPI.getPlugin(), 1, 2);
        return bar;
    }


    //Timed BossBars
    public static KeyedBossBar sendBossBarTimed(Player p, String barID, String message, BarColor color, BarStyle style, long timeInSeconds){
        KeyedBossBar bar = createBossBar(p, message, barID, color, style);
        new BukkitRunnable(){
            double i = timeInSeconds*10;
            final double totalTime = i;
            public void run(){
                if (i == 0 && !bar.getPlayers().isEmpty()){
                    cancel();
                    new TimedBossBarEndEvent(bar, bar.getPlayers()).callEvent();

                    //Ability Cooldown
                    AbilityHandler.CooldownType type = null;
                    if (barID.contains(AbilityHandler.CooldownType.PRIMARY.getName())){
                        type = AbilityHandler.CooldownType.PRIMARY;
                    }
                    else if (barID.contains(AbilityHandler.CooldownType.SECONDARY.getName())) {
                        type = AbilityHandler.CooldownType.SECONDARY;
                    }
                    if (type != null){
                        new AbilityReturnedEvent(bar, barID, p, type).callEvent();
                    }

                    removeBossBar(bar);
                    return;
                }
                else if (!p.isOnline()){
                    removeBossBar(bar);
                    cancel();
                    return;
                }
                else if (bar.getPlayers().isEmpty() || !bar.isVisible()){
                    removeBossBar(bar);
                    cancel();
                    return;
                }
                double progress = i/(totalTime);
                bar.setProgress(progress);
                i--;
            }
        }.runTaskTimer(CoreAPI.getPlugin(), 1, 2);
        return bar;
    }

    public static KeyedBossBar sendBossBarTimed(Collection<Player> players, String barID, String message, BarColor color, BarStyle style, long timeInSeconds){
        KeyedBossBar bar = createBossBar(null, message, barID, color, style);
        for (Player p : players){
            if (p.isOnline()){
                bar.addPlayer(p);
            }
        }
        new BukkitRunnable(){
            double i = timeInSeconds*10;
            final double totaltime = i;
            public void run(){
                if (i == 0 && !bar.getPlayers().isEmpty()){
                    cancel();
                    new TimedBossBarEndEvent(bar, bar.getPlayers()).callEvent();
                    removeBossBar(bar);
                    return;
                }
                if (bar.getPlayers().isEmpty() || !bar.isVisible()){
                    removeBossBar(bar);
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
        }.runTaskTimer(CoreAPI.getPlugin(), 1, 2);
        return bar;
    }

    public static void removeBossBar(BossBar bar){
        bar.removeAll();
        if (bar instanceof KeyedBossBar kBar){
            Bukkit.removeBossBar(kBar.getKey());
        }
    }

    public static void removeBossBar(String barID){
        NamespacedKey key = new NamespacedKey(CoreAPI.getPlugin(), barID);
        KeyedBossBar bar = Bukkit.getBossBar(key);
        if (bar != null){
            bar.removeAll();
        }
        Bukkit.removeBossBar(key);
    }

    private static void removePlayersFromBar(BossBar bar){
        bar.removeAll();
    }

    public static void removeAllBossBars(){
        HashSet<String> allBarIDs = new HashSet<>();
        for (Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext();) {
            KeyedBossBar bar = it.next();
            NamespacedKey key = bar.getKey();
            if (key.getNamespace().equalsIgnoreCase(CoreAPI.getPlugin().getName())){
                allBarIDs.add(key.value());
            }
        }

        for (String s : allBarIDs){
            NamespacedKey key = new NamespacedKey(CoreAPI.getPlugin(), s);
            Bukkit.removeBossBar(key);
        }
    }
}
