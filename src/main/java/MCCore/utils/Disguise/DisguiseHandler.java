package MCCore.utils.Disguise;

import MCCore.Core;
import MCCore.utils.PlayerSkins.PlayerSkin;
import MCCore.utils.PlayerSkins.PlayerSkinLoader;
import dev.iiahmed.disguise.*;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class DisguiseHandler {

    private static final DisguiseProvider provider = DisguiseManager.getProvider();
    private static final Set<DisguisePlayer> disguisedPlayers = new HashSet<>();


    public static void disguisePlayer(Player p, String nickname, PlayerSkin skin){
        if (isDisguised(p)) return;
        if (nickname == null){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Failed to disguise player! Invalid nickname!");
            return;
        }
        if (nickname.length() > 16){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Failed to disguise player! Nickname length is greater than 16 characters");
            return;
        }
        Disguise.Builder builder = Disguise.builder()
                .setName(nickname, false);


        if (skin != null){
            builder.setSkin(skin.getTexture(), skin.getSignature());
        }

        provider.disguise(p, builder.build());
        new BukkitRunnable(){
            public void run(){
                disguisedPlayers.add(new DisguisePlayer(p, nickname, provider.getInfo(p), skin));
            }
        }.runTaskLater(Core.getInstance(), 1);

        p.displayName(Component.text(nickname));
    }

    public static void disguisePlayerRandom(Player p){
        if (isDisguised(p)) return;
        String randomNickname = "randomNick";
        Disguise.Builder builder = Disguise.builder().setName(randomNickname, false);


        PlayerSkin skin = PlayerSkinLoader.getRandomSkin();
        if (skin != null){
            builder.setSkin(skin.getTexture(), skin.getSignature());
        }

        provider.disguise(p, builder.build());
        disguisedPlayers.add(new DisguisePlayer(p, randomNickname, provider.getInfo(p), skin));
        p.displayName(Component.text(randomNickname));
    }

    public static void disguisePlayerRandom(Player p, PlayerSkin skin){
        if (isDisguised(p)) return;
        String randomNickname = "randomNick";
        Disguise.Builder builder = Disguise.builder().setName("randomnick", false);

        if (skin != null){
            builder.setSkin(skin.getTexture(), skin.getSignature());
        }

        provider.disguise(p, builder.build());
        disguisedPlayers.add(new DisguisePlayer(p, randomNickname, provider.getInfo(p), skin));
        p.displayName(Component.text(randomNickname));
    }

    public static void undisguisePlayer(Player p){
        DisguisePlayer dPlayer = null;
        for (DisguisePlayer player : disguisedPlayers){
            if (player.getPlayer().equals(p)){
                dPlayer = player;
            }
        }
        if (dPlayer != null){
            disguisedPlayers.remove(dPlayer);
            dPlayer.remove();
        }

        if (isDisguised(p)){
            PlayerInfo info = getPlayerInfo(p);
            p.displayName(Component.text(info.getName()));
            provider.undisguise(p);
        }

    }


    public static boolean isDisguised(Player p){
        return provider.isDisguised(p);
    }

    public static PlayerInfo getPlayerInfo(Player p){
        return provider.getInfo(p);
    }

    public static PlayerInfo getPlayerInfo(String nickname){
        for (DisguisePlayer player : disguisedPlayers){
            if (player.getNickname().equals(nickname)){
                return player.getPlayerInfo();
            }
        }
        return null;
    }

    public static String getRealName(Player player){
        if (!isDisguised(player)){
            return player.getName();
        }
        return getPlayerInfo(player).getName();
    }

}
