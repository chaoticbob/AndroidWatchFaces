package com.sample.watchfacetest;

import android.support.wearable.watchface.Gles2WatchFaceService;

public class Fluids2DWatchFace extends Gles2WatchFaceService{

    private class MyEngine extends Gles2WatchFaceService.Engine {

    }

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }
}
