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

    public Surface8u(int width, int height, byte[] data, Format format) {
        super(width, height, format);
        allocateData();
        int n = getWidth()*getHeight()*getPixelWidth();
        System.arraycopy(data, 0, mData, 0, n);
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

    public void setPixel(int x, int y, byte[] value) {
        int idx = (y*getWidth() + x)*getPixelWidth();
        int n = Math.min(getPixelWidth(), value.length);
        for(int i = 0; i < n; ++i) {
            mData[idx + i] = value[i];
        }
    }

    public static Surface8u createTestPattern() {
        Surface8u result = new Surface8u(256, 256, Format.RGB);
        byte[] pixel = {0, 0, 0};
        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                int r = x;
                int g = y;
                int b = 0;
                pixel[0] = (byte)r;
                pixel[1] = (byte)g;
                pixel[2] = (byte)b;
                result.setPixel(x, y, pixel);
            }
        }
        return result;
    }
}
