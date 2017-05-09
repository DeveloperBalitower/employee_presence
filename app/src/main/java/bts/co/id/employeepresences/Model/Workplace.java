package bts.co.id.employeepresences.Model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Andreas Panjaitan on 7/28/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class Workplace {
    @SerializedName("workplace_id")
    @Expose
    private String workplaceId;
    @SerializedName("workplace_name")
    @Expose
    private String workplaceName;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("address")
    @Expose
    private String address;

    private double distance;


    public Workplace() {

    }

//    public Workplace(Cursor cursor) {
//        this.workplaceId = cursor.getString(cursor.getColumnIndexOrThrow("workplace_id"));
//        this.workplaceName = cursor.getString(cursor.getColumnIndex("workplace_name"));
//        this.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
//        this.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
//        this.address = cursor.getString(cursor.getColumnIndex("address"));
////        this.distance = distance;
//    }

    public Workplace(Cursor cursor) {
        this.workplaceId = cursor.getString(cursor.getColumnIndexOrThrow("workplace_id"));
        this.workplaceName = cursor.getString(cursor.getColumnIndex("workplace_name"));
        this.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
        this.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
        this.address = cursor.getString(cursor.getColumnIndex("address"));
    }

    /**
     * @return The workplaceId
     */
    public String getWorkplaceId() {
        return workplaceId;
    }

    /**
     * @param workplaceId The workplace_id
     */
    public void setWorkplaceId(String workplaceId) {
        this.workplaceId = workplaceId;
    }

    /**
     * @return The workplaceName
     */
    public String getWorkplaceName() {
        return workplaceName;
    }

    /**
     * @param workplaceName The workplace_name
     */
    public void setWorkplaceName(String workplaceName) {
        this.workplaceName = workplaceName;
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
     * @return The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
