package com.codingforlove.wear.watchfaces;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import org.hai.fx.Fluids2D;
import org.hai.gl.GlslProg;
import org.hai.gl.Texture;
import org.hai.grfx.Camera;
import org.hai.grfx.Rect;
import org.hai.grfx.Surface;
import org.hai.grfx.Surface8u;
import org.hai.grfx.es2.TriMesh3D;

import java.nio.ByteBuffer;
import java.util.Calendar;

public class Fluids2DWatchFace extends Gles2WatchFaceService{

    private static final String TAG = "Fluids2DWatchFace";

    //private class MyEngine extends Gles2WatchFaceService.Engine implements SensorEventListener, View.OnTouchListener {
    private class MyEngine extends Gles2WatchFaceService.Engine implements SensorEventListener {

        private static final int FLUIDS_RES_X = 100;
        private static final int FLUIDS_RES_Y = 100;

        private static final float FONT_WIDTH  = 14.0f;
        private static final float FONT_HEIGHT = 38.0f;

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

        private long mStartTime = 0;

        private class Digit {
            public int value = 0;
            public float prevX = 0.0f;
            public float prevY = 0.0f;
            public int stepCount = 0;
        };

        //private float[] mPrevPos = { 0, 0 };
        //private int mStepCount = 0;

        static final int DIGIT_0 = 0;
        static final int DIGIT_1 = 1;
        static final int DIGIT_2 = 2;
        static final int DIGIT_3 = 3;

        private Digit[] mDigits = null;

//        private int mDigit0 = 0;
//        private int mDigit1 = 0;
//        private int mDigit2 = 0;
//        private int mDigit3 = 0;

        static final int MSG_UPDATE_TIME = 0;
        static final int INTERACTIVE_UPDATE_RATE_MS = 10*1000;

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

        private void updateDisplayTime() {
            if(null == mFluids2D) {
                return;
            }

            mStartTime = System.nanoTime();

            mDigits[DIGIT_0].stepCount = 0;
            mDigits[DIGIT_1].stepCount = 0;
            mDigits[DIGIT_2].stepCount = 0;
            mDigits[DIGIT_3].stepCount = 0;


            Calendar c = Calendar.getInstance();
            int hours = c.get(Calendar.HOUR);
            int minutes = c.get(Calendar.MINUTE);
            mDigits[DIGIT_0].value =   hours / 10;
            mDigits[DIGIT_1].value =   hours % 10;
            mDigits[DIGIT_2].value = minutes / 10;
            mDigits[DIGIT_3].value = minutes % 10;
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            Log.i(TAG, "onCreate");
            super.onCreate(holder);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            if(insets.isRound()) {
                if(null != mTexMesh) {
                    mTexMesh.getTransform().setOriginOffset(0, -20, 0);
                }
            }
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

            int nx = FLUIDS_RES_X;
            int ny = FLUIDS_RES_Y;
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

            mDigits = new Digit[4];
            mDigits[DIGIT_0] = new Digit();
            mDigits[DIGIT_1] = new Digit();
            mDigits[DIGIT_2] = new Digit();
            mDigits[DIGIT_3] = new Digit();
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

        /*
        private void setupTouch() {
            if(mInteractiveTouchView != null) {
                return;
            }

            mInteractiveTouchView = new InteractiveTouchView(getApplicationContext());
            mInteractiveTouchView.setOnTouchListener(this);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    (int)(mScreenWidth * mTouchAreaScreenFraction),
                    (int)(mScreenHeight * mTouchAreaScreenFraction),
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);

            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.addView(mInteractiveTouchView, params);
        }

        private void teardownTouch() {
            if(mInteractiveTouchView == null) {
                return; // Nothing to remove
            }

            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(mInteractiveTouchView);
            mInteractiveTouchView = null;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);
            switch(action) {
                case (MotionEvent.ACTION_DOWN) :
                    updatePositions(event);
                case (MotionEvent.ACTION_MOVE) :
                    updatePositions(event);
                case (MotionEvent.ACTION_UP) :
                    updatePositions(event);
                default :
                    break;
            }
            return true;
        }

        private void updatePositions(MotionEvent event) {
            mOffsetX = event.getRawX();
            mOffsetY = event.getRawY();
        }
        */

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.i(TAG, "onVisibilityChanged:" + visible);
            super.onVisibilityChanged(visible);

            if(visible) {
                registerSensor();
                //setupTouch();
                invalidate();
            }
            else {
                unregisterSensor();
                //teardownTouch();
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

        private void line(int x1, int y1, int x2, int y2) {
            int w = x2 - x1;
            int h = y2 - y1;
            int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
            if (w<0) dx1 = -1; else if (w>0) dx1 = 1;
            if (h<0) dy1 = -1; else if (h>0) dy1 = 1;
            if (w<0) dx2 = -1; else if (w>0) dx2 = 1;
            int longest = Math.abs(w);
            int shortest = Math.abs(h);
            if (!(longest>shortest)) {
                longest = Math.abs(h);
                shortest = Math.abs(w);
                if (h<0) dy2 = -1; else if (h>0) dy2 = 1;
                dx2 = 0;
            }
            int numerator = longest >> 1;
            for (int i=0;i<=longest;i++) {
                //putpixel(x1,y1,color);
                mForcePoints[0].x = (float)x1/(float)FLUIDS_RES_X;
                mForcePoints[0].y = (float)y1/(float)FLUIDS_RES_Y;

                float scale = 1.0f;
                mFluids2D.splatDensity(mForcePoints, 0.2f*scale);
                mFluids2D.splatVelocity(mForcePoints, 0.048f*scale);

                numerator += shortest;
                if (!(numerator<longest)) {
                    numerator -= longest;
                    x1 += dx1;
                    y1 += dy1;
                } else {
                    x1 += dx2;
                    y1 += dy2;
                }
            }
        }

        private void placeNumber(int x, int y, Digit digit, float t) {
            float[][] data = StrokeFont.getNumber( digit.value );

            float seg_dt = 1.0f/(float)data.length;
            int i = (int)Math.floor(t/seg_dt);
            float x0 = data[i][0]*FONT_WIDTH;
            float y0 = data[i][1]*FONT_HEIGHT;
            float x1 = data[i][2]*FONT_WIDTH;
            float y1 = data[i][3]*FONT_HEIGHT;
            float dx = (x1 - x0);
            float dy = (y1 - y0);

            float s = (t - (float)i*seg_dt)/seg_dt;
            float fx = (x0 + s*dx) + x;
            float fy = (y0 + s*dy) + y;

//            mForcePoints[0].x = fx/(float)FLUIDS_RES_X;
//            mForcePoints[0].y = fy/(float)FLUIDS_RES_Y;
//            mForcePoints[0].dx = 0.0f;
//            mForcePoints[0].dy = 0.0f;
            if(digit.stepCount > 0) {
                mForcePoints[0].dx = (fx - digit.prevX)/(float)FLUIDS_RES_X;
                mForcePoints[0].dy = (fy - digit.prevY)/(float)FLUIDS_RES_Y;
                
                int lx1 = (int)(digit.prevX + 0.5f);
                int ly1 = (int)(digit.prevY + 0.5f);
                int lx2 = (int)(fx + 0.5f);
                int ly2 = (int)(fy + 0.5f);

                //Log.i(TAG, "(" + lx1 + ", " + ly1 + ") -> (" + lx2 + ", " + ly2 + ")");

                line(lx1, ly1, lx2, ly2);
            }

            //mFluids2D.splatDensity(mForcePoints, 0.27f);
            //mFluids2D.splatVelocity(mForcePoints, 0.0072f);

            digit.prevX = fx;
            digit.prevY = fy;
            ++digit.stepCount;
        }

        private void draw() {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            //float currentTouchStrength = getTouchStrength();
            //mStrength = 1.0f;

            if(mStartTime > 0) {
                double span = 2.0;
                double elapsed = (double)(System.nanoTime() - mStartTime)/1000000000.0;
                if( elapsed <= span ) {
                    float t = (float)(elapsed/span);
                    int x = -(int)(1.5f*FONT_WIDTH/2.0f);
                    int y = (int)(FLUIDS_RES_Y - FONT_HEIGHT)/2;
                    if(mDigits[DIGIT_0].value > 0) {
                        x = -(int)(0.8f*FONT_WIDTH/2.0f);
                        placeNumber(x + 5, y, mDigits[DIGIT_0], t);
                    }
                    placeNumber(x + 28, y, mDigits[DIGIT_1], t);
                    placeNumber(x + 59, y, mDigits[DIGIT_2], t);
                    placeNumber(x + 82, y, mDigits[DIGIT_3], t);
                }
            }

            if(mStrength > 0.1f) {
                /*
                mForcePoints[0].x = mOffsetX / (float)mScreenWidth;
                mForcePoints[0].y = mOffsetY / (float)mScreenHeight;
                mForcePoints[0].dx = (mOffsetX - mPrevOffsetX) / (float)mScreenWidth * mStrength;
                mForcePoints[0].dy = (mOffsetY - mPrevOffsetY) / (float)mScreenHeight * mStrength;
                mFluids2D.splatDensity(mForcePoints, mStrength);
                mFluids2D.splatVelocity(mForcePoints, mStrength);
                */
                mForcePoints[0].x = (mScreenWidth / 2.0f + mOffsetX) / (float)mScreenWidth;
                mForcePoints[0].y = (mScreenHeight / 2.0f + mOffsetY) / (float)mScreenHeight;
                mForcePoints[0].dx = (mOffsetX - mPrevOffsetX) / (float)mScreenWidth * mStrength;
                mForcePoints[0].dy = (mOffsetY - mPrevOffsetY) / (float)mScreenHeight * mStrength;
                mFluids2D.splatDensity(mForcePoints, 0.10f*mStrength);
                mFluids2D.splatVelocity(mForcePoints, 0.5f*mStrength);
            }
            mStrength *= 0.99f;

            mFluids2D.update();
            float[] den = mFluids2D.getDensity1();
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
            //mRectMesh.getTransform().setTranslate(mOffsetX, mOffsetY, 0.0f);

            /*
            mRectMesh.drawBegin();
            mRectMesh.getShader().uniform("xform", mRectMesh.getTransform().getMatrix());
            mRectMesh.getShader().uniform("color", 0.0f, 0.0f, 1.0f);
            mRectMesh.draw(this.mCamera);
            mRectMesh.drawEnd();
            */
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
            mOffsetTargetX =  (x/7.5f)*hw;
            mOffsetTargetY = -(y/7.5f)*hh;
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
