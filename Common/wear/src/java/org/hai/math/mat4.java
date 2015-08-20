package org.hai.math;

public class mat4 {

    public float[] m = {
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,
    };

    public mat4() {}

    /** \fn constructor
     * 
     * \brief Constructor for mat4.
     * 
     * \param a00 [float]
     * \param a01 [float]
     * \param a02 [float]
     * \param a03 [float]
     * \param a10 [float]
     * \param a11 [float]
     * \param a12 [float]
     * \param a13 [float]
     * \param a20 [float]
     * \param a21 [float]
     * \param a22 [float]
     * \param a23 [float]
     * \param a30 [float]
     * \param a31 [float]
     * \param a32 [float]
     * \param a33 [float]
     *
     * Initializes the newly created mat4 with [a00..a33] if they are present.
     * Otherwise the newly created matrix will contain and identity matrix.
     * 
     */
    public mat4 (
        float a00, float a01, float a02, float a03,
        float a10, float a11, float a12, float a13,
        float a20, float a21, float a22, float a23,
        float a30, float a31, float a32, float a33
    ) {
        this.m[ 0] = a00;
        this.m[ 4] = a01;
        this.m[ 8] = a02;
        this.m[12] = a03;
        this.m[ 1] = a10;
        this.m[ 5] = a11;
        this.m[ 9] = a12;
        this.m[13] = a13;
        this.m[ 2] = a20;
        this.m[ 6] = a21;
        this.m[10] = a22;
        this.m[14] = a23;
        this.m[ 3] = a30;
        this.m[ 7] = a31;
        this.m[11] = a32;
        this.m[15] = a33;
    }

    public mat4(mat4 obj) {
        System.arraycopy(obj.m, 0, this.m, 0, 16);
    }

    /** \fn str
     *
     * \brief Returns string of elements
     *
     */
    public String toString() {
        String result = "[";
        for(int i = 0; i < 16; ++i) {
            result += this.m[i];
            if (i < 15) {
                result += " ";
            }
        }
        result += "]";
        return result;
    }

    /** \fn copy
     *
     * \brief Copies the elements of matA to current matrix
     *
     */
    public mat4 copy(mat4 matA) {
        for(int i = 0; i < 16; ++i) {
            this.m[i] = matA.m[i];
        }
        return this;
    }

    /** \fn load
     *
     * \brief Replaces the current matrix with [a00..a33]
     *
     * Replaces the current matrix with [a00..a33] if they are present.
     * Otherwise the current matrix will be loaded with an identity matrix.
     *
     */
    public mat4 set(
        float a00, float a01, float a02, float a03,
        float a10, float a11, float a12, float a13,
        float a20, float a21, float a22, float a23,
        float a30, float a31, float a32, float a33
    ) {
        this.m[ 0] = a00;
        this.m[ 4] = a01;
        this.m[ 8] = a02;
        this.m[12] = a03;
        this.m[ 1] = a10;
        this.m[ 5] = a11;
        this.m[ 9] = a12;
        this.m[13] = a13;
        this.m[ 2] = a20;
        this.m[ 6] = a21;
        this.m[10] = a22;
        this.m[14] = a23;
        this.m[ 3] = a30;
        this.m[ 7] = a31;
        this.m[11] = a32;
        this.m[15] = a33;
        return this;
    }

    /** \fn setIdentity
     *
     * \brief Replaces the current matrix with identity matrix
     *
     */
    public mat4 setIdentity() {
        this.m[ 0] = 1.0f;
        this.m[ 4] = 0.0f;
        this.m[ 8] = 0.0f;
        this.m[12] = 0.0f;
        this.m[ 1] = 0.0f;
        this.m[ 5] = 1.0f;
        this.m[ 9] = 0.0f;
        this.m[13] = 0.0f;
        this.m[ 2] = 0.0f;
        this.m[ 6] = 0.0f;
        this.m[10] = 1.0f;
        this.m[14] = 0.0f;
        this.m[ 3] = 0.0f;
        this.m[ 7] = 0.0f;
        this.m[11] = 0.0f;
        this.m[15] = 1.0f;
        return this;
    }

    /** \fn add add
     *
     * \brief Replaces current matrix with matA + matB
     *
     * \param matA [mat4]
     * \param matB [mat4]
     *
     */
    public mat4 add(mat4 matA, mat4 matB) {
        float[] a = matA.m;
        float[] b = matB.m;
        for(int i = 0; i < 16; ++i) {
            this.m[i] = a[i] + b[i];
        }
        return this;
    }

    /** \fn add
     *
     * \brief Returns matA + matB
     *
     * \returns [mat4] - returns a matrix that is the result of: matA + matB
     *
     * \param matA [mat4]
     * \param matB [mat4]
     *
     */
    public static mat4 r_add(mat4 matA, mat4 matB) {
        float[] a = matA.m;
        float[] b = matB.m;
        mat4 result = new mat4();
        for(int i = 0; i < 16; ++i) {
            result.m[i] = a[i] + b[i];
        }
        return result;
    }

    /** \fn sub
     *
     * \brief Replaces current matrix with matA - matB
     *
     */
    public mat4 sub(mat4 matA, mat4 matB) {
        float[] a = matA.m;
        float[] b = matB.m;
        for(int i = 0; i < 16; ++i) {
            this.m[i] = a[i] - b[i];
        }
        return this;
    }

    /** \fn sub
     *
     * \brief Returns matA - matB
     *
     */
    public static mat4 r_sub(mat4 matA, mat4 matB) {
        float[] a = matA.m;
        float[] b = matB.m;
        mat4 result = new mat4();
        for(int i = 0; i < 16; ++i) {
            result.m[i] = a[i] - b[i];
        }
        return result;
    }

    /** \fn mul
     *
     * \brief Replaces current matrix with matA * matB
     *
     */
    public mat4 mul(mat4 matA, mat4 matB) {
        float[] a = matA.m;
        float[] b = matB.m;

        this.m[ 0] = a[ 0] * b[ 0] + a[ 4] * b[ 1] + a[ 8] * b[ 2] + a[12] * b[ 3];
        this.m[ 1] = a[ 1] * b[ 0] + a[ 5] * b[ 1] + a[ 9] * b[ 2] + a[13] * b[ 3];
        this.m[ 2] = a[ 2] * b[ 0] + a[ 6] * b[ 1] + a[10] * b[ 2] + a[14] * b[ 3];
        this.m[ 3] = a[ 3] * b[ 0] + a[ 7] * b[ 1] + a[11] * b[ 2] + a[15] * b[ 3];

        this.m[ 4] = a[ 0] * b[ 4] + a[ 4] * b[ 5] + a[ 8] * b[ 6] + a[12] * b[ 7];
        this.m[ 5] = a[ 1] * b[ 4] + a[ 5] * b[ 5] + a[ 9] * b[ 6] + a[13] * b[ 7];
        this.m[ 6] = a[ 2] * b[ 4] + a[ 6] * b[ 5] + a[10] * b[ 6] + a[14] * b[ 7];
        this.m[ 7] = a[ 3] * b[ 4] + a[ 7] * b[ 5] + a[11] * b[ 6] + a[15] * b[ 7];

        this.m[ 8] = a[ 0] * b[ 8] + a[ 4] * b[ 9] + a[ 8] * b[10] + a[12] * b[11];
        this.m[ 9] = a[ 1] * b[ 8] + a[ 5] * b[ 9] + a[ 9] * b[10] + a[13] * b[11];
        this.m[10] = a[ 2] * b[ 8] + a[ 6] * b[ 9] + a[10] * b[10] + a[14] * b[11];
        this.m[11] = a[ 3] * b[ 8] + a[ 7] * b[ 9] + a[11] * b[10] + a[15] * b[11];

        this.m[12] = a[ 0] * b[12] + a[ 4] * b[13] + a[ 8] * b[14] + a[12] * b[15];
        this.m[13] = a[ 1] * b[12] + a[ 5] * b[13] + a[ 9] * b[14] + a[13] * b[15];
        this.m[14] = a[ 2] * b[12] + a[ 6] * b[13] + a[10] * b[14] + a[14] * b[15];
        this.m[15] = a[ 3] * b[12] + a[ 7] * b[13] + a[11] * b[14] + a[15] * b[15];

        return this;
    }

    /** \fn mul
     *
     * \brief Returns matA * matB
     *
     */
    public static mat4 r_mul(mat4 matA, mat4 matB) {
        float[] a = matA.m;
        float[] b = matB.m;
        mat4 result = new mat4();

        result.m[ 0] = a[ 0] * b[ 0] + a[ 4] * b[ 1] + a[ 8] * b[ 2] + a[12] * b[ 3];
        result.m[ 1] = a[ 1] * b[ 0] + a[ 5] * b[ 1] + a[ 9] * b[ 2] + a[13] * b[ 3];
        result.m[ 2] = a[ 2] * b[ 0] + a[ 6] * b[ 1] + a[10] * b[ 2] + a[14] * b[ 3];
        result.m[ 3] = a[ 3] * b[ 0] + a[ 7] * b[ 1] + a[11] * b[ 2] + a[15] * b[ 3];

        result.m[ 4] = a[ 0] * b[ 4] + a[ 4] * b[ 5] + a[ 8] * b[ 6] + a[12] * b[ 7];
        result.m[ 5] = a[ 1] * b[ 4] + a[ 5] * b[ 5] + a[ 9] * b[ 6] + a[13] * b[ 7];
        result.m[ 6] = a[ 2] * b[ 4] + a[ 6] * b[ 5] + a[10] * b[ 6] + a[14] * b[ 7];
        result.m[ 7] = a[ 3] * b[ 4] + a[ 7] * b[ 5] + a[11] * b[ 6] + a[15] * b[ 7];

        result.m[ 8] = a[ 0] * b[ 8] + a[ 4] * b[ 9] + a[ 8] * b[10] + a[12] * b[11];
        result.m[ 9] = a[ 1] * b[ 8] + a[ 5] * b[ 9] + a[ 9] * b[10] + a[13] * b[11];
        result.m[10] = a[ 2] * b[ 8] + a[ 6] * b[ 9] + a[10] * b[10] + a[14] * b[11];
        result.m[11] = a[ 3] * b[ 8] + a[ 7] * b[ 9] + a[11] * b[10] + a[15] * b[11];

        result.m[12] = a[ 0] * b[12] + a[ 4] * b[13] + a[ 8] * b[14] + a[12] * b[15];
        result.m[13] = a[ 1] * b[12] + a[ 5] * b[13] + a[ 9] * b[14] + a[13] * b[15];
        result.m[14] = a[ 2] * b[12] + a[ 6] * b[13] + a[10] * b[14] + a[14] * b[15];
        result.m[15] = a[ 3] * b[12] + a[ 7] * b[13] + a[11] * b[14] + a[15] * b[15];

        return result;
    }

    /** \fn negate
     *
     * \brief Negates the current matrix
     *
     */
    public mat4 negate() {
        for(int i = 0; i < 16; ++i) {
            this.m[i] = -this.m[i];
        }
        return this;
    }

    /** \fn negated
     *
     * \brief Returns a negated copy of the current matrix
     *
     */
    public mat4 negated() {
        mat4 result = new mat4(this);
        result.negate();
        return result;
    }

    /** \fn copyNegated
     *
     * \brief Sets current matrix to a negated copy of matA
     * 
     * \param matA [mat4]
     *
     */
    public mat4 copyNegated(mat4 matA) {
        for(int i = 0; i < 16; ++i) {
            this.m[i] = -matA.m[i];
        }
        return this;
    }

    /** \fn invert
     *
     * \brief Inverts the current matrix
     *
     */
    public mat4 invert() {
        mat4_util_inplace_invert(this.m);
        return this;
    }

    /** \fn inverted
     *
     * \brief Returns an inverted copy of the current matrix
     *
     */
    public mat4 inverted() {
        mat4 result = new mat4(this);
        mat4_util_inplace_invert(result.m);
        return result;
    }

    /** \fn copyInverted
     *
     * \brief Sets current matrix to an inverted copy of matA
     *
     * \param matA [mat4]
     *
     */
    public mat4 copyInverted(mat4 matA) {
        this.copy(matA);
        mat4_util_inplace_invert(this.m);
        return this;
    }

    /** \fn translate
     *
     * \brief Translates current matrix using v
     *
     * \param v vec3
     *
     */
    public mat4 translate(vec3 v) {
        float[] m = this.m;
        m[12] += m[ 0] * v.x() + m[ 4] * v.y() + m[ 8] * v.z();
        m[13] += m[ 1] * v.x() + m[ 5] * v.y() + m[ 9] * v.z();
        m[14] += m[ 2] * v.x() + m[ 6] * v.y() + m[10] * v.z();
        m[15] += m[ 3] * v.x() + m[ 7] * v.y() + m[11] * v.z();
        return this;
    }

    /** \fn translate
     *
     * \brief Translates current matrix using x,y,z
     *
     * \param x float
     * \param y float
     * \param z float
     *
     */
    public mat4 translate(float x, float y, float z) {
        float[] m = this.m;
        m[12] += m[ 0] * x + m[ 4] * y + m[ 8] * z;
        m[13] += m[ 1] * x + m[ 5] * y + m[ 9] * z;
        m[14] += m[ 2] * x + m[ 6] * y + m[10] * z;
        m[15] += m[ 3] * x + m[ 7] * y + m[11] * z;
        return this;
    }

    /** \fn loadTranslate
     *
     * \brief Replaces the current matrix with a translation matrix using v
     *
     * \param v vec3
     *
     */
    public mat4 setTranslate(vec3 v) {
        this.setIdentity();
        this.m[12] = v.x();
        this.m[13] = v.y();
        this.m[14] = v.z();
        return this;
    }

    /** \fn loadTranslate
     *
     * \brief Replaces the current matrix with a translation matrix using x,y,z
     *
     * \param x float
     * \param y float
     * \param z float
     *
     */
    public mat4 setTranslate(float x, float y, float z) {
        this.setIdentity();
        this.m[12] = x;
        this.m[13] = y;
        this.m[14] = z;
        return this;
    }

    /** \fn rotate
     *
     * \brief Rotates current matrix using v
     *
     * \param v vec3
     *
     */
    public mat4 rotate(vec3 v) {
        mat4_util_inplace_rotate(this.m, v.x(), v.y(), v.z());
        return this;
    }

    /** \fn rotate
     *
     * \brief Rotates current matrix using x,y,z
     *
     * \param x float
     * \param y float
     * \param z float
     *
     */
    public mat4 rotate(float x, float y, float z) {
        mat4_util_inplace_rotate(this.m, x, y, z);
        return this;
    }

    /** \fn loadRotate
     *
     * \brief Replaces the current matrix with a rotation matrix using v
     *
     * \param v vec3
     *
     */
    public mat4 loadRotate(vec3 v) {
        mat4_util_load_rotate(this.m, v.x(), v.y(), v.z());
        return this;
    }

    /** \fn loadRotate
     *
     * \brief Replaces the current matrix with a rotation matrix using x,y,z
     *
     * \param x float Euler Radians
     * \param y float Euler Radians
     * \param z float Euler Radians
     *
     */
    public mat4 loadRotate(float x, float y, float z) {
        mat4_util_load_rotate(this.m, x, y, z);
        return this;
    }


    /** \fn scale
     *
     * \brief Scales current matrix using v
     *
     * \param v vec3
     *
     */
    public mat4 scale(vec3 v) {
        float[] m = this.m;
        m[ 0] *= v.x();
        m[ 4] *= v.y();
        m[ 8] *= v.z(); // m[12] *= 1.0
        m[ 1] *= v.x();
        m[ 5] *= v.y();
        m[ 9] *= v.z(); // m[13] *= 1.0
        m[ 2] *= v.x();
        m[ 6] *= v.y();
        m[10] *= v.z(); // m[14] *= 1.0
        m[ 3] *= v.x();
        m[ 7] *= v.y();
        m[11] *= v.z(); // m[15] *= 1.0
        return this;
    }

    /** \fn scale
     *
     * \brief Scales current matrix using x,y,z
     *
     * \param x float
     * \param y float
     * \param z float
     *
     */
    public mat4 scale(float x, float y, float z) {
        float[] m = this.m;
        m[ 0] *= x;
        m[ 4] *= y;
        m[ 8] *= z; // m[12] *= 1.0
        m[ 1] *= x;
        m[ 5] *= y;
        m[ 9] *= z; // m[13] *= 1.0
        m[ 2] *= x;
        m[ 6] *= y;
        m[10] *= z; // m[14] *= 1.0
        m[ 3] *= x;
        m[ 7] *= y;
        m[11] *= z; // m[15] *= 1.0
        return this;
    }

    /** \fn scale
     *
     * \brief Scales current matrix using s
     *
     * \param s float
     *
     */
    public mat4 scale(float s) {
        float[] m = this.m;
        m[ 0] *= s;
        m[ 4] *= s;
        m[ 8] *= s; // m[12] *= 1.0
        m[ 1] *= s;
        m[ 5] *= s;
        m[ 9] *= s; // m[13] *= 1.0
        m[ 2] *= s;
        m[ 6] *= s;
        m[10] *= s; // m[14] *= 1.0
        m[ 3] *= s;
        m[ 7] *= s;
        m[11] *= s; // m[15] *= 1.0
        return this;
    }

    /** \fn setScale
     *
     * \brief Replaces the current matrix with a scale matrix using v
     *
     * \param v vec3
     *
     */
    public mat4 setScale(vec3 v) {
        this.setIdentity();
        this.m[ 0] = v.x();
        this.m[ 5] = v.y();
        this.m[10] = v.z();
        return this;
    }

    /** \fn setScale
     *
     * \brief Replaces the current matrix with a scale matrix using x,y,z
     *
     * \param x float
     * \param y float
     * \param z float
     *
     */
    public mat4 setScale(float x, float y, float z) {
        this.setIdentity();
        this.m[ 0] = x;
        this.m[ 5] = y;
        this.m[10] = z;
        return this;
    }

    /** \fn setscale
     *
     * \brief Replaces the current matrix with a scale matrix using x,y,z
     *
     * \param x [float]
     * \param y [float]
     * \param z [float]
     *
     */
    public mat4 setscale(float s) {
        this.setIdentity();
        this.m[ 0] = s;
        this.m[ 5] = s;
        this.m[10] = s;
        return this;
    }

    /** \fn lookAt
     *
     * \brief Replaces current matrix with a lookAt(model view) matrix
     *
     * \param eyePoint [vec3]
     * \param target   [vec3]
     * \param worldUp  [vec3]
     *
     */
    public mat4 lookAt(vec3 eyePoint, vec3 target, vec3 worldUp) {
        vec3 w = new vec3();
        vec3 u = new vec3();
        vec3 v = new vec3();

        w.sub(target, eyePoint);
        w.normalize();
        w.negate();

        u.cross(worldUp, w);
        v.cross(w, u);

        float dx = -eyePoint.dot(u);
        float dy = -eyePoint.dot(v);
        float dz = -eyePoint.dot(w);

        this.set(
            u.x(), u.y(), u.z(),   dx,
            v.x(), v.y(), v.z(),   dy,
            w.x(), w.y(), w.z(),   dz,
             0.0f,  0.0f,  0.0f, 1.0f
        );
        return this;
    }

    /** \fn perspective
     *
     * \brief Replaces current matrix with a perspective matrix
     *
     * \param fovDegrees [float]
     * \param aspect     [float]
     * \param near       [float]
     * \param far        [float]
     *
     */
    public mat4 perspective(float fovDegrees, float aspect, float near, float far) {
        float t = near * (float)Math.tan((fovDegrees / 2.0f) * Math.PI / 180.0f);
        float b = -t;
        float r = aspect * t;
        float l = -r;
        float n = near;
        float f = far;
        float rl = r - l;
        float tb = t - b;
        float fn = f - n;

        float[] m = this.m;
        m[ 0] =  (2.0f * n) / rl;
        m[ 1] =  0.0f;
        m[ 2] =  0.0f;
        m[ 3] =  0.0f;
        m[ 4] =  0.0f;
        m[ 5] =  (2.0f * n) / tb;
        m[ 6] =  0.0f;
        m[ 7] =  0.0f;
        m[ 8] =  (r + l) / rl;
        m[ 9] =  (t + b) / tb;
        m[10] = -(f + n) / fn;
        m[11] = -1.0f;
        m[12] =  0.0f;
        m[13] =  0.0f;
        m[14] = -(2.0f * f * n) / fn;
        m[15] =  0.0f;

        return this;
    }

    /** \fn transformVector
     *
     * \brief Returns a transformed vector
     *
     * \param v [vec3]
     *
     */
    public vec3 transformVector(vec3 v) {
        vec3 result = new vec3();
        result.m[ 0] = this.m[ 0] * v.m[ 0] + this.m[ 4] * v.m[ 1] + this.m[ 8] * v.m[ 2];
        result.m[ 1] = this.m[ 1] * v.m[ 0] + this.m[ 5] * v.m[ 1] + this.m[ 9] * v.m[ 2];
        result.m[ 2] = this.m[ 2] * v.m[ 0] + this.m[ 6] * v.m[ 1] + this.m[10] * v.m[ 2];
        return result;
    }

    /** \fn calcTransformVector
     *
     * \brief Transforms vector v and stores the result in out
     *
     * \param out [vec3]
     * \param v   [vec3]
     *
     */
    public void calcTransformVector(vec3 out, vec3 v) {
        out.m[ 0] = this.m[ 0] * v.m[ 0] + this.m[ 4] * v.m[ 1] + this.m[ 8] * v.m[ 2];
        out.m[ 1] = this.m[ 1] * v.m[ 0] + this.m[ 5] * v.m[ 1] + this.m[ 9] * v.m[ 2];
        out.m[ 2] = this.m[ 2] * v.m[ 0] + this.m[ 6] * v.m[ 1] + this.m[10] * v.m[ 2];
    }

    /** \fn transformPoint
     *
     * \brief Returns a transformed point
     *
     * \param v [vec3]
     *
     */
    public vec3 transformPoint(vec3 v) {
        vec3 result = new vec3();
        result.m[ 0] = this.m[ 0] * v.m[ 0] + this.m[ 4] * v.m[ 1] + this.m[ 8] * v.m[ 2] + this.m[12];
        result.m[ 1] = this.m[ 1] * v.m[ 0] + this.m[ 5] * v.m[ 1] + this.m[ 9] * v.m[ 2] + this.m[13];
        result.m[ 2] = this.m[ 2] * v.m[ 0] + this.m[ 6] * v.m[ 1] + this.m[10] * v.m[ 2] + this.m[14];
        return result;
    }

    /** \fn calcTransformPoint
     *
     * \brief Transforms point v and stores the result in out
     *
     * \param out [vec3]
     * \param v   [vec3]
     *
     */
    public void calcTransformPoint(vec3 out, vec3 v) {
        out.m[ 0] = this.m[ 0] * v.m[ 0] + this.m[ 4] * v.m[ 1] + this.m[ 8] * v.m[ 2] + this.m[12];
        out.m[ 1] = this.m[ 1] * v.m[ 0] + this.m[ 5] * v.m[ 1] + this.m[ 9] * v.m[ 2] + this.m[13];
        out.m[ 2] = this.m[ 2] * v.m[ 0] + this.m[ 6] * v.m[ 1] + this.m[10] * v.m[ 2] + this.m[14];
    }

    /** \fn createLookAt
     *
     * \brief Creates a lookAt(model view) matrix
     *
     * \param eyePoint [vec3]
     * \param target   [vec3]
     * \param worldUp  [vec3]
     *
     */
    public static mat4 createLookAt(vec3 eyePoint, vec3 target, vec3 worldUp) {
        mat4 result = new mat4();
        result.lookAt(eyePoint, target, worldUp);
        return result;
    }

    /** \fn createPerspective
     *
     * \brief Creates a perspective matrix
     *
     * \param fovDegrees [float]
     * \param aspect     [float]
     * \param near       [float]
     * \param far        [float]
     *
     */
    public static mat4 createPerspective(float fovDegrees, float aspect, float near, float far) {
        mat4 result = new mat4();
        result.perspective(fovDegrees, aspect, near, far);
        return result;
    }

    /** \fn createTranslate
     *
     * \brief Creates a translation matrix using v
     *
     * \param v [vec3]
     *
     */
    public static mat4 createTranslate(vec3 v) {
        mat4 result = new mat4();
        result.m[12] = v.m[ 0];
        result.m[13] = v.m[ 1];
        result.m[14] = v.m[ 2];
        return result;
    }

    /** \fn createTranslate
     *
     * \brief Creates a translation matrix using x,y,z
     *
     * \param x [float]
     * \param y [float]
     * \param z [float]
     *
     */
    public static mat4 createTranslate(float x, float y, float z) {
        mat4 result = new mat4();
        result.m[12] = x;
        result.m[13] = y;
        result.m[14] = z;
        return result;
    }

    /** \fn createRotate
     *
     * \brief Creates a [rotation matrix based on v]
     *
     * \param v [vec3] Euler Radians
     *
     */
    public static mat4 createRotate(vec3 v) {
        mat4 result = new mat4();
        mat4_util_load_rotate(result.m, v.x(), v.y(), v.z());
        return result;
    }

    /** \fn createRotate
     *
     * \brief Creates a [rotation matrix based on x,y,z]
     *
     * \param x [float] Euler Radian
     * \param y [float] Euler Radian
     * \param z [float] Euler Radian
     *
     */
    public static mat4 createRotate(float x, float y, float z) {
        mat4 result = new mat4();
        mat4_util_load_rotate(result.m, x, y, z);
        return result;
    }

    /** \fn createScale
     *
     * \brief Creates a scale matrix using v
     *
     * \param v [vec3]
     *
     */
    public static mat4 createScale(vec3 v) {
        mat4 result = new mat4();
        result.m[ 0] = v.m[ 0];
        result.m[ 5] = v.m[ 1];
        result.m[10] = v.m[ 2];
        return result;
    }

    /** \fn createScale
     *
     * \brief Creates a scale matrix using x,y,z
     *
     * \param x [float]
     * \param y [float]
     * \param z [float]
     *
     */
    public static mat4 createScale(float x, float y, float z) {
        mat4 result = new mat4();
        result.m[ 0] = x;
        result.m[ 5] = y;
        result.m[10] = z;
        return result;
    }

    /** \fn createscale
     *
     * \brief Creates a scale matrix using s
     *
     * \param s [float]
     *
     */
    public static mat4 createscale(float s) {
        mat4 result = new mat4();
        result.m[ 0] = s;
        result.m[ 5] = s;
        result.m[10] = s;
        return result;
    }

    /*******************************************************************************
     *
     * Utility Methods
     *
     *******************************************************************************/

    /** \fn mat4_util_load_rotate
     *
     * \brief Loads outMatrix with rotation matrix using eulerRadiansX, eulerRadiansY, eulerRadiansZ
     *
     * \param outMatrix     [mat4]
     * \param eulerRadiansX [float]
     * \param eulerRadiansY [float]
     * \param eulerRadiansZ [float]
     *
     */
    private static void mat4_util_load_rotate(float[] outMatrix, float eulerRadiansX, float eulerRadiansY, float eulerRadiansZ) {
        //
        // The ordering for this is . In OpenGL, the ordering
        // is the same, but the operations needs to happen in
        // reverse:
        //
        //     glRotatef( eulerRadians.z, 0.0f, 0.0f 1.0f );
        //     glRotatef( eulerRadians.y, 0.0f, 1.0f 0.0f );
        //     glRotatef( eulerRadians.x, 1.0f, 0.0f 0.0f );
        //
        float cos_rx = (float)Math.cos(eulerRadiansX);
        float cos_ry = (float)Math.cos(eulerRadiansY);
        float cos_rz = (float)Math.cos(eulerRadiansZ);

        float sin_rx = (float)Math.sin(eulerRadiansX);
        float sin_ry = (float)Math.sin(eulerRadiansY);
        float sin_rz = (float)Math.sin(eulerRadiansZ);

        outMatrix[ 0] = cos_rz * cos_ry;
        outMatrix[ 1] = sin_rz * cos_ry;
        outMatrix[ 2] = -sin_ry;
        outMatrix[ 3] = 0.0f;

        outMatrix[ 4] = -sin_rz * cos_rx + cos_rz * sin_ry * sin_rx;
        outMatrix[ 5] = cos_rz * cos_rx + sin_rz * sin_ry * sin_rx;
        outMatrix[ 6] = cos_ry * sin_rx;
        outMatrix[ 7] = 0.0f;

        outMatrix[ 8] = sin_rz * sin_rx + cos_rz * sin_ry * cos_rx;
        outMatrix[ 9] = -cos_rz * sin_rx + sin_rz * sin_ry * cos_rx;
        outMatrix[10] = cos_ry * cos_rx;
        outMatrix[11] = 0.0f;

        outMatrix[12] = 0.0f;
        outMatrix[13] = 0.0f;
        outMatrix[14] = 0.0f;
        outMatrix[15] = 1.0f;
    }

    /** \fn mat4_util_inplace_rotate
     *
     * \brief In place rotate using eulerRadiansX, eulerRadiansY, eulerRadiansZ
     *
     * \param inOutMatrix   [mat4]
     * \param eulerRadiansX [float]
     * \param eulerRadiansY [float]
     * \param eulerRadiansZ [float]
     *
     */
    private static void mat4_util_inplace_rotate(float[] inOutMatrix, float eulerRadiansX, float eulerRadiansY, float eulerRadiansZ) {
        //
        // The ordering for this is . In OpenGL, the ordering
        // is the same, but the operations needs to happen in
        // reverse:
        //
        //     glRotatef( eulerRadians.z, 0.0f, 0.0f 1.0f );
        //     glRotatef( eulerRadians.y, 0.0f, 1.0f 0.0f );
        //     glRotatef( eulerRadians.x, 1.0f, 0.0f 0.0f );
        //

        float cos_rx = (float)Math.cos(eulerRadiansX);
        float cos_ry = (float)Math.cos(eulerRadiansY);
        float cos_rz = (float)Math.cos(eulerRadiansZ);

        float sin_rx = (float)Math.sin(eulerRadiansX);
        float sin_ry = (float)Math.sin(eulerRadiansY);
        float sin_rz = (float)Math.sin(eulerRadiansZ);

        float b00 = cos_rz * cos_ry;
        float b10 = sin_rz * cos_ry;
        float b20 = -sin_ry;
        float b30 = 0.0f;

        float b01 = -sin_rz * cos_rx + cos_rz * sin_ry * sin_rx;
        float b11 = cos_rz * cos_rx + sin_rz * sin_ry * sin_rx;
        float b21 = cos_ry * sin_rx;
        float b31 = 0.0f;

        float b02 = sin_rz * sin_rx + cos_rz * sin_ry * cos_rx;
        float b12 = -cos_rz * sin_rx + sin_rz * sin_ry * cos_rx;
        float b22 = cos_ry * cos_rx;
        float b32 = 0.0f;

        float b03 = 0.0f;
        float b13 = 0.0f;
        float b23 = 0.0f;
        float b33 = 1.0f;

        float a00 = inOutMatrix[ 0];
        float a10 = inOutMatrix[ 1];
        float a20 = inOutMatrix[ 2];
        float a30 = inOutMatrix[ 3];

        float a01 = inOutMatrix[ 4];
        float a11 = inOutMatrix[ 5];
        float a21 = inOutMatrix[ 6];
        float a31 = inOutMatrix[ 7];

        float a02 = inOutMatrix[ 8];
        float a12 = inOutMatrix[ 9];
        float a22 = inOutMatrix[10];
        float a32 = inOutMatrix[11];

        float a03 = inOutMatrix[12];
        float a13 = inOutMatrix[13];
        float a23 = inOutMatrix[14];
        float a33 = inOutMatrix[15];

        inOutMatrix[ 0] = a00 * b00 + a01 * b10 + a02 * b20 + a03 * b30;
        inOutMatrix[ 1] = a10 * b00 + a11 * b10 + a12 * b20 + a13 * b30;
        inOutMatrix[ 2] = a20 * b00 + a21 * b10 + a22 * b20 + a23 * b30;
        inOutMatrix[ 3] = a30 * b00 + a31 * b10 + a32 * b20 + a33 * b30;

        inOutMatrix[ 4] = a00 * b01 + a01 * b11 + a02 * b21 + a03 * b31;
        inOutMatrix[ 5] = a10 * b01 + a11 * b11 + a12 * b21 + a13 * b31;
        inOutMatrix[ 6] = a20 * b01 + a21 * b11 + a22 * b21 + a23 * b31;
        inOutMatrix[ 7] = a30 * b01 + a31 * b11 + a32 * b21 + a33 * b31;

        inOutMatrix[ 8] = a00 * b02 + a01 * b12 + a02 * b22 + a03 * b32;
        inOutMatrix[ 9] = a10 * b02 + a11 * b12 + a12 * b22 + a13 * b32;
        inOutMatrix[10] = a20 * b02 + a21 * b12 + a22 * b22 + a23 * b32;
        inOutMatrix[11] = a30 * b02 + a31 * b12 + a32 * b22 + a33 * b32;

        inOutMatrix[12] = a00 * b03 + a01 * b13 + a02 * b23 + a03 * b33;
        inOutMatrix[13] = a10 * b03 + a11 * b13 + a12 * b23 + a13 * b33;
        inOutMatrix[14] = a20 * b03 + a21 * b13 + a22 * b23 + a23 * b33;
        inOutMatrix[15] = a30 * b03 + a31 * b13 + a32 * b23 + a33 * b33;
    }

    /** \fn mat4_util_inplace_invert
     *
     * \brief In place inversion of inOutMatrix
     *
     * \param inOutMatrix [FloatArray32(16)]
     *
     */
    private static void mat4_util_inplace_invert(float[] inOutMatrix) {
        float[] m = inOutMatrix;

        float a0 = m[ 0] * m[ 5] - m[ 1] * m[ 4];
        float a1 = m[ 0] * m[ 6] - m[ 2] * m[ 4];
        float a2 = m[ 0] * m[ 7] - m[ 3] * m[ 4];
        float a3 = m[ 1] * m[ 6] - m[ 2] * m[ 5];
        float a4 = m[ 1] * m[ 7] - m[ 3] * m[ 5];
        float a5 = m[ 2] * m[ 7] - m[ 3] * m[ 6];
        float b0 = m[ 8] * m[13] - m[ 9] * m[12];
        float b1 = m[ 8] * m[14] - m[10] * m[12];
        float b2 = m[ 8] * m[15] - m[11] * m[12];
        float b3 = m[ 9] * m[14] - m[10] * m[13];
        float b4 = m[ 9] * m[15] - m[11] * m[13];
        float b5 = m[10] * m[15] - m[11] * m[14];

        float det = a0 * b5 - a1 * b4 + a2 * b3 + a3 * b2 - a4 * b1 + a5 * b0;

        if(Math.abs(det) > 0.000000001) {
            float e00 = +m[ 5] * b5 - m[ 6] * b4 + m[ 7] * b3;
            float e01 = -m[ 4] * b5 + m[ 6] * b2 - m[ 7] * b1;
            float e02 = +m[ 4] * b4 - m[ 5] * b2 + m[ 7] * b0;
            float e03 = -m[ 4] * b3 + m[ 5] * b1 - m[ 6] * b0;
            float e10 = -m[ 1] * b5 + m[ 2] * b4 - m[ 3] * b3;
            float e11 = +m[ 0] * b5 - m[ 2] * b2 + m[ 3] * b1;
            float e12 = -m[ 0] * b4 + m[ 1] * b2 - m[ 3] * b0;
            float e13 = +m[ 0] * b3 - m[ 1] * b1 + m[ 2] * b0;
            float e20 = +m[13] * a5 - m[14] * a4 + m[15] * a3;
            float e21 = -m[12] * a5 + m[14] * a2 - m[15] * a1;
            float e22 = +m[12] * a4 - m[13] * a2 + m[15] * a0;
            float e23 = -m[12] * a3 + m[13] * a1 - m[14] * a0;
            float e30 = -m[ 9] * a5 + m[10] * a4 - m[11] * a3;
            float e31 = +m[ 8] * a5 - m[10] * a2 + m[11] * a1;
            float e32 = -m[ 8] * a4 + m[ 9] * a2 - m[11] * a0;
            float e33 = +m[ 8] * a3 - m[ 9] * a1 + m[10] * a0;

            float invDet = 1.0f / det;
            m[ 0]  = e00 * invDet;
            m[ 1]  = e10 * invDet;
            m[ 2]  = e20 * invDet;
            m[ 3]  = e30 * invDet;
            m[ 4]  = e01 * invDet;
            m[ 5]  = e11 * invDet;
            m[ 6]  = e21 * invDet;
            m[ 7]  = e31 * invDet;
            m[ 8]  = e02 * invDet;
            m[ 9]  = e12 * invDet;
            m[10] = e22 * invDet;
            m[11] = e32 * invDet;
            m[12] = e03 * invDet;
            m[13] = e13 * invDet;
            m[14] = e23 * invDet;
            m[15] = e33 * invDet;
        }
    }
}
