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