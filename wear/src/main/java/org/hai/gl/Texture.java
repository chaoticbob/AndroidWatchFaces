package org.hai.gl;

import android.opengl.GLES20;

import org.hai.grfx.Surface32f;
import org.hai.grfx.Surface8u;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Texture {

    private static boolean[] sBoundUnits = null;

    private static int nextBoundUnit() {
        if(null == sBoundUnits) {
            int[] numUnits = new int[1];
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS, numUnits, 0);
            sBoundUnits = new boolean[numUnits[0]];
            for(int i = 0; i < numUnits[0]; ++i) {
                sBoundUnits[i] = false;
            }
        }

        int result = -1;
        for(int i = 0; i < sBoundUnits.length; ++i) {
            if(! sBoundUnits[i]) {
                sBoundUnits[i] = true;
                result = i;
                break;
            }
        }
        return result;
    }

    private static boolean acquireBoundUnit(int unit) {
        boolean result = false;
        if((null != sBoundUnits) && (unit >= 0) && (unit < sBoundUnits.length)) {
            if(! sBoundUnits[unit]) {
                sBoundUnits[unit] = true;
                result = true;
            }
        }
        return result;
    }

    private static void releaseBoundUnit(int unit) {
        if((null != sBoundUnits) && (unit >= 0) && (unit < sBoundUnits.length)) {
            sBoundUnits[unit] = false;
        }
    }


    private int mTextureId  = -1;
    private int mBoundUnit  = -1;

    private int mWidth      = 0;
    private int mHeight     = 0;

    private int mFormat     = GLES20.GL_RGB;
    private int mDataType   = GLES20.GL_UNSIGNED_BYTE;

    public Texture() {
        initialize();
    }

    public Texture(Surface8u surface) {
        initialize();
        texImage2D(surface);
    }

    public Texture(Surface32f surface) {
        initialize();
        texImage2D(surface);
    }

    private void initialize() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTextureId = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        if(-1 != mTextureId) {
            int[] textures = new int[1];
            textures[0] = mTextureId;
            GLES20.glDeleteTextures(1, textures, 0);
        }
    }

    public int bind() {
        int texUnit = Texture.nextBoundUnit();
        if(-1 != texUnit) {
            mBoundUnit = texUnit;
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mBoundUnit);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        }
        return texUnit;
    }

    public boolean bind(int texUnit) {
        boolean result = Texture.acquireBoundUnit(texUnit);
        if(result) {
            mBoundUnit = texUnit;
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mBoundUnit);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        }
        return result;
    }

    public void unbind() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        Texture.releaseBoundUnit(mBoundUnit);
        mBoundUnit = -1;
    }

    public void texImage2D(Surface8u surface) {
        if((-1 == mTextureId) || (null == surface)) {
            return;
        }

        int target = GLES20.GL_TEXTURE_2D;
        int level = 0;
        int internalFormat = GLES20.GL_RGB;
        int width = surface.getWidth();
        int height = surface.getHeight();
        int border = 0;
        int format = GLES20.GL_RGB;
        int type = GLES20.GL_UNSIGNED_BYTE;

        ByteBuffer buffer = ByteBuffer.allocateDirect(surface.getDataSizeInBytes());
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(surface.getData());
        buffer.position(0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void texImage2D(Surface32f surface) {
        if(-1 == mTextureId) {
            return;
        }

        int target = GLES20.GL_TEXTURE_2D;
        int level = 0;
        int internalFormat = GLES20.GL_RGB;
        int width = surface.getWidth();
        int height = surface.getHeight();
        int border = 0;
        int format = GLES20.GL_RGB;
        int type = GLES20.GL_UNSIGNED_BYTE;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(surface.getDataSizeInBytes());
        byteBuffer.order(ByteOrder.nativeOrder());

        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        buffer.put(surface.getData());
        buffer.position(0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}
