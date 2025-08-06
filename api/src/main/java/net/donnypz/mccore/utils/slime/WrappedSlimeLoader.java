package net.donnypz.mccore.utils.slime;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public abstract class WrappedSlimeLoader<T> {
    protected T slimeLoader;

    public WrappedSlimeLoader(T slimeLoader){
        this.slimeLoader = slimeLoader;
    }

    public T getSlimeLoader() {
        return slimeLoader;
    }

    public abstract byte[] readWorld(@NotNull String worldName) throws IOException;

    public abstract void saveWorld(@NotNull String worldName, byte @NotNull[] serializedWorld) throws IOException;

    public abstract void deleteWorld(@NotNull String worldName) throws IOException;

    public abstract List<String> listWorlds() throws IOException;

    public abstract boolean worldExists(@NotNull String worldName) throws  IOException;
}
