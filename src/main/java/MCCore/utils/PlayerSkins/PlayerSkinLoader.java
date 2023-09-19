package MCCore.utils.PlayerSkins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class PlayerSkinLoader {

    //Cannot be instantiated
    private PlayerSkinLoader(){

    }
    static Map<String, PlayerSkin> allSkins = new HashMap<>();

    public static PlayerSkin getSkin(String skinName){
        return allSkins.get(skinName);
    }

    public static PlayerSkin getRandomSkin(){
        Random r = new Random();
        ArrayList<PlayerSkin> list = new ArrayList<>(allSkins.values());
        return list.get(r.nextInt(allSkins.size()));
    }
}
