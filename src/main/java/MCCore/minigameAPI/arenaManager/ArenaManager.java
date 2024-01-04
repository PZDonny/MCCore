package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.events.ArenaCreatedEvent;
import MCCore.events.ArenaDeletedEvent;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.ConnectedParty;
import MCCore.minigameAPI.GameState;
import MCCore.minigameAPI.RamThresholdManager;
import MCCore.sockets.Messages;
import MCCore.utils.PlayerUtils;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ArenaManager {

    private static final Map<SlimeWorld, Arena> activeArenas = new HashMap<>();
    private static final Map<World, Arena> activeManualWorldArenas = new HashMap<>();
    private static final ArrayList<Arena> inactiveManualWorldArenas = new ArrayList<>();
    private static final Map<UUID, Arena> playerTargetArenas = new HashMap<>();


    private static void setQueueTargetArena(Collection<UUID> uuids, Arena arena){
        for (UUID uuid : uuids){
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            if (p.isOnline()){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        arena.addPlayer((Player) p, GameMode.SPECTATOR, true);
                    }
                }.runTask(Core.getInstance());
            }
            else{
                playerTargetArenas.put(uuid, arena);
            }
        }
    }

    public static void setQueueTargetArena(Collection<UUID> uuids, int queueID){
        for (Arena arena : activeArenas.values()){
            //if (arena.getMode().equals(mode) && arena.getPlayers().size()+uuids.size() <= max){
            if (arena.getQueueID() == queueID){
                setQueueTargetArena(uuids, arena);
                if (uuids.size() > 1){
                    arena.addConnectedParty(new ConnectedParty(uuids));
                }
                return;
            }
        }
    }

    public static Arena getQueueTargetArena(UUID uuid){
        return playerTargetArenas.get(uuid);
    }

    private static void removePlayerTargetArena(Player p){
        playerTargetArenas.remove(p.getUniqueId());
    }

    public static void addPlayerToTargetArena(Player p){
        Arena arena = playerTargetArenas.get(p.getUniqueId());
        if (arena == null || !arena.isUsable()){
            //do smthn
            return;
        }
        arena.addPlayer(p, GameMode.SPECTATOR, true);
        removePlayerTargetArena(p);
    }



    private static void removeQueueTargetArena(Arena arena){
        for (UUID uuid : new HashSet<>(playerTargetArenas.keySet())){
            if (playerTargetArenas.get(uuid).equals(arena)){
                playerTargetArenas.remove(uuid);
            }
        }
    }





    public static void startArena(Arena arena, Collection<UUID> players){
        new BukkitRunnable(){
            public void run(){
                for (UUID uuid : new ArrayList<>(players)){
                    Player p = Bukkit.getPlayer(uuid);
                //Players already online
                    if (p != null && p.isOnline()){
                        players.remove(uuid);
                        arena.addPlayer(p, GameMode.SPECTATOR, true);
                    }
                }
            //Offline Players
                if (!players.isEmpty()){
                    setQueueTargetArena(players, arena);
                }

                if (arena.isManualWorld()){
                    new ArenaCreatedEvent(arena).callEvent();
                }
                arena.doCountdown();
            }
        }.runTaskLater(Core.getInstance(), 10);
    }

    static void deleteArena(Arena arena, String cause){
        if (cause != null){
            Bukkit.getConsoleSender().sendMessage(cause);
        }
        new BukkitRunnable(){
            public void run(){
                Bukkit.getPluginManager().callEvent(new ArenaDeletedEvent(arena));

                if (arena.getGameState() == GameState.CONNECTING){
                    for (Player p : arena.getOnlineStartPlayers()){
                        if (cause != null){
                            p.sendMessage(cause);
                        }
                        PlayerUtils.sendToLobby(p);
                    }
                }
                else{
                //Remove players from Arena and World
                    for (Player p : arena.getArenaPlayers()){
                        if (cause != null){
                            p.sendMessage(cause);
                        }
                        for (Entity e : p.getPassengers()){
                            p.removePassenger(e);
                        }

                        Arena targetArena = getQueueTargetArena(p.getUniqueId());
                        if (targetArena == null || targetArena.equals(arena)){
                            PlayerUtils.sendToLobby(p);
                        }
                        p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
                        ArenaManager.refreshPlayer(p, GameMode.SPECTATOR);
                    }
                }

                removeQueueTargetArena(arena);

                if (Core.isDataProxyAllowed()){
                    String[] out = new String[]{Messages.MINIGAMEAPI_STOPARENA.getID(), String.valueOf(arena.getQueueID())};
                    Core.getClient().sendMessage(out);
                }


                if (arena.isManualWorld()){
                    inactiveManualWorldArenas.remove(arena);
                    activeManualWorldArenas.remove(arena.getManualWorld());
                }
                else{
                    activeArenas.remove(arena.getArenaWorld());
                }
                arena.deleteArena();
                ramThresholdCheck();
            }
        }.runTask(Core.getInstance());
    }

    private static void ramThresholdCheck(){
        new BukkitRunnable(){
            @Override
            public void run() {
                RamThresholdManager manager = Core.getRamManager();
                if (manager == null || manager.isStopped() || !manager.isRestartingSoon()){
                    return;
                }
                if (activeArenas.size() == 0) {
                    manager.restartServer();
                }
            }
        }.runTaskLater(Core.getInstance(), 30);
    }


    public static void addArena(SlimeWorld world, Arena arena){
        activeArenas.put(world, arena);
    }

    static boolean addManualArena(Arena arena, World world){
        if (activeManualWorldArenas.containsKey(world)){
            return false;
        }
        activeManualWorldArenas.put(world, arena);
        inactiveManualWorldArenas.remove(arena);
        return true;
    }

    static void addInactiveManualArena(Arena arena){
        if (arena.isManualWorld()){
            inactiveManualWorldArenas.add(arena);
        }
    }

    static void removeInactiveManualArena(Arena arena){
        if (arena.isManualWorld()){
            inactiveManualWorldArenas.add(arena);
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


    public static void removePlayerFromArena(Player p, boolean removeTargetArena, PlayerRemovedFromArenaEvent.RemoveCause cause){
        if (removeTargetArena){
            removePlayerTargetArena(p);
        }
        Arena arena = getArenaOfPlayer(p);
        if (arena != null){
            arena.removePlayer(p, cause);
        }
    }


    public static Arena getArena(String worldName){
        for (Arena arena : inactiveManualWorldArenas){
            World w = arena.getManualWorld();
            if (w != null && w.getName().equalsIgnoreCase(worldName)){
                return arena;
            }
        }

        for (Arena arena : activeManualWorldArenas.values()){
            World w = arena.getManualWorld();
            if (w != null && w.getName().equalsIgnoreCase(worldName)){
                return arena;
            }
        }

        for (SlimeWorld world : activeArenas.keySet()){
            if (world.getName().equalsIgnoreCase(worldName)){
                return activeArenas.get(world);
            }
        }
        return null;
    }

    public static Arena getArenaOfPlayer(Player p){
        if (p == null){
            return null;
        }
        for (Arena arena : inactiveManualWorldArenas){
            if (doesArenaContainPlayer(p, arena, null) != null){
                return arena;
            }
        }

        for (Arena arena : activeManualWorldArenas.values()){
            World w = arena.getManualWorld();
            if (w != null && doesArenaContainPlayer(p, arena, w.getName()) != null){
                return arena;
            }
        }

        for (Arena arena : activeArenas.values()){
            SlimeWorld sw = arena.getArenaWorld();
            if (sw != null && doesArenaContainPlayer(p, arena, sw.getName()) != null){
                return arena;
            }
        }
        return null;
    }

    private static Arena doesArenaContainPlayer(Player p, Arena arena, String worldName){
        if (p == null){
            return null;
        }
        if (worldName != null){
            if (worldName.equalsIgnoreCase(p.getWorld().getName())){
                return arena;
            }
        }

        if (arena.getGameState() == GameState.CONNECTING && arena.getStartPlayers().contains(p)){
            return arena;
        }
        else if (arena.getArenaPlayers().contains(p)){
            return arena;
        }
        return null;
    }


    public static void refreshPlayer(Player p, GameMode gameMode){
        if (!p.isOnline()){
            return;
        }
    //UI/Visuals
        p.getInventory().clear();
        p.setShoulderEntityLeft(null);
        p.setShoulderEntityRight(null);
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
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setHealth(20);
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Iterator<KeyedBossBar> bars = Bukkit.getBossBars();
        while(bars.hasNext()){
            BossBar bar = bars.next();
            bar.removePlayer(p);
        }
        WorldBorder worldborder = p.getWorldBorder();
        if (worldborder != null){
            worldborder.reset();
        }


    //Other
        p.undiscoverRecipes(p.getDiscoveredRecipes());
        p.setCanPickupItems(true);
        p.setBedSpawnLocation(null, true);
        p.setFlying(false);
        p.setSwimming(false);
        p.setGliding(false);
        p.setInvulnerable(false);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);
        p.setGameMode(gameMode);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
    }
}
