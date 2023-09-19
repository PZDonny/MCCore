package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.events.ArenaCreatedEvent;
import MCCore.events.ArenaDeletedEvent;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.RamThresholdManager;
import MCCore.sockets.Messages;
import MCCore.utils.PlayerBalancerAPI;
import MCCore.utils.SlimeTools;
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
    private static final Map<UUID, Arena> playerTargetArenas = new HashMap<>();


    public static void addPlayerToHashArena(Player p){
        Arena arena = playerTargetArenas.get(p.getUniqueId());
        if (arena == null || !arena.isUsuable()){
            //do smthn
            return;
        }
        arena.addPlayer(p);
        removeHashPlayer(p.getUniqueId());
    }

    public static boolean addPlayersToAvaliableArena(List<UUID> uuids, int queueID){
        for (Arena arena : activeArenas.values()){
            //if (arena.getMode().equals(mode) && arena.getPlayers().size()+uuids.size() <= max){
            if (arena.getQueueID() == queueID){
                addHashPlayers(uuids, arena);
                return true;
            }
        }
    //If a new arena must be created
        return false;
    }


    public static void createArena(Arena arena, List<UUID> players, boolean onlyUseDesiredWorld){
        //SlimeWorld worldCloned = world.clone(world.getName()+"_"+ arena.getQueueID(), null);
        new BukkitRunnable() {
            public void run() {
                /*SlimeTools.getSlimePlugin().loadWorld(worldCloned);
                activeArenas.put(worldCloned, arena);
                arena.setArenaWorld(worldCloned);
            //Template World Name
                arena.setTemplateWorldName(world.getName());*/
                //Arena Created Event
                Bukkit.getPluginManager().callEvent(new ArenaCreatedEvent(arena));
                new BukkitRunnable(){
                    public void run(){
                        for (UUID uuid : new ArrayList<>(players)){
                            Player p = Bukkit.getPlayer(uuid);
                            if (p != null && p.isOnline()){
                                players.remove(uuid);
                                arena.addPlayer(p);
                            }
                        }
                        if (!players.isEmpty()) addHashPlayers(players, arena);
                        arena.doCountdown();
                    }
                }.runTaskLater(Core.getInstance(), 20);
            }
        }.runTask(Core.getInstance());
    }

    public static void deleteArena(Arena arena, String cause){
        if (cause != null){
            Bukkit.getConsoleSender().sendMessage(cause);
        }
        new BukkitRunnable(){
            public void run(){
                Bukkit.getPluginManager().callEvent(new ArenaDeletedEvent(arena));

            //Remove players from World
                if (!arena.getArenaPlayers().isEmpty()){
                    for (Player p : arena.getArenaPlayers()){
                        if (cause != null){
                            p.sendMessage(cause);
                        }
                        for (Entity e : p.getPassengers()){
                            p.removePassenger(e);
                        }
                        p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
                        PlayerBalancerAPI.connectPlayerToFallback(p);
                    }
                }


                removeHashPlayers(arena);

                String[] out = new String[]{Messages.MINIGAMEAPI_STOPARENA.getID(), String.valueOf(arena.getQueueID())};
                Core.getClient().sendMessage(out);

                activeArenas.remove(arena.getArenaWorld());
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
                if (activeArenas.size() == 0){
                    String message = Core.prefix+ChatColor.RED+"All arenas have ended their matches, restarting NOW!";
                    Bukkit.getConsoleSender().sendMessage(message);
                    Bukkit.shutdown();
                }
                /*else{
                    System.out.println(activeArenas.size());
                    for (Arena arena : activeArenas.values()){
                        System.out.println(arena.getTemplateWorldName());
                    }
                }*/
            }
        }.runTaskLater(Core.getInstance(), 30);
    }

    public static void addArena(SlimeWorld world, Arena arena){
        activeArenas.put(world, arena);
    }

    public static Collection<Arena> getActiveArenas(){
        return activeArenas.values();
    }


    private static void addHashPlayers(List<UUID> players, Arena arena){
        for (UUID uuid : players){
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            if (p.isOnline()) arena.addPlayer((Player) p);
            else playerTargetArenas.put(uuid, arena);
        }
    }



    private static void removeHashPlayer(UUID uuid){
        playerTargetArenas.remove(uuid);
    }

    private static void removeHashPlayers(Arena arena){
        for (UUID uuid : new HashSet<>(playerTargetArenas.keySet())){
            if (playerTargetArenas.get(uuid).equals(arena)) playerTargetArenas.remove(uuid);
        }
    }

    public static void removePlayerFromArena(Player p, boolean removeFromHash, PlayerRemovedFromArenaEvent.RemoveCause cause){
        if (removeFromHash){
            removeHashPlayer(p.getUniqueId());
        }
        Arena arena = getArenaOfPlayer(p);
        if (arena != null){
            arena.removePlayer(p, cause);
        }
    }


    public static Arena getArena(String slimeWorldName){
        for (SlimeWorld world : activeArenas.keySet()){
            if (world.getName().equals(slimeWorldName)){
                return activeArenas.get(world);
            }
        }
        return null;
    }

    public static Arena getArenaOfPlayer(Player p){
        for (Arena arena : activeArenas.values()){
            SlimeWorld sw = arena.getArenaWorld();
            if (sw != null && sw.getName().equals(p.getWorld().getName())){
                return arena;
            }

            if (arena.getArenaPlayers().contains(p)){
                return arena;
            }
        }
        return null;
    }


    public static void refreshPlayer(Player p, GameMode gameMode){
    //UI/Visuals
        p.getInventory().clear();
        p.setShoulderEntityLeft(null);
        p.setShoulderEntityRight(null);
        p.setLevel(0);
        p.setExp(0);
        p.setFoodLevel(20);
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
