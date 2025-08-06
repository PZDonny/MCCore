package net.donnypz.mccore.utils.item;

import net.donnypz.mccore.utils.ability.AbilityHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.util.function.Consumer;

public class AbilityItemAction extends ItemAction {
    private final AbilityHandler.CooldownType cooldownType;
    private final double abilityDuration;
    private String failPrefix = "";
    private String failMessage = null;

    public AbilityItemAction(@NotNull String itemActionID, @NotNull AbilityHandler.CooldownType cooldownType, @Nonnegative double abilityDuration) {
        super(itemActionID);
        this.cooldownType = cooldownType;
        this.abilityDuration = abilityDuration;
    }

    public AbilityItemAction(@NotNull String itemActionID, @NotNull AbilityHandler.CooldownType cooldownType, @Nonnegative double abilityDuration, @NotNull String failPrefix) {
        super(itemActionID);
        this.cooldownType = cooldownType;
        this.abilityDuration = abilityDuration;
        this.failPrefix = failPrefix;
    }

    public AbilityItemAction(@NotNull String itemActionID, @NotNull AbilityHandler.CooldownType cooldownType, @Nonnegative double abilityDuration, @NotNull String failPrefix, @NotNull String failMessage) {
        super(itemActionID);
        this.cooldownType = cooldownType;
        this.abilityDuration = abilityDuration;
        this.failPrefix = failPrefix;
        this.failMessage = failMessage;
    }

    @Override
    protected void accept(Consumer<ItemActionResult> consumer, ItemActionResult result, boolean applyAnyClick){
        if (failMessage != null){
            if (AbilityHandler.isOnCooldown(result.player(), cooldownType, failPrefix, failMessage)){
                return;
            }
        }

        else{
            if (AbilityHandler.isOnCooldown(result.player(), cooldownType, failPrefix)){
                return;
            }
        }

        if (applyAnyClick){
            if (anyCheck != null){
                if (!anyCheck.test(result)){
                    return;
                }
            }

            if (any != null){
                any.accept(result);
            }
        }

        if (consumer != null){
            consumer.accept(result);
        }
    }

    public AbilityHandler.CooldownType getCooldownType() {
        return cooldownType;
    }
}