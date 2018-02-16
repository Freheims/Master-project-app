package no.uib.master_project_app.models;

/**
 * @author Fredrik V. Heimsæter
 * @version 1.0
 */

public class Ibeacon {
    private String Uuid;
    private int major;
    private int minor;

    public Ibeacon(String uuid, int major, int minor) {
        Uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public String getUuid() {
        return Uuid;
    }

    public void setUuid(String uuid) {
        Uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }
}
