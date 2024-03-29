package no.uib.master_project_app.util;


import java.util.List;

import no.uib.master_project_app.models.Session;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public interface ApiInterface {
    @POST("session")
    Call<ResponseBody> createSession(@Body Session session);

    @GET("sessions")
    Call<List<Session>> getAllSessions();

    @GET("session/{sessionid}")
    Call<Session> getSessionFromId(@Path ("sessionid") int sessionId);

    @FormUrlEncoded
    @POST("sessions")
    Call<List<Session>> getSessionsByStatus(@Field("Finished") boolean status);


    @PUT("session/{sessionid}")
    Call<Void> updateSession(@Body Session session, @Path ("sessionid") int sessionId);
}
