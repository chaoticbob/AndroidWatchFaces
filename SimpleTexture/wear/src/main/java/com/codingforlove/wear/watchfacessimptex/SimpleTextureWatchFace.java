package com.codingforlove.wear.watchfacessimptex;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.opengl.GLES20;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import org.hai.gl.GlslProg;
import org.hai.gl.Texture;
import org.hai.grfx.Camera;
import org.hai.grfx.Surface8u;
import org.hai.grfx.es2.TriMesh3D;

import java.io.InputStream;

public class SimpleTextureWatchFace  extends Gles2WatchFaceService {

    private static final String TAG = "SimpleTextureWatchFace";

    private Bitmap mBitmap = null;

    private class MyEngine extends Gles2WatchFaceService.Engine implements SensorEventListener {
        private Camera mCamera = null;
        private Texture mTex = null;
        private GlslProg mTexShader = null;
        private TriMesh3D mTexMesh = null;

        private int mScreenWidth = 0;
        private int mScreenHeight = 0;

        @Override
        public void onCreate(SurfaceHolder holder) {
            Log.i(TAG, "onCreate");
            super.onCreate(holder);
        }

        @Override
        public void onGlContextCreated() {
            Log.i(TAG, "onGlContextCreated");
            super.onGlContextCreated();

            org.hai.gl.Env.initialize();

            try {
                String vertShaderSrc =
                    "attribute vec4 vPosition;" + "\n" +
                    "attribute vec2 vTexCoord;" + "\n" +
                    "uniform mat4 mvp;" + "\n" +
                    "uniform mat4 xform;" + "\n" +
                    "varying vec2 uv;" + "\n" +
                    "void main() {" + "\n" +
                    "   gl_Position = mvp*xform*vPosition;" + "\n" +
                    "   uv = vTexCoord;" + "\n" +
                    "}";

                String fragShaderSrc =
                    "precision mediump float;" + "\n" +
                    "uniform sampler2D tex0;" + "\n" +
                    "varying vec2 uv;" + "\n" +
                    "void main() {" + "\n" +
                    "    gl_FragColor = texture2D(tex0, uv);" + "\n" +
                    "}";

                mTexShader = GlslProg.create(vertShaderSrc, fragShaderSrc);
                Log.i(TAG, "mTexShader successful");
            }
            catch(Exception e) {
                Log.e(TAG, "mTexShader failed: " + e.toString());
            }
        }

        @Override
        public void onGlSurfaceCreated(int width, int height) {
            Log.i(TAG, "onGlSurfaceCreated");
            super.onGlSurfaceCreated(width, height);

            mScreenWidth = width;
            mScreenHeight = height;

            Surface8u surf = new Surface8u(mBitmap);
            mTex = new Texture(surf);

            mTexMesh = TriMesh3D.createRectUL(0, 0, mScreenWidth, mScreenHeight);
            mTexMesh.setShader(mTexShader);

            mCamera = Camera.createPixelAlignedUL(mScreenWidth, mScreenHeight, 15.0f);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            invalidate();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.i(TAG, "onVisibilityChanged:" + visible);
            super.onVisibilityChanged(visible);

            if(visible) {
                invalidate();
            }
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
        }

        private void drawAmbient() {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        }

        private void draw() {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            mTexMesh.drawBegin();
            mTexMesh.getShader().uniform("xform", mTexMesh.getTransform().getMatrix());
            mTexMesh.getShader().uniform("tex0", mTex);
            mTexMesh.draw(this.mCamera);
            mTexMesh.drawEnd();
        }

        @Override
        public void onDraw() {
            super.onDraw();

            if( isInAmbientMode() ) {
                drawAmbient();
            }
            else {
                draw();
            }

            if(isVisible() && ! isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    @Override
    public Engine onCreateEngine() {
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("flower.jpg");
            mBitmap = BitmapFactory.decodeStream(is);
        }
        catch(Exception e) {
            Log.e(TAG, "Image load failed: " + e.getMessage());
        }

        return new MyEngine();
    }
}
