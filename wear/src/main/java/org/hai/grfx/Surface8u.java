package org.hai.grfx;

public class Surface8u extends Surface {

    private byte[] mData = null;

    public Surface8u() {

    }

    public Surface8u(int width, int height) {
        super(width, height);
        allocateData();
        clear();
    }

    public Surface8u(int width, int height, Format format) {
        super(width, height, format);
        allocateData();
    }

    public int getBytesPerPixel() {
        return getPixelWidth();
    }

    protected void allocateData() {
        int dataSize = getWidth()*getHeight()* getPixelWidth();
        mData = new byte[dataSize];
    }

    public void clear() {
        java.util.Arrays.fill(mData, (byte)0);
    }

    public byte[] getData() {
        return mData;
    }
}
