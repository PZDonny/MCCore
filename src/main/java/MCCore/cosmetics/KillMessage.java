package MCCore.cosmetics;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class KillMessage extends Cosmetic{


    private String messagePrefix = "";
    private String messageSuffix = "";

    public KillMessage(String killMessageName){
        super(killMessageName);
    }

    public String getFullMessage(Player killer, Player victim){
        return ChatColor.WHITE+"☠ "+ChatColor.GOLD+killer.getDisplayName()+ ChatColor.YELLOW+" "+messagePrefix+" "+ChatColor.AQUA+victim.getDisplayName()+" "+ChatColor.YELLOW+messageSuffix;
    }

    public String getFullMessage(String killerName, String victimName){
        return ChatColor.WHITE+"☠ "+ChatColor.GOLD+killerName+ ChatColor.YELLOW+" "+messagePrefix+" "+ChatColor.AQUA+victimName+" "+ChatColor.YELLOW+messageSuffix;
    }

    public KillMessage prefix(String messagePrefix) {
        this.messagePrefix = messagePrefix;
        return this;
    }

    public KillMessage suffix(String messageSuffix) {
        this.messageSuffix = messageSuffix;
        return this;
    }

}
