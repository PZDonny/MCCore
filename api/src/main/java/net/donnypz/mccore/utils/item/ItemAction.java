package net.donnypz.mccore.utils.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemAction {

    private static final HashMap<String, ItemAction> itemActions = new HashMap<>();
    Predicate<ItemActionResult> leftCheck = null;
    Predicate<ItemActionResult> rightCheck = null;
    Predicate<ItemActionResult> anyCheck = null;
    Consumer<ItemActionResult> left = null;
    Consumer<ItemActionResult> right = null;
    Consumer<ItemActionResult> any = null;
    Consumer<ItemActionResult> consume = null;
    final String itemActionID;
    private ArenaCheckType arenaCheckType = ArenaCheckType.NONE;


    public ItemAction(@NotNull String itemActionID){
        if (itemActions.containsKey(itemActionID)){
            throw new IllegalArgumentException("Item Action ID already exists: "+ itemActionID);
        }
        this.itemActionID = itemActionID;
        itemActions.put(itemActionID, this);
    }

    public ItemAction setArenaCheckType(ArenaCheckType type){
        this.arenaCheckType = type;
        return this;
    }


    public ItemAction setLeftClickCheck(Predicate<ItemActionResult> predicate){
        leftCheck = predicate;
        return this;
    }

    public ItemAction setRightClickCheck(Predicate<ItemActionResult> predicate){
        rightCheck = predicate;
        return this;
    }

    public ItemAction setAnyClickCheck(Predicate<ItemActionResult> predicate){
        anyCheck = predicate;
        return this;
    }


    public ItemAction setLeftClickAction(Consumer<ItemActionResult> consumer){
        left = consumer;
        return this;
    }
    
    public ItemAction setRightClickAction(Consumer<ItemActionResult> consumer){
        right = consumer;
        return this;
    }

    public ItemAction setAnyClickAction(Consumer<ItemActionResult> consumer){
        any = consumer;
        return this;
    }

    public ItemAction setConsumeAction(Consumer<ItemActionResult> consumer){
        consume = consumer;
        return this;
    }

    public void executeRight(ItemActionResult result){
        if (rightCheck != null){
            if (!rightCheck.test(result)){
                return;
            }
        }
        accept(right, result, true);
    }

    public void executeLeft(ItemActionResult result){
        if (leftCheck != null){
            if (!leftCheck.test(result)){
                return;
            }
        }
        accept(left, result, true);
    }

    public void executeConsume(ItemActionResult result){
        accept(consume, result, false);
    }

    protected void accept(Consumer<ItemActionResult> consumer, ItemActionResult result, boolean applyAnyClick){
        if (arenaCheckType == ArenaCheckType.IN_ARENA){
            if (result.arena() == null){
                return;
            }
        }
        else if (arenaCheckType == ArenaCheckType.OUTSIDE_ARENA){
            if (result.arena() != null){
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


    public String getItemActionID() {
        return itemActionID;
    }
    
    public static ItemAction getItemAction(String itemActionID){
        return itemActions.get(itemActionID);
    }

    public static <T> T getItemAction(String itemActionID, Class<T> itemActionClass){
        if (itemActionClass.isInstance(ItemAction.class)){
            return itemActionClass.cast(itemActions.get(itemActionID));
        }
        return null;
    }

    public static boolean hasItemAction(ItemStack itemStack){
        return ItemUtils.hasItemAction(itemStack);
    }


    public enum ArenaCheckType{
        NONE,
        IN_ARENA,
        OUTSIDE_ARENA;
    }

}
