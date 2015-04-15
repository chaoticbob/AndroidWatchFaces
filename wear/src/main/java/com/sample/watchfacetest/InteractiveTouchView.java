package com.sample.watchfacetest;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vasiliy on 4/14/15.
 */
public class InteractiveTouchView extends View {

    public InteractiveTouchView(Context context) {
        super(context);

        this.setBackgroundColor(0x80FF0000);
        setFilterTouchesWhenObscured(false);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    public static final String DEBUG_TAG = InteractiveTouchView.class.getSimpleName();

}
