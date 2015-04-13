package org.hai.grfx;

import org.hai.math.mat4;
import org.hai.math.vec3;

public class Transform3D {

    private vec3 mOriginOffset  = new vec3(0, 0, 0);

    private vec3 mTranslate     = new vec3(0, 0, 0);
    private vec3 mRotate        = new vec3(0, 0, 0);
    private vec3 mScale         = new vec3(1, 1, 1);

    private mat4 mMatrix        = new mat4();

    private boolean mDirty      = true;

    public Transform3D() {

    }

    private void evaluate() {
        mMatrix.setIdentity();
        mMatrix.translate(mTranslate);
        mMatrix.rotate(mRotate);
        mMatrix.scale(mScale);
        mMatrix.translate(mOriginOffset);
    }

    public Transform3D setOriginOffset(float x, float y, float z) {
        mOriginOffset.set(x, y, z);
        mDirty = true;
        return this;
    }

    public Transform3D setTranslate(float x, float y, float z) {
        mTranslate.set(x, y, z);
        mDirty = true;
        return this;
    }

    public Transform3D setRotate(float x, float y, float z) {
        mRotate.set(x, y, z );
        mDirty = true;
        return this;
    }

    public Transform3D setScale(float x, float y, float z) {
        mScale.set(x, y, z );
        mDirty = true;
        return this;
    }

    public mat4 getMatrix() {
        if(mDirty) {
            evaluate();
            mDirty = false;
        }
        return mMatrix;
    }
}
