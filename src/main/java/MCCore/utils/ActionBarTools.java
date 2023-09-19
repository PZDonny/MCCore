package MCCore.utils;

import MCCore.Core;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActionBarTools {

//Timed Actionbar
    //Single Player
    public static void sendActionBarTimer(Player p, String message, int duration, boolean showTime){
        if (duration <= 0) return;
        new BukkitRunnable(){
            int iteration = duration;
            public void run(){
                if (iteration == 0){
                    p.sendActionBar(Component.text());
                    cancel();
                    return;
                }
                if (showTime) p.sendActionBar(Component.text(message+iteration));
                else p.sendActionBar(Component.text(message));
                iteration--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20);
    }

    //Multiple Players
    public static void sendActionBarTimer(Collection<Player> players, String message, int duration, boolean showTime){
        if (duration <= 0) return;
        new BukkitRunnable(){
            int iteration = duration;
            public void run(){
                if (iteration == 0){
                    for (Player p : players){
                        p.sendActionBar(Component.text());
                    }
                    cancel();
                    return;
                }
                for (Player p : players){
                    if (showTime) p.sendActionBar(Component.text(message+iteration));
                    else p.sendActionBar(Component.text(message));
                }
                iteration--;
            }
        }.runTaskTimer(Core.getInstance(), 0, 20);
    }

    //Generating Action Bar
    public static void sendActionBarGenerated(Player p, String message){
        generateActionBar(p, message, "", null, 0, 0, 0);
    }

    public static void sendActionBarGenerated(Player p, String message, Sound sound, float volume, float pitch){
        generateActionBar(p, message, "", sound, volume, pitch, 0);
    }


    private static void generateActionBar(Player p, String message, String generatedMessage, Sound sound, float volume, float pitch, int position){
        List<Character> delayers = new ArrayList<>();
        delayers.add('.');
        delayers.add('!');
        delayers.add('?');
        delayers.add(',');
        delayers.add(';');
        int delay = 2;
        if (position > 0 && delayers.contains(message.charAt(position-1))) delay = 3;
        if (message.charAt(position) == '§'){
            String currentMSG = generatedMessage+message.charAt(position)+message.charAt(position+1);
            if (delay == 3){
                new BukkitRunnable(){
                    public void run(){
                        p.playSound(p, sound, volume, pitch-0.2f);
                        generateActionBar(p, message, currentMSG, sound, volume, pitch, position+2);
                    }
                }.runTaskLater(Core.getInstance(), delay);
            }
            else if (position+2 < message.length()) generateActionBar(p, message, currentMSG, sound, volume, pitch, position+2);
            return;
        }

        int finalDelay = delay;
        new BukkitRunnable(){
            String currentMSG = generatedMessage;
            public void run(){
                currentMSG = currentMSG+(message.charAt(position));
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(currentMSG));
                if (sound != null){
                    if (finalDelay == 3) p.playSound(p, sound, volume, pitch-0.2f);
                    else p.playSound(p, sound, volume, pitch);
                }
                if (position+1 < message.length()) generateActionBar(p, message, currentMSG, sound, volume, pitch, position+1);
            }
        }.runTaskLater(Core.getInstance(), delay);
    }

    //Generating Action Bar with Background
    public static void sendActionBarGeneratedBG(Player p, String message){
        generateActionBar(p, message, "", null, 0, 0, 0);
    }

    public static void sendActionBarGeneratedBG(Player p, String message, Sound sound, float volume, float pitch){
        generateActionBarBG(p, message, "", sound, volume, pitch, 0);
    }

    private static void generateActionBarBG(Player p, String message, String generatedMessage, Sound sound, float volume, float pitch, int position){
        List<Character> delayers = new ArrayList<>();
        delayers.add('.');
        delayers.add('!');
        delayers.add('?');
        delayers.add(',');
        delayers.add(';');
        int delay = 2;
        if (position > 0 && delayers.contains(message.charAt(position-1))) delay = 3;
        if (message.charAt(position) == '§'){
            String currentMSG = generatedMessage+message.charAt(position)+message.charAt(position+1);
            if (delay == 3){
                new BukkitRunnable(){
                    public void run(){
                        p.playSound(p, sound, volume, pitch-0.2f);
                        generateActionBarBG(p, message, currentMSG, sound, volume, pitch, position+2);
                    }
                }.runTaskLater(Core.getInstance(), delay);
            }
            else if (position+2 < message.length()) generateActionBarBG(p, message, currentMSG, sound, volume, pitch, position+2);
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

                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(currentMSG+ChatColor.DARK_GRAY+subString));
                if (sound != null){
                    if (finalDelay == 3) p.playSound(p, sound, volume, pitch-0.2f);
                    else p.playSound(p, sound, volume, pitch);
                }
                if (position+1 < message.length()) generateActionBarBG(p, message, currentMSG, sound, volume, pitch, position+1);
            }
        }.runTaskLater(Core.getInstance(), delay);
    }



    public static void clearActionBar(Player p){
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent());
    }
}
