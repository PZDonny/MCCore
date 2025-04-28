package net.donnypz.mccore.minigame.arena;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.utils.ui.actionbar.ActionBarUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class ArenaCountdown {

    Arena arena;
    private Component message = MiniMessage.miniMessage().deserialize("<yellow>Connecting players... <white>");

    private boolean isStarted = false;

    ArenaCountdown(Arena arena){
        this.arena = arena;
    }

    public boolean isStarted(){
        return isStarted;
    }

    public void setMessage(String message){
        this.message = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public void setMessage(Component message){
        this.message = message;
    }

    void start(CountdownStyle style, int countdownTime){
        if (countdownTime <= 0 || isStarted){
            return;
        }

        isStarted = true;


        switch(style){
            case BOSSBAR:
                sendBossBarTimed(System.currentTimeMillis()+"_mgCD", message, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS, countdownTime);
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
                            Title title = Title.title(message.append(Component.text(iteration)), Component.empty());
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
                            Title title = Title.title(Component.empty(), message.append(Component.text(iteration)));
                            p.showTitle(title);
                        }
                        iteration--;
                    }
                }.runTaskTimer(Core.getInstance(), 0, 20);
                break;
            case ACTIONBAR:
                ActionBarUtils.sendActionBarTimer(arena.getStartPlayers(), message, Component.empty(), countdownTime);
                break;
        }
    }


    private void attemptStart(){
        if (arena.getArenaType() != Arena.ArenaType.BUKKITMANUAL){
            if (arena.getMainArenaWorld() == null || Bukkit.getWorld(arena.getMainArenaWorld().getName()) == null){
                ArenaManager.deleteArena(arena, ChatColor.RED+"An unexpected error occurred when attempting to start this minigame. Game cancelled!");
                arena = null;
                return;
            }

            Set<Player> startPlayers = arena.getOnlineStartPlayers();

            if (startPlayers.size() < arena.getMinPlayers()){
                ArenaManager.deleteArena(arena, ChatColor.RED+"Insufficient player count. Game cancelled!");
                arena = null;
                return;
            }

            for (Player p : startPlayers){
                p.leaveVehicle();
                p.teleportAsync(Bukkit.getWorld(arena.getMainArenaWorld().getName()).getSpawnLocation());
                if (arena.getArenaType() == Arena.ArenaType.SLIME){
                    Collection<PotionEffect> potions = p.getActivePotionEffects();
                    for (PotionEffect effect : potions){
                        p.removePotionEffect(effect.getType());
                    }
                }
            }
        }
        else{
            if (arena.getManualBukkitWorld() == null){
                ArenaManager.deleteArena(arena, ChatColor.RED+"Manual worlds not loaded. Game cancelled!");
                arena = null;
                return;
            }
        }


        new BukkitRunnable(){
            public void run(){
                arena.setStateToPlaying();
                arena = null;
            }
        }.runTaskLater(Core.getInstance(), 1);
    }

    private BossBar createBossBar(Component message, String barID, BossBar.Color color, BossBar.Overlay overlay){
        return BossBar.bossBar(message, 0, color, overlay);
    }

    private void sendBossBarTimed(String barID, Component message, BossBar.Color color, BossBar.Overlay overlay, long duration){
        BossBar bar = createBossBar(message, barID, color, overlay);
        new BukkitRunnable(){
            double i = 0;
            final double totaltime = duration*10;
            public void run(){
                for (Player p : arena.getOnlineStartPlayers()){
                    bar.addViewer(p);
                }

                if (i == totaltime){
                    //if (!bar.getPlayers().isEmpty()){
                        attemptStart();
                        cancel();

                        List<Player> viewers = new ArrayList<>();
                        bar.viewers().forEach(viewer -> viewers.add((Player) viewer));
                        viewers.forEach(bar::removeViewer);
                    return;
                    //}
                }
                List<Player> viewers = new ArrayList<>();
                bar.viewers().forEach(viewer -> viewers.add((Player) viewer));
                viewers.forEach(v -> {
                    if (!arena.getOnlineStartPlayers().contains(v)){
                        bar.removeViewer(v);
                    }
                });

                float prog = (float) (i/(totaltime));
                bar.progress(prog);
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
