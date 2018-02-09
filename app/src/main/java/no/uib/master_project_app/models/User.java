package no.uib.master_project_app.models;

/**
 * Created by moled on 09.02.2018.
 */

public class User {
    String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
