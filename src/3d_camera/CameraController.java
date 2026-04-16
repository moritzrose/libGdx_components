package org.moritas.inputcontroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import org.moritas.Settings;

import static java.lang.Math.PI;

/**
 * Handles camera movement, rotation, and zoom via keyboard and mouse input,
 * ensuring the camera always faces a central rotation point.
 *
 * <p><b>Controls:</b>
 * <ul>
 *   <li><b>WASD:</b> Moves the rotation center (forward/backward/left/right).</li>
 *   <li><b>Middle Mouse Drag:</b> Rotates the camera around the center (yaw/pitch).</li>
 *   <li><b>Mouse Wheel:</b> Adjusts the camera distance from the center (zoom).</li>
 * </ul>
 */
public class CameraController extends InputAdapter {

    private final Camera camera;

    private final Vector3 rotationCenter;
    private float distanceToCenter;
    private float yaw;
    private float pitch;

    private boolean middleMouseButtonPressed;
    private final IntIntMap pressedKeys = new IntIntMap();

    // convenience to store tmp values instead of initializing new vectors everywhere
    private final Vector3 tmp1 = new Vector3();
    private final Vector3 tmp2 = new Vector3();
    private final Vector3 tmp3 = new Vector3();

    public CameraController(Camera camera) {
        this(camera, Settings.CAM_START_POS);
    }

    public CameraController(Camera camera, Vector3 startPos) {
        this.camera = camera;

        // set initial values
        this.rotationCenter = startPos;
        this.distanceToCenter = Settings.CAM_START_DISTANCE;
        this.yaw = Settings.CAM_START_YAW;
        this.pitch = Settings.CAM_START_PITCH;

        // snap camera directly to its target so the first frame has a valid (invertible) view matrix
        camera.position.set(
                rotationCenter.x + distanceToCenter * (float) (Math.cos(pitch) * Math.sin(yaw)),
                rotationCenter.y + distanceToCenter * (float) Math.sin(pitch),
                rotationCenter.z + distanceToCenter * (float) (Math.cos(pitch) * Math.cos(yaw))
        );
        camera.direction.set(rotationCenter).sub(camera.position).nor();
        camera.update(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        pressedKeys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        pressedKeys.remove(keycode, 0);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE) {
            middleMouseButtonPressed = true;
            Gdx.input.setCursorCatched(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE) {
            middleMouseButtonPressed = false;
            Gdx.input.setCursorCatched(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        // camera rotation
        if (middleMouseButtonPressed) {

            float deltaX = Gdx.input.getDeltaX();
            float deltaY = Gdx.input.getDeltaY();

            // yaw difference
            float diffYaw = (float) Math.clamp(deltaX * Settings.CAM_ROTATION_SPEED, -0.1, 0.1); // clamp this, to avoid huge lerp distances
            this.yaw -= diffYaw;
            this.yaw %= (float) (2 * PI);

            // pitch difference
            float diffPitch = deltaY * Settings.CAM_ROTATION_SPEED;
            this.pitch += diffPitch;
            this.pitch = Math.clamp(pitch, Settings.CAM_MIN_PITCH, Settings.CAM_MAX_PITCH);

            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        distanceToCenter += amountY * Settings.CAM_ZOOM_SPEED;
        distanceToCenter = Math.clamp(distanceToCenter, Settings.CAM_MIN_DISTANCE, Settings.CAM_MAX_DISTANCE);
        return true;
    }

    /**
     * Updates camera position and orientation according to the distance to the rotation center and the pitch and yaw.
     */
    public void update(float deltaTime) {

        // move rotationCenter
        tmp3.setZero();
        float increment = deltaTime * Settings.CAM_SCROLL_SPEED;
        if (pressedKeys.containsKey(Input.Keys.W)) tmp3.add(fwdBwdVector(1));
        if (pressedKeys.containsKey(Input.Keys.S)) tmp3.add(fwdBwdVector(-1));
        if (pressedKeys.containsKey(Input.Keys.A)) tmp3.add(sideWaysVector(-1));
        if (pressedKeys.containsKey(Input.Keys.D)) tmp3.add(sideWaysVector(1));
        tmp3.nor().scl(increment);
        rotationCenter.add(tmp3);

        // calculate cam position to lerp to based on new rotationCenter position
        float target_x = (float) (rotationCenter.x + distanceToCenter * Math.cos(pitch) * Math.sin(yaw));
        float target_y = (float) (rotationCenter.y + distanceToCenter * Math.sin(pitch));
        float target_z = (float) (rotationCenter.z + distanceToCenter * Math.cos(pitch) * Math.cos(yaw));
        tmp1.set(target_x, target_y, target_z);
        camera.position.lerp(tmp1,Settings.CAM_LERP_FACTOR * deltaTime);

        // make camera direction point to rotationCenter
        tmp1.set(rotationCenter).sub(camera.position).nor(); // tmp1 = vector to centerPos
        this.camera.direction.set(tmp1);

        this.camera.update(true);
    }

    private Vector3 fwdBwdVector(int sign) {
        tmp1.set(camera.direction).y = 0;
        tmp1.nor().scl(sign);
        return tmp1;
    }

    private Vector3 sideWaysVector(int sign) {
        tmp2.set(camera.direction).y = 0;
        tmp2.nor();
        tmp1.set(tmp2).crs(camera.up).nor().scl(sign);
        return tmp1;
    }
}
