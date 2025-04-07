package net.donnypz.mccore.events;

import net.donnypz.mccore.minigame.arenaManager.Arena;
import org.bukkit.entity.Player;

public class PlayerSpectateArenaEvent extends ArenaEvent {

    private final Player player;
    private final boolean wasArenaPlayer;

    public PlayerSpectateArenaEvent(Arena arena, Player player){
        super(arena);
        this.player = player;
        this.wasArenaPlayer = arena.getStartPlayers().contains(player);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean wasArenaPlayer() {
        return wasArenaPlayer;
    }
}
