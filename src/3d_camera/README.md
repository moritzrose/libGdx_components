[← Back to main](https://morigamio.github.io/libGdx_components/)

<h2>3D Game Camera</h2>

<h3>Features:</h3>
  - move the camera with WASD
  - rotate and tilt the camera around a center with middle mouse button
  - zoom in and zoom out with scroll wheel
  - smooth movement
  - rotation speed, scroll speed, zoom speed and min max values for angles or distances are customizable in a settings class

<h3>Implementation:</h3>

This implementation is split into two parts:
   
<ol>
<li>Change the camera's parameters like: position, distance to center, pitch and yaw</li>
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
<h4>Instance Variables</h4>
As said above, we need to change the cameras parameters. We will store those in instance variables.
If you do not know exactly what certain words mean, I would suggest googling them - they should not be to complex to understand.
```java
public class CameraController extends InputAdapter {

    private final Camera camera;

    private float pitch;
    private float yaw;
    private float distanceToCenter;
    private final Vector3 rotationCenter;

    private boolean middleMouseButtonPressed;
    private boolean leftMouseButtonPressed;
    private boolean rightMouseButtonPressed;
    private final IntIntMap pressedKeys = new IntIntMap();

    // convenience to store tmp values instead of initializing new vectors everywhere
    private final Vector3 tmp1 = new Vector3();
    private final Vector3 tmp2 = new Vector3();
    private final Vector3 tmp3 = new Vector3();

    // constructor + rest of the class
```
We start with variables for the pitch, the yaw, the distance to the rotation center and a vector for the position of the rotation center itself.

We also need a boolean to store, if the middle mouse button was pressed and a so called IntIntMap, where we store the keys, that were pressed in this loop.

The IntIntMap might be new to you, it basically works like a normal java map with int keys and int values, but was optimized concerning resource management and efficiency. Feel free to dive deeper into that topic!

Lastly, we are going to initiate three temporary vectors - I basically added them as I go and ended up with three in this implementation. 
I like having "containers" ready to fill whenever I need to without having to initialize new vectors all over the place. Give our garbage collector a break!


<h4>WASD Input</h4>


<h4>Zoom</h4>
<h4>Rotation</h4>
