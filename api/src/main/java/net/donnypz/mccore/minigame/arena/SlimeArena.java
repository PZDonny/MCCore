package net.donnypz.mccore.minigame.arena;

import net.donnypz.mccore.events.ArenaWorldGeneratedEvent;
import net.donnypz.mccore.events.PlayerRemovedFromArenaEvent;
import net.donnypz.mccore.minigame.ArenaState;
import net.donnypz.mccore.utils.misc.WorldUtils;
import net.donnypz.mccore.utils.slime.SlimeUtils;
import net.donnypz.mccore.utils.slime.WrappedSlimeWorld;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class SlimeArena extends Arena{

    WrappedSlimeWorld<?> world;
    boolean isSlimeManual;

    SlimeArena(@Nullable UUID queueUUID, OfflinePlayer host, @NotNull ArenaSettings arenaSettings, boolean isSlimeManual) {
        super(queueUUID, host, arenaSettings);
        this.isSlimeManual = isSlimeManual;
    }


    public WrappedSlimeWorld<?> getSlimeWorld(){
        return world;
    }

    public void generateArenaWorld(String slimeWorldName, boolean autoGameRules){
        Arena arena = this;
        Bukkit.getScheduler().runTaskAsynchronously(CoreAPI.getPlugin(), () -> {
            WrappedSlimeWorld<?> worldCloned = SlimeUtils.getCloneFromLoader(slimeWorldName, slimeWorldName+"_"+queueUUID, true);
            world = worldCloned;
            arena.templateWorldName = slimeWorldName;

            //Could probably be moved to world loaded event
            new BukkitRunnable(){
                final int maxAttempts = 100;
                int attempt = 0;
                public void run() {
                    World bukkitWorld = Bukkit.getWorld(worldCloned.getName());
                    if (attempt >= maxAttempts) {
                        cancel();
                        return;
                    }
                    if (bukkitWorld != null) {
                        arena.setActive();
                        new ArenaWorldGeneratedEvent(arena).callEvent();

                        if (autoGameRules) {
                            WorldUtils.useMinigameGamerules(bukkitWorld);
                        }
                        cancel();
                    }
                    attempt++;
                }
            }.runTaskTimer(CoreAPI.getPlugin(), 5, 2);
        });

    }

    public void generateEmptyArenaWorld(boolean autoGameRules){
        generateEmptyArenaWorld(autoGameRules, null);
    }

    public void generateEmptyArenaWorld(boolean autoGameRules, @Nullable World.Environment environment){
        String emptyWorldName = "empty_"+queueUUID;
        Arena arena = this;
        Bukkit.getScheduler().runTaskAsynchronously(CoreAPI.getPlugin(), () -> {
            WrappedSlimeWorld<?> emptyWorld = SlimeUtils.getHandler().generateEmptyArenaWorld(emptyWorldName, environment);
            if (emptyWorld == null){
                return;
            }
            world = emptyWorld;
            templateWorldName = emptyWorldName;

            new BukkitRunnable(){
                final int maxAttempts = 100;
                int attempt = 0;
                public void run() {
                    World bukkitWorld = Bukkit.getWorld(emptyWorld.getName());
                    if (attempt >= maxAttempts) {
                        cancel();
                        return;
                    }
                    if (bukkitWorld != null) {
                        arena.setActive();
                        new WorldLoadEvent(bukkitWorld).callEvent();
                        new ArenaWorldGeneratedEvent(arena).callEvent();

                        if (autoGameRules) {
                            WorldUtils.useMinigameGamerules(bukkitWorld);
                        }
                        cancel();
                        return;
                    }
                    attempt++;
                }
            }.runTaskTimer(CoreAPI.getPlugin(), 5, 2);
        });
    }

    @Override
    boolean containsPlayer(@NotNull Player player){
        World w = player.getWorld();
        World arenaWorld = getBukkitWorld();
        if (w.equals(arenaWorld)) {
            return true;
        }
        if (arenaState == ArenaState.CONNECTING && startPlayers.contains(player)){
            return true;
        }
        return spectators.contains(player) || playingPlayers.contains(player);
    }

    @Override
    public World getBukkitWorld(){
        if (world == null){
            return null;
        }
        return Bukkit.getWorld(world.getName());
    }

    @Override
    void onPlayerRemoval(Player player, PlayerRemovedFromArenaEvent.RemoveCause cause){
        if (!isSlimeManual && cause != PlayerRemovedFromArenaEvent.RemoveCause.UNKNOWN){
            ArenaManager.refreshPlayer(player, GameMode.SPECTATOR, true, true);
        }
    }

    @Override
    void onArenaDeletion() {
        //Send to Lobby

        if (world != null){
            String arenaWorldName = world.getName();
            Bukkit.getConsoleSender().sendMessage(MiniMessage
                    .miniMessage()
                    .deserialize("<gold>Unloaded slime world <aqua>"+arenaWorldName+"<gold>!"));
            world = null;
            WorldUtils.destroyWorld(Bukkit.getWorld(arenaWorldName));
        }
    }
}
