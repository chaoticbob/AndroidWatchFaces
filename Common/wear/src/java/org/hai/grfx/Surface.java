package org.hai.grfx;

public abstract class Surface {

    public enum Format {
        R, RG, RGB, RGBA
    }

    protected Format mFormat    = Format.RGB;
    protected int mWidth        = 0;
    protected int mHeight       = 0;
    protected int mPixelWidth   = 3;

    public Surface() {

    }

    public Surface(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public Surface(int width, int height, Format format) {
        mWidth = width;
        mHeight = height;
        mFormat = format;

        switch(mFormat) {
            case R    : mPixelWidth = 1; break;
            case RG   : mPixelWidth = 2; break;
            case RGB  : mPixelWidth = 3; break;
            case RGBA : mPixelWidth = 4; break;
        }
    }

    public Format getFormat() {
        return mFormat;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getPixelWidth() {
        return mPixelWidth;
    }

    public abstract int getBytesPerPixel();

    public int getDataSizeInBytes() {
        int result = getWidth()*getHeight()*getBytesPerPixel();
        return result;
    }

    protected abstract void allocateData();

    public abstract void clear();

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        allocateData();
        clear();
    }
}
