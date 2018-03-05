package no.uib.master_project_app.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public class ApiClient {
    public static final String API_BASE_URL = "http://firetracker.freheims.xyz:8000";

    private static Retrofit retrofit = null;

    /**
     * Get a client so you can work with the backend
     * @return The client
     */
    public static Retrofit getClient() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
