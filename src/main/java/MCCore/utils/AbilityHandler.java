package MCCore.utils;

import MCCore.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.*;

public class AbilityHandler {
    private static final HashMap<UUID, UUID> entityAbilities = new HashMap<>();
    private static final Map<UUID, UUID> abilityDamager = new HashMap<>();
    private static final Map<UUID, Long> primaryCooldowns = new HashMap<>();
    private static final Map<UUID, Long> secondaryCooldowns = new HashMap<>();
    private static final HashSet<UUID> fallDamageResistant = new HashSet<>();
    private static final Map<UUID, Long> abilitiesDisabled = new HashMap<>();

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
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 10);
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
        if (victim != null & attacker != null) PlayerTools.sendHPToPlayer(Bukkit.getPlayer(attacker), Bukkit.getPlayer(victim), 2);
        if (abilityDamager.containsKey(victim)) abilityDamager.replace(victim, attacker);
        if (!(abilityDamager.containsKey(victim))) abilityDamager.put(victim, attacker);
    }

    public static UUID getAbilityDamager(UUID victim){
        if (abilityDamager.containsKey(victim)) return abilityDamager.get(victim);
        return null;
    }

    public static void unsetAbilityDamager(UUID victim){
        abilityDamager.remove(victim);
    }


//Disabled Abilities
    public static void disableAbility(Player p, long seconds, @Nonnull String prefix){
        abilitiesDisabled.put(p.getUniqueId(), System.currentTimeMillis()+(seconds * 1000));
        String barTitle = prefix + ChatColor.RED+"Abilities Disabled: "+ChatColor.WHITE+seconds+" seconds";
        String barID = p.getUniqueId()+"_disabled";
        BossBarTools.sendBossBarTimed(p, barID, barTitle, BarColor.RED, BarStyle.SOLID, seconds);
    }

    public static void disableAbility(Player p, long seconds, BarColor color, BarStyle style, @Nonnull String prefix){
        abilitiesDisabled.put(p.getUniqueId(), System.currentTimeMillis()+(seconds * 1000));
        String barTitle = prefix + ChatColor.RED+"Abilities Disabled: "+ChatColor.WHITE+seconds+" seconds";
        String barID = p.getUniqueId()+"_disabled";
        BossBarTools.sendBossBarTimed(p, barID, barTitle, color, style, seconds);
    }

    public static void enableAbility(Player p){
        abilitiesDisabled.remove(p.getUniqueId());
        BossBarTools.removeFromBar(p, p.getUniqueId()+"_disabled");
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
    public static void setPlayerCooldown(Player p, long seconds, CooldownType cooldownType, String abilityName){
        cooldownType.getMap().put(p.getUniqueId(), System.currentTimeMillis()+(seconds * 1000));
        abilityName = abilityName+ ChatColor.GRAY+" Cooldown: "+ChatColor.YELLOW+seconds+" seconds";
        String barID = p.getUniqueId()+"_"+cooldownType.getName();
        BossBarTools.sendBossBarTimed(p, barID, abilityName, BarColor.YELLOW, BarStyle.SEGMENTED_20, seconds);
    }

    public static void setPlayerCooldown(Player p, long seconds, CooldownType cooldownType, BarColor color, BarStyle style, String abilityName){
        cooldownType.getMap().put(p.getUniqueId(), System.currentTimeMillis()+(seconds * 1000));
        abilityName = abilityName+ ChatColor.GRAY+" Cooldown: "+ChatColor.YELLOW+seconds+" seconds";
        String barID = p.getUniqueId()+"_"+cooldownType.getName();
        BossBarTools.sendBossBarTimed(p, barID, abilityName, color, style, seconds);
    }

    public static void removePlayerCooldown(Player p, CooldownType cooldownType){
        cooldownType.getMap().remove(p.getUniqueId());
        BossBarTools.removeFromBar(p, p.getUniqueId()+"_"+cooldownType.getName());
    }

    public static void removePlayerCooldown(Player p, CooldownType cooldownType, String message){
        if (cooldownType.getMap().containsKey(p.getUniqueId())){
            p.sendMessage(message);
            cooldownType.getMap().remove(p.getUniqueId());
            BossBarTools.removeFromBar(p, p.getUniqueId()+"_"+cooldownType.getName());
        }
    }

    public static long getCooldownTimestamp(Player p, CooldownType cooldownType){
        if (!isOnCooldown(p, cooldownType)) return 0;
        return cooldownType.getMap().get(p.getUniqueId());
    }

    public static long getCooldownSecondsLeft(Player p, CooldownType cooldownType){
        if (!isOnCooldown(p, cooldownType)) return 0;
        return (cooldownType.getMap().get(p.getUniqueId())-System.currentTimeMillis())/1000;
    }

    public static boolean isOnCooldown(Player p, CooldownType cooldownType){
        if (!cooldownType.getMap().containsKey(p.getUniqueId()) || cooldownType.getMap().get(p.getUniqueId()) < System.currentTimeMillis()){
            return false;
        }
        return true;
    }

    public static boolean isOnCooldown(Player p, CooldownType cooldownType, String prefix){
        if (!cooldownType.getMap().containsKey(p.getUniqueId()) || cooldownType.getMap().get(p.getUniqueId()) < System.currentTimeMillis()){
            return false;
        }
        p.sendMessage(prefix+ChatColor.RED+"Your ability is currently on cooldown! Please wait "+ChatColor.YELLOW+getCooldownSecondsLeft(p, cooldownType)+ChatColor.RED+" second(s)!");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        return true;
    }


//Fall Damage Resistance
    public static void makePlayerFallDamageResistant(Player p, boolean autoRemove){
        fallDamageResistant.add(p.getUniqueId());
        if (!autoRemove) return;
        new BukkitRunnable(){
            public void run(){
                if (p.isOnGround()) removePlayerFallDamageResistance(p);
            }
        }.runTaskTimer(Core.getInstance(), 3, 2);
    }


    public static void removePlayerFallDamageResistance(Player p){
        fallDamageResistant.remove(p.getUniqueId());
    }

    public static boolean isPlayerFallDamageResistant(Player p){
        return fallDamageResistant.contains(p);
    }

    public static void removePlayerData(UUID player){
        abilityDamager.remove(player);
        primaryCooldowns.remove(player);
        secondaryCooldowns.remove(player);
        fallDamageResistant.remove(player);
        abilitiesDisabled.remove(player);
    }

    public static void removePlayerData(Collection<OfflinePlayer> players){
        for (OfflinePlayer p : players){
            removePlayerData(p.getUniqueId());
        }
    }


}
