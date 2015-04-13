package org.hai.gl;

import android.opengl.GLES20;
import android.opengl.GLException;

public class Shader {
    private int mShaderId = 0;
    private int mShaderType = GLES20.GL_INVALID_ENUM;

    public Shader(int shaderType, String shaderSrc) {
        if((GLES20.GL_VERTEX_SHADER != shaderType) && (GLES20.GL_FRAGMENT_SHADER != shaderType)) {
            throw new GLException(GLES20.GL_INVALID_ENUM, "invalid shader type");
        }

        if(shaderSrc.isEmpty()) {
            throw new GLException(GLES20.GL_INVALID_ENUM, "Shader source empty");
        }

        mShaderType = shaderType;
        mShaderId = GLES20.glCreateShader(mShaderType);
        if(0 == mShaderId) {
            throw new RuntimeException("glCreateShader failed");
        }

        GLES20.glShaderSource(mShaderId, shaderSrc);
        GLES20.glCompileShader(mShaderId);

        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(mShaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if(0 == compileStatus[0]) {
            String info = GLES20.glGetShaderInfoLog(mShaderId);
            String msg = Util.shaderTypeString(mShaderType) + " compile failed: " + info;
            throw new RuntimeException( msg );
        }
    }

    static Shader createVertexShader(String shaderSrc) {
        Shader result = new Shader(GLES20.GL_VERTEX_SHADER, shaderSrc);
        return result;
    }

    static Shader createFragmentShader(String shaderSrc) {
        Shader result =new Shader(GLES20.GL_FRAGMENT_SHADER, shaderSrc);
        return result;
    }

    public void destroy() {
        GLES20.glDeleteShader(mShaderId);
        mShaderId = 0;
    }

    public int getId() {
        return mShaderId;
    }

    public int getShaderType() {
        return mShaderType;
    }
}
