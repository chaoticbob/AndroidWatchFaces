package org.hai.gl;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Vbo {
    private int mVboId = 0;

    private int mTarget = GLES20.GL_INVALID_ENUM;

    private int mAttribLocation = -1;
    private int mAttribSize = 0;
    private int mAttribStride = 0;
    private int mAttribUsage = GLES20.GL_STATIC_DRAW;

    /** Vbo
     *
     * @param target Buffer type target
     *
     */
    public Vbo(int target) {
        int[] vbos = new int[1];
        GLES20.glGenBuffers(1, vbos, 0);
        mVboId = vbos[0];

        if((GLES20.GL_ARRAY_BUFFER != target) && (GLES20.GL_ELEMENT_ARRAY_BUFFER != target)) {
            throw new RuntimeException("Invalid target type for VBO");
        }
        mTarget = target;
    }

    /** createFloatBuffer
     *
     * @return Returns a float vertex buffer object
     *
     */
    public static Vbo createFloatBuffer() {
        return new Vbo(GLES20.GL_ARRAY_BUFFER);
    }

    /** createIndexBuffer
     *
     * @return Returns an integer vertex buffer object
     *
     */
    public static Vbo createIndexBuffer() {
        return new Vbo(GLES20.GL_ELEMENT_ARRAY_BUFFER);
    }

    /** destroy
     *
     */
    public void destroy() {
        int[] vbos = new int[1];
        vbos[0] = mVboId;
        GLES20.glDeleteBuffers(1, vbos, 0);
        mVboId = -1;
    }

    /** getId
     *
     * @return
     *
     */
    public int getId() {
        return mVboId;
    }

    /** getAttribLocation
     *
     */
    public int getAttribLocation() {
        return mAttribLocation;
    }

    /** setAttribLocation
     *
     */
    public void setAttribLocation(int loc) {
        mAttribLocation = loc;
    }

    /** getAttribSize
     *
     */
    public int getAttribSize() {
        return mAttribSize;
    }

    /** setAttribSize
     *
     */
    public void setAttribSize(int size) {
        mAttribSize = size;
    }

    /** getAttribStride
     *
     */
    public int getAttribStride() {
        return mAttribStride;
    }

    /** setAttribStride
     *
     */
    public void setAttribStride(int stride) {
        mAttribStride = stride;
    }

    /** getAttribUsage
     *
     */
    public int getAttribUsage() {
        return mAttribUsage;
    }

    /** setAttribUsage
     *
     */
    public void setAttribUsage(int usage) {
        switch(usage) {
            case GLES20.GL_STATIC_DRAW:
            case GLES20.GL_STREAM_DRAW:
            case GLES20.GL_DYNAMIC_DRAW:
                mAttribUsage = usage;
                break;

            default:
                mAttribUsage = GLES20.GL_STATIC_DRAW;
        }
    }

    /** setStaticDraw
     *
     */
    public void setStaticDraw() {
        mAttribUsage = GLES20.GL_STATIC_DRAW;
    }

    /** setStreamDraw
     *
     */
    public void setStreamDraw() {
        mAttribUsage = GLES20.GL_STREAM_DRAW;
    }

    /** setDynamicDraw
     *
     */
    public void setDynamicDraw() {
        mAttribUsage = GLES20.GL_DYNAMIC_DRAW;
    }

    /** bind
     *
     */
    public void bind() {
        GLES20.glBindBuffer(mTarget, mVboId);
    }

    /** unbind
     *
     */
    public void unbind() {
        GLES20.glBindBuffer(mTarget, 0);
    }

    /** bufferData
     *
     * @param data Integer data. Automatically converts to short if platform doesn't support 32 bit indices.
     *
     */
    public void bufferData(int[] data) {
        int indexSize = org.hai.gl.Env.supportsIndexElementUInt() ? 4 : 2;
        int dataSizeInBytes = indexSize*data.length;

        GLES20.glBindBuffer(mTarget, mVboId);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(dataSizeInBytes);
        byteBuffer.order(ByteOrder.nativeOrder());

        if(4 == indexSize) {
            IntBuffer buffer = byteBuffer.asIntBuffer();
            buffer.put(data);
            buffer.position(0);
            GLES20.glBufferData(mTarget, dataSizeInBytes, buffer, mAttribUsage);
        }
        else if(2 == indexSize) {
            ShortBuffer buffer = byteBuffer.asShortBuffer();
            for(int val : data ) {
                buffer.put((short)val);
            }
            buffer.position(0);
            GLES20.glBufferData(mTarget, dataSizeInBytes, buffer, mAttribUsage);
        }

        GLES20.glBindBuffer(mTarget, 0);
    }

    /** bufferData
     *
     * @param data Float data
     *
     */
    public void bufferData(float[] data) {
        int dataSizeInBytes = 4*data.length;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(dataSizeInBytes);
        byteBuffer.order(ByteOrder.nativeOrder());

        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);

        GLES20.glBindBuffer(mTarget, mVboId);
        GLES20.glBufferData(mTarget, dataSizeInBytes, buffer, mAttribUsage);
        GLES20.glBindBuffer(mTarget, 0);
    }

    /** bufferSubData
     *
     * @param data Integer data
     *
     */
    public void bufferSubData(int[] data) {
        int indexSize = org.hai.gl.Env.supportsIndexElementUInt() ? 4 : 2;
        int dataSizeInBytes = indexSize*data.length;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(dataSizeInBytes);
        byteBuffer.order(ByteOrder.nativeOrder());

        if(4 == indexSize) {
            IntBuffer buffer = byteBuffer.asIntBuffer();
            buffer.put(data);
            buffer.position(0);
            GLES20.glBufferSubData(mTarget, 0, dataSizeInBytes, buffer);
        }
        else if(2 == indexSize) {
            ShortBuffer buffer = byteBuffer.asShortBuffer();
            for(int val : data ) {
                buffer.put((short)val);
            }
            buffer.position(0);
            GLES20.glBufferSubData(mTarget, 0, dataSizeInBytes, buffer);
        }
    }

    /** bufferSubData
     *
     * @param data Float data
     *
     */
    public void bufferSubData(float[] data) {
        int dataSizeInBytes = 4*data.length;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(dataSizeInBytes);
        byteBuffer.order(ByteOrder.nativeOrder());

        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);

        GLES20.glBufferSubData(mTarget, 0, dataSizeInBytes, buffer);
    }

    /** enableAttrib
     *
     */
    public void enableAttrib() {
        if((-1 != mAttribLocation) && (mAttribSize > 0)) {
            GLES20.glVertexAttribPointer(mAttribLocation, mAttribSize, GLES20.GL_FLOAT, false, 0, 0);
            GLES20.glEnableVertexAttribArray(mAttribLocation);
        }
    }

    /** enableAttrib
     *
     * @param location Attribute location
     * @param size Attribute size
     *
     */
    public void enableAttrib(int location, int size) {
        setAttribLocation(location);
        setAttribSize(size);
        enableAttrib();
    }

    /** disableAttrib
     *
     */
    public void disableAttrib() {
        if(-1 != mAttribLocation) {
            GLES20.glDisableVertexAttribArray(mAttribLocation);
        }
    }

    /** drawBegin
     *
     */
    public void drawBegin() {
        bind();
        if(GLES20.GL_ARRAY_BUFFER == mTarget) {
            enableAttrib();
        }
    }

    /** drawEnd
     *
     */
    public void drawEnd() {
        unbind();
    }
}
