package bts.co.id.employeepresences.Model;

import java.util.Date;

/**
 * Created by Andreas Panjaitan on 7/25/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class ServicesLog {
    public final Date date;
    public final String message;

    public ServicesLog(Date date, String message) {
        this.date = date;
        this.message = message;
    }
}
