package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IT on 7/14/2016.
 */
public class PresencesHistory {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("location_id")
    @Expose
    private String locationId;
    @SerializedName("user_nik")
    @Expose
    private String userNik;
    @SerializedName("location_name")
    @Expose
    private String locationName;
    @SerializedName("check_in_date")
    @Expose
    private String checkInDate;
    @SerializedName("check_in_time")
    @Expose
    private String checkInTime;
    @SerializedName("check_out_date")
    @Expose
    private String checkOutDate;
    @SerializedName("check_out_time")
    @Expose
    private String checkOutTime;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The locationId
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * @param locationId The location_id
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * @return The userNik
     */
    public String getUserNik() {
        return userNik;
    }

    /**
     * @param userNik The user_nik
     */
    public void setUserNik(String userNik) {
        this.userNik = userNik;
    }

    /**
     * @return The locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * @param locationName The location_name
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * @return The checkInDate
     */
    public String getCheckInDate() {
        return checkInDate;
    }

    /**
     * @param checkInDate The check_in_date
     */
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    /**
     * @return The checkInTime
     */
    public String getCheckInTime() {
        return checkInTime;
    }

    /**
     * @param checkInTime The check_in_time
     */
    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    /**
     * @return The checkOutDate
     */
    public String getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * @param checkOutDate The check_out_date
     */
    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    /**
     * @return The checkOutTime
     */
    public String getCheckOutTime() {
        return checkOutTime;
    }

    /**
     * @param checkOutTime The check_out_time
     */
    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
