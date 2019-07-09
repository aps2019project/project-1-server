package ap.spring2019.project.server;

public class MousePos {
    public float x;
    public float y;
    public MouseState mouseState = MouseState.NOTHING;

    public MousePos(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setMouseState(MouseState mouseState) {
        this.mouseState = mouseState;
    }

    public MouseState getMouseState() {
        return mouseState;
    }
}
