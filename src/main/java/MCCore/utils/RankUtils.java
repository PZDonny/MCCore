package MCCore.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RankUtils {

    private static final LuckPerms LPAPI = LuckPermsProvider.get();



    public static String getPlayerPrefix(Player p){
        if (p.isOnline()){
            User user = LPAPI.getPlayerAdapter(Player.class).getUser(p);
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix == null) prefix = "";
            return prefix;
        }
        else return getOfflinePlayerPrefix(p.getUniqueId());
    }



    public static String getOfflinePlayerPrefix(UUID uuid){
        OfflinePlayer p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()){
            return getPlayerPrefix(p.getPlayer());
        }
        String prefix = LPAPI.getUserManager().loadUser(uuid).join().getCachedData().getMetaData().getPrefix();
        if (prefix == null) prefix = "";
        return prefix;
    }
}
