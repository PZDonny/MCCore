package MCCore.sockets;

import MCCore.Core;
import MCCore.events.ArenaCreatedEvent;
import MCCore.events.PlayerRemovedFromArenaEvent;
import MCCore.minigameAPI.ConnectedParty;
import MCCore.minigameAPI.arenaManager.Arena;
import MCCore.minigameAPI.arenaManager.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MinigameAPIMessages {

    public static void run(Object[] message){
        String tag = (String) message[0];
        if (tag.equals(Messages.MINIGAMEAPI_CREATEARENA.getID())){
            String uuids = (String) message[1];
            int minPlayers = (int) message[2];
            int maxPlayers = (int) message[3];
            int queueID = (int) message[4];
            String mode = (String) message[5];
            String minigame = (String) message[6];
            OfflinePlayer host = null;
            String privateSettings = (String) message[8];
            ArrayList<String[]> connectedParties = (ArrayList<String[]>) message[9];
            if (!message[7].equals("")){
                host = Bukkit.getOfflinePlayer(UUID.fromString((String) message[7]));
            }

            List<String> uuidsAsString = new ArrayList<>(Arrays.asList(uuids.replace(" ", "").replace("[", "").replace("]", "").split(",")));
            List<UUID> playerList = new ArrayList<>();
            for (String uuid : uuidsAsString){
                playerList.add(UUID.fromString(uuid));
                Player p = Bukkit.getPlayer(UUID.fromString(uuid));

            //Remove From Arena If Already Playing
                if (p != null && ArenaManager.getArenaOfPlayer(p) != null){
                    new BukkitRunnable(){
                        public void run(){
                            ArenaManager.removePlayerFromArena(p, false, PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW);
                            p.teleport(Core.getInstance().getMinigameWaitingWorld().getSpawnLocation());
                            ArenaManager.refreshPlayer(p, GameMode.ADVENTURE);
                        }
                    }.runTask(Core.getInstance());
                }
            }

            //if (!ArenaManager.addPlayersToExistingArena(playerList, queueID)){
                Arena arena = new Arena(queueID, host, privateSettings);
                arena.setMinigame(minigame, mode);
                arena.setMinimumPlayers(minPlayers);
                arena.setMaximumPlayers(maxPlayers);
                ArenaManager.startArena(arena, playerList);

            //Get parties of players and add them to the arena
                for (String[] partyArray : connectedParties){
                    arena.addConnectedParty(new ConnectedParty(partyArray));
                }
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        new ArenaCreatedEvent(arena).callEvent();
                    }
                }.runTask(Core.getInstance());
            //}
            return;
        }
        if (message instanceof String[] stringArray){
            if (tag.equals(Messages.MINIGAMEAPI_ADDTOEXISTING.getID())){
                String uuids = stringArray[1];
                int queueID = Integer.parseInt(stringArray[2]);
                List<String> uuidsAsString = new ArrayList<>(Arrays.asList(uuids.replace(" ", "").replace("[", "").replace("]", "").split(",")));
                List<UUID> playerList = new ArrayList<>();

                for (String uuid : uuidsAsString){
                    playerList.add(UUID.fromString(uuid));
                    Player p = Bukkit.getPlayer(UUID.fromString(uuid));
                    if (p != null && ArenaManager.getArenaOfPlayer(p) != null){
                        new BukkitRunnable(){
                            public void run(){
                                ArenaManager.removePlayerFromArena(p, false, PlayerRemovedFromArenaEvent.RemoveCause.JOINEDNEW);
                            }
                        }.runTask(Core.getInstance());
                    }
                }
                ArenaManager.setQueueTargetArena(playerList, queueID);
            }
        }
    }
}
