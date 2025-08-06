package net.donnypz.mccore.version.v1_21_8;

import com.infernalsuite.asp.api.exceptions.UnknownWorldException;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import net.donnypz.mccore.utils.slime.WrappedSlimeLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class WrappedSlimeLoaderImpl extends WrappedSlimeLoader<SlimeLoader> {

    public WrappedSlimeLoaderImpl(SlimeLoader slimeLoader) {
        super(slimeLoader);
    }

    @Override
    public byte[] readWorld(@NotNull String worldName) throws IOException{
        try{
            return slimeLoader.readWorld(worldName);
        }catch(UnknownWorldException e){
            throw new IOException(e);
        }
    }

    @Override
    public void saveWorld(@NotNull String worldName, byte @NotNull [] serializedWorld) throws IOException{
        slimeLoader.saveWorld(worldName, serializedWorld);
    }

    @Override
    public void deleteWorld(@NotNull String worldName) throws IOException{
        try {
            slimeLoader.deleteWorld(worldName);
        }
        catch(UnknownWorldException e){
            throw new IOException(e);
        }
    }

    @Override
    public List<String> listWorlds() throws IOException {
        return slimeLoader.listWorlds();
    }

    @Override
    public boolean worldExists(@NotNull String worldName) throws IOException{
        return slimeLoader.worldExists(worldName);
    }
}
