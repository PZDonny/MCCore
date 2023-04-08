package MCCore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class MongoTools {

//Object To List
    public static ArrayList<Object> locationToList(Location location){
        ArrayList<Object> newLocation = new ArrayList<>();
        newLocation.add(location.getX());
        newLocation.add(location.getY());
        newLocation.add(location.getZ());
        newLocation.add(location.getPitch());
        newLocation.add(location.getYaw());
        newLocation.add(location.getWorld().getName());
        return newLocation;
    }


//List to Object
    public static Location listToLocation(List<Object> list){
        double x = (double) list.get(0);
        double y = (double) list.get(1);
        double z = (double) list.get(2);
        double pitch = (double) list.get(3);
        double yaw = (double) list.get(4);
        String worldName = (String) list.get(5);
        World world = Bukkit.getWorld(worldName);

        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }
}
