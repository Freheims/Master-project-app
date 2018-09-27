package no.uib.master_project_app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import no.uib.master_project_app.models.Session;

public class SessionSaver {
    private static final String SHARED_PREFERENCES_KEY = "list-of-sessions";

    public static void saveSessionInSharedPreferences(Context context, Session session) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        ArrayList<Session> listOfSessions = new ArrayList<>();

        if (sharedPreferences.contains(SHARED_PREFERENCES_KEY)) {
            listOfSessions = gson.fromJson(sharedPreferences.getString(SHARED_PREFERENCES_KEY,""), new TypeToken<ArrayList<Session>>(){}.getType());
            for (Session sessionInList :
                    listOfSessions) {
                System.out.println("STORED SESSSIONS");
                System.out.println(sessionInList);
            }
        }
        listOfSessions.add(session);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String serializedSessionList = gson.toJson(listOfSessions);
        editor.putString(SHARED_PREFERENCES_KEY, serializedSessionList);
        editor.apply();
    }
}
