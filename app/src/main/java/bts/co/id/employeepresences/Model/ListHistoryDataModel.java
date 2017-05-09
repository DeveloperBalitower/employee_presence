package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas Panjaitan on 10/13/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class ListHistoryDataModel {
    @SerializedName("presences_history")
    @Expose
    private List<HistoryDataModel> historyDataModels = new ArrayList<HistoryDataModel>();

    /**
     * @return The presencesHistory
     */
    public List<HistoryDataModel> getHistoryDataModels() {
        return historyDataModels;
    }

    /**
     * @param historyDataModels The presences_history
     */
    public void setHistoryDataModels(List<HistoryDataModel> historyDataModels) {
        this.historyDataModels = historyDataModels;
    }
}
