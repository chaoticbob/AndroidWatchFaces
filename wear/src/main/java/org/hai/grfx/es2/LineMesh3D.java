package org.hai.grfx.es2;

import android.opengl.GLES20;

import org.hai.grfx.Camera;

public class LineMesh3D extends Mesh3D {

    public LineMesh3D() {
    }

    public void draw(Camera cam) {
        getShader().uniform("mvp", cam.getModelViewProjectionMatrix());
        if(hasIndices()) {
            int count = getNumActiveVertices();
            GLES20.glDrawElements(GLES20.GL_LINES, count, GLES20.GL_UNSIGNED_INT, 0);
        }
        else {
            int count = getNumActiveVertices();
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, count);
        }
    }
}