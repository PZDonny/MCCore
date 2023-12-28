package MCCore.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class RankUtils {

    private static final LuckPerms LPAPI = LuckPermsProvider.get();
    private static final HashMap<UUID, String> cachedPrefixes = new HashMap<>();

    private static final HashMap<UUID, String> cachedGroupNames = new HashMap<>();


    public static String getPlayerGroupName(Player p){
        if (cachedGroupNames.containsKey(p.getUniqueId())){
            return cachedGroupNames.get(p.getUniqueId());
        }

        User user = LPAPI.getPlayerAdapter(Player.class).getUser(p);
        String groupName = user.getPrimaryGroup();
        cachedGroupNames.put(p.getUniqueId(), groupName);
        return groupName;
    }

    public static String getPlayerPrefix(Player p){
        if (cachedPrefixes.containsKey(p.getUniqueId())){
            return cachedPrefixes.get(p.getUniqueId());
        }
        if (p.isOnline()){
            User user = LPAPI.getPlayerAdapter(Player.class).getUser(p);

            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix == null){
                Group group = LPAPI.getGroupManager().getGroup(user.getPrimaryGroup());
                if (group != null){
                    prefix = group.getCachedData().getMetaData().getPrefix();
                }
            }

            if (prefix == null|| prefix.equals("&7")){
                prefix = "";
            }
            else{
                prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            }
            cachedPrefixes.put(p.getUniqueId(), prefix);
            return prefix;
        }
        else return getOfflinePlayerPrefix(p.getUniqueId());
    }



    public static String getOfflinePlayerPrefix(UUID uuid){
        if (cachedPrefixes.containsKey(uuid)){
            return cachedPrefixes.get(uuid);
        }
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()){
            return getPlayerPrefix(p);
        }

        String prefix = LPAPI.getUserManager().loadUser(uuid).join().getCachedData().getMetaData().getPrefix();
        if (prefix == null){
            Group group = LPAPI.getGroupManager().getGroup(LPAPI.getUserManager().loadUser(uuid).join().getPrimaryGroup());
            if (group != null){
                prefix = group.getCachedData().getMetaData().getPrefix();
            }
        }

        if (prefix == null || prefix.equals("&7")){
            prefix = "";
        }
        else{
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        }
        cachedPrefixes.put(uuid, prefix);
        return prefix;
    }

    public static ChatColor getPlayerChatColor(Player p){
    //Online Player
        if (p.hasPermission("mc.whitechat")){
            return ChatColor.WHITE;
        }
        else{
            return ChatColor.GRAY;
        }

    /*
    //Offline Player
        String prefix = getPlayerPrefix(p);
        if (prefix == null || prefix.equals("")){
            return ChatColor.GRAY;
        }
        else{
            return ChatColor.WHITE;
        }
        */
    }

    public static void removeCachedPlayer(OfflinePlayer player){
        removeCachedPlayer(player.getUniqueId());
    }

    public static void removeCachedPlayer(UUID uuid){
        cachedPrefixes.remove(uuid);
        cachedGroupNames.remove(uuid);
    }

    public static Group getGroup(String groupName){
        return LPAPI.getGroupManager().getGroup(groupName);
    }

    public static HashSet<Group> getAllGroups(){
        return new HashSet<>(LPAPI.getGroupManager().getLoadedGroups());
    }

    public static @NotNull HashSet<String> getAllGroupPrefixes(){
        HashSet<String> prefixes = new HashSet<>();
        for (Group g : LPAPI.getGroupManager().getLoadedGroups()){
            prefixes.add(getGroupPrefix(g));
        }
        return prefixes;
    }

    public static @NotNull HashSet<String> getAllGroupNames(){
        HashSet<String> names = new HashSet<>();
        for (Group g : LPAPI.getGroupManager().getLoadedGroups()){
            names.add(g.getName());
        }
        return names;
    }

    public static @NotNull HashMap<String, String> getAllGroupNamesAndPrefixes(){
        HashMap<String, String> nameAndPrefixes = new HashMap<>();
        for (Group g : LPAPI.getGroupManager().getLoadedGroups()){
            nameAndPrefixes.put(g.getName(), ChatColor.translateAlternateColorCodes('&', g.getCachedData().getMetaData().getPrefix()));
        }
        return nameAndPrefixes;
    }

    public static int getGroupWeight(String groupName){
        Group g = LPAPI.getGroupManager().getGroup(groupName);
        if (g == null || g.getWeight().isEmpty()) return -1;
        return g.getWeight().getAsInt();

    }

    public static String getGroupPrefix(Group group){
        return group.getCachedData().getMetaData().getPrefix();
    }


}
