package org.hai.grfx.es2;

import android.opengl.GLES20;

import org.hai.grfx.Camera;
import org.hai.math.vec2;
import org.hai.math.vec3;

public class TriMesh3D extends Mesh3D {

    public TriMesh3D() {

    }

    public void draw(Camera cam) {
        getShader().uniform("mvp", cam.getModelViewProjectionMatrix());
        if(hasIndices()) {
            int count = getNumActiveVertices();
            int type = org.hai.gl.Env.supportsIndexElementUInt() ? GLES20.GL_UNSIGNED_INT : GLES20.GL_UNSIGNED_SHORT;
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, type, 0);
        }
        else {
            int count = getNumActiveVertices();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count);
        }
    }

    public static TriMesh3D createRect(float x, float y, float width, float height) {
        TriMesh3D result = new TriMesh3D();

        vec3 P0 = new vec3(x, y, 0.0f);
        vec3 P1 = new vec3(x, y - height, 0.0f);
        vec3 P2 = new vec3(x + width, y - height, 0.0f);
        vec3 P3 = new vec3(x + width, y, 0.0f);
        vec2 uv0 = new vec2(0, 1);
        vec2 uv1 = new vec2(0, 0);
        vec2 uv2 = new vec2(1, 0);
        vec2 uv3 = new vec2(1, 1);

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

        float[] uvs = new float[2*4];
        uvs[0*2 + 0] = uv0.x();
        uvs[0*2 + 1] = uv0.y();
        uvs[1*2 + 0] = uv1.x();
        uvs[1*2 + 1] = uv1.y();
        uvs[2*2 + 0] = uv2.x();
        uvs[2*2 + 1] = uv2.y();
        uvs[3*2 + 0] = uv3.x();
        uvs[3*2 + 1] = uv3.y();

        int[] indices = { 0, 1, 2, 0, 2, 3 };

        result.bufferIndices(indices);
        result.bufferPositions(positions);
        result.bufferTexCoords(uvs);

        return result;
    }

    public static TriMesh3D createRectUL(float x, float y, float width, float height) {
        TriMesh3D result = new TriMesh3D();

        vec3 P0 = new vec3(x, y, 0.0f);
        vec3 P1 = new vec3(x, y + height, 0.0f);
        vec3 P2 = new vec3(x + width, y + height, 0.0f);
        vec3 P3 = new vec3(x + width, y, 0.0f);
        vec2 uv0 = new vec2(0, 0);
        vec2 uv1 = new vec2(0, 1);
        vec2 uv2 = new vec2(1, 1);
        vec2 uv3 = new vec2(1, 0);

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

        float[] uvs = new float[2*4];
        uvs[0*2 + 0] = uv0.x();
        uvs[0*2 + 1] = uv0.y();
        uvs[1*2 + 0] = uv1.x();
        uvs[1*2 + 1] = uv1.y();
        uvs[2*2 + 0] = uv2.x();
        uvs[2*2 + 1] = uv2.y();
        uvs[3*2 + 0] = uv3.x();
        uvs[3*2 + 1] = uv3.y();

        int[] indices = { 0, 1, 2, 0, 2, 3 };

        result.bufferIndices(indices);
        result.bufferPositions(positions);
        result.bufferTexCoords(uvs);

        return result;
    }
}
