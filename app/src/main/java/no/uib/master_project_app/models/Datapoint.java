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

    public Datapoint(String uuid, int major, int minor, long timestamp, int rssi) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.timestamp = timestamp;
        this.rssi = rssi;
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
                '}';
    }
}