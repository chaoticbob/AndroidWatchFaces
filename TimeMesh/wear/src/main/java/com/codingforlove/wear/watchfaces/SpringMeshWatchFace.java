package com.codingforlove.wear.watchfaces;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import org.hai.fx.SpringMesh;
import org.hai.gl.GlslProg;
import org.hai.grfx.Camera;
import org.hai.grfx.Rect;
import org.hai.grfx.es2.LineMesh3D;
import org.hai.grfx.es2.TriMesh3D;

import java.util.Calendar;


public class SpringMeshWatchFace extends Gles2WatchFaceService {
    private static final String TAG = "SpringMeshWatchFace";

    private class MyEngine extends Gles2WatchFaceService.Engine implements SensorEventListener {
        private static final int ROUND_FACE_OFFSET = -20;

        private static final int TIME_MORNING   = 0;
        private static final int TIME_AFTERNOON = 1;
        private static final int TIME_EVENING   = 2;
        private static final int TIME_NIGHT     = 3;
        private static final int TIME_TOTAL     = TIME_NIGHT + 1;

        private final int[][] TIME_COLORS = {
            { 235, 209,  48,  30, 178, 181, },
            { 254, 214,  50, 238,  78,  54, },
            { 251,  11,  26, 167, 173, 251, },
            { 177, 100, 227,  82, 181, 252, },
        };

        private static final int MESH_SIZE_X = 30;
        private static final int MESH_SIZE_Y = 30;

        private SensorManager mSensorMananger = null;
        private float[] mAccel = null;

        private boolean mIsRoundFace = false;

        private Camera mCamera = null;
        private SpringMesh mSpringMesh = null;

        private GlslProg mGradientShader = null;
        private GlslProg mAmbientShader = null;

        private LineMesh3D mCrossHatch = null;

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
        static final int INTERACTIVE_UPDATE_RATE_MS = 60*1000;

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

        void updateColorGradient() {
            Calendar c = Calendar.getInstance();
            int hours = c.get(Calendar.HOUR);
            int minutes = c.get(Calendar.MINUTE);
            if(Calendar.PM == c.get(Calendar.AM_PM)) {
                hours += 12;
            }

            // Start with night
            int timeIndex = TIME_NIGHT;
            // Calculate based on time of day
            if ((hours >= 6) && (hours < 12)) {
                timeIndex = TIME_MORNING;
            }
            else if ((hours >= 12) && (hours < 16)) {
                timeIndex = TIME_AFTERNOON;
            }
            else if ((hours >= 16) && (hours < 20)) {
                timeIndex = TIME_EVENING;
            }

            mColor0[0] = TIME_COLORS[timeIndex][0] / 255.0f;
            mColor0[1] = TIME_COLORS[timeIndex][1] / 255.0f;
            mColor0[2] = TIME_COLORS[timeIndex][2] / 255.0f;
            mColor1[0] = TIME_COLORS[timeIndex][3] / 255.0f;
            mColor1[1] = TIME_COLORS[timeIndex][4] / 255.0f;
            mColor1[2] = TIME_COLORS[timeIndex][5] / 255.0f;
        }

        private void updateDisplayTime() {
           //Log.d(TAG, "updateDisplayTime");
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

            int digit0 =   hours / 10;
            int digit1 =   hours % 10;
            int digit2 = minutes / 10;
            int digit3 = minutes % 10;
            if(0 == hours) {
                digit0 = 1;
                digit1 = 2;
            }

            int y = (mIsRoundFace && (320 != mScreenHeight)) ? 1 : 0;

            if (digit0 > 0) {
                placeNumber(4, 3 + y, digit0, texCoords);
            }
            placeNumber(17,  3 + y, digit1, texCoords);
            placeNumber( 4, 17 + y, digit2, texCoords);
            placeNumber(17, 17 + y, digit3, texCoords);

            updateColorGradient();
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            Log.d(TAG, "onCreate");
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SpringMeshWatchFace.this)
                .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                .setStatusBarGravity(Gravity.RIGHT | Gravity.TOP)
                .setHotwordIndicatorGravity(Gravity.LEFT | Gravity.TOP)
                .setShowSystemUiTime(false)
                .build());
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            if(insets.isRound()) {
                if(null != mSpringMesh) {
                    mIsRoundFace = true;
                    if( 320 != mScreenHeight ) {
                        mSpringMesh.setOriginOffset(0, ROUND_FACE_OFFSET);
                    }
                }
            }
        }

        @Override
        public void onGlContextCreated() {
            Log.d(TAG, "onGlContextCreated");
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
                    "    float t = floor(uv.x - 1.0) + 1.0;" + "\n" +
                    "    vec3 fc0 = mix(color0, color1, uv.y);" + "\n" +
                    "    vec3 fc1 = vec3(1.0, 1.0, 1.0);" + "\n" +
                    "    gl_FragColor = vec4(mix(fc0, fc1, t), 1.0);" + "\n" +
                    "}";

                mGradientShader = GlslProg.create(vertShaderSrc, fragShaderSrc);
                Log.d(TAG, "mGradientShader successful");
            }
            catch(Exception e) {
                Log.e(TAG, "mGradientShader failed: " + e.toString());
            }

            try {
                String vertShaderSrc =
                "attribute vec4 vPosition;" + "\n" +
                "uniform mat4 mvp;" + "\n" +
                "uniform mat4 xform;" + "\n" +
                "void main() {" + "\n" +
                "   gl_Position = mvp*xform*vPosition;" + "\n" +
                "}";

                String fragShaderSrc =
                "precision mediump float;" + "\n" +
                "void main() {" + "\n" +
                "    gl_FragColor = vec4(1.0);" + "\n" +
                "}";

                mAmbientShader = GlslProg.create(vertShaderSrc, fragShaderSrc);
                Log.d(TAG, "mAmbientShader successful");
            }
            catch(Exception e) {
                Log.e(TAG, "mAmbientShader failed: " + e.toString());
            }
        }

        @Override
        public void onGlSurfaceCreated(int width, int height) {
            Log.d(TAG, "onGlSurfaceCreated: " + width + "x" + height);
            super.onGlSurfaceCreated(width, height);

            mScreenWidth = width;
            mScreenHeight = height;

            mCamera = Camera.createPixelAlignedUL(mScreenWidth, mScreenHeight, 15.0f);

            int nx = MESH_SIZE_X;
            int ny = MESH_SIZE_Y;
            Rect rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
            mSpringMesh = new SpringMesh(nx, ny, rect);
            mSpringMesh.setShader(mGradientShader);

            mCrossHatch = LineMesh3D.createCrossHatch(0, 0, mSpringMesh.getCellSizeX(), mSpringMesh.getCellSizeY());
            mCrossHatch.setShader(mAmbientShader);

            mRectMesh = TriMesh3D.createRectUL(0, 0, 16, 16);
            mRectMesh.setShader(mGradientShader);
            mRectMesh.getTransform().setOriginOffset(-8.0f, -8.0f, 0.0f);

            updateColorGradient();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            Log.d(TAG, "onAmbientModeChanged: " + inAmbientMode);
            super.onAmbientModeChanged(inAmbientMode);

            if( ! inAmbientMode ) {
                updateTimer();
            }

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
            Log.d(TAG, "onVisibilityChanged:" + visible);
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
            invalidate();
        }

        private void placeNumberAmbient(int x, int y, int num) {
            final float cx = mSpringMesh.getCellSizeX();
            final float cy = mSpringMesh.getCellSizeY();

            final float yOffset = (mIsRoundFace && (320 != mScreenHeight)) ? ROUND_FACE_OFFSET : 0;

            float[] srcBuf = BlockFontAmbient.sData[num];
            for(int j = 0; j < BlockFontAmbient.RES_Y; ++j) {
                for(int i = 0; i < BlockFontAmbient.RES_X; ++i) {
                    int srcIdx = j*BlockFontAmbient.RES_X + i;
                    if(srcBuf[srcIdx] > 0) {
                        float x0 = (x + i)*cx;
                        float y0 = (y + j)*cy;
                        mCrossHatch.getTransform().setTranslate(x0, y0 + yOffset, 0.0f);
                        mCrossHatch.getShader().uniform("xform", mCrossHatch.getTransform().getMatrix());
                        mCrossHatch.draw(mCamera);
                    }
                }
            }
        }

        private void drawAmbient() {
            Log.d(TAG, "drawAmbient: " + System.currentTimeMillis());

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


            Calendar c = Calendar.getInstance();
            int hours = c.get(Calendar.HOUR);
            int minutes = c.get(Calendar.MINUTE);

            int digit0 =   hours / 10;
            int digit1 =   hours % 10;
            int digit2 = minutes / 10;
            int digit3 = minutes % 10;
            if(0 == hours) {
                digit0 = 1;
                digit1 = 2;
            }

            int y = (mIsRoundFace && (320 != mScreenHeight)) ? 1 : 0;

            mCrossHatch.drawBegin();

            if (digit0 > 0) {
                placeNumberAmbient(4, 4 + y, digit0);
            }
            placeNumberAmbient(17,  4 + y, digit1);
            placeNumberAmbient( 4, 18 + y, digit2);
            placeNumberAmbient(17, 18 + y, digit3);

            mCrossHatch.drawEnd();
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

            // Move the force point around
            float a = 0.08f;
            mOffsetX += a*(mOffsetTargetX - mOffsetX);
            mOffsetY += a*(mOffsetTargetY - mOffsetY);

            //
            // Save for Debug
            //
            //mRectMesh.getTransform().setTranslate(mScreenWidth/2.0f + mOffsetX, mScreenHeight/2.0f + mOffsetY, 0.0f);
            //mRectMesh.drawBegin();
            //mRectMesh.getShader().uniform("xform", mRectMesh.getTransform().getMatrix());
            //mRectMesh.getShader().uniform("color", 0.0f, 0.0f, 1.0f);
            //mRectMesh.draw(this.mCamera);
            //mRectMesh.drawEnd();
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
