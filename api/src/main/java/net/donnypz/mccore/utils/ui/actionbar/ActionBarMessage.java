package net.donnypz.mccore.utils.ui.actionbar;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ActionBarMessage {

    private Component message;

    public ActionBarMessage(@NotNull Component message){
        this.message = message;
    }

    public ActionBarMessage(@NotNull String message){
        this.message = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public void setMessage(@NotNull Component message){
        this.message = message;
    }

    public Component getMessage() {
        return message;
    }
}
