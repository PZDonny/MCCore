package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.events.ArenaCreatedEvent;
import MCCore.utils.PlayerBalancerAPI;
import MCCore.utils.SlimeTools;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;

public class ArenaManager {

    private static final Map<SlimeWorld, Arena> activeArenas = new HashMap<>();
    private static final Map<UUID, Arena> playerTargetArenas = new HashMap<>();


    public static void addPlayerToHashArena(Player p){
        if (playerTargetArenas.containsKey(p.getUniqueId())){
            Arena arena = playerTargetArenas.get(p.getUniqueId());
            arena.addPlayer(p);
            removeHashPlayer(p.getUniqueId());
        }
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
        new BukkitRunnable(){
            public void run(){
                SlimeWorld world = arena.getArenaWorld();
                World bukkitWorld = Bukkit.getWorld(world.getName());

            //Remove players from World
                if (!arena.getAllPlayers().isEmpty()){
                    for (Player p : arena.getAllPlayers()){
                        if (cause != null) p.sendMessage(cause);
                        p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
                        PlayerBalancerAPI.connectPlayerToFallback(p);
                    }
                }

                if (bukkitWorld == null){
                    Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.RED+"Unable to delete arena world that doesn't exist!"+ChatColor.GOLD+" ("+world.getName()+")");
                    return;
                }
            //Unload World when all players are removed
                Bukkit.unloadWorld(bukkitWorld, false);
                new BukkitRunnable(){
                    public void run(){
                        if (Bukkit.getWorld(bukkitWorld.getName()) == null){
                            removeHashPlayers(arena);
                            activeArenas.remove(world);
                            String[] out = new String[]{"minigameapi:stoparena"};
                            Core.getClient().sendMessage(out);
                            arena.deleteArena();

                            Bukkit.getConsoleSender().sendMessage("Removed "+bukkitWorld.getName()+" from the Cache!");
                            cancel();
                        }
                    }
                }.runTaskTimer(Core.getInstance(), 5 ,15);
            }
        }.runTask(Core.getInstance());
    }

    public static Map<SlimeWorld, Arena> getActiveArenas(){
        return activeArenas;
    }

    public static List<String> getAllTemplateArenaName(){
        try{
            return SlimeTools.getSlimeLoader().listWorlds();
        }
        catch(IOException e){
            Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.RED+"Failed to retrieve template arenas! None were found!");
            return null;
        }
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

    public static void removePlayerFromArena(Player p, boolean removeFromHash){
        if (removeFromHash) removeHashPlayer(p.getUniqueId());
        for (Arena arena : ArenaManager.getActiveArenas().values()){
            arena.removePlayer(p);
        }
    }


    public static Arena getArena(String worldName){
        for (SlimeWorld sw : activeArenas.keySet()){
            if (sw.getName().equals(worldName)) return activeArenas.get(sw);
        }
        return null;
    }

    public static Arena getArenaOfPlayer(Player p){
        return getArena(p.getWorld().getName());
    }

    public static void refreshPlayer(Player p){
        p.getInventory().clear();
        WorldBorder worldborder = p.getWorldBorder();
        if (worldborder != null) worldborder.reset();
        p.setLevel(0);
        p.setExp(0);
        p.resetTitle();
        p.resetCooldown();
        p.setArrowsInBody(0);
        p.clearTitle();
        p.sendActionBar(Component.empty());
        p.setFireTicks(0);
        p.setFreezeTicks(0);
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        Collection<PotionEffect> potions = p.getActivePotionEffects();
        for (PotionEffect effect : potions){
            p.removePotionEffect(effect.getType());
        }
    }
}
