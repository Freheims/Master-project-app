package no.uib.master_project_app.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public class Datapoint implements Serializable{
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("major")
    private int major;
    @SerializedName("minor")
    private int minor;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("rssi")
    private int rssi;
    @SerializedName("steps")
    private long steps;
    @SerializedName("rotationx")
    private float rotX;
    @SerializedName("rotationy")
    private float rotY;
    @SerializedName("rotationz")
    private float rotZ;

    public Datapoint(String uuid, int major, int minor, long timestamp, int rssi, long steps, float rotX, float rotY, float rotZ ) {
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