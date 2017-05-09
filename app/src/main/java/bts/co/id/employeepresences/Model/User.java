package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IT on 7/14/2016.
 */
public class User {

    @SerializedName("nik")
    @Expose
    private String nik;
    @SerializedName("emp_name")
    @Expose
    private String empName;
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * @return The nik
     */
    public String getNik() {
        return nik;
    }

    /**
     * @param nik The nik
     */
    public void setNik(String nik) {
        this.nik = nik;
    }

    /**
     * @return The empName
     */
    public String getEmpName() {
        return empName;
    }

    /**
     * @param empName The emp_name
     */
    public void setEmpName(String empName) {
        this.empName = empName;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
