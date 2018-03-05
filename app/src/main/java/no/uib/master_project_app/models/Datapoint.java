package no.uib.master_project_app.models;

import java.io.Serializable;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public class Datapoint implements Serializable{
    private String uuid;
    private int major;
    private int minor;
    private long timestamp;
    private int rssi;
    private long steps;
    private float rotX;
    private float rotY;
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