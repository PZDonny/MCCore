package net.donnypz.mccore.minigame.arenaManager;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.events.ArenaCreatedEvent;
import net.donnypz.mccore.events.PlayerRemovedFromArenaEvent;
import net.donnypz.mccore.minigame.GameState;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import net.donnypz.mccore.utils.SlimeUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public final class ArenaManager {

    private static final Map<String, Arena> activeArenas = new HashMap<>();

    private static final Map<World, Arena> activeManualWorldArenas = new HashMap<>();
    private static final ArrayList<Arena> inactiveManualWorldArenas = new ArrayList<>();

    private static final ArrayList<Arena> inactiveSlimeManualArenas = new ArrayList<>();

    private static final Map<UUID, Arena> playerQueueTargetArenas = new HashMap<>();
    private static final Map<UUID, Arena> playerArenas = new HashMap<>();
    private static final Map<UUID, String[]> playerSpectateArenas = new HashMap<>();


    private ArenaManager(){}

    private static void setQueueTargetArena(Collection<UUID> uuids, Arena arena){
        for (UUID uuid : uuids){
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            if (p.isOnline()){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        //arena.addPlayerAutomatic((Player) p, GameMode.SPECTATOR, true);
                        arena.addPlayerAutomatic((Player) p, true);
                    }
                }.runTask(Core.getInstance());
            }

            else{
                playerQueueTargetArenas.put(uuid, arena);
            }
        }
    }

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




    private static void removeQueueTargetArena(Arena arena){
        for (UUID uuid : new HashSet<>(playerQueueTargetArenas.keySet())){
            if (playerQueueTargetArenas.get(uuid).equals(arena)){
                playerQueueTargetArenas.remove(uuid);
            }
        }
    }

    @ApiStatus.Internal
    public static void addArena(Arena arena, Collection<UUID> players){
        if (players != null){
            for (UUID uuid : new ArrayList<>(players)){
                Player p = Bukkit.getPlayer(uuid);

                //Players already online
                if (p != null && p.isOnline()){
                    players.remove(uuid);
                    arena.addPlayerAutomatic(p, true);
                }
            }

        //Offline Players
            if (!players.isEmpty()){
                setQueueTargetArena(players, arena);
            }
        }



        if (arena.getArenaType() != Arena.ArenaType.SLIME){
            new ArenaCreatedEvent(arena).callEvent();
        }

        if (arena.getArenaType() == Arena.ArenaType.SLIMEMANUAL) {
            addInactiveSlimeManualArena(arena);
        }
        arena.startCountdown();
    }

    static void deleteArena(Arena arena, String cause){
        if (cause != null){
            Bukkit.getConsoleSender().sendMessage(cause);
        }

        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            Set<Player> arenaPlayers = arena.getArenaPlayers();
            for (Player p : arenaPlayers){
                ArenaManager.unsetPlayerInArena(p.getUniqueId(), arena);
            }

            if (arena.getGameState() == GameState.CONNECTING){
                for (Player p : arena.getOnlineStartPlayers()){
                    if (cause != null){
                        p.sendMessage(cause);
                    }

                    if (arena.getArenaType() == Arena.ArenaType.SLIME){
                        //Send to lobby
                    }
                }
            }
            else{
                World w;
                if (arena.getArenaType() == Arena.ArenaType.BUKKITMANUAL){
                    w = arena.manualBukkitWorld;
                }
                else{
                    w = arena.getArenaAsBukkitWorld();
                }
                //Remove players from Arena and World
                for (Player p : w.getPlayers()){
                    if (cause != null){
                        p.sendMessage(cause);
                    }
                    for (Entity e : p.getPassengers()){
                        p.removePassenger(e);
                    }

                    p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
                    if (arena.getArenaType() == Arena.ArenaType.SLIME){
                        ArenaManager.refreshPlayer(p, GameMode.SPECTATOR, true, true);
                    }
                }
            }

            removeQueueTargetArena(arena);


            if (arena.getArenaType() == Arena.ArenaType.BUKKITMANUAL){
                inactiveManualWorldArenas.remove(arena);
                activeManualWorldArenas.remove(arena.getManualBukkitWorld());
            }
            else{
                if (arena.getArenaType() == Arena.ArenaType.SLIMEMANUAL){
                    inactiveSlimeManualArenas.remove(arena);
                }
                activeArenas.remove(arena.getArenaWorldName());
            }


            ArenaContainer container = ArenaContainer.getArenaContainer(arena);
            if (container != null){
                container.delete();
            }
            arena.deleteArena();
        });
    }

    static void registerArena(Arena arena){
        if (arena.getArenaType() == Arena.ArenaType.SLIMEMANUAL){
            removeInactiveSlimeManualArena(arena);
        }
        activeArenas.put(arena.getArenaWorldName(), arena);

    }

    static boolean addManualBukkitArena(Arena arena, World world){
        if (activeManualWorldArenas.containsKey(world)){
            return false;
        }
        activeManualWorldArenas.put(world, arena);
        inactiveManualWorldArenas.remove(arena);
        return true;
    }

    static void addInactiveManualBukkitArena(Arena arena){
        if (arena.getArenaType() == Arena.ArenaType.BUKKITMANUAL){
            inactiveManualWorldArenas.add(arena);
        }
    }

    static void removeInactiveManualBukkitArena(Arena arena){
        if (arena.getArenaType() == Arena.ArenaType.BUKKITMANUAL){
            inactiveManualWorldArenas.remove(arena);
        }
    }

    private static void addInactiveSlimeManualArena(Arena arena){
        if (arena.getArenaType() == Arena.ArenaType.SLIMEMANUAL){
            inactiveSlimeManualArenas.add(arena);
        }
    }

    private static void removeInactiveSlimeManualArena(Arena arena){
        if (arena.getArenaType() == Arena.ArenaType.SLIMEMANUAL){
            inactiveSlimeManualArenas.remove(arena);
        }
    }

    public static Collection<Arena> getActiveArenas(){
        return activeArenas.values();
    }

    public static Collection<Arena> getActiveManualArenas(){
        return activeManualWorldArenas.values();
    }

    public static Collection<Arena> getInactiveManualArenas(){
        return new ArrayList<>(inactiveManualWorldArenas);
    }

    public static Collection<Arena> getInactiveSlimeManualArenas(){
        return new ArrayList<>(inactiveSlimeManualArenas);
    }



    @ApiStatus.Internal
    public static void removePlayerFromArena(Player p,  PlayerRemovedFromArenaEvent.RemoveCause cause){
        Arena arena = getArenaOfPlayer(p);
        if (arena != null){
            arena.removePlayer(p, cause);
        }
    }


    public static Arena getArena(String worldName){
        for (Arena arena : inactiveManualWorldArenas){
            World w = arena.getManualBukkitWorld();
            if (w != null && w.getName().equals(worldName)){
                return arena;
            }
        }

        for (Arena arena : inactiveSlimeManualArenas){
            World w = arena.getManualBukkitWorld();
            if (w != null && w.getName().equals(worldName)){
                return arena;
            }
        }

        Arena arena = activeManualWorldArenas.get(Bukkit.getWorld(worldName));
        if (arena != null){
            return arena;
        }

        if (Core.isSlimeInstalled()){
            SlimeWorld sw = SlimeUtils.getSlimeWorld(worldName);
            if (sw == null){
                return null;
            }
            return activeArenas.get(sw.getName());
        }

        return null;
    }

    public static Arena getArena(UUID queueUUID){
        for (Arena arena : inactiveManualWorldArenas){
            if (arena.getQueueUUID().equals(queueUUID)){
                return arena;
            }
        }

        for (Arena arena : activeManualWorldArenas.values()){
            if (arena.getQueueUUID().equals(queueUUID)){
                return arena;
            }
        }

        for (Arena arena : inactiveSlimeManualArenas){
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
        return playerArenas.get(p.getUniqueId());
    }

    public static void refreshPlayer(Player p, GameMode gameMode, boolean clearInventory, boolean resetScoreboard){
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
        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        Registry.ATTRIBUTE.forEach(attribute -> {
            AttributeInstance instance = p.getAttribute(attribute);
            if (instance == null){
                return;
            }

            for (AttributeModifier mod : instance.getModifiers()){
                instance.removeModifier(mod);
            }
        });

        p.setHealth(p.getAttribute(Attribute.MAX_HEALTH).getValue());
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
        p.setRespawnLocation(null, true);
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
