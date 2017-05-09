package bts.co.id.employeepresences.Model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Andreas Panjaitan on 10/7/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */


public class HistoryDataModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("server_id")
    @Expose
    private Integer server_id;
    @SerializedName("location_id")
    @Expose
    private String locationId;
    //    @SerializedName("user_nik")
//    @Expose
//    private String userNik;
    @SerializedName("location_name")
    @Expose
    private String locationName;
    @SerializedName("user_nik")
    @Expose
    private String user_nik;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("check_in_date")
    @Expose
    private String check_in_date;
    @SerializedName("check_in_time")
    @Expose
    private String check_in_time;
    @SerializedName("checkin_lat")
    @Expose
    private Double checkin_lat;
    @SerializedName("checkin_long")
    @Expose
    private Double checkin_long;
    @SerializedName("checkout_lat")
    @Expose
    private Double checkout_lat;
    @SerializedName("checkout_long")
    @Expose
    private Double checkout_long;
    @SerializedName("check_out_date")
    @Expose
    private String check_out_date;
    @SerializedName("check_out_time")
    @Expose
    private String check_out_time;
    @SerializedName("upload_status")
    @Expose
    private Integer upload_status;

    public HistoryDataModel(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.server_id = cursor.getInt(cursor.getColumnIndex("server_id"));
        this.user_nik = cursor.getString(cursor.getColumnIndex("user_nik"));
        this.check_in_date = cursor.getString(cursor.getColumnIndex("check_in_date"));
        this.check_in_time = cursor.getString(cursor.getColumnIndex("check_in_time"));
        this.checkin_lat = cursor.getDouble(cursor.getColumnIndex("checkin_lat"));
        this.checkin_long = cursor.getDouble(cursor.getColumnIndex("checkin_long"));
        this.check_out_date = cursor.getString(cursor.getColumnIndex("check_out_date"));
        this.check_out_time = cursor.getString(cursor.getColumnIndex("check_out_time"));
        this.checkout_lat = cursor.getDouble(cursor.getColumnIndex("checkout_lat"));
        this.checkout_long = cursor.getDouble(cursor.getColumnIndex("checkout_long"));
        this.site = cursor.getString(cursor.getColumnIndex("site"));
        this.upload_status = cursor.getInt(cursor.getColumnIndex("upload_status"));
    }

    public HistoryDataModel() {

    }

    public Integer getServer_id() {
        return server_id;
    }

    public void setServer_id(Integer server_id) {
        this.server_id = server_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCheck_in_date() {
        return check_in_date;
    }

    public void setCheck_in_date(String check_in_date) {
        this.check_in_date = check_in_date;
    }

    public String getCheck_in_time() {
        return check_in_time;
    }

    public void setCheck_in_time(String check_in_time) {
        this.check_in_time = check_in_time;
    }

    public String getCheck_out_date() {
        return check_out_date;
    }

    public void setCheck_out_date(String check_out_date) {
        this.check_out_date = check_out_date;
    }

    public String getCheck_out_time() {
        return check_out_time;
    }

    public void setCheck_out_time(String check_out_time) {
        this.check_out_time = check_out_time;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getUser_nik() {
        return user_nik;
    }

    public void setUser_nik(String user_nik) {
        this.user_nik = user_nik;
    }

    public void setSite_name(String site_name) {
        this.site = site_name;
    }

    public Integer getUpload_status() {
        return upload_status;
    }

    public void setUpload_status(Integer upload_status) {
        this.upload_status = upload_status;
    }

    public Double getCheckin_lat() {
        return checkin_lat;
    }

    public void setCheckin_lat(Double checkin_lat) {
        this.checkin_lat = checkin_lat;
    }

    public Double getCheckin_long() {
        return checkin_long;
    }

    public void setCheckin_long(Double checkin_long) {
        this.checkin_long = checkin_long;
    }

    public Double getCheckout_lat() {
        return checkout_lat;
    }

    public void setCheckout_lat(Double checkout_lat) {
        this.checkout_lat = checkout_lat;
    }

    public Double getCheckout_long() {
        return checkout_long;
    }

    public void setCheckout_long(Double checkout_long) {
        this.checkout_long = checkout_long;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}