package org.hai.grfx.es2;

import android.opengl.GLES20;

import org.hai.grfx.Camera;

public class PointMesh3D extends Mesh3D {

    public PointMesh3D() {

    }

    public void draw(Camera cam) {
        getShader().uniform("mvp", cam.getModelViewProjectionMatrix());
        if(hasIndices()) {
            int count = getNumActiveVertices();
            int type = org.hai.gl.Env.supportsIndexElementUInt() ? GLES20.GL_UNSIGNED_INT : GLES20.GL_UNSIGNED_SHORT;
            GLES20.glDrawElements(GLES20.GL_POINTS, count, type, 0);
        }
        else {
            int count = getNumActiveVertices();
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);
        }
    }
}
