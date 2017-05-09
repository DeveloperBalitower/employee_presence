package bts.co.id.employeepresences.Manager;

import java.io.IOException;

import bts.co.id.employeepresences.EmployeePresencesApplication;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Andreas Panjaitan on 8/5/2016.
 * mail : me@andreaspanjaitan.com
 * http://andreaspanjaitan.com/
 */

public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        EmployeePresencesApplication.getInstances().getGlobalManager().writeTextFile(String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        int tryCount = 0;
        while (!response.isSuccessful() && tryCount < 3) {

            Log.d("intercept", "Request is not successful - " + tryCount);

            EmployeePresencesApplication.getInstances().getGlobalManager().writeTextFile("Request is not successful - " + tryCount);
            tryCount++;

            // retry the request
            response = chain.proceed(request);
        }

        long t2 = System.nanoTime();
        EmployeePresencesApplication.getInstances().getGlobalManager().writeTextFile(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}