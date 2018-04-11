package no.uib.master_project_app.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public class Datapoint implements Serializable{
    @SerializedName("UUID")
    private String uuid;
    @SerializedName("Major")
    private String major;
    @SerializedName("Minor")
    private String minor;
    @SerializedName("Timestamp")
    private long timestamp;
    @SerializedName("RSSI")
    private int rssi;
    @SerializedName("Steps")
    private long steps;
    @SerializedName("RotationX")
    private float rotX;
    @SerializedName("RotationY")
    private float rotY;
    @SerializedName("RotationZ")
    private float rotZ;

    public Datapoint(String uuid, String major, String minor, long timestamp, int rssi, long steps, float rotX, float rotY, float rotZ ) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.timestamp = timestamp;
        this.rssi = rssi;
        this.steps = steps;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getRssi() {
        return rssi;
    }

    @Override
    public String toString() {
        return "Datapoint{" +
                "uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", timestamp=" + timestamp +
                ", rssi=" + rssi +
                ", steps=" + steps +
                ", rotX=" + rotX +
                ", rotY=" + rotY +
                ", rotZ=" + rotZ +
                '}';
    }
}