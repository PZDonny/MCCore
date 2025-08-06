package net.donnypz.mccore.utils.ability;

import net.donnypz.mccore.utils.entity.PlayerUtils;
import net.donnypz.mccore.utils.ui.BossBarUtils;
import net.donnypz.mccore.version.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AbilityHandler {
    protected static final HashMap<UUID, UUID> entityAbilities = new HashMap<>();
    protected static final Map<UUID, UUID> abilityDamager = new HashMap<>();
    protected static final Map<UUID, Long> primaryCooldowns = new HashMap<>();
    protected static final Map<UUID, Long> secondaryCooldowns = new HashMap<>();
    protected static final HashSet<UUID> fallDamageResistant = new HashSet<>();
    protected static final Map<UUID, Long> abilitiesDisabled = new HashMap<>();

    public enum CooldownType{
        PRIMARY("primary", primaryCooldowns),
        SECONDARY("secondary", secondaryCooldowns);

        final Map<UUID, Long> cooldown;
        final String name;
        CooldownType(String name, Map<UUID, Long> cooldown){
            this.cooldown = cooldown;
            this.name = name;
        }

        Map<UUID, Long> getMap() {
            return cooldown;
        }

        public String getName(){
            return name;
        }
    }

//Entity Abilities
    public static void setEntityAbilityCaster(UUID abilityEntity, UUID caster){
        entityAbilities.put(abilityEntity, caster);
        new BukkitRunnable(){
            int iteration = 0;
            public void run(){
                //Stop checking if map doesn't contain uuid
                if (!entityAbilities.containsKey(abilityEntity)){
                    cancel();
                    return;
                }
                //Remove after 30s
                if (iteration == 60){
                    entityAbilities.remove(abilityEntity);
                    cancel();
                    return;
                }
                iteration++;
            }
        }.runTaskTimerAsynchronously(CoreAPI.getPlugin(), 0, 10);
    }

    public static void removeEntityAbility(Entity entity){
        entityAbilities.remove(entity.getUniqueId());
    }

    public static void removeEntityAbility(UUID abilityEntity){
        entityAbilities.remove(abilityEntity);
    }


    public static boolean isEntityAbility(Entity entity){
        return (entityAbilities.containsKey(entity.getUniqueId()));
    }

    public static UUID getEntityAbilityCaster(Entity entity){
        return entityAbilities.get(entity.getUniqueId());
    }


//Player Ability Damage
    public static void dealAbilityDamage(UUID victim, UUID attacker, double damageAmount){
        setAbilityDamager(victim, attacker);
        if (damageAmount > 0){
            Player vic = Bukkit.getPlayer(victim);
            if (vic != null){
                vic.damage(damageAmount);
            }
        }
    }

    public static void setAbilityDamager(UUID victim, UUID attacker){
        if (victim != null & attacker != null){
            Bukkit
                    .getScheduler()
                    .runTaskLater(CoreAPI.getPlugin(), () -> PlayerUtils.sendHPToPlayer(Bukkit.getPlayer(attacker), Bukkit.getPlayer(victim)), 2);
        }
        abilityDamager.put(victim, attacker);
    }

    public static UUID getAbilityDamager(UUID victim){
        return abilityDamager.get(victim);
    }

    public static void unsetAbilityDamager(UUID victim){
        abilityDamager.remove(victim);
    }


//Disabled Abilities
    public static void disableAbility(Player player, long seconds, @NotNull String prefix){
        disableAbility(player, seconds, BarColor.RED, BarStyle.SOLID, prefix);
    }

    public static void disableAbility(Player player, long seconds, BarColor color, BarStyle style, @NotNull String prefix){
        abilitiesDisabled.put(player.getUniqueId(), System.currentTimeMillis()+(seconds * 1000));
        String barTitle = prefix + ChatColor.RED+"Abilities Disabled: "+ChatColor.WHITE+seconds+" seconds";
        String barID = player.getUniqueId()+"_disabled";
        BossBarUtils.sendBossBarTimed(player, barID, barTitle, color, style, seconds);
    }

    public static void enableAbility(Player p){
        abilitiesDisabled.remove(p.getUniqueId());
        BossBarUtils.removeFromBar(p, p.getUniqueId()+"_disabled");
    }

    public static long getAbilityDisabledTimestamp(Player p){
        if (!isAbilityDisabled(p)) return 0;
        return abilitiesDisabled.get(p.getUniqueId());
    }

    public static long getAbilityDisabledSecondsLeft(Player p){
        if (!isAbilityDisabled(p)) return 0;
        return (abilitiesDisabled.get(p.getUniqueId())-System.currentTimeMillis())/1000;
    }

    public static boolean isAbilityDisabled(Player p){
        if (!abilitiesDisabled.containsKey(p.getUniqueId()) || abilitiesDisabled.get(p.getUniqueId()) < System.currentTimeMillis()){
            return false;
        }
        return true;
    }

    public static boolean isAbilityDisabled(Player p, String prefix){
        if (!abilitiesDisabled.containsKey(p.getUniqueId()) || abilitiesDisabled.get(p.getUniqueId()) < System.currentTimeMillis()){
            return false;
        }
        p.sendMessage(prefix+ChatColor.RED+"Your ability is currently disabled! Please wait "+ChatColor.YELLOW+getAbilityDisabledSecondsLeft(p)+ChatColor.RED+" second(s)!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        return true;
    }


//Cooldowns
    public static void setPlayerCooldown(Player player, long seconds, CooldownType cooldownType, String abilityName){
        setPlayerCooldown(player, seconds, cooldownType, BarColor.YELLOW, BarStyle.SEGMENTED_20, abilityName);
    }

    public static void setPlayerCooldown(Player player, long seconds, CooldownType cooldownType, BarColor color, BarStyle style, String abilityName){
        cooldownType.getMap().put(player.getUniqueId(), System.currentTimeMillis()+(seconds * 1000));
        abilityName = abilityName+ ChatColor.GRAY+" Cooldown: "+ChatColor.YELLOW+seconds+" seconds";
        String barID = player.getUniqueId()+"_"+cooldownType.getName();
        BossBarUtils.sendBossBarTimed(player, barID, abilityName, color, style, seconds);
    }

    public static void removePlayerCooldown(Player player, CooldownType cooldownType){
        removePlayerCooldown(player, cooldownType, null);
    }

    public static void removePlayerCooldown(Player player, CooldownType cooldownType, String message){
        if (cooldownType.getMap().containsKey(player.getUniqueId())){
            if (message != null && !message.isBlank()){
                player.sendMessage(message);
            }
            cooldownType.getMap().remove(player.getUniqueId());
            BossBarUtils.removeFromBar(player, player.getUniqueId()+"_"+cooldownType.getName());
        }
    }

    public static long getCooldownTimestamp(Player player, CooldownType cooldownType){
        if (!isOnCooldown(player, cooldownType)) return 0;
        return cooldownType.getMap().get(player.getUniqueId());
    }

    public static long getCooldownSecondsLeft(Player player, CooldownType cooldownType){
        if (!isOnCooldown(player, cooldownType)) return 0;
        return (cooldownType.getMap().get(player.getUniqueId())-System.currentTimeMillis())/1000;
    }

    public static boolean isOnCooldown(Player player, CooldownType cooldownType){
        if (!cooldownType.getMap().containsKey(player.getUniqueId()) || cooldownType.getMap().get(player.getUniqueId()) < System.currentTimeMillis()){
            return false;
        }
        return true;
    }

    public static boolean isOnCooldown(Player player, CooldownType cooldownType, String messagePrefix){
        String message = ChatColor.RED+"Your ability is currently on cooldown! Please wait "+ChatColor.YELLOW+getCooldownSecondsLeft(player, cooldownType)+ChatColor.RED+" second(s)!";
        return isOnCooldown(player, cooldownType, messagePrefix, message);
    }

    public static boolean isOnCooldown(Player player, CooldownType cooldownType, String messagePrefix, @NotNull String message){
        if (!cooldownType.getMap().containsKey(player.getUniqueId()) || cooldownType.getMap().get(player.getUniqueId()) < System.currentTimeMillis()){
            return false;
        }
        player.sendMessage(messagePrefix+message);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        return true;
    }


//Fall Damage Resistance
    public static void makePlayerFallDamageResistant(Player player, boolean autoRemove){
        fallDamageResistant.add(player.getUniqueId());
        if (!autoRemove){
            return;
        }
        new BukkitRunnable(){
            public void run(){
                if (player.isOnGround() && fallDamageResistant.contains(player.getUniqueId())){
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            removeFallDamageResistance(player);
                        }
                    }.runTaskLater(CoreAPI.getPlugin(), 1);
                    cancel();
                }
            }
        }.runTaskTimer(CoreAPI.getPlugin(), 3, 2);
    }


    public static void removeFallDamageResistance(Player player){
        fallDamageResistant.remove(player.getUniqueId());
    }

    public static boolean isFallDamageResistant(Player player){
        return fallDamageResistant.contains(player.getUniqueId());
    }

    public static void removePlayerData(UUID playerUUID){
        abilityDamager.remove(playerUUID);
        primaryCooldowns.remove(playerUUID);
        secondaryCooldowns.remove(playerUUID);
        fallDamageResistant.remove(playerUUID);
        abilitiesDisabled.remove(playerUUID);
    }

    public static void removePlayerData(Collection<Player> players){
        for (Player p : players){
            removePlayerData(p.getUniqueId());
        }
    }


}
