package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas Panjaitan on 8/2/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class WorkPlaces {
    @SerializedName("workplaces")
    @Expose
    private List<Workplace> workplaces = new ArrayList<Workplace>();

    /**
     * @return The workplaces
     */
    public List<Workplace> getWorkplaces() {
        return workplaces;
    }

    /**
     * @param workplaces The workplaces
     */
    public void setWorkplaces(List<Workplace> workplaces) {
        this.workplaces = workplaces;
    }
}
