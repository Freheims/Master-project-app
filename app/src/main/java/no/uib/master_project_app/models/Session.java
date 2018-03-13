package no.uib.master_project_app.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edvard P. Bjørgen and Fredrik V. Heimsæter
 */

public class Session {
    @SerializedName("ID")
    int sessionId;
    @SerializedName("Name")
    String sessionName;
    @SerializedName("User")
    String sessionUser;
    @SerializedName("Datapoints")
    List<Datapoint> datapoints;
    @SerializedName("StartTime")
    long sessionStart;
    @SerializedName("EndTime")
    long sessionEnd;

    public Session(String sessionName, String sessionUser) {
        this.sessionName = sessionName;
        this.sessionUser = sessionUser;
        datapoints = new ArrayList<>();
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(String sessionUser) {
        this.sessionUser = sessionUser;
    }

    public long getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(long sessionStart) {
        this.sessionStart = sessionStart;
    }

    public long getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(long sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public List<Datapoint> getDatapoints() {
        return datapoints;
    }

    public void addDataPoint(Datapoint datapoint) {
        datapoints.add(datapoint);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionName='" + sessionName + '\'' +
                ", sessionUser=" + sessionUser +
                ", datapoints=" + datapoints +
                ", sessionStart=" + sessionStart +
                ", sessionEnd=" + sessionEnd +
                '}';
    }
}
