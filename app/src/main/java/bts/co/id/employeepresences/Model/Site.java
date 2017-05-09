package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IT on 7/14/2016.
 */
public class Site {
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("site_name")
    @Expose
    private String siteName;
    @SerializedName("site_address")
    @Expose
    private String siteAddress;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("actual_longitude")
    @Expose
    private String actualLongitude;
    @SerializedName("actual_latitude")
    @Expose
    private String actualLatitude;
    @SerializedName("current_status")
    @Expose
    private String currentStatus;

    /**
     * @return The siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * @param siteId The site_id
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * @return The siteName
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * @param siteName The site_name
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * @return The siteAddress
     */
    public String getSiteAddress() {
        return siteAddress;
    }

    /**
     * @param siteAddress The site_address
     */
    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    /**
     * @return The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The actualLongitude
     */
    public String getActualLongitude() {
        return actualLongitude;
    }

    /**
     * @param actualLongitude The actual_longitude
     */
    public void setActualLongitude(String actualLongitude) {
        this.actualLongitude = actualLongitude;
    }

    /**
     * @return The actualLatitude
     */
    public String getActualLatitude() {
        return actualLatitude;
    }

    /**
     * @param actualLatitude The actual_latitude
     */
    public void setActualLatitude(String actualLatitude) {
        this.actualLatitude = actualLatitude;
    }

    /**
     * @return The currentStatus
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @param currentStatus The current_status
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
