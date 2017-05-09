package bts.co.id.employeepresences.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IT on 7/14/2016.
 */
public class Auth {
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("exp")
    @Expose
    private String exp;

    /**
     * @return The token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return The exp
     */
    public String getExp() {
        return exp;
    }

    /**
     * @param exp The exp
     */
    public void setExp(String exp) {
        this.exp = exp;
    }
}
