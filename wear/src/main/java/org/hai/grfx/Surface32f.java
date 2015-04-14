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

    public Surface32f(int width, int height, float[] data, Format format) {
        super(width, height, format);
        allocateData();
        int n = getWidth()*getHeight()*getPixelWidth();
        System.arraycopy(data, 0, mData, 0, n);
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

    public void setPixel(int x, int y, float[] value) {
        int idx = (y*getWidth() + x)*getPixelWidth();
        int n = Math.min(getPixelWidth(), value.length);
        for(int i = 0; i < n; ++i) {
            mData[idx + i] = value[i];
        }
    }

    public static Surface32f createTestPattern() {
        Surface32f result = new Surface32f(256, 256, Format.RGB);
        float[] pixel = {0, 0, 0};
        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                float r = (float)x/256.0f;
                float g = 0;
                float b = 0;
                pixel[0] = r;
                pixel[1] = g;
                pixel[2] = b;
                result.setPixel(x, y, pixel);
            }
        }
        return result;
    }
}
