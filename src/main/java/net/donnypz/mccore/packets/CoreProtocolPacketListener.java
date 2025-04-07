package net.donnypz.mccore.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.donnypz.mccore.Core;
import net.donnypz.mccore.minigame.arenaManager.Arena;
import net.donnypz.mccore.minigame.arenaManager.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CoreProtocolPacketListener {

    public static void registerOutgoing(){
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player receiver = event.getPlayer();
                Arena arena = ArenaManager.getArenaOfPlayer(receiver);
                if (arena == null || arena.isEndingOrNotUsable() || arena.tablistHidesSpectators()){
                    return;
                }

                PacketContainer packet = event.getPacket();
                //int length = packet.getIntegers().read(0);
                List<UUID> uuids = packet.getUUIDLists().read(0);
                for (UUID uuid : uuids){
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null || !p.isOnline()){ //Allow Packet
                        continue;
                    }

                    if (arena.isPlayerSpectating(p, false)){ //Disallow Packet for spectators
                        event.setCancelled(true);
                    }
                }
            }
        });
    }
}
