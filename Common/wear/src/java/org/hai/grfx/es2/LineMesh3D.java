package org.hai.grfx.es2;

import android.opengl.GLES20;

import org.hai.grfx.Camera;
import org.hai.math.vec2;
import org.hai.math.vec3;

public class LineMesh3D extends Mesh3D {

    public LineMesh3D() {
    }

    public void draw(Camera cam) {
        getShader().uniform("mvp", cam.getModelViewProjectionMatrix());
        if(hasIndices()) {
            int count = getNumActiveVertices();
            int type = org.hai.gl.Env.supportsIndexElementUInt() ? GLES20.GL_UNSIGNED_INT : GLES20.GL_UNSIGNED_SHORT;
            GLES20.glDrawElements(GLES20.GL_LINES, count, type, 0);
        }
        else {
            int count = getNumActiveVertices();
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, count);
        }
    }

    public static LineMesh3D createCrossHatch(float x, float y, float width, float height) {
        LineMesh3D result = new LineMesh3D();

        vec3 P0 = new vec3(x, y, 0.0f);
        vec3 P1 = new vec3(x, y - height, 0.0f);
        vec3 P2 = new vec3(x + width, y - height, 0.0f);
        vec3 P3 = new vec3(x + width, y, 0.0f);

        float[] positions = new float[3*4];
        positions[0*3 + 0] = P0.x();
        positions[0*3 + 1] = P0.y();
        positions[0*3 + 2] = P0.z();
        positions[1*3 + 0] = P1.x();
        positions[1*3 + 1] = P1.y();
        positions[1*3 + 2] = P1.z();
        positions[2*3 + 0] = P2.x();
        positions[2*3 + 1] = P2.y();
        positions[2*3 + 2] = P2.z();
        positions[3*3 + 0] = P3.x();
        positions[3*3 + 1] = P3.y();
        positions[3*3 + 2] = P3.z();

        int[] indices = {
            0, 1,   // left
            1, 2,   // bottom
            2, 3,   // right
            3, 0,   // top
            0, 2,   // diag:TL->BR
            3, 1,   // diag:TR->BL
        };

        result.bufferIndices(indices);
        result.bufferPositions(positions);

        return result;
    }
}