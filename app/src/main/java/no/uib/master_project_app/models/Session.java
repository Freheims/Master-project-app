package no.uib.master_project_app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edvard P. Bjørgen and Fredrik V. Heimsæter
 */

public class Session {
    String sessionName;
    String sessionPerson;
    List<Datapoint> datapoints;
    long sessionStart;
    long sessionEnd;

    public Session(String sessionName, String sessionPerson) {
        this.sessionName = sessionName;
        this.sessionPerson = sessionPerson;
        datapoints = new ArrayList<>();
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionPerson() {
        return sessionPerson;
    }

    public void setSessionPerson(String sessionPerson) {
        this.sessionPerson = sessionPerson;
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
                ", sessionPerson=" + sessionPerson +
                ", datapoints=" + datapoints +
                ", sessionStart=" + sessionStart +
                ", sessionEnd=" + sessionEnd +
                '}';
    }
}
