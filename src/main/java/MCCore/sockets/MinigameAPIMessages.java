package MCCore.sockets;

import MCCore.minigameAPI.SlimeTools;
import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import MCCore.minigameAPI.arenaManager.CountdownStyle;
import MCCore.tempMinigame.TempArena;
import com.infernalsuite.aswm.api.world.SlimeWorld;

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
                    }

                    //Check if players should be added to an existing arena or if a new one must be created (will automatically be created through the check method)
                    if (!ArenaManager.addPlayersToAvaliableArena(playerList, queueID)){
                        List<String> worldList = SlimeTools.getSlimeLoader().listWorlds();
                        SlimeWorld world = SlimeTools.getSlimePlugin().getWorld(worldList.get(new Random().nextInt(worldList.size())));
                        Arena arena = new TempArena(queueID);
                        arena.setMode(mode);
                        arena.setMinimumPlayers(min);
                        arena.setCountdownStyle(CountdownStyle.BOSSBAR);
                        ArenaManager.createArena(world, arena, playerList, true);
                    }
                }

                case "minigameapi:addtoexisting" : {
                    String uuids = message[1];
                    int queueID = Integer.parseInt(message[2]);
                    List<String> uuidsAsString = new ArrayList<>(Arrays.asList(uuids.replace(" ", "").replace("[", "").replace("]", "").split(",")));
                    List<UUID> playerList = new ArrayList<>();
                    //Arena arena = ArenaManager.getActiveArenas().values().stream().filter(a -> a.getQueueID() == queueID).findFirst().orElse(null);

                    for (String uuid : uuidsAsString){
                        playerList.add(UUID.fromString(uuid));
                    }
                    ArenaManager.addPlayersToAvaliableArena(playerList, queueID);
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
