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

public class SiteList {
    @SerializedName("sites")
    @Expose
    private List<Site> sites = new ArrayList<Site>();

    /**
     * @return The sites
     */
    public List<Site> getSites() {
        return sites;
    }

    /**
     * @param sites The sites
     */
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }
}
