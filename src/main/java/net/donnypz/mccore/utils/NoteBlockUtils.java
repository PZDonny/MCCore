package net.donnypz.mccore.utils;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class NoteBlockUtils {
    public static Song getSongFromPlugin(JavaPlugin plugin, String resourcePath){
        InputStream songStream;
        if (resourcePath.contains(".nbs")){
            songStream = plugin.getResource(resourcePath);
        }
        else{
            songStream = plugin.getResource(resourcePath+".nbs");
        }
        return NBSDecoder.parse(songStream);
    }
}
