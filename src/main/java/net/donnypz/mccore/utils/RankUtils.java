package net.donnypz.mccore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    private static LuckPerms LPAPI = null;


    public static void registerLuckPerms(){
        LPAPI = LuckPermsProvider.get();
    }

    public static String getPlayerGroupName(Player p){
        User user = LPAPI.getPlayerAdapter(Player.class).getUser(p);
        return user.getPrimaryGroup();
    }

    public static String getPlayerPrefixString(Player p){
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
            return prefix;
        }
        else return getOfflinePlayerPrefixString(p.getUniqueId());
    }

    public static Component getPlayerPrefix(Player p){
        if (p.isOnline()){
            User user = LPAPI.getPlayerAdapter(Player.class).getUser(p);

            String prefixString = user.getCachedData().getMetaData().getPrefix();
            if (prefixString == null){
                Group group = LPAPI.getGroupManager().getGroup(user.getPrimaryGroup());
                if (group != null){
                    prefixString = group.getCachedData().getMetaData().getPrefix();
                }
            }

            return getPrefixComponent(prefixString);
        }
        else return getOfflinePlayerPrefix(p.getUniqueId());
    }



    public static String getOfflinePlayerPrefixString(UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()){
            return getPlayerPrefixString(p);
        }

        String prefix = LPAPI.getUserManager().loadUser(uuid).join().getCachedData().getMetaData().getPrefix();
        if (prefix == null){
            Group group = LPAPI.getGroupManager().getGroup(LPAPI.getUserManager().loadUser(uuid).join().getPrimaryGroup());
            if (group != null){
                prefix = group.getCachedData().getMetaData().getPrefix();
            }
        }

        if (prefix == null || prefix.equals("&7")){
            return prefix = "";
        }
        else{
            return prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        }
    }

    public static Component getOfflinePlayerPrefix(UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()){
            return getPlayerPrefix(p);
        }

        String prefixString = LPAPI.getUserManager().loadUser(uuid).join().getCachedData().getMetaData().getPrefix();
        if (prefixString == null){
            Group group = LPAPI.getGroupManager().getGroup(LPAPI.getUserManager().loadUser(uuid).join().getPrimaryGroup());
            if (group != null){
                prefixString = group.getCachedData().getMetaData().getPrefix();
            }
        }

        return getPrefixComponent(prefixString);
    }

    private static Component getPrefixComponent(String prefixString){
        if (prefixString == null){
            return Component.empty();
        }
        else if (prefixString.equals("&7")){
            return Component.text("", NamedTextColor.GRAY);
        }
        else{
            return LegacyComponentSerializer.legacySection().deserialize(prefixString);
        }
    }

    public static ChatColor getPlayerChatColor(Player p){
    //Online Player
        if (p.hasPermission("mc.ranked")){
            return ChatColor.WHITE;
        }
        else{
            return ChatColor.GRAY;
        }
    }

    public static NamedTextColor getPlayerNamedTextColor(Player p){
    //Online Player
        if (p.hasPermission("mc.ranked")){
            return NamedTextColor.WHITE;
        }
        else{
            return NamedTextColor.GRAY;
        }
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
