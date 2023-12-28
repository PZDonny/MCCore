package MCCore.utils;

import MCCore.Core;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerUtils {

    static Map<UUID, Integer> playerChatChannels = new ConcurrentHashMap<>();

    public static void sendToLobby(Player player){
        if (Core.isLobbyServer()){
            player.sendMessage(ChatColor.RED+"You are already connected to a lobby server!");
            return;
        }
        String channel = "mccore:lobby";
        if (Core.isMinigameEnabled() && player.getWorld().equals(Core.getInstance().getMinigameWaitingWorld())){
                channel = "mccore:lobbydequeue";
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getUniqueId().toString());
        player.sendPluginMessage(Core.getInstance(), channel, out.toByteArray());
    }

    public static void sendHPToPlayer(Player attacker, Player victim){
        if (isNPC(victim)) return;
        double vicHealth = ((double) Math.round((victim.getHealth()+victim.getAbsorptionAmount()) * 100) / 100);
        attacker.sendMessage(ChatColor.YELLOW+victim.getDisplayName()+"'s"+ChatColor.RED+" HP"+ChatColor.WHITE+" - "+vicHealth);
    }

    public static void sendHPToPlayer(Player attacker, Player victim, int delayInTicks){
        new BukkitRunnable(){
            public void run(){
                if (attacker.isOnline() && victim.isOnline()){
                    sendHPToPlayer(attacker, victim);
                }
            }
        }.runTaskLater(Core.getInstance(), delayInTicks);
    }

    public static boolean hasEnoughSpace(Player p, Collection<ItemStack> items, boolean checkArmor, boolean sendMSG){
        int avaliableSlots = 0;
        for (ItemStack i : p.getInventory().getStorageContents()){
            if (i == null){
                avaliableSlots++;
            }
        }
        if (checkArmor){
            if (p.getInventory().getHelmet() != null) avaliableSlots--;
            if (p.getInventory().getChestplate() != null) avaliableSlots--;
            if (p.getInventory().getLeggings() != null) avaliableSlots--;
            if (p.getInventory().getBoots() != null) avaliableSlots--;
        }

        if (items.size() <= avaliableSlots) {
            return true;
        }
        if (sendMSG){
            p.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(items.size()-avaliableSlots)+ChatColor.RED+" slot(s)");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return false;
    }

    public static boolean hasEnoughSpace(Player p, ItemStack item, boolean checkArmor, boolean sendMSG){
        int avaliableSlots = 0;
        for (ItemStack i : p.getInventory().getStorageContents()){
            if (i == null){
                avaliableSlots++;
            }
            else if (i.equals(item)){
                if (i.getAmount()+item.getAmount() <= item.getMaxStackSize()){
                    avaliableSlots++;
                }
            }
        }
        if (checkArmor){
            if (p.getInventory().getHelmet() != null) avaliableSlots--;
            if (p.getInventory().getChestplate() != null) avaliableSlots--;
            if (p.getInventory().getLeggings() != null) avaliableSlots--;
            if (p.getInventory().getBoots() != null) avaliableSlots--;
        }

        if (1 <= avaliableSlots) {
            return true;
        }
        if (sendMSG){
            p.sendMessage(ChatColor.GOLD+"⚠ "+ChatColor.RED+"You do not have enough inventory space! Make room for "+ChatColor.YELLOW+(1-avaliableSlots)+ChatColor.RED+" slot(s)");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        }
        return false;
    }

    public static boolean isNPC(Player p){
        return p.hasMetadata("NPC");
    }

    public static int getPlayerChatChannel(Player player){
        return getPlayerChatChannel(player.getUniqueId());
    }

    public static int getPlayerChatChannel(UUID uuid){
        if (!playerChatChannels.containsKey(uuid)){
            return -1;
        }
        return playerChatChannels.get(uuid);
    }

    public static void setPlayerChatChannel(Player player, int channel){
        setPlayerChatChannel(player.getUniqueId(), channel);
    }

    public static void setPlayerChatChannel(UUID uuid, int channel){
        playerChatChannels.put(uuid, channel);
    }

    public static void unsetPlayerChatChannel(Player player){
        unsetPlayerChatChannel(player.getUniqueId());
    }

    public static void unsetPlayerChatChannel(UUID uuid){
        playerChatChannels.remove(uuid);
    }
}
