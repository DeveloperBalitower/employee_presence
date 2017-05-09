package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IT on 7/20/2016.
 */
public class ListPresencesHistory {
    @SerializedName("presences_history")
    @Expose
    private List<PresencesHistory> presencesHistory = new ArrayList<PresencesHistory>();

    /**
     * @return The presencesHistory
     */
    public List<PresencesHistory> getPresencesHistory() {
        return presencesHistory;
    }

    /**
     * @param presencesHistory The presences_history
     */
    public void setPresencesHistory(List<PresencesHistory> presencesHistory) {
        this.presencesHistory = presencesHistory;
    }

}
