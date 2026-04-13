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

After setting these parameters, we need to call update() to calculate our cameras initial position and direction.
It’s important to use delta time here; otherwise, your camera’s movement will depend on the computer’s processing power—leading to inconsistent behavior across different systems, which is not what you want.
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

    // calculate cameras initial position and direction according to initial centerPos, distance, yaw and pitch
    update(Gdx.graphics.getDeltaTime());
}
```


<h3>WASD Input</h3>


<h3>Zoom</h3>
<h3>Rotation</h3>
<h3>update()</h3>
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
    public static final float CAM_LERP_FACTOR = 0.17f;
}
```