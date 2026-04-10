package org.moritas;

import com.badlogic.gdx.math.Vector3;

import static java.lang.Math.PI;

public class Settings {

    // camera
    public static final Vector3 CAM_START_POS = new Vector3(0, 0, 0);
    public static final float CAM_START_DISTANCE = 25f;
    public static final float CAM_MIN_DISTANCE = 5f;
    public static final float CAM_MAX_DISTANCE = 50f;
    public static final float CAM_START_YAW = (float) (0 * PI);
    public static final float CAM_START_PITCH = (float) (PI / 10);
    public static final float CAM_MIN_PITCH = (float) (0.1 * PI / 2);
    public static final float CAM_MAX_PITCH = (float) (0.6 * PI / 2);
    public static final float CAM_ROTATION_SPEED = (float) (PI / 1650);
    public static final float CAM_SCROLL_SPEED = 15f;
    public static final float CAM_ZOOM_SPEED = 2.5f;
    public static final float CAM_LERP_FACTOR = 0.17f;
}
