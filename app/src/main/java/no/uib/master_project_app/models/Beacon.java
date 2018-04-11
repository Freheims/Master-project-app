package no.uib.master_project_app.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public class Beacon {
    @SerializedName("ID")
    private int id;
    @SerializedName("SessionId")
    private int sessionId;
    @SerializedName("UUID")
    private String uuid;
    @SerializedName("Major")
    private String major;
    @SerializedName("Minor")
    private String minor;
    @SerializedName("Name")
    private String name;
    @SerializedName("XCoordinate")
    private int xCoordinate;
    @SerializedName("YCoordinate")
    private int yCoordinate;

    public Beacon(int id, int sessionId, String uuid, String major, String minor, String name, int xCoordinate, int yCoordinate) {
        this.id = id;
        this.sessionId = sessionId;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "id=" + id +
                ", sessionId=" + sessionId +
                ", uuid='" + uuid + '\'' +
                ", major='" + major + '\'' +
                ", minor='" + minor + '\'' +
                ", name='" + name + '\'' +
                ", xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                '}';
    }
}
