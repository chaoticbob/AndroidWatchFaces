package org.hai.gl;

import android.opengl.GLES20;
import android.util.Log;

import org.hai.math.mat4;

import java.util.HashMap;

public class GlslProg {
    private int mProgramId = 0;

    private Shader mVertShader = null;
    private Shader mFragShader = null;

    private HashMap<String, Integer> mAttributeLocations = new HashMap<>();
    private HashMap<String, Integer> mUniformLocations = new HashMap<>();

    /** GlslProg
     *
     * @param vertShader Vertex shader object
     * @param fragShader Fragment shader object
     *
     */
    public GlslProg(Shader vertShader, Shader fragShader) {
        mVertShader = vertShader;
        mFragShader = fragShader;

        mProgramId = GLES20.glCreateProgram();
        if(0 == mProgramId) {
            throw new RuntimeException("glCreateProgram failed");
        }

        GLES20.glAttachShader(mProgramId, mVertShader.getId());
        GLES20.glAttachShader(mProgramId, mFragShader.getId());

        GLES20.glLinkProgram(mProgramId);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgramId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if(0 == linkStatus[0]) {
            String info = GLES20.glGetProgramInfoLog(mProgramId);
            String msg = "GlslProg link failed: " + info;
            throw new RuntimeException( msg );
        }

        cacheAttributeLocations();
        cacheUniformLocations();
    }

    /** cacheAttributeLocations
     *
     */
    private void cacheAttributeLocations() {
        int[] numActive = new int[1];
        int[] maxNameLength = new int[1];
        GLES20.glGetProgramiv(mProgramId, GLES20.GL_ACTIVE_ATTRIBUTES, numActive, 0);
        GLES20.glGetProgramiv(mProgramId, GLES20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, maxNameLength, 0);
        for(int i = 0; i < numActive[0]; ++i) {
            int[] length = new int[1];
            int[] size   = new int[1];
            int[] type   = new int[1];
            byte[] name  = new byte[maxNameLength[0]];
            GLES20.glGetActiveAttrib(mProgramId, i, maxNameLength[0], length, 0, size, 0, type, 0, name, 0);

            try {
                String nameStr = new String(name, 0, length[0]);
                Log.i("MyWatchFace", "Attrib " + i + " : " + nameStr);

                int location = GLES20.glGetAttribLocation(mProgramId, nameStr);
                if(-1 == location) {
                    throw new RuntimeException("Could not get location for " + nameStr);
                }

                mAttributeLocations.put(nameStr, location);
            }
            catch(Exception e) {
                throw new RuntimeException("Failed caching attribute locations: " + e.toString());
            }
        }
    }

    /** cacheUniformLocations
     *
     */
    private void cacheUniformLocations() {
        int[] numActive = new int[1];
        int[] maxNameLength = new int[1];
        GLES20.glGetProgramiv(mProgramId, GLES20.GL_ACTIVE_UNIFORMS, numActive, 0);
        GLES20.glGetProgramiv(mProgramId, GLES20.GL_ACTIVE_UNIFORM_MAX_LENGTH, maxNameLength, 0);
        for(int i = 0; i < numActive[0]; ++i) {
            int[] length = new int[1];
            int[] size   = new int[1];
            int[] type   = new int[1];
            byte[] name  = new byte[maxNameLength[0]];
            GLES20.glGetActiveUniform(mProgramId, i, maxNameLength[0], length, 0, size, 0, type, 0, name, 0);

            try {
                String nameStr = new String(name, 0, length[0]);
                Log.i("MyWatchFace", "Uniform " + i + " : " + nameStr);

                int location = GLES20.glGetUniformLocation(mProgramId, nameStr);
                if(-1 == location) {
                    throw new RuntimeException("Could not get location for " + nameStr);
                }

                mUniformLocations.put(nameStr, location);
            }
            catch(Exception e) {
                throw new RuntimeException("Failed caching uniform locations: " + e.toString());
            }
        }
    }

    /** create
     *
     * @param vertShader Vertex shader object
     * @param fragShader Fragment shader object
     *
     */
    static public GlslProg create(Shader vertShader, Shader fragShader) {
        GlslProg result = new GlslProg(vertShader, fragShader);
        return result;
    }

    /** create
     *
     * @param vertShaderSrc Vertex shader source
     * @param fragShaderSrc Fragment shader source
     *
     */
    static public GlslProg create(String vertShaderSrc, String fragShaderSrc) {
        Shader vertShader = Shader.createVertexShader(vertShaderSrc);
        Shader fragShader = Shader.createFragmentShader(fragShaderSrc);
        GlslProg result = create(vertShader, fragShader);
        return result;
    }

    /** destroy
     *
     */
    public void destroy() {
        if(null != mVertShader) {
            mVertShader.destroy();
        }

        if(null != mFragShader) {
            mFragShader.destroy();
        }

        GLES20.glDeleteProgram(mProgramId);
        mProgramId = 0;
    }

    /** getId
     *
     */
    public int getId() {
        return mProgramId;
    }

    public int getAttribLocation(String attribName) {
        int result = -1;
        if(mAttributeLocations.containsKey(attribName)) {
            result = mAttributeLocations.get(attribName);
        }
        return result;
    }

    /** bind
     *
     */
    public void bind() {
        GLES20.glUseProgram(mProgramId);
    }

    /** unbind
     *
     */
    public void unbind() {
        GLES20.glUseProgram(0);
    }

    /** uniform
     *
     * @param name Uniform name
     * @param x Value 0
     *
     */
    public void uniform(String name, float x) {
        int location = mUniformLocations.get(name);
        GLES20.glUniform1f(location, x);
    }

    /** uniform
     *
     * @param name Uniform name
     * @param x Value 0
     * @param y Value 1
     *
     */
    public void uniform(String name, float x, float y) {
        int location = mUniformLocations.get(name);
        GLES20.glUniform2f(location, x, y);
    }

    /** uniform
     *
     * @param name Uniform name
     * @param x Value 0
     * @param y Value 1
     * @param z Value 2
     *
     */
    public void uniform(String name, float x, float y, float z) {
        int location = mUniformLocations.get(name);
        GLES20.glUniform3f(location, x, y, z);
    }

    /** uniform
     *
     * @param name Uniform name
     * @param x Value 0
     * @param y Value 1
     * @param z Value 2
     * @param w Value 3
     *
     */
    public void uniform(String name, float x, float y, float z, float w) {
        int location = mUniformLocations.get(name);
        GLES20.glUniform4f(location, x, y, z, w);
    }

    /** uniform
     *
     * @param name Uniform name
     * @param mat Matrix
     */
    public void uniform(String name, mat4 mat) {
        int location = mUniformLocations.get(name);
        GLES20.glUniformMatrix4fv(location, 1, false, mat.m, 0);
    }
}
