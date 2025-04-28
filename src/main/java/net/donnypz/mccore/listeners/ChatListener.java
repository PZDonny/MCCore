package net.donnypz.mccore.listeners;

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

    //static YamlConfiguration filter;
    //static final Collection<String> strictWords;
    //static final Collection<String> softWords;
    private static final HashMap<UUID, Long> cooldown = new HashMap<>();

    static {
        //filter = YamlConfiguration.loadConfiguration(new InputStreamReader(Core.getInstance().getResource("chatfilter.yml")));
        //strictWords = Collections.unmodifiableCollection(filter.getStringList("strict"));
        //softWords = Collections.unmodifiableCollection(filter.getStringList("soft"));
    }

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

        //Chat Filtering
        /*String serializedMessage = PlainTextComponentSerializer.plainText().serialize(eventMessage);
        StringBuilder builder = new StringBuilder();
        String[] message = serializedMessage.split(" ");


        filtering:
        for (String s : message) {
            String word = s;

            if (word.length() >= 3) {
                String decodedWord = word
                        .replaceAll("[^0134a-zA-z@$\\s]|[\\^`\\[\\]_\\\\]", "")
                        .replace("0", "o")
                        .replace("1", "i")
                        .replace("3", "e")
                        .replace("4", "a");

                //Hard/Strict Filter
                for (String strictWord : strictWords) {
                    if (decodedWord.toLowerCase().contains(strictWord)) {
                        builder.append("*".repeat(decodedWord.length()));
                        continue filtering;
                    }
                }

                //Soft Filter
                for (String softWord : softWords) {
                    if (decodedWord.equalsIgnoreCase(softWord)) {
                        builder.append("*".repeat(decodedWord.length()));
                        continue filtering;
                    }
                }

            }
            word = word.replace("%", "%%");
            builder.append(word)
                    .append(" ");
        }*/
        NamedTextColor color = RankUtils.getPlayerNamedTextColor(p);
        //e.message(Component.text(builder.toString(), color));
        e.message(eventMessage.color(color));

        e.viewers().clear();
        e.viewers().addAll(p.getWorld().getPlayers());

        //With Rank Prefix
        if (Core.isLuckPermsInstalled()){
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
