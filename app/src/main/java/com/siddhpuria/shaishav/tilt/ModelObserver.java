package com.siddhpuria.shaishav.tilt;

import android.hardware.SensorEvent;

/**
 * ModelObserver.java
 * Abstract observer class to implement a simple subscriber-notification pattern.
 */

public interface ModelObserver {

    void notifySensorEventUpdate(SensorEvent event);

}
