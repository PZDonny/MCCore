package net.donnypz.mccore.utils.toasts;

import net.donnypz.mccore.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Toast {
    Material icon;
    private final String message;
    private final ToastType type;
    private final Background background;

    public Toast(Material icon, String message, ToastType type, Background background){
        this.icon = icon;
        this.message = message;
        this.type = type;
        this.background = background;
    }

    public void showToPlayer(Player player){
        NamespacedKey key = new NamespacedKey(Core.getInstance(), UUID.randomUUID().toString());
        createAdvancement(key);
        grantAdvancement(player, key);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> revokeAdvancement(player, key), 10);
    }

    private String generateJson(){
        return "{\n" +
                "    \"criteria\": {\n" +
                "        \"trigger\": {\n" +
                "            \"trigger\": \"minecraft:impossible\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"display\": {\n" +
                "        \"icon\": {\n" +
                "            \"id\": \"minecraft:" + icon.toString().toLowerCase() + "\"\n" +
                "        },\n" +
                "        \"title\": {\n" +
                "            \"text\": \"" + message.replace("|", "\n") + "\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "            \"text\": \"\"\n" +
                "        },\n" +
                "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/"+background.name().toLowerCase()+".png\",\n" +
                "        \"frame\": \"" + type.toString().toLowerCase() + "\",\n" +
                "        \"announce_to_chat\": false,\n" +
                "        \"show_toast\": true,\n" +
                "        \"hidden\": true\n" +
                "    },\n" +
                "    \"requirements\": [\n" +
                "        [\n" +
                "            \"trigger\"\n" +
                "        ]\n" +
                "    ]\n" +
                "}";
    }

    private void createAdvancement(NamespacedKey key){
        Bukkit.getUnsafe().loadAdvancement(key, generateJson());
    }

    private void grantAdvancement(Player player, NamespacedKey key){
        if (player.getAdvancementProgress(Bukkit.getAdvancement(key)).isDone()){
            revokeAdvancement(player, key);
        }
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("trigger");
    }

    private void revokeAdvancement(Player player, NamespacedKey key){
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("trigger");
        Bukkit.getUnsafe().removeAdvancement(key);
    }

    public Material getIcon(){
        return icon;
    }

    public Background getBackground() {
        return background;
    }

    public ToastType getType(){
        return type;
    }

    public String getMessage(){
        return message;
    }

}
