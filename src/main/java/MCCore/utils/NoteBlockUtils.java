package MCCore.utils;

import MCCore.Core;
import MCCore.sockets.Messages;

public class NoteBlockUtils {

//Request Songs
    public static void requestSong(String songName, String requestTag){
        if (!Core.isNBAPIInstalled()){
            return;
        }
        Core.getClient().sendMessage(new String[]{Messages.NBAPI_GETSONG.getID(), songName, requestTag});
    }

    public static void requestSong(String songName, String folderName, String requestTag){
        if (!Core.isNBAPIInstalled()){
            return;
        }
        Core.getClient().sendMessage(new String[]{Messages.NBAPI_GETSONG.getID(), folderName+"/"+songName, requestTag});
    }

//Get All Song Names
    public static void getAllSongNames(String requestTag){
        if (!Core.isNBAPIInstalled()){
            return;
        }
        Core.getClient().sendMessage(new String[]{Messages.NBAPI_GETSONGNAMES.getID(), requestTag, ""});
    }

    public static void getAllSongNames(String folderName, String requestTag){
        if (!Core.isNBAPIInstalled()){
            return;
        }
        Core.getClient().sendMessage(new String[]{Messages.NBAPI_GETSONGNAMES.getID(), requestTag, folderName});
    }


}
