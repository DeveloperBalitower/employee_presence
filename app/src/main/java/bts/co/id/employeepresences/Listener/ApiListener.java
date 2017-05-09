package bts.co.id.employeepresences.Listener;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by IT on 7/14/2016.
 */
public interface ApiListener {

    public void onFailure(Request request, IOException e);

    public void onResponseSuccess(String responseBody) throws IOException;

    public void onResponseError(String response);
}
