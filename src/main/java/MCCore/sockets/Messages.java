package MCCore.sockets;

public enum Messages {
    NETWORK_DATA("network_data"),
    SPIGOT_PLAYERCOUNT("spigot_pc"),
    MINIGAMEAPI_JOINQUEUE("mgapi_joinQueue"),
    MINIGAMEAPI_CREATEPRIVATE("mgapi_createPrivate"),
    MINIGAMEAPI_JOINPRIVATE("mgapi_joinPrivate"),
    MINIGAMEAPI_CREATEARENA("mgapi_createArena"),
    MINIGAMEAPI_STARTARENA("mgapi_startArena"),
    MINIGAMEAPI_STOPARENA("mgapi_stopArena"),
    MINIGAMEAPI_ADDTOEXISTING("mgapi_addToExisting"),
    MINIGAMEAPI_BLOCK("mgapi_block"),
    NBAPI_GETSONGNAMES("nbapi_getSongNames"),
    NBAPI_GETSONG("nbapi_getSong"),

    DISCONNECT("disconnect");


    private final String id;

    Messages(String id){
        this.id = id;
    }

    public String getID() {
        return id;
    }


}
