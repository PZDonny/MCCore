package net.donnypz.mccore.utils.slime;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class WrappedSlimeWorld<T> {
    protected T slimeWorld;

    public WrappedSlimeWorld(T slimeWorld){
        this.slimeWorld = slimeWorld;
    }

    public T getSlimeWorld() {
        return slimeWorld;
    }

    public abstract WrappedSlimeWorld<T> clone(@NotNull String cloneWorldName);

    public abstract WrappedSlimeWorld<T> clone(@NotNull String cloneWorldName, WrappedSlimeLoader<?> loader) throws IOException;

    public abstract @NotNull String getName();

    public abstract @NotNull WrappedSlimeLoader<?> getLoader();
}
