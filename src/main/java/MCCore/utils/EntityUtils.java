package MCCore.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EntityUtils {
    public static void removePassengers(Entity vehicle){
        if (vehicle == null || vehicle.isDead() || vehicle.isEmpty()){
            return;
        }
        for (Entity passenger : new ArrayList<>(vehicle.getPassengers())){
            removePassengers(passenger);
            vehicle.removePassenger(passenger);
            if (!(passenger instanceof Player)){
                passenger.remove();
            }
        }
        vehicle.remove();
    }
}
