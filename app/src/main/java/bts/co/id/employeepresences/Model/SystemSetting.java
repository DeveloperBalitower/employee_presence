package bts.co.id.employeepresences.Model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IT on 7/22/2016.
 */
public class SystemSetting {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("setting_name")
    @Expose
    private String settingName;
    @SerializedName("value_int")
    @Expose
    private Integer valueInt;
    @SerializedName("value_string")
    @Expose
    private String valueString;
    @SerializedName("activated")
    @Expose
    private Integer activated;


    public SystemSetting() {

    }

    public SystemSetting(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.settingName = cursor.getString(cursor.getColumnIndex("setting_name"));
        this.valueInt = cursor.getInt(cursor.getColumnIndex("value_int"));
        this.valueString = cursor.getString(cursor.getColumnIndex("value_string"));
        this.activated = cursor.getInt(cursor.getColumnIndex("activated"));
    }

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
     * @return The settingName
     */
    public String getSettingName() {
        return settingName;
    }

    /**
     * @param settingName The setting_name
     */
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    /**
     * @return The valueInt
     */
    public Integer getValueInt() {
        return valueInt;
    }

    /**
     * @param valueInt The value_int
     */
    public void setValueInt(Integer valueInt) {
        this.valueInt = valueInt;
    }

    /**
     * @return The valueString
     */
    public String getValueString() {
        return valueString;
    }

    /**
     * @param valueString The value_string
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    /**
     * @return The activated
     */
    public Integer getActivated() {
        return activated;
    }

    /**
     * @param activated The activated
     */
    public void setActivated(Integer activated) {
        this.activated = activated;
    }

}
