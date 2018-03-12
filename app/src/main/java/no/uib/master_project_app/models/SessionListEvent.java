package no.uib.master_project_app.models;

/**
 * Created by tacobabe on 12.03.2018.
 */

import java.util.List;

/**
 * @author Edvard P. Bjørgen
 * @version 1.0
 */

public class SessionListEvent {
    private final List<Session> sessionList;

    public SessionListEvent(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    public List<Session> getSessionList() {
        return sessionList;
    }
}
