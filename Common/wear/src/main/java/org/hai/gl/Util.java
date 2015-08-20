package org.hai.gl;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.util.Arrays;

public class Util {
    private static String[] sExtenions = null;

    public static void checkGlError(String glOp) {
        int error = GLES20.glGetError();
        if(GLES20.GL_NO_ERROR != error) {
            String errorStr = GLU.gluErrorString(error);
            if(null == errorStr) {
                errorStr = GLUtils.getEGLErrorString(error);
            }

            String msg = glOp + " caused GL error 0x" + Integer.toHexString(error) + ":" + errorStr;
            throw new GLException(error, msg);
        }
    }

    public static String shaderTypeString(int shaderType) {
        String result = "UNKNOWN SHADER";
        if(GLES20.GL_VERTEX_SHADER == shaderType) {
            result = "VERTEX SHADER";
        }
        else if(GLES20.GL_FRAGMENT_SHADER == shaderType) {
            result = "FRAGMENT SHADER";
        }
        return result;
    }

    public static String[] getExtensions() {
        if(null == sExtenions) {
            sExtenions = GLES20.glGetString(GLES20.GL_EXTENSIONS).split("\\s");
        }
        return sExtenions;
    }

    public static boolean supportsExtension(String ext) {
        return Arrays.asList(sExtenions).contains(ext);
    }
}
