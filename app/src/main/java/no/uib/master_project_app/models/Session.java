package no.uib.master_project_app.models;

/**
 * Created by moled on 09.02.2018.
 */

public class Session {
    String sessionName;
    User sessionPerson;

    public Session(String sessionName, User sessionPerson) {
        this.sessionName = sessionName;
        this.sessionPerson = sessionPerson;
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
}
