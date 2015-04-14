package com.sample.watchfacetest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import org.hai.fx.Fluids2D;
import org.hai.gl.GlslProg;
import org.hai.gl.Texture;
import org.hai.grfx.Camera;
import org.hai.grfx.Rect;
import org.hai.grfx.Surface;
import org.hai.grfx.Surface8u;
import org.hai.grfx.es2.TriMesh3D;

public class Fluids2DWatchFace extends Gles2WatchFaceService{

    private static final String TAG = "Fluids2DWatchFace";

    private class MyEngine extends Gles2WatchFaceService.Engine implements SensorEventListener {

        private SensorManager mSensorMananger = null;
        private float[] mAccel = null;

        private Camera mCamera = null;
        private Fluids2D mFluids2D = null;
        private Surface8u mSurf = null;
        private Texture mTex = null;
        private GlslProg mColorShader = null;
        private GlslProg mTexShader = null;
        private TriMesh3D mTexMesh = null;

        private TriMesh3D mRectMesh = null;

        private int mScreenWidth = 0;
        private int mScreenHeight = 0;

        private boolean mHasSample = false;
        private float mPrevSampleX = 0.0f;
        private float mPrevSampleY = 0.0f;

        private float mOffsetX = 0.0f;
        private float mOffsetY = 0.0f;
        private float mOffsetTargetX = 0.0f;
        private float mOffsetTargetY = 0.0f;
        private float mPrevOffsetX = 0.0f;
        private float mPrevOffsetY = 0.0f;
        private float mStrength = 0.0f;
        private Fluids2D.Force[] mForcePoints = new Fluids2D.Force[1];

        @Override
        public void onCreate(SurfaceHolder holder) {
            Log.i(TAG, "onCreate");
            super.onCreate(holder);
        }

        @Override
        public void onGlContextCreated() {
            Log.i(TAG, "onGlContextCreated");
            super.onGlContextCreated();

            try {
                String vertShaderSrc =
                    "attribute vec4 vPosition;" + "\n" +
                    "uniform mat4 mvp;" + "\n" +
                    "uniform mat4 xform;" + "\n" +
                    "void main() {" + "\n" +
                    "   gl_PointSize = 3.0;" + "\n" +
                    "   gl_Position = mvp*xform*vPosition;" + "\n" +
                    "}";

                String fragShaderSrc =
                    "precision mediump float;" + "\n" +
                    "uniform vec3 color;" + "\n" +
                    "void main() {" + "\n" +
                    "    gl_FragColor = vec4(color, 1.0);" + "\n" +
                    "}";

                mColorShader = GlslProg.create(vertShaderSrc, fragShaderSrc);
                Log.i(TAG, "mColorShader successful");
            }
            catch(Exception e) {
                Log.e(TAG, "mColorShader failed: " + e.toString());
            }

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

            mCamera = Camera.createPixelAlignedUL(mScreenWidth, mScreenHeight, 15.0f);

            int nx = 96;
            int ny = 96;
            Rect rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
            mFluids2D = new Fluids2D(nx, ny, rect);

            mSurf = new Surface8u(nx, ny, Surface.Format.RGB);
            mTex = new Texture(mSurf);

            mTexMesh = TriMesh3D.createRectUL(0, 0, mScreenWidth, mScreenHeight);
            mTexMesh.setShader(mTexShader);

            mRectMesh = TriMesh3D.createRectUL(0, 0, 12, 12);
            mRectMesh.setShader(mColorShader);
            mRectMesh.getTransform().setOriginOffset(-6.0f, -6.0f, 0.0f);

            mForcePoints[0] = mFluids2D.new Force();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            invalidate();
        }

        private void registerSensor() {
            mSensorMananger = (SensorManager)getSystemService(getBaseContext().SENSOR_SERVICE);
            Sensor accelerometer = mSensorMananger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorMananger.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        private void unregisterSensor() {
            mSensorMananger.unregisterListener(this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.i(TAG, "onVisibilityChanged:" + visible);
            super.onVisibilityChanged(visible);

            if(visible) {
                registerSensor();
                invalidate();
            }
            else {
                unregisterSensor();
                mHasSample = false;
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

            if(mStrength > 0.1f) {
                mForcePoints[0].x = (mScreenWidth / 2.0f + mOffsetX) / (float)mScreenWidth;
                mForcePoints[0].y = (mScreenHeight / 2.0f + mOffsetY) / (float)mScreenHeight;
                mForcePoints[0].dx = (mOffsetX - mPrevOffsetX) / (float)mScreenWidth * mStrength;
                mForcePoints[0].dy = (mOffsetY - mPrevOffsetY) / (float)mScreenHeight * mStrength;
                mFluids2D.splatDensity(mForcePoints, mStrength);
                mFluids2D.splatVelocity(mForcePoints, mStrength);
            }
            mStrength *= 0.99f;

            mFluids2D.update();
            float[] den = mFluids2D.getDensity();
            byte[] data = mSurf.getData();
            for(int j = 0; j < mSurf.getHeight(); ++j) {
                for(int i = 0; i < mSurf.getWidth(); ++i) {
                    int idx = j*mSurf.getWidth() + i;
                    int val = (int)Math.min(255.0f*den[idx], 255.0f);
                    val = Math.max(val, 0);
                    data[3*idx + 0] = (byte)val;
                    data[3*idx + 1] = (byte)val;
                    data[3*idx + 2] = (byte)val;
                }
            }
            mTex.texImage2D(mSurf);

            mTexMesh.drawBegin();
            mTexMesh.getShader().uniform("xform", mTexMesh.getTransform().getMatrix());
            mTexMesh.getShader().uniform("tex0", mTex);
            mTexMesh.draw(this.mCamera);
            mTexMesh.drawEnd();

            mPrevOffsetX = mOffsetX;
            mPrevOffsetY = mOffsetY;

            float a = 0.08f;
            mOffsetX += a*(mOffsetTargetX - mOffsetX);
            mOffsetY += a*(mOffsetTargetY - mOffsetY);
            mRectMesh.getTransform().setTranslate(mScreenWidth/2.0f + mOffsetX, mScreenHeight/2.0f + mOffsetY, 0.0f);

            mRectMesh.drawBegin();
            mRectMesh.getShader().uniform("xform", mRectMesh.getTransform().getMatrix());
            mRectMesh.getShader().uniform("color", 0.0f, 0.0f, 1.0f);
            mRectMesh.draw(this.mCamera);
            mRectMesh.drawEnd();
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

        static final float ALPHA = 0.15f;
        private float[] lowPass(float[] input, float[] output) {
            if(null == output) {
                return input;
            }
            for(int i = 0; i < input.length; ++i) {
                output[i] = output[i] + ALPHA*(input[i] - output[i]);
            }
            return output;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            mAccel = lowPass(event.values, mAccel);
            float x = mAccel[0];
            float y = mAccel[1];
            float z = mAccel[2];

            mOffsetTargetX =  (x/9.5f)*(mScreenWidth/2.0f);
            mOffsetTargetY = -(y/9.5f)*(mScreenHeight/2.0f);

            if(mHasSample) {
                float max = 7.5f;
                float maxSq = max*max;
                float dx = mOffsetTargetX - mPrevSampleX;
                float dy = mOffsetTargetY - mPrevSampleY;
                float d = dx*dx + dy*dy;
                if(d >= maxSq) {
                    mStrength = 1.0f;
                }

                mPrevSampleX = mOffsetTargetX;
                mPrevSampleY = mOffsetTargetY;
            }
            else {
                mPrevSampleX = mOffsetTargetX;
                mPrevSampleY = mOffsetTargetY;
                mHasSample = true;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }
}
