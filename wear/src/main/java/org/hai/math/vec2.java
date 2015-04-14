package org.hai.math;

public class vec2 {

    public static final vec2 XAXIS = new vec2(1.0f, 0.0f);
    public static final vec2 YAXIS = new vec2(0.0f, 1.0f);

    public float[] m = { 0.0f, 0.0f };

    public vec2() {}

    public vec2(float x) {
        this.m[0] = x;
        this.m[1] = x;
    }

    public vec2(float x, float y) {
        this.m[0] = x;
        this.m[1] = y;
    }

    public vec2(float[] v) {
        int n = Math.min(this.m.length, 2);
        System.arraycopy(v, 0, this.m, 0, n);
    }

    public vec2(vec2 obj) {
        System.arraycopy(obj.m, 0, this.m, 0, 2);
    }

    public String toString() {
        return "<" + this.m[0] + ", " + this.m[1] + ">";
    }

    /** \brief getter/setter for x, y, z
     *
     */
    public float x() { return this.m[0]; }
    public void  x(float val) { this.m[0] = val; }

    public float y() { return this.m[1]; }
    public void  y(float val) { this.m[1] = val; }

    /** \brief getter/setter for r, g, b
     *
     */
    public float r() { return this.m[0]; }
    public void  r(float val) { this.m[0] = val; }

    public float g() { return this.m[1]; }
    public void  g(float val) { this.m[1] = val; }

    /** \brief this = a
     *
     */
    public vec2 copy(vec2 a) {
        this.m[0] = a.m[0];
        this.m[1] = a.m[1];
        return this;
    }

    /** \fn load
     *
     * \brief
     *
     */
    public vec2 set(float x, float y) {
        this.m[0] = x;
        this.m[1] = y;
        return this;
    }    
}
