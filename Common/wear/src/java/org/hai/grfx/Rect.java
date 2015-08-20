package org.hai.grfx;

/**
 * Created by hai on 4/12/15.
 */
public class Rect {
    public float x1 = 0.0f;
    public float y1 = 0.0f;
    public float x2 = 0.0f;
    public float y2 = 0.0f;

    public Rect() {
    }

    public Rect(float ax1, float ay1, float ax2, float ay2) {
        x1 = ax1;
        y1 = ay1;
        x2 = ax2;
        y2 = ay2;
    }

    public Rect(Rect obj) {
        x1 = obj.x1;
        y1 = obj.y1;
        x2 = obj.x2;
        y2 = obj.y2;
    }

    public String toString() {
        String result = "[" + x1 + ", " + y1 +"] - [" + x2 + ", " + y2 + "]";
        return result;
    }

    public float getWidth() {
        return x2 - x1;
    }

    public float getHeight() {
        return y2 - y1;
    }
}
