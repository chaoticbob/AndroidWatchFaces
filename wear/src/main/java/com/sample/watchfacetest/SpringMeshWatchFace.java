package com.sample.watchfacetest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import org.hai.fx.SpringMesh;
import org.hai.gl.GlslProg;
import org.hai.gl.Texture;
import org.hai.grfx.Camera;
import org.hai.grfx.Rect;
import org.hai.grfx.es2.TriMesh3D;
import org.hai.math.vec3;

public class SpringMeshWatchFace extends Gles2WatchFaceService {
    private static final String TAG = "SpringMeshWatchFace";

    private class MyEngine extends Gles2WatchFaceService.Engine implements SensorEventListener {

        private SensorManager mSensorMananger = null;
        private float[] mAccel = null;

        private Camera mCamera = null;
        private SpringMesh mSpringMesh = null;
        private GlslProg mColorShader = null;

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
        private float mStrength = 0.0f;
        private float[] mForcePoints = {0.0f, 0,0f};

        @Override
        public void onCreate(SurfaceHolder holder) {
            Log.i(TAG, "onCreate");
            super.onCreate(holder);

            vec3 v = new vec3(1, 2, 3);
            Log.i(TAG, "v=" + v.toString());
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
        }

        @Override
        public void onGlSurfaceCreated(int width, int height) {
            Log.i(TAG, "onGlSurfaceCreated");
            super.onGlSurfaceCreated(width, height);

            Texture.nextBoundUnit();

            mScreenWidth = width;
            mScreenHeight = height;

            mCamera = Camera.createPixelAlignedUL(mScreenWidth, mScreenHeight, 15.0f);

            int nx = 30;
            int ny = 30;
            Rect rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
            mSpringMesh = new SpringMesh(nx, ny, rect);
            mSpringMesh.setShader(mColorShader);

            mRectMesh = TriMesh3D.createRectUL(0, 0, 16, 16);
            mRectMesh.setShader(mColorShader);
            mRectMesh.getTransform().setOriginOffset(-8.0f, -8.0f, 0.0f);
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
            super.onVisibilityChanged(visible);

            if(visible) {
                registerSensor();
            }
            else {
                unregisterSensor();
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

            mForcePoints[0] = mScreenWidth/2.0f + mOffsetX;
            mForcePoints[1] = mScreenHeight/2.0f + mOffsetY;
            mSpringMesh.applyCircleForce(mForcePoints, 32.0f*mStrength, 50000.0f*mStrength);
            mStrength *= 0.92f;

            mSpringMesh.update();
            mSpringMesh.draw(this.mCamera);

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

                mPrevSampleX = mOffsetTargetY;
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
