package org.hai.math;

public class vec3 {

    public static final vec3 XAXIS = new vec3(1.0f, 0.0f, 0.0f);
    public static final vec3 YAXIS = new vec3(0.0f, 1.0f, 0.0f);
    public static final vec3 ZAXIS = new vec3(0.0f, 0.0f, 1.0f);

    public float[] m = { 0.0f, 0.0f, 0.0f };

    public vec3() {}

    public vec3(float x) {
        this.m[0] = x;
        this.m[1] = x;
        this.m[2] = x;
    }

    public vec3(float x, float y, float z) {
        this.m[0] = x;
        this.m[1] = y;
        this.m[2] = z;
    }

    public vec3(float[] v) {
        int n = Math.min(this.m.length, 3);
        System.arraycopy(v, 0, this.m, 0, n);
    }

    public vec3(vec3 obj) {
        System.arraycopy(obj.m, 0, this.m, 0, 3);
    }

    public String toString() {
        return "<" + this.m[0] + ", " + this.m[1] + ", " + this.m[2] + ">";
    }

    /** \brief getter/setter for x, y, z
     *
     */
    public float x() { return this.m[0]; }
    public void  x(float val) { this.m[0] = val; }

    public float y() { return this.m[1]; }
    public void  y(float val) { this.m[1] = val; }

    public float z() { return this.m[2]; }
    public void  z(float val) { this.m[2] = val; }

    /** \brief getter/setter for r, g, b
     *
     */
    public float r() { return this.m[0]; }
    public void  r(float val) { this.m[0] = val; }

    public float g() { return this.m[1]; }
    public void  g(float val) { this.m[1] = val; }

    public float b() { return this.m[2]; }
    public void  b(float val) { this.m[2] = val; }

    /** \brief this = a
     *
     */
    public vec3 copy(vec3 a) {
        this.m[0] = a.m[0];
        this.m[1] = a.m[1];
        this.m[2] = a.m[2];
        return this;
    }

    /** \fn load
     *
     * \brief
     *
     */
    public vec3 set(float x, float y, float z) {
        this.m[0] = x;
        this.m[1] = y;
        this.m[2] = z;
        return this;
    }

    /** \brief this = a + b
     *
     */
    public vec3 add(vec3 a, vec3 b) {
        this.m[0] = a.m[0] + b.m[0];
        this.m[1] = a.m[1] + b.m[1];
        this.m[2] = a.m[2] + b.m[2];
        return this;
    }

    /** \brief Returns: a + b
     *
     */
    public static vec3 r_add(vec3 a, vec3 b) {
        float x = a.m[0] + b.m[0];
        float y = a.m[1] + b.m[1];
        float z = a.m[2] + b.m[2];
        vec3 result = new vec3( x, y, z );
        return result;
    }

    /** \brief this = a - b
     *
     */
    public vec3 sub(vec3 a, vec3 b) {
        this.m[0] = a.m[0] - b.m[0];
        this.m[1] = a.m[1] - b.m[1];
        this.m[2] = a.m[2] - b.m[2];
        return this;
    }

    /** \brief Returns: a - b
     *
     */
    public static vec3 r_sub(vec3 a, vec3 b) {
        float x = a.m[0] - b.m[0];
        float y = a.m[1] - b.m[1];
        float z = a.m[2] - b.m[2];
        vec3 result = new vec3( x, y, z );
        return result;
    }

    /** \brief this = a * b
     *
     */
    public vec3 mul(vec3 a, vec3 b) {
        this.m[0] = a.m[0] * b.m[0];
        this.m[1] = a.m[1] * b.m[1];
        this.m[2] = a.m[2] * b.m[2];
        return this;
    }

    /** \brief Returns: a * b
     *
     */
    public static vec3 r_mul(vec3 a, vec3 b) {
        float x = a.m[0] * b.m[0];
        float y = a.m[1] * b.m[1];
        float z = a.m[2] * b.m[2];
        vec3 result = new vec3( x, y, z );
        return result;
    }

    /** \brief this = a / b
     *
     */
    public vec3 div(vec3 a, vec3 b) {
        this.m[0] = a.m[0] / b.m[0];
        this.m[1] = a.m[1] / b.m[1];
        this.m[2] = a.m[2] / b.m[2];
        return this;
    }

    /** \brief Returns: a / b
     *
     */
    public static vec3 r_div(vec3 a, vec3 b) {
        float x = a.m[0] / b.m[0];
        float y = a.m[1] / b.m[1];
        float z = a.m[2] / b.m[2];
        vec3 result = new vec3( x, y, z );
        return result;
    }

    /** \fn scale
     *
     * \brief Scales the current vector
     * \param s
     *
     */
    public vec3 scale(float s) {
        this.m[0] *= s;
        this.m[1] *= s;
        this.m[2] *= s;
        return this;
    }

    /** \fn scaled
     *
     * \brief Returns a scaled copy of the current vector
     * \param s
     *
     */
    public vec3 scaled(float s) {
        float x = this.m[0];
        float y = this.m[1];
        float z = this.m[2];
        vec3 result = new vec3( x*s, y*s, z*s );
        return result;
    }


    /** \fn scale
     *
     * \brief Scales the current vector
     * \param s
     *
     */
    public static vec3 r_scale(vec3 v, float s) {
        float x = v.m[0];
        float y = v.m[1];
        float z = v.m[2];
        vec3 result = new vec3( x*s, y*s, z*s );
        return result;
    }

    /** \brief this = -this
     *
     */
    public vec3 negate() {
        this.m[0] = -this.m[0];
        this.m[1] = -this.m[1];
        this.m[2] = -this.m[2];
        return this;
    }

    /** \brief Returns: -this
     *
     */
    public vec3 negated() {
        vec3 result = new vec3( -this.m[0], -this.m[1], -this.m[2] );
        return result;
    }

    /** \brief Negated copy of a
     *
     */
    public vec3 copyNegated(vec3 a) {
        this.m[0] = -a.m[0];
        this.m[1] = -a.m[1];
        this.m[2] = -a.m[2];
        return this;
    }

    /** \brief this = normalize( this )
     *
     */
    public vec3 normalize() {
        float x = this.m[0];
        float y = this.m[1];
        float z = this.m[2];
        float s = 1.0f / (float)Math.sqrt( x*x + y*y + z*z );
        this.m[0] = x*s;
        this.m[1] = y*s;
        this.m[2] = z*s;
        return this;
    }

    /** \brief Returns: normalize( this )
     *
     */
    public vec3 normalized() {
        float x = this.m[0];
        float y = this.m[1];
        float z = this.m[2];
        float s = 1.0f / (float)Math.sqrt( x*x + y*y + z*z );
        vec3 result = new vec3( x*s, y*s, z*s );
        return result;
    }

    /** \brief Normalized copy of a
     *
     */
    public vec3 copyNormalized(vec3 a) {
        float x = a.m[0];
        float y = a.m[1];
        float z = a.m[2];
        float s = 1.0f / (float)Math.sqrt( x*x + y*y + z*z );
        this.m[0] = x*s;
        this.m[1] = y*s;
        this.m[2] = z*s;
        return this;
    }

    /** \brief Returns: dot( this, b )
     *
     */
    public float dot(vec3 b) {
        float xSq = this.m[0] * b.m[0];
        float ySq = this.m[1] * b.m[1];
        float zSq = this.m[2] * b.m[2];
        float result = xSq + ySq + zSq;
        return result;
    }

    /** \brief Returns: dot(vec3 a, vec3 b)
     *
     */
    public static float r_dot(vec3 a, vec3 b) {
        float xSq = a.m[0] * b.m[0];
        float ySq = a.m[1] * b.m[1];
        float zSq = a.m[2] * b.m[2];
        float result = xSq + ySq + zSq;
        return result;
    }

    /** \brief this = cross(vec3 a, vec3 b)
     *
     */
    public vec3 cross(vec3 a, vec3 b) {
        float ax = a.m[0];
        float ay = a.m[1];
        float az = a.m[2];
        float bx = b.m[0];
        float by = b.m[1];
        float bz = b.m[2];
        this.m[0] = ay*bz - az*by;
        this.m[1] = az*bx - ax*bz;
        this.m[2] = ax*by - ay*bx;
        return this;
    }

    /** \brief this = cross(vec3 a, vec3 b)
     *
     */
    public static vec3 r_cross(vec3 a, vec3 b) {
        float ax = a.m[0];
        float ay = a.m[1];
        float az = a.m[2];
        float bx = b.m[0];
        float by = b.m[1];
        float bz = b.m[2];
        float x = ay*bz - az*by;
        float y = az*bx - ax*bz;
        float z = ax*by - ay*bx;
        vec3 result = new vec3( x, y, z );
        return result;
    }

    /** \brief Returns: length( this )
     *
     */
    public float length() {
        float x = this.m[0];
        float y = this.m[1];
        float z = this.m[2];
        float result = (float)Math.sqrt( x*x + y*y + z*z );
        return result;
    }

    /** \brief Returns: lengthSquared( this )
     *
     */
    public float lengthSquared() {
        float x = this.m[0];
        float y = this.m[1];
        float z = this.m[2];
        float result = x*x + y*y + z*z;
        return result;
    }

    /** \brief distance( this, b )
     *
     */
    public float distance(vec3 b) {
        float x = this.m[0] - b.m[0];
        float y = this.m[1] - b.m[1];
        float z = this.m[2] - b.m[2];
        float result = (float)Math.sqrt( x*x + y*y + z*z );
        return result;
    }

    /** \brief distance(vec3 a, vec3 b)
     *
     */
    public static float r_distance(vec3 a, vec3 b) {
        float x = a.m[0] - b.m[0];
        float y = a.m[1] - b.m[1];
        float z = a.m[2] - b.m[2];
        float result = (float)Math.sqrt( x*x + y*y + z*z );
        return result;
    }

    /** \brief distanceSquared( this, b )
     *
     */
    public float distanceSquared(vec3 b) {
        float x = this.m[0] - b.m[0];
        float y = this.m[1] - b.m[1];
        float z = this.m[2] - b.m[2];
        float result = x*x + y*y + z*z;
        return result;
    }

    /** \brief distanceSquared(vec3 a, vec3 b)
     *
     */
    public static float r_distanceSquared(vec3 a, vec3 b) {
        float x = a.m[0] - b.m[0];
        float y = a.m[1] - b.m[1];
        float z = a.m[2] - b.m[2];
        float result = x*x + y*y + z*z;
        return result;
    }

    /** \brief this = lerp( a, b, t )
     *
     */
    public vec3 lerp(vec3 a, vec3 b, float t) {
        float dx = b.m[0] - a.m[0];
        float dy = b.m[1] - a.m[1];
        float dz = b.m[2] - a.m[2];
        this.m[0] = a.m[0] + t*dx;
        this.m[1] = a.m[1] + t*dy;
        this.m[2] = a.m[2] + t*dz;
        return this;
    }

    /** \brief Returns: lerp( a, b, t )
     *
     */
    public static vec3 r_lerp(vec3 a, vec3 b, float t) {
        float dx = b.m[0] - a.m[0];
        float dy = b.m[1] - a.m[1];
        float dz = b.m[2] - a.m[2];
        float x = a.m[0] + t*dx;
        float y = a.m[1] + t*dy;
        float z = a.m[2] + t*dz;
        vec3 result = new vec3(x, y, z);
        return result;
    }
}