package no.uib.master_project_app.models;

import java.io.Serializable;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public class Datapoint implements Serializable{
    private Ibeacon beacon;
    private long timestamp;
    private int rssi;

    public Datapoint(Ibeacon beacon, long timestamp, int rssi) {
        this.beacon = beacon;
        this.timestamp = timestamp;
        this.rssi = rssi;
    }

    public Ibeacon getBeacon() {
        return beacon;
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
                "beacon=" + beacon +
                ", timestamp=" + timestamp +
                ", rssi=" + rssi +
                '}';
    }
}