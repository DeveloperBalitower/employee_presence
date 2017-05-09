package bts.co.id.employeepresences.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andreas Panjaitan on 8/3/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class WorkPlacesLocaation {

    static class StoreLocation {
        public LatLng mLatLng;
        public String mId;

        StoreLocation(LatLng latlng, String id) {
            mLatLng = latlng;
            mId = id;
        }
    }

}
