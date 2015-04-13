package org.hai.grfx;

public class Surface32f extends Surface {

    private float[] mData = null;

    public Surface32f() {

    }

    public Surface32f(int width, int height) {
        super(width, height);
        allocateData();
        clear();
    }

    public Surface32f(int width, int height, Format format) {
        super(width, height, format);
        allocateData();
    }

    public int getBytesPerPixel() {
        return 4*getPixelWidth();
    }

    protected void allocateData() {
        int dataSize = getWidth()*getHeight()* getPixelWidth();
        mData = new float[dataSize];
    }

    public void clear() {
        java.util.Arrays.fill(mData, 0.0f);
    }

    public float[] getData() {
        return mData;
    }
}
