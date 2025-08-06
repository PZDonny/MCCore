package net.donnypz.mccore.listener;

import net.donnypz.mccore.Core;
import net.donnypz.mccore.utils.misc.RankUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChatListener implements Listener {

    private static final HashMap<UUID, Long> cooldown = new HashMap<>();


    public static void removeCooldown(Player p){
        cooldown.remove(p.getUniqueId());
    }

    private static boolean hasChatCooldown(Player p){
        return cooldown.containsKey(p.getUniqueId()) && System.currentTimeMillis() < cooldown.get(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerChat(AsyncChatEvent e){
        Player p = e.getPlayer();
        Component eventMessage = e.message();

        NamedTextColor color = RankUtils.getPlayerNamedTextColor(p);
        e.message(eventMessage.color(color));

        e.viewers().clear();
        e.viewers().addAll(p.getWorld().getPlayers());

        //With Rank Prefix
        if (Core.isLPAPIInstalled){
            Component prefix = RankUtils.getPlayerPrefix(p);
            e.renderer(new ChatRenderer() {
                @Override
                public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
                    return prefix
                            .append(p.displayName())
                            .append(Component.text(" >> ", NamedTextColor.GOLD, TextDecoration.BOLD)
                                    .append(e.message().decorationIfAbsent(TextDecoration.BOLD, TextDecoration.State.FALSE)));
                }
            });
        }

        //Without Rank Prefix
        else {
            e.renderer(new ChatRenderer() {
                @Override
                public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
                    return p.displayName()
                            .append(Component.text(" >> ", NamedTextColor.GOLD, TextDecoration.BOLD)
                                    .append(e.message().decorationIfAbsent(TextDecoration.BOLD, TextDecoration.State.FALSE)));
                }
            });
        }
    }
}
