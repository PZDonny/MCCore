package MCCore.sockets;

import MCCore.Core;
import MCCore.events.ArenaCreatedEvent;
import MCCore.utils.SlimeTools;
import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import MCCore.minigameAPI.arenaManager.CountdownStyle;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;

public class MinigameAPIMessages {

    public static void run(String[] message){
        try{
            switch (message[0]){
                case "minigameapi:joinarena" : {
                    String uuids = message[1];
                    int min = Integer.parseInt(message[2]);
                    int max = Integer.parseInt(message[3]);
                    int queueID = Integer.parseInt(message[4]);
                    String mode = message[5];
                    List<String> uuidsAsString = new ArrayList<>(Arrays.asList(uuids.replace(" ", "").replace("[", "").replace("]", "").split(",")));
                    List<UUID> playerList = new ArrayList<>();
                    for (String uuid : uuidsAsString){
                        playerList.add(UUID.fromString(uuid));
                        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
                        if (p != null && ArenaManager.getArenaOfPlayer(p) != null){
                            new BukkitRunnable(){
                                public void run(){
                                    ArenaManager.removePlayerFromArena(p, false);
                                }
                            }.runTask(Core.getInstance());

                        }
                    }

                //Check if players should be added to an existing arena or if a new one must be created
                    if (!ArenaManager.addPlayersToAvaliableArena(playerList, queueID)){
                        List<String> worldList = SlimeTools.getSlimeLoader().listWorlds();
                        SlimeWorld world = SlimeTools.getSlimePlugin().getWorld(worldList.get(new Random().nextInt(worldList.size())));
                        Arena arena = new Arena(queueID);
                        arena.setMode(mode);
                        arena.setMinimumPlayers(min);
                        arena.setCountdownStyle(CountdownStyle.BOSSBAR);
                        ArenaManager.createArena(arena, playerList, true);
                    }
                }

                case "minigameapi:addtoexisting" : {
                    String uuids = message[1];
                    int queueID = Integer.parseInt(message[2]);
                    List<String> uuidsAsString = new ArrayList<>(Arrays.asList(uuids.replace(" ", "").replace("[", "").replace("]", "").split(",")));
                    List<UUID> playerList = new ArrayList<>();

                    for (String uuid : uuidsAsString){
                        playerList.add(UUID.fromString(uuid));
                        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
                        if (p != null && ArenaManager.getArenaOfPlayer(p) != null){
                            new BukkitRunnable(){
                                public void run(){
                                    ArenaManager.removePlayerFromArena(p, false);
                                }
                            }.runTask(Core.getInstance());

                        }
                    }
                    ArenaManager.addPlayersToAvaliableArena(playerList, queueID);
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
