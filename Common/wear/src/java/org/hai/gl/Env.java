package org.hai.gl;

import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Env {

    private static boolean sInitialized = false;
    private static List<String> sExtensions = null;

    private static boolean sHasExtIndexElementUInt = false;

    public static void initialize() {
        if( sInitialized ) {
            return;
        }

        String str = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        String[] exts = str.split(" ");

        sExtensions = new ArrayList<>();
        for(String ext : exts) {
            sExtensions.add(ext);
            Log.i("", "Found OpenGL ES extension: " + ext);
        }

        sHasExtIndexElementUInt = sExtensions.contains("GL_OES_element_index_uint");

        sInitialized = true;
    }

    public static boolean supportsIndexElementUInt() {
        return sHasExtIndexElementUInt;
    }
}
