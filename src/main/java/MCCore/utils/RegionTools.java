package MCCore.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;

public class RegionTools {

    public static boolean isPlayerinRegion(Player p, String region){
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion rg = container.get(BukkitAdapter.adapt(p.getWorld())).getRegion(region);
        if (rg != null && rg.contains(BlockVector3.at(x, y, z))){
            return true;
        }
        return false;
    }
}
