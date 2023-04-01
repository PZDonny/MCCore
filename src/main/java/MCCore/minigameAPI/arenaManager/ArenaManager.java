package MCCore.minigameAPI.arenaManager;

import MCCore.Core;
import MCCore.minigameAPI.SlimeTools;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.infernalsuite.aswm.api.exceptions.WorldAlreadyExistsException;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
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


    public static void createArena(SlimeWorld world, Arena arena, List<UUID> players, boolean onlyUseDesiredWorld){
        try{
            SlimeWorld worldCloned = world.clone(world.getName()+"_"+ arena.getQueueID(), null);
            new BukkitRunnable() {
                public void run() {
                    SlimeTools.getSlimePlugin().loadWorld(worldCloned);
                    activeArenas.put(worldCloned, arena);
                    arena.setArenaWorld(worldCloned);
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
            //return worldCloned;

        } catch (IOException | WorldAlreadyExistsException e){
            throw new RuntimeException(e);
        }
    }

    public static void deleteArena(Arena arena){
        new BukkitRunnable(){
            public void run(){
                SlimeWorld world = arena.getArenaWorld();
                World bukkitWorld = Bukkit.getWorld(world.getName());
                if (bukkitWorld == null){
                    Bukkit.getConsoleSender().sendMessage(Core.prefix+ChatColor.RED+"Unable to delete world that doesn't exist!"+ChatColor.GOLD+" ("+world.getName()+")");
                    return;
                }

            //Remove players from World
                if (!arena.getPlayers().isEmpty()){
                    for (Player p : arena.getPlayers()){
                        p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF(p.getUniqueId().toString());
                        out.writeUTF(Core.fallbackServer);
                        p.sendPluginMessage(Core.getInstance(), "mccore:connect", out.toByteArray());
                    }
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
                }.runTaskTimer(Core.getInstance(), 5 ,20);
            }
        }.runTask(Core.getInstance());
    }

    public static Map<SlimeWorld, Arena> getActiveArenas(){
        return activeArenas;
    }

    public static List<String> getAllTemplateArenas(){
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

    public static void removePlayerFromArena(Player p){
        removeHashPlayer(p.getUniqueId());
        for (Arena arena : ArenaManager.getActiveArenas().values()){
            arena.getPlayers().remove(p);
        }
        p.clearTitle();
        p.sendActionBar(Component.empty());
    }
}
