package com.codingforlove.wear.watchfaces;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import org.hai.fx.SpringMesh;
import org.hai.gl.GlslProg;
import org.hai.grfx.Camera;
import org.hai.grfx.Rect;
import org.hai.grfx.es2.TriMesh3D;

import java.util.Calendar;


public class SpringMeshWatchFace extends Gles2WatchFaceService {
    private static final String TAG = "SpringMeshWatchFace";

    private class MyEngine extends Gles2WatchFaceService.Engine implements SensorEventListener {

        private static final int MESH_SIZE_X = 30;
        private static final int MESH_SIZE_Y = 30;

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

        private float[] mColor0 = { 0, 0, 0 };
        private float[] mColor1 = { 1, 1, 1 };

        static final int MSG_UPDATE_TIME = 0;
        static final int INTERACTIVE_UPDATE_RATE_MS = 1000;

        // handler to update the time once a second in interactive mode
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        updateDisplayTime();
                        //invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void placeNumber(int x, int y, int num, float[] dstBuf) {
            float[] srcBuf = BlockFont.sData[num];
            for(int j = 0; j < BlockFont.RES_Y; ++j) {
                for(int i = 0; i < BlockFont.RES_X; ++i) {
                    int dstIdx = (y + j)*(MESH_SIZE_X +1) + (x + i);
                    int srcIdx = j*BlockFont.RES_X + i;
                    dstBuf[2*dstIdx + 0] = srcBuf[srcIdx];
                }
            }
        }

        private void updateDisplayTime() {
           //Log.i(TAG, "updateDisplayTime");
            if(null == mSpringMesh) {
                return;
            }

            float[] texCoords = mSpringMesh.getTexCoords();
            int n = texCoords.length/2;
            for(int i = 0; i < n; ++i) {
                texCoords[2*i + 0] = 0.0f;
            }

            Calendar c = Calendar.getInstance();
            int hours = c.get(Calendar.HOUR);
            int minutes = c.get(Calendar.MINUTE);
            int seconds = c.get(Calendar.SECOND);
            //Log.i(TAG, "updateDisplayTime: " + hours + " : " + minutes + " : " + seconds );

            int digit0 = seconds % 10;
            int digit1 = seconds % 10;
            int digit2 = seconds / 10;
            int digit3 = seconds / 10;

            placeNumber( 4,  3, digit0, texCoords);
            placeNumber(17,  3, digit1, texCoords);
            placeNumber( 4, 17, digit2, texCoords);
            placeNumber(17, 17, digit3, texCoords);
        }

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
                    "attribute vec2 vTexCoord;" + "\n" +
                    "uniform mat4 mvp;" + "\n" +
                    "uniform mat4 xform;" + "\n" +
                    "varying vec2 uv;" + "\n" +
                    "void main() {" + "\n" +
                    "   gl_PointSize = 3.0;" + "\n" +
                    "   gl_Position = mvp*xform*vPosition;" + "\n" +
                    "   uv = vTexCoord;" + "\n" +
                    "}";

                String fragShaderSrc =
                    "precision mediump float;" + "\n" +
                    "uniform vec3 color0;" + "\n" +
                    "uniform vec3 color1;" + "\n" +
                    "varying vec2 uv;" + "\n" +
                    "void main() {" + "\n" +
                    "    vec3 fc0 = mix(color0, color1, uv.y);" + "\n" +
                    "    vec3 fc1 = vec3(1.0, 1.0, 1.0);" + "\n" +
                    "    gl_FragColor = vec4(mix(fc0, fc1, uv.x), 1.0);" + "\n" +
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

            mScreenWidth = width;
            mScreenHeight = height;

            mCamera = Camera.createPixelAlignedUL(mScreenWidth, mScreenHeight, 15.0f);

            int nx = MESH_SIZE_X;
            int ny = MESH_SIZE_Y;
            Rect rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
            mSpringMesh = new SpringMesh(nx, ny, rect);
            mSpringMesh.setShader(mColorShader);

            mRectMesh = TriMesh3D.createRectUL(0, 0, 16, 16);
            mRectMesh.setShader(mColorShader);
            mRectMesh.getTransform().setOriginOffset(-8.0f, -8.0f, 0.0f);

            mColor0[0] = 254.0f/255.0f;
            mColor0[1] = 209.0f/255.0f;
            mColor0[2] = 13.0f/255.0f;
            mColor1[0] = 254.0f/255.0f;
            mColor1[1] =  53.0f/255.0f;
            mColor1[2] =  35.0f/255.0f;
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

            updateTimer();
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
            mSpringMesh.applyCircleForce(mForcePoints, 32.0f*mStrength, 25000.0f*mStrength);
            mStrength *= 0.97f;

            mSpringMesh.update();
            mSpringMesh.getShader().bind();
            mSpringMesh.getShader().uniform("color0", mColor0[0], mColor0[1], mColor0[2]);
            mSpringMesh.getShader().uniform("color1", mColor1[0], mColor1[1], mColor1[2]);
            mSpringMesh.draw(this.mCamera);

            float a = 0.08f;
            mOffsetX += a*(mOffsetTargetX - mOffsetX);
            mOffsetY += a*(mOffsetTargetY - mOffsetY);
            mRectMesh.getTransform().setTranslate(mScreenWidth/2.0f + mOffsetX, mScreenHeight/2.0f + mOffsetY, 0.0f);

//            mRectMesh.drawBegin();
//            mRectMesh.getShader().uniform("xform", mRectMesh.getTransform().getMatrix());
//            mRectMesh.getShader().uniform("color", 0.0f, 0.0f, 1.0f);
//            mRectMesh.draw(this.mCamera);
//            mRectMesh.drawEnd();
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

            float hw = mScreenWidth/2.0f;
            float hh = mScreenHeight/2.0f;
            mOffsetTargetX =  (x/6.5f)*hw;
            mOffsetTargetY = -(y/6.5f)*hh;
            float ws = mOffsetTargetX < 0.0f ? -1.0f : 1.0f;
            float hs = mOffsetTargetY < 0.0f ? -1.0f : 1.0f;
            mOffsetTargetX = ws*Math.min(Math.abs(mOffsetTargetX), hw);
            mOffsetTargetY = hs*Math.min(Math.abs(mOffsetTargetY), hh);

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
