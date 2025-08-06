package net.donnypz.mccore.minigame.arena;

import net.donnypz.mccore.utils.ui.actionbar.ActionBarUtils;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;


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
                            attemptPlayState();
                            cancel();
                            return;
                        }
                        for (Player p : arena.getOnlineStartPlayers()){
                            Title title = Title.title(message.append(Component.text(iteration)), Component.empty());
                            p.showTitle(title);
                        }
                        iteration--;
                    }
                }.runTaskTimer(CoreAPI.getPlugin(), 0, 20);
                break;
            case SUBTITLE:
                new BukkitRunnable(){
                    int iteration = countdownTime;
                    public void run(){
                        if (iteration == 0){
                            for (Player p : arena.getOnlineStartPlayers()){
                                p.clearTitle();
                            }
                            attemptPlayState();
                            cancel();
                            return;
                        }
                        for (Player p : arena.getOnlineStartPlayers()){
                            Title title = Title.title(Component.empty(), message.append(Component.text(iteration)));
                            p.showTitle(title);
                        }
                        iteration--;
                    }
                }.runTaskTimer(CoreAPI.getPlugin(), 0, 20);
                break;
            case ACTIONBAR:
                ActionBarUtils.sendActionBarTimer(arena.getStartPlayers(), message, Component.empty(), countdownTime);
                break;
        }
    }


    private void attemptPlayState(){
        World w = arena.getBukkitWorld();
        if (w == null){
            ArenaManager.deleteArena(arena, Component.text("Game arena world not loaded. Game cancelled!", NamedTextColor.RED));
            return;
        }


        if (arena.getOnlineStartPlayers().size() < arena.getMinPlayers()){
            ArenaManager.deleteArena(arena, Component.text("Insufficient player count. Game cancelled!", NamedTextColor.RED));
            arena = null;
            return;
        }

        Bukkit.getScheduler().runTask(CoreAPI.getPlugin(), () -> {
            arena.setStateToPlaying();
            arena = null;
        });
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
                        attemptPlayState();
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
        }.runTaskTimer(CoreAPI.getPlugin(), 1, 2);
    }

    private void sendActionBarTimed(String message, int duration){
        new BukkitRunnable(){
            int iteration = duration;
            public void run(){
                if (iteration == 0){
                    for (Player p : arena.getOnlineStartPlayers()){
                        p.sendActionBar(Component.empty());
                    }
                    attemptPlayState();
                    cancel();
                    return;
                }
                for (Player p : arena.getOnlineStartPlayers()){
                    p.sendActionBar(Component.text(message+iteration));
                }
                iteration--;
            }
        }.runTaskTimer(CoreAPI.getPlugin(), 0, 20);
    }

}
