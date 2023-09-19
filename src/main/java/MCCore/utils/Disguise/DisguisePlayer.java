package MCCore.utils.Disguise;

import MCCore.utils.PlayerSkins.PlayerSkin;
import dev.iiahmed.disguise.PlayerInfo;
import org.bukkit.entity.Player;

public class DisguisePlayer {
    Player player;
    String nickname;
    PlayerInfo playerInfo;
    PlayerSkin skin;

    DisguisePlayer(Player player, String nickname, PlayerInfo playerInfo, PlayerSkin skin){
        this.player = player;
        this.nickname = nickname;
        this.playerInfo = playerInfo;
        this.skin = skin;
    }

    public Player getPlayer() {
        return player;
    }

    public String getNickname() {
        return nickname;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public PlayerSkin getSkin() {
        return skin;
    }

    public void remove(){
        this.player = null;
        this.nickname = null;
        this.playerInfo = null;
        this.skin = null;
    }

}
