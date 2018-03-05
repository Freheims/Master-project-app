package no.uib.master_project_app.util;


import no.uib.master_project_app.models.Session;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public interface ApiInterface {
    @POST("session")
    Call<ResponseBody> createSession(@Body Session session);
}
