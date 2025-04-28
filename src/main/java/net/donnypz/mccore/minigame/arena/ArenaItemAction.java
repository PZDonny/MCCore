package net.donnypz.mccore.minigame.arena;

import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.mccore.utils.item.ItemUtils;
import net.donnypz.mccore.utils.entity.PlayerUtils;
import net.donnypz.mccore.utils.inventory.gui.ChestGUI;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.donnypz.mccore.utils.item.ItemAction;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

class ArenaItemAction {

    static{
        new ItemAction("core:spectate_players").setAnyClickAction(result -> {
            Arena arena = result.arena();
            if (arena == null){
                return;
            }
            Player p = result.player();
            if (!arena.isPlayerSpectating(p, false)){
                return;
            }
            openSpectateGUI((int) Math.ceil((double) arena.getPlayingPlayers().size()/9), p, arena);
        });
    }


    private static void openSpectateGUI(int rows, Player p, Arena arena){
        if (rows == 0){
            p.sendMessage(Component.text("There aren't any players for you to spectate!", NamedTextColor.RED));
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return;
        }

        ChestGUI gui = new ChestGUI(rows, Component.text("Spectate a Player"));
        int slot = 0;
        PlayerScoreboard sb = ScoreboardUtils.getPlayerScoreboard(p);
        List<Player> nonTeammates = new ArrayList<>();

        Team playerTeam;
        if (sb != null){
            playerTeam = sb.getPlayerTeam();
        }
        else{
            playerTeam = null;
        }

        if (playerTeam != null){
            for (Player o : arena.getPlayingPlayers()){
                if (o.getUniqueId() == p.getUniqueId()){
                    continue;
                }
                if (!o.isConnected()){
                    continue;
                }

                if (playerTeam.hasPlayer(o)){
                    createPlayerInGUI(gui, slot, o, arena, NamedTextColor.GREEN);
                    slot++;
                }
                else{
                    nonTeammates.add(o);
                }

            }

            for (Player o : nonTeammates){
                createPlayerInGUI(gui, slot, o, arena, NamedTextColor.WHITE);
                slot++;
            }
        }
        else{
            for (Player o : arena.getPlayingPlayers()){
                if (o.getUniqueId() == p.getUniqueId()){
                    continue;
                }
                if (!o.isConnected()){
                    continue;
                }

                createPlayerInGUI(gui, slot, o, arena, NamedTextColor.WHITE);
                slot++;
            }
        }



        gui.openToPlayer(p);
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1.5f);
    }

    private static void createPlayerInGUI(ChestGUI gui, int slot, Player activePlayer, Arena arena, TextColor textColor){
        ItemStack skull = PlayerUtils.getPlayerHead(activePlayer);
        ItemUtils.setDisplayName(skull, activePlayer.displayName().color(textColor));
        ItemUtils.setLore(skull, List.of(Component.text("Click to spectate this player", NamedTextColor.GRAY)));

        new GUIItem(gui, slot, skull, click ->{
            Player clicker = (Player) click.getWhoClicked();
            if (!activePlayer.isConnected() || arena.getPlayingPlayers().contains(activePlayer)){
                clicker.sendMessage(Component.text("Failed to spectate player!", NamedTextColor.RED));
                clicker.closeInventory();
                clicker.playSound(clicker, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
                return;
            }
            clicker.sendMessage(Component.text("You are now spectating ", NamedTextColor.YELLOW).append(activePlayer.displayName().color(textColor)));
            clicker.teleport(activePlayer.getLocation(), TeleportFlag.EntityState.RETAIN_PASSENGERS);
        });
    }
}
