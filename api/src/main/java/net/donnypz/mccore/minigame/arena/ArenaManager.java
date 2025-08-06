package net.donnypz.mccore.minigame.arena;

import net.donnypz.mccore.events.PlayerRemovedFromArenaEvent;
import net.donnypz.mccore.minigame.ArenaState;
import net.donnypz.mccore.utils.slime.SlimeUtils;
import net.donnypz.mccore.utils.slime.WrappedSlimeWorld;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ArenaManager {

    static final Map<String, Arena> activeArenas = new HashMap<>();
    static final HashSet<Arena> inactiveArenas = new HashSet<>();
    private static final Map<UUID, Arena> playerArenas = new HashMap<>();


    private ArenaManager(){}

    static void setPlayerArena(UUID playerUUID, Arena arena){
        playerArenas.put(playerUUID, arena);
    }

    static void unsetPlayerArena(UUID playerUUID){
        playerArenas.remove(playerUUID);
    }

    static void unsetPlayerInArena(UUID playerUUID, Arena arena){
        Arena currentArena = getArenaOfPlayer(playerUUID);
        if (currentArena == arena){
            playerArenas.remove(playerUUID);
        }
    }


    static void deleteArena(Arena arena, Component cause){
        if (cause != null){
            Bukkit.getConsoleSender().sendMessage(cause);
        }

        Bukkit.getScheduler().runTask(CoreAPI.getPlugin(), () -> {
            Set<Player> arenaPlayers = arena.getArenaPlayers();
            for (Player p : arenaPlayers){
                ArenaManager.unsetPlayerInArena(p.getUniqueId(), arena);
            }

            if (arena.getGameState() == ArenaState.CONNECTING){
                for (Player p : arena.getOnlineStartPlayers()){
                    if (cause != null){
                        p.sendMessage(cause);
                    }
                }
            }
            else{
                World w = arena.getBukkitWorld();

                //Remove players from Arena and World
                for (Player p : w.getPlayers()){
                    if (cause != null){
                        p.sendMessage(cause);
                    }
                    for (Entity e : p.getPassengers()){
                        p.removePassenger(e);
                    }

                    p.teleport(CoreAPI.getConfigOptions().waitingWorld.getSpawnLocation());
                    if (arena instanceof SlimeArena sa && !sa.isSlimeManual){
                        ArenaManager.refreshPlayer(p, GameMode.SPECTATOR, true, true);
                    }
                }
            }

            inactiveArenas.remove(arena);
            activeArenas.remove(arena.getArenaWorldName());
            ArenaContainer container = ArenaContainer.getArenaContainer(arena);
            if (container != null){
                container.delete();
            }
            arena.deleteArena();
        });
    }



    public static Collection<Arena> getActiveArenas(){
        return new HashSet<>(activeArenas.values());
    }

    public static Collection<Arena> getInactiveArenas(){
        return new HashSet<>(inactiveArenas);
    }



    @ApiStatus.Internal
    public static void removePlayerFromArena(Player p,  PlayerRemovedFromArenaEvent.RemoveCause cause){
        Arena arena = getArenaOfPlayer(p);
        if (arena != null){
            arena.removePlayer(p, cause);
        }
    }


    public static Arena getArena(@NotNull String worldName){
        for (Arena arena : inactiveArenas){
            if (worldName.equals(arena.getArenaWorldName())){
                return arena;
            }
        }

        Arena arena = activeArenas.get(worldName);
        if (arena != null){
            return arena;
        }

        if (SlimeUtils.isSlimeInstalled()){
            WrappedSlimeWorld<?> sw = SlimeUtils.getSlimeWorld(worldName);
            if (sw == null){
                return null;
            }
            return activeArenas.get(sw.getName());
        }
        return null;
    }

    public static Arena getArena(UUID queueUUID){
        for (Arena arena : inactiveArenas){
            if (arena.getQueueUUID().equals(queueUUID)){
                return arena;
            }
        }

        for (Arena arena : activeArenas.values()){
            if (arena.getQueueUUID().equals(queueUUID)){
                return arena;
            }
        }
        return null;
    }


    public static Arena getArenaOfPlayer(UUID playerUUID){
        return playerArenas.get(playerUUID);
    }

    public static Arena getArenaOfPlayer(Player p){
        if (p == null){
            return null;
        }
        return getArenaOfPlayer(p.getUniqueId());
    }

    public static void refreshPlayer(@NotNull Player p, @NotNull GameMode gameMode, boolean clearInventory, boolean resetScoreboard){
        if (!p.isConnected()){
            return;
        }

    //UI/Visuals
        if (clearInventory){
            p.getInventory().clear();
        }
        p.releaseLeftShoulderEntity();
        p.releaseRightShoulderEntity();
        p.setLevel(0);
        p.setExp(0);
        p.setFoodLevel(20);
        p.setSaturation(5);
        p.setExhaustion(0);
        p.setArrowsInBody(0);
        p.setBeeStingersInBody(0);
        p.setFireTicks(0);
        p.setFreezeTicks(0);
        p.setGlowing(false);
        p.setVisualFire(false);
        p.resetTitle();
        p.sendActionBar(Component.empty());
        p.resetCooldown();
        p.resetPlayerTime();
        p.resetPlayerWeather();
        p.getAttribute(CoreAPI.getVersionHandler().getMaxHealthAttribute()).setBaseValue(20);
        Registry.ATTRIBUTE.forEach(attribute -> {
            AttributeInstance instance = p.getAttribute(attribute);
            if (instance == null){
                return;
            }

            for (AttributeModifier mod : instance.getModifiers()){
                instance.removeModifier(mod);
            }
        });

        p.setHealth(p.getAttribute(CoreAPI.getVersionHandler().getMaxHealthAttribute()).getValue());
        if (resetScoreboard){
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        Iterator<KeyedBossBar> bars = Bukkit.getBossBars();
        while(bars.hasNext()){
            BossBar bar = bars.next();
            bar.removePlayer(p);
        }
        p.setWorldBorder(p.getWorld().getWorldBorder());


    //Other
        p.undiscoverRecipes(p.getDiscoveredRecipes());
        p.setCanPickupItems(true);
        CoreAPI.getVersionHandler().setRespawnLocation(p, null, true);
        p.setFlying(false);
        p.setGliding(false);
        p.setInvulnerable(false);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);
        p.setGameMode(gameMode);
        Collection<PotionEffect> effects = p.getActivePotionEffects();
        for (PotionEffect effect : effects){
            p.removePotionEffect(effect.getType());
        }
    }
}
