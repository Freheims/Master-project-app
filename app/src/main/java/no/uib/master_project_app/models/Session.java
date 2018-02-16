package no.uib.master_project_app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edvard P. Bjørgen and Fredrik V. Heimsæter
 */

public class Session {
    String sessionName;
    User sessionPerson;
    List<Datapoint> datapoints;

    public Session(String sessionName, User sessionPerson) {
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

    public User getSessionPerson() {
        return sessionPerson;
    }

    public void setSessionPerson(User sessionPerson) {
        this.sessionPerson = sessionPerson;
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
                '}';
    }
}
