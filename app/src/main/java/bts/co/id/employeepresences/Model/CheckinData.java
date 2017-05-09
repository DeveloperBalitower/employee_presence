package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IT on 7/14/2016.
 */
public class CheckinData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_nik")
    @Expose
    private String userNik;
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
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

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

    /**
     * @return The site
     */
    public String getSite() {
        return site;
    }

    /**
     * @param site The site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * @return The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
