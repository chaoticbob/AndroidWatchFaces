package org.hai.grfx.es2;

import org.hai.gl.GlslProg;
import org.hai.gl.Vbo;
import org.hai.grfx.Camera;
import org.hai.grfx.Transform3D;

public abstract class Mesh3D {
    private int mPositionSize           = 3;
    private int mNormalSize             = 3;
    private int mTexCoordSize           = 2;

    private Vbo mIndices                = null;
    private Vbo mPositions              = null;
    private Vbo mNormals                = null;
    private Vbo mTexCoords              = null;
    private GlslProg mShaderProg        = null;

    private boolean mPositionsEnabled   = false;
    private boolean mNormalsEnabled     = false;
    private boolean mTexCoordsEnabled   = false;

    private int mPositionsLocation      = -1;
    private int mNormalsLocation        = -1;
    private int mTexCoordsLocation      = -1;

    private int mNumActiveVertices      = 0;

    private Transform3D mTransform      = new Transform3D();

    /** MeshES2
     *
     */
    public Mesh3D() {
    }

    /** MeshES2
     *
     */
    public Mesh3D(int positionsStride) {
        mPositionSize = positionsStride;
    }

    /** destroy
     *
     */
    public void destroy() {
        if(null != mPositions) {
            mPositions.destroy();
        }

        if(null != mNormals) {
            mNormals.destroy();
        }

        if(null != mTexCoords) {
            mTexCoords.destroy();
        }
    }

    /** getIndices
     *
     */
    public Vbo getIndices() {
        return mIndices;
    }

    /** getPositions
     *
     */
    public Vbo getPositions() {
        return mPositions;
    }

    /** getNormals
     *
     */
    public Vbo getNormals() {
        return mNormals;
    }

    /** getTexCoords
     *
     */
    public Vbo getTexCoords() {
        return mTexCoords;
    }

    /** getNumActiveVertices
     *
     * @return
     *
     */
    public int getNumActiveVertices() {
        return mNumActiveVertices;
    }

    /** setNumActiveVertices
     *
     * @param count
     *
     */
    public void setNumActiveVertices(int count) {
        mNumActiveVertices = count;
    }

    /** getTransform
     *
     */
    public Transform3D getTransform() {
        return mTransform;
    }

    /** enablePositions
     *
     */
    private void enablePositions() {
        if((! mPositionsEnabled) && (-1 != mPositionsLocation) && (null != mPositions)) {
            mPositions.enableAttrib(mPositionsLocation, mPositionSize);
            mPositionsEnabled = true;
        }
    }

    /** enableNormals
     * 
     */
    private void enableNormals() {
        if((! mNormalsEnabled) && (-1 != mNormalsLocation) && (null != mNormals)) {
            mNormals.enableAttrib(mNormalsLocation, mNormalSize);
            mNormalsEnabled = true;
        }        
    }

    /** enableTexCoords
     *
     */
    private void enableTexCoords() {
        if((! mTexCoordsEnabled) && (-1 != mTexCoordsLocation) && (null != mTexCoords)) {
            mTexCoords.enableAttrib(mTexCoordsLocation, mTexCoordSize);
            mTexCoordsEnabled = true;
        }
    }

    /** disablePositions
     *
     */
    private void disablePositions() {
        if(null != mPositions) {
            mPositions.disableAttrib();
        }
        mPositionsEnabled = false;
        mPositionsLocation = -1;
    }

    /** disableNormals
     *
     */
    private void disableNormals() {
        if(null != mNormals) {
            mNormals.disableAttrib();
        }
        mNormalsEnabled = false;
        mNormalsLocation = -1;
    }

    /** disableTexCoords
     *
     */
    private void disableTexCoords() {
        if(null != mTexCoords) {
            mTexCoords.disableAttrib();
        }
        mTexCoordsEnabled = false;
        mTexCoordsLocation = -1;
    }

    public boolean hasIndices() {
        return null != mIndices;
    }

    /** bufferIndices
     *
     * @param data Inddex data
     *
     */
    public void bufferIndices(int[] data) {
        if(null == mIndices) {
            mIndices = Vbo.createIndexBuffer();
        }

        mIndices.bufferData(data);

        setNumActiveVertices(data.length);
    }

    /** bufferPositions
     *
     * @param data Position data
     *
     */
    public void bufferPositions(float[] data) {
        if(null == mPositions) {
            mPositions = Vbo.createFloatBuffer();
            enablePositions();
        }

        mPositions.bind();
        mPositions.bufferData(data);
        mPositions.unbind();

        if(null == mIndices) {
            setNumActiveVertices(data.length/mPositionSize);
        }
    }

    /** bufferNormals
     *
     * @param data Normal data
     *
     */
    public void bufferNormals(float[] data) {
        if(null == mNormals) {
            mNormals = Vbo.createFloatBuffer();
            enableNormals();
        }

        mNormals.bind();
        mNormals.bufferData(data);
        mNormals.unbind();
    }

    /** bufferTexCoords
     *
     * @param data Texture coordinate data
     *
     */
    public void bufferTexCoords(float[] data) {
        if(null == mTexCoords) {
            mTexCoords = Vbo.createFloatBuffer();
            enableTexCoords();
        }

        enableTexCoords();

        mTexCoords.bind();
        mTexCoords.bufferData(data);
        mTexCoords.unbind();
    }

    /** setShader
     *
     * @param shaderProg Shader program
     *
     */
    public void setShader(GlslProg shaderProg) {
        mShaderProg = shaderProg;
        if(null != mShaderProg) {
            disablePositions();
            disableNormals();
            disableTexCoords();

            mPositionsLocation = mShaderProg.getAttribLocation("vPosition");
            mNormalsLocation = mShaderProg.getAttribLocation("vNormal");
            mTexCoordsLocation = mShaderProg.getAttribLocation("vTexCoord");

            enablePositions();
            enableNormals();
            enableTexCoords();
        }
    }

    /** getShader
     *
     */
    public GlslProg getShader() {
        return mShaderProg;
    }

    /** drawBegin
     *
     */
    public void drawBegin() {
        getShader().bind();

        if(null != mPositions) {
            mPositions.drawBegin();
        }

        if(null != mNormals) {
            mNormals.drawBegin();
        }

        if(null != mTexCoords) {
            mTexCoords.drawBegin();
        }

        if(null != mIndices) {
            mIndices.drawBegin();
        }
    }

    /** drawEnd
     *
     */
    public void drawEnd() {
        if(null != mPositions) {
            mPositions.drawEnd();
        }

        if(null != mNormals) {
            mNormals.drawEnd();
        }

        if(null != mTexCoords) {
            mTexCoords.drawEnd();
        }
        getShader().unbind();
    }

    /** draw
     *
     */
    public abstract void draw(Camera cam);
}
