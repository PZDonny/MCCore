package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;


public class ArenaCountdown {

    Arena arena;

    ArenaCountdown(Arena arena){
        this.arena = arena;
    }

    void start(CountdownStyle style, int countdownTime){
        if (countdownTime <= 0) return;
        String defaultMSG = ChatColor.YELLOW+"Connecting players... "+ChatColor.WHITE;


        switch(style){
            case BOSSBAR:
                sendBossBarTimed(System.currentTimeMillis()+"_mgCD", defaultMSG, BarColor.WHITE, BarStyle.SOLID, countdownTime);
                break;
            case TITLE:
                new BukkitRunnable(){
                    int iteration = countdownTime;
                    public void run(){
                        if (iteration == 0){
                            for (Player p : arena.getOnlineStartPlayers()){
                                p.clearTitle();
                            }
                            attemptStart();
                            cancel();
                            return;
                        }
                        for (Player p : arena.getOnlineStartPlayers()){
                            Title title = Title.title(Component.text(defaultMSG + iteration), Component.empty());
                            p.showTitle(title);
                        }
                        iteration--;
                    }
                }.runTaskTimer(Core.getInstance(), 0, 20);
                break;
            case SUBTITLE:
                new BukkitRunnable(){
                    int iteration = countdownTime;
                    public void run(){
                        if (iteration == 0){
                            for (Player p : arena.getOnlineStartPlayers()){
                                p.clearTitle();
                            }
                            attemptStart();
                            cancel();
                            return;
                        }
                        for (Player p : arena.getOnlineStartPlayers()){
                            Title title = Title.title( Component.empty(), Component.text(defaultMSG + iteration));
                            p.showTitle(title);
                        }
                        iteration--;
                    }
                }.runTaskTimer(Core.getInstance(), 0, 20);
                break;
            case ACTIONBAR:
                sendActionBarTimed(defaultMSG, countdownTime);
                break;
        }
    }


    private void attemptStart(){
        if (!arena.isManualWorld()){
            if (arena.getArenaWorld() == null || Bukkit.getWorld(arena.getArenaWorld().getName()) == null){
                ArenaManager.deleteArena(arena, ChatColor.RED+"An unexpected error occurred when attempting to start minigame!");
                return;
            }

            if (arena.getOnlineStartPlayers().size() < arena.getMinPlayers()){
                ArenaManager.deleteArena(arena, ChatColor.RED+"Insufficient player count. Game cancelled!");
                return;
            }

            for (Player p : arena.getOnlineStartPlayers()){
                p.teleportAsync(Bukkit.getWorld(arena.getArenaWorld().getName()).getSpawnLocation());
                Collection<PotionEffect> potions = p.getActivePotionEffects();
                for (PotionEffect effect : potions){
                    p.removePotionEffect(effect.getType());
                }
            }
        }


        new BukkitRunnable(){
            public void run(){
                arena.setStateToPlaying();
            }
        }.runTaskLater(Core.getInstance(), 1);


        /*new BukkitRunnable(){
            public void run(){
                arena.setGameState(GameState.ENDING);
            }
        }.runTaskLater(Core.getInstance(), 20*10);*/

    }

    private KeyedBossBar createBossBar(String message, String barID, BarColor color, BarStyle style){
        KeyedBossBar bar = Bukkit.createBossBar(new NamespacedKey(Core.getInstance(), barID), message, color, style);
        bar.setVisible(true);
        return bar;
    }

    public void sendBossBarTimed(String barID, String message, BarColor color, BarStyle style, long duration){
        KeyedBossBar bar = createBossBar(message, barID, color, style);
        new BukkitRunnable(){
            double i = 0;
            final double totaltime = duration*10;
            public void run(){
                for (Player p : arena.getOnlineStartPlayers()){
                    if (p.isOnline() && !bar.getPlayers().contains(p)){
                        bar.addPlayer(p);
                    }
                }

                if (i == totaltime){
                    //if (!bar.getPlayers().isEmpty()){
                        attemptStart();
                        cancel();
                        bar.removeAll();
                    return;
                    //}
                }
                for (Player p : bar.getPlayers()){
                    if (!arena.getOnlineStartPlayers().contains(p)){
                        bar.removePlayer(p);
                    }
                }
                double prog = i/(totaltime);
                bar.setProgress(prog);
                i++;
            }
        }.runTaskTimer(Core.getInstance(), 1, 2);
    }

    private void sendActionBarTimed(String message, int duration){
        new BukkitRunnable(){
            int iteration = duration;
            public void run(){
                if (iteration == 0){
                    for (Player p : arena.getOnlineStartPlayers()){
                        p.sendActionBar(Component.empty());
                    }
                    attemptStart();
                    cancel();
                    return;
                }
                for (Player p : arena.getOnlineStartPlayers()){
                    p.sendActionBar(Component.text(message+iteration));
                }
                iteration--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20);
    }
}
