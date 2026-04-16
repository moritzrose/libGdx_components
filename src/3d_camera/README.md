[← Back to main](https://morigamio.github.io/libGdx_components/)

<h1>3D Game Camera</h1>

<h2>Features:</h2>
  - move the camera with WASD
  - rotate and tilt the camera around a center with middle mouse button
  - zoom in and zoom out with scroll wheel
  - smooth movement
  - rotation speed, scroll speed, zoom speed and min max values for angles or distances are customizable in a settings class

<h2>Implementation:</h2>

This implementation is split into two parts:
   
<ol>
<li>Change the camera's parameters like position, distance to center, pitch and yaw</li>
<li>Update the camera's position and orientation based on the previously modified parameters</li>
</ol>

    

To process input via keyboard, mouse or touch, we need a class that implements the methods from the InputProcessor interface.
We can make our life easier by extending the InputAdapter, which is simply an implementation of that interface, which returns false for every method.
This way, we only need to override the methods we need.

So lets create a new class - I called mine CameraController. We are also going to need a constructor where we pass in the camera, since we are going to manipulate its parameters.

Lastly, we are going to add a method called update() where we update the camera, after processing the input.

```java
public class CameraController extends InputAdapter {

    public CameraController(Camera camera) {
        this(camera);
    }

    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return true;
    }

    /**
     * Updates camera position and orientation according to the distance to the rotation center, the pitch and the yaw.
     */
    public void update(float deltaTime) {
        this.camera.update(true);
    }
}
```
<h3>Instance Variables</h3>
As said above, we need to change the cameras parameters. We will store those in instance variables.
If you are not familiar with certain words, I would suggest googling them - they should not be to hard to understand.
```java
public class CameraController extends InputAdapter {

    private final Camera camera;

    private float pitch;
    private float yaw;
    private float distanceToCenter;
    private final Vector3 rotationCenter;

    private boolean middleMouseButtonPressed;
    private final IntIntMap pressedKeys = new IntIntMap();

    // convenience to store tmp values instead of initializing new vectors everywhere
    private final Vector3 tmp1 = new Vector3();
    private final Vector3 tmp2 = new Vector3();
    private final Vector3 tmp3 = new Vector3();

    // constructor + rest of the class
    // ...
}
```
We start with variables for the pitch, the yaw, the distance to the rotation center and a vector for the position of the rotation center itself.

We also need a boolean to store, if the middle mouse button was pressed and a so called IntIntMap, where we store the keys, that were pressed in this loop.

The IntIntMap might be new to you. It basically works like a normal java map with int keys and int values, but was optimized concerning resource management and efficiency. Feel free to dive deeper into that topic!

Lastly, we are going to initiate three temporary vectors - I usually add them as I go and ended up with three in this implementation. 
I like having "containers" ready to fill whenever I need to without having to initialize new vectors all over the place. Give our garbage collector a break!

<h3>Constructors</h3>

We will initialize our camera parameters with start values. 

To keep everything nice and clean, I like to store those in a constance class 'Settings'. Feel free to adjust them to your needs.
You can find the class at the end of the page.

We are also adding a second constructor - this is just convenience to be able to set a start position. 

After setting these parameters, we need to place the camera at its correct initial position.
You might be tempted to just call `update()` here — but `update()` uses lerping to smoothly move the camera over time. On the very first frame there is no previous position to lerp from, which can leave the view matrix in an invalid state.
Instead, we calculate the position directly and snap the camera there right away.

The position is derived from spherical coordinates: given a center point, a distance, a yaw (horizontal angle) and a pitch (vertical angle), we get:
```
x = center.x + distance * cos(pitch) * sin(yaw)
y = center.y + distance * sin(pitch)
z = center.z + distance * cos(pitch) * cos(yaw)
```
```java
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
```


<h3>WASD Input</h3>

These two methods are pretty straight forward. Whenever a key is pressed, we store the corresponding keycode in pressedKeys. When we release the key, we remove it. 
We also return true to signal, that the input was processed.
```java
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
```

<h3>Zoom</h3>

Zooming is the simplest part — we just adjust the distance to the rotation center when the scroll wheel moves.
`amountY` is positive when scrolling down (zoom out) and negative when scrolling up (zoom in). We then clamp the result so the camera can never get too close or clip through the scene.

```java
@Override
public boolean scrolled(float amountX, float amountY) {
    distanceToCenter += amountY * Settings.CAM_ZOOM_SPEED;
    distanceToCenter = Math.clamp(distanceToCenter, Settings.CAM_MIN_DISTANCE, Settings.CAM_MAX_DISTANCE);
    return true;
}
```

<h3>Rotation</h3>

Rotation works in three steps: detect when the middle mouse button is pressed, track its state, and use the mouse delta to update yaw and pitch while it is held.

When the middle button goes down, we catch the cursor so it can't leave the window while rotating. On release, we free it again.

```java
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
```

While dragging, we read the mouse delta and translate it into changes in yaw and pitch.
Notice that yaw is clamped to a small step per frame. Since the camera tries to reach target coordinates, allowing huge target vectors would make our camera move closer to  the center, since it tries to take the shortest way. The graph below illustrates that. 
![huge rotation diff.png](C%3A/Users/morit/IdeaProjects/libGdx_components/src/3d_camera/huge%20rotation%20diff.png)
Pitch is simply clamped to the configured min/max range so the camera can't flip upside down.

```java
@Override
public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (middleMouseButtonPressed) {
        float deltaX = Gdx.input.getDeltaX();
        float deltaY = Gdx.input.getDeltaY();

        // yaw difference
        float diffYaw = (float) Math.clamp(deltaX * Settings.CAM_ROTATION_SPEED, -0.1, 0.1);
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
```

<h3>update()</h3>

`update()` is called once per game loop. It does three things in order: move the rotation center based on held keys, lerp the camera position toward its target based on distance to center, yaw and pitch, then point the camera at the center.

**Moving the rotation center**

We accumulate a movement vector from whichever WASD keys are currently held, normalize it (so diagonal movement is not faster), scale it by speed and delta time, and add it to `rotationCenter`.

The two helper methods `fwdBwdVector` and `sideWaysVector` build direction vectors that are projected onto the horizontal plane (y = 0) so that moving forward never changes the height of the center — regardless of the camera's current pitch.

**Lerping the camera position**

From the current `rotationCenter`, `distanceToCenter`, `yaw` and `pitch` we compute the exact target position using the same spherical coordinate formula as in the constructor. We then lerp the camera toward that target.

It is important to multiply the lerp factor by `deltaTime` here. Without it, the smoothing speed would depend on the frame rate — on a fast machine the camera would feel snappier than on a slow one.

**Pointing at the center**

Finally, we set the camera direction to the vector from its current position to `rotationCenter` and call `camera.update(true)` to apply all changes.

```java
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
    camera.position.lerp(tmp1, Settings.CAM_LERP_FACTOR * deltaTime);

    // make camera direction point to rotationCenter
    tmp1.set(rotationCenter).sub(camera.position).nor();
    this.camera.direction.set(tmp1);

    this.camera.update(true);
}

private Vector3 fwdBwdVector(int sign) {
    tmp1.set(camera.direction);
    tmp1.y = 0;
    tmp1.nor().scl(sign);
    return tmp1;
}

private Vector3 sideWaysVector(int sign) {
    tmp2.set(camera.direction);
    tmp2.y = 0;
    tmp2.nor();
    tmp1.set(tmp2).crs(camera.up).nor().scl(sign);
    return tmp1;
}
```

<h3>Settings</h3>
Using radians for angles is more appropriate here, since trigonometric functions (sin, cos) in most libraries expect radians by default. While you can use degrees, you’d need to manually convert them to radians first—which adds unnecessary complexity.

These values are the result of experimentation and trial and error. Feel free to tweak them to better suit your specific needs.
```java
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
    public static final float CAM_LERP_FACTOR = 11.6f;
}
```