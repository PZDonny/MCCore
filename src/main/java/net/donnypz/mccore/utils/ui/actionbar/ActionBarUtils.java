package net.donnypz.mccore.utils.ui.actionbar;

import net.donnypz.mccore.Core;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ActionBarUtils {

    private static final HashMap<UUID, Long> lastActionBarCancels = new HashMap<>();

    private static boolean isCancelled(Player player, long startTime){
        if (lastActionBarCancels.containsKey(player.getUniqueId())){
            return startTime < lastActionBarCancels.get(player.getUniqueId());
        }
        return false;
    }

//Timed Actionbar
    public static void sendActionBarTimer(Player player, String message, int durationInTicks, boolean showTime){
        sendActionBarTimer(player, Component.text(message), durationInTicks, showTime);
    }

    public static void sendActionBarTimer(Player player, Component message, int durationInTicks, boolean showTime){
        sendActionBarTimer(player, new ActionBarMessage(message), durationInTicks, showTime);
    }

    public static void sendActionBarTimer(Player player, ActionBarMessage message, int durationInTicks, boolean showTime){
        if (durationInTicks <= 0) return;
        long startTime = System.currentTimeMillis();
        new BukkitRunnable(){
            int iteration = durationInTicks;
            public void run(){
                if (!player.isOnline() || ActionBarUtils.isCancelled(player, startTime)){
                    cancel();
                    return;
                }
                if (iteration <= 0){
                    player.sendActionBar(Component.text());
                    cancel();
                    return;
                }

                if (iteration == durationInTicks || iteration % 10 == 0){
                    if (showTime){
                        player.sendActionBar(message.getMessage().append((Component.text(iteration))));
                    }
                    else{
                        player.sendActionBar(message.getMessage());
                    }
                }
                iteration--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 1);
    }

    public static void sendActionBarTimer(Player player, String prefix, String suffix, int duration){
        sendActionBarTimer(player, Component.text(prefix), Component.text(suffix), duration);
    }

    public static void sendActionBarTimer(Player player, Component prefix, Component suffix, int duration){
        if (duration <= 0) return;
        long startTime = System.currentTimeMillis();
        new BukkitRunnable(){
            int iteration = duration;
            public void run(){
                if (!player.isOnline() || ActionBarUtils.isCancelled(player, startTime)){
                    cancel();
                    return;
                }

                if (iteration == 0){
                    player.sendActionBar(Component.text());
                    cancel();
                    return;
                }

                player.sendActionBar(prefix.append(Component.text(iteration)).append(suffix));
                iteration--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20);
    }

    public static void sendActionBarTimer(Collection<Player> players, String message, int duration, boolean showTime){
        for (Player p : players){
            sendActionBarTimer(p, message, duration, showTime);
        }
    }

    public static void sendActionBarTimer(Collection<Player> players, String prefix, String suffix, int duration){
        for (Player p : players){
            sendActionBarTimer(p, prefix, suffix, duration);
        }
    }


    public static void sendActionBarTimer(Collection<Player> players, Component prefix, Component suffix, int duration){
        for (Player p : players){
            sendActionBarTimer(p, prefix, suffix, duration);
        }
    }

//Generating Action Bar
    public static void sendActionBarGenerated(Player player, String message, int stayAfterTicks){
        sendActionBarGenerated(player, LegacyComponentSerializer.legacyAmpersand().deserialize(message), stayAfterTicks);
    }

    public static void sendActionBarGenerated(Player player, String message, Sound sound, float volume, float pitch, int stayAfterTicks){
        sendActionBarGenerated(player, LegacyComponentSerializer.legacyAmpersand().deserialize(message), sound, volume, pitch, stayAfterTicks);
    }

    public static void sendActionBarGenerated(Player player, Component message, int stayAfterTicks){
        generateActionBar(player, LegacyComponentSerializer.legacySection().serialize(message), "", null, 0, 0, 0, System.currentTimeMillis(), stayAfterTicks, null);
    }

    public static void sendActionBarGenerated(Player player, Component message, Sound sound, float volume, float pitch, int stayAfterTicks){
        generateActionBar(player, LegacyComponentSerializer.legacySection().serialize(message), "", sound, volume, pitch, 0, System.currentTimeMillis(), stayAfterTicks, null);
    }

    public static void sendActionBarGenerated(Player player, ActionBarMessage message, int stayAfterTicks){
        generateActionBar(player, LegacyComponentSerializer.legacySection().serialize(message.getMessage().asComponent()), "", null, 0, 0, 0, System.currentTimeMillis(), stayAfterTicks, message);
    }

    public static void sendActionBarGenerated(Player player, ActionBarMessage message, Sound sound, float volume, float pitch, int stayAfterTicks){
        generateActionBar(player, LegacyComponentSerializer.legacySection().serialize(message.getMessage().asComponent()), "", sound, volume, pitch, 0, System.currentTimeMillis(), stayAfterTicks, message);
    }


    private static void generateActionBar(Player player, String message, String generatedMessage, Sound sound, float volume, float pitch, int position, long startTime, int stayAfterTicks, ActionBarMessage actionBarMessage){
        if (!player.isOnline() || isCancelled(player, startTime)){
            return;
        }
        List<Character> delayers = new ArrayList<>();
        delayers.add('.');
        delayers.add('!');
        delayers.add('?');
        delayers.add(',');
        delayers.add(';');
        delayers.add(':');
        int delay = 2;
        if (position > 0 && delayers.contains(message.charAt(position-1))){
            delay = 3;
        }
    //Skip Over Chat Colors
        if (message.charAt(position) == '§'){
            String currentMSG = generatedMessage+message.charAt(position)+message.charAt(position+1);
            if (delay == 3){
                new BukkitRunnable(){
                    public void run(){
                        player.playSound(player, sound, volume, pitch-0.2f);
                        generateActionBar(player, message, currentMSG, sound, volume, pitch, position+2, startTime, stayAfterTicks, actionBarMessage);
                    }
                }.runTaskLater(Core.getInstance(), delay);
            }
            else if (position+2 < message.length()){
                generateActionBar(player, message, currentMSG, sound, volume, pitch, position+2, startTime, stayAfterTicks, actionBarMessage);
            }
            return;
        }

        int finalDelay = delay;
        new BukkitRunnable(){
            String currentMSG = generatedMessage;
            public void run(){
                currentMSG = currentMSG+(message.charAt(position));
                player.sendActionBar(Component.text(currentMSG));
                if (sound != null){
                    if (finalDelay == 3) player.playSound(player, sound, volume, pitch-0.2f);
                    else player.playSound(player, sound, volume, pitch);
                }
                if (position+1 < message.length()){
                    generateActionBar(player, message, currentMSG, sound, volume, pitch, position+1, startTime, stayAfterTicks, actionBarMessage);
                }
                else if (stayAfterTicks > 0){
                    if (actionBarMessage != null){
                        sendActionBarTimer(player, actionBarMessage, stayAfterTicks, false);
                    }
                    else{
                        sendActionBarTimer(player, currentMSG, stayAfterTicks, false);
                    }
                }
            }
        }.runTaskLater(Core.getInstance(), delay);
    }

//Generating Action Bar with Background
    public static void sendActionBarGeneratedBG(Player player, String message, int stayAfterTicks){
        sendActionBarGeneratedBG(player, LegacyComponentSerializer.legacyAmpersand().deserialize(message), null, 0, 0, stayAfterTicks);
    }

    public static void sendActionBarGeneratedBG(Player player, String message, Sound sound, float volume, float pitch, int stayAfterTicks){
        sendActionBarGeneratedBG(player, LegacyComponentSerializer.legacyAmpersand().deserialize(message), sound, volume, pitch, stayAfterTicks);
    }

    public static void sendActionBarGeneratedBG(Player player, Component message, int stayAfterTicks){
        generateActionBarBG(player, LegacyComponentSerializer.legacySection().serialize(message), "", null, 0, 0, 0, System.currentTimeMillis(), stayAfterTicks, null);
    }

    public static void sendActionBarGeneratedBG(Player player, Component message, Sound sound, float volume, float pitch, int stayAfterTicks){
        generateActionBarBG(player, LegacyComponentSerializer.legacySection().serialize(message), "", sound, volume, pitch, 0, System.currentTimeMillis(), stayAfterTicks, null);
    }

    public static void sendActionBarGeneratedBG(Player player, ActionBarMessage message, int stayAfterTicks){
        generateActionBarBG(player, LegacyComponentSerializer.legacySection().serialize(message.getMessage().asComponent()), "", null, 0, 0, 0, System.currentTimeMillis(), stayAfterTicks, message);
    }
    public static void sendActionBarGeneratedBG(Player player, ActionBarMessage message, Sound sound, float volume, float pitch, int stayAfterTicks){
        generateActionBarBG(player, LegacyComponentSerializer.legacySection().serialize(message.getMessage().asComponent()), "", sound, volume, pitch, 0, System.currentTimeMillis(), stayAfterTicks, message);
    }

    private static void generateActionBarBG(Player player, String message, String generatedMessage, Sound sound, float volume, float pitch, int position, long startTime, int stayAfterTicks, ActionBarMessage actionBarMessage){
        if (!player.isOnline() || ActionBarUtils.isCancelled(player, startTime)){
            return;
        }
        List<Character> delayers = new ArrayList<>();
        delayers.add('.');
        delayers.add('!');
        delayers.add('?');
        delayers.add(',');
        delayers.add(';');
        int delay = 2;
        if (position > 0 && delayers.contains(message.charAt(position-1))){
            delay = 3;
        }

    //Skip Over ChatColors
        if (message.charAt(position) == '§'){
            String currentMSG = generatedMessage+message.charAt(position)+message.charAt(position+1);
            if (delay == 3){
                new BukkitRunnable(){
                    public void run(){
                        player.playSound(player, sound, volume, pitch-0.2f);
                        generateActionBarBG(player, message, currentMSG, sound, volume, pitch, position+2, startTime, stayAfterTicks, actionBarMessage);
                    }
                }.runTaskLater(Core.getInstance(), delay);
            }
            else if (position+2 < message.length()){
                generateActionBarBG(player, message, currentMSG, sound, volume, pitch, position+2, startTime, stayAfterTicks, actionBarMessage);
            }
            return;
        }

        int finalDelay = delay;
        new BukkitRunnable(){
            String currentMSG = generatedMessage;
            public void run(){
                currentMSG = currentMSG+(message.charAt(position));
                String subString = message.substring(position+1);
                //Fix issues with ColorCodes in subString
                for (int i = 0; i < subString.length(); i++){
                    if (subString.charAt(i) == '§'){
                        subString = subString.substring(0, i+1) + "8"
                                + subString.substring(i+2);
                    }
                }
                player.sendActionBar(Component.text(currentMSG).append(Component.text(subString, NamedTextColor.DARK_GRAY)));
                if (sound != null){
                    if (finalDelay == 3){
                        player.playSound(player, sound, volume, pitch-0.2f);
                    }
                    else{
                        player.playSound(player, sound, volume, pitch);
                    }
                }
                if (position+1 < message.length()){
                    generateActionBarBG(player, message, currentMSG, sound, volume, pitch, position+1, startTime, stayAfterTicks, actionBarMessage);
                }
                else if (stayAfterTicks > 0){
                    if (actionBarMessage != null){
                        sendActionBarTimer(player, actionBarMessage, stayAfterTicks, false);
                    }
                    else{
                        sendActionBarTimer(player, currentMSG, stayAfterTicks, false);
                    }
                }
            }
        }.runTaskLater(Core.getInstance(), delay);
    }


    public static void cancelActionBar(Player player){
        if (player.isOnline()){
            lastActionBarCancels.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    public static void removePlayerCancel(Player player){
        lastActionBarCancels.remove(player.getUniqueId());
    }
}
