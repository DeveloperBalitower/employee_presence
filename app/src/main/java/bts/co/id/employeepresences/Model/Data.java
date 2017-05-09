package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IT on 7/14/2016.
 */
public class Data {
    @SerializedName("auth")
    @Expose
    private Auth auth;
    @SerializedName("user")
    @Expose
    private List<User> user = new ArrayList<User>();
    @SerializedName("system_setting")
    @Expose
    private List<SystemSetting> systemSetting = new ArrayList<SystemSetting>();
    @SerializedName("checkin_data")
    @Expose
    private CheckinData checkinData;
    @SerializedName("workplaces")
    @Expose
    private List<Workplace> workplaces = new ArrayList<Workplace>();
    @SerializedName("sites")
    @Expose
    private List<Site> sites = new ArrayList<Site>();
    @SerializedName("presences_history")
    @Expose
    private List<HistoryDataModel> presencesHistory = new ArrayList<HistoryDataModel>();

    /**
     * @return The auth
     */
    public Auth getAuth() {
        return auth;
    }

    /**
     * @param auth The auth
     */
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    /**
     * @return The user
     */
    public List<User> getUser() {
        return user;
    }

    /**
     * @param user The user
     */
    public void setUser(List<User> user) {
        this.user = user;
    }

    /**
     * @return The systemSetting
     */
    public List<SystemSetting> getSystemSetting() {
        return systemSetting;
    }

    /**
     * @param systemSetting The system_setting
     */
    public void setSystemSetting(List<SystemSetting> systemSetting) {
        this.systemSetting = systemSetting;
    }

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

    /**
     * @return The sites
     */
    public List<Site> getSites() {
        return sites;
    }

    /**
     *
     * @param sites
     * The sites
     */
    /**
     * @return The checkinData
     */
    public CheckinData getCheckinData() {
        return checkinData;
    }

    /**
     * @param checkinData The checkin_data
     */
    public void setCheckinData(CheckinData checkinData) {
        this.checkinData = checkinData;
    }

    public List<HistoryDataModel> getPresencesHistory() {
        return presencesHistory;
    }

    public void setPresencesHistory(List<HistoryDataModel> presencesHistory) {
        this.presencesHistory = presencesHistory;
    }
}
