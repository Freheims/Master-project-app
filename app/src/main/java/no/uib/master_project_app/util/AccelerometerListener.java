package no.uib.master_project_app.util;

/**
 * Created by sotsys055 on 3/5/17.
 */

public interface AccelerometerListener {

    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force);
}
