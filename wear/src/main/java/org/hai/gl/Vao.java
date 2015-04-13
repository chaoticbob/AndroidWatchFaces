package org.hai.gl;

import android.opengl.GLES30;


public class Vao {
    private int mVaoId = -1;

    /** Vao
     *
     */
    public Vao() {
        int[] vaos = new int[1];
        GLES30.glGenVertexArrays(1, vaos, 0);
        mVaoId = vaos[0];
    }

    /** destroy
     *
     */
    public void destroy() {
        int[] vaos = new int[1];
        vaos[0] = mVaoId;
        GLES30.glDeleteVertexArrays(1, vaos, 0);
        mVaoId = -1;
    }

    /** getId
     *
     * @return Vertex object array id
     *
     */
    public int getId() {
        return mVaoId;
    }
}
