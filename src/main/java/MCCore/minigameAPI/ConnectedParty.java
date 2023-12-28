package MCCore.minigameAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ConnectedParty {
    //final UUID leader;
    final ArrayList<UUID> allMembers = new ArrayList<>();

    public ConnectedParty(String[] partyArray){
        //this.leader = UUID.fromString(partyArray[0]);
        for (int i = 0; i < partyArray.length; i++){
            allMembers.add(UUID.fromString(partyArray[i]));
        }
    }
    public ConnectedParty(Collection<UUID> partyList){
        //this.leader = UUID.fromString(partyList.get(0));
        allMembers.addAll(partyList);
    }



    /*public UUID getLeader() {
        return leader;
    }*/

    public ArrayList<UUID> getMembers() {
        return allMembers;
    }
}
