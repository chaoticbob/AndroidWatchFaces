package com.codingforlove.wear.watchfacesfl2d;

/**
 * Created by hai on 8/10/15.
 */
public class StrokeFont {

    public static float[][] getNumber( int num ) {
        float[][] result = null;
        switch( num ) {
            case 0: result = NUM_0; break;
            case 1: result = NUM_1; break;
            case 2: result = NUM_2; break;
            case 3: result = NUM_3; break;
            case 4: result = NUM_4; break;
            case 5: result = NUM_5; break;
            case 6: result = NUM_6; break;
            case 7: result = NUM_7; break;
            case 8: result = NUM_8; break;
            case 9: result = NUM_9; break;
        }
        return result;
    }

    public static final float[][] NUM_0 = {
        { 1.0f, 0.0f, 0.0f, 0.0f },
        { 0.0f, 0.0f, 0.0f, 1.0f },
        { 0.0f, 1.0f, 1.0f, 1.0f },
        { 1.0f, 1.0f, 1.0f, 0.0f },
    };

    public static final float[][] NUM_1 = {
        { 1.0f, 0.0f, 1.0f, 1.0f },
    };

    public static final float[][] NUM_2 = {
        { 0.0f, 0.0f, 1.0f, 0.0f },
        { 1.0f, 0.0f, 1.0f, 0.5f },
        { 1.0f, 0.5f, 0.0f, 0.5f },
        { 0.0f, 0.5f, 0.0f, 1.0f },
        { 0.0f, 1.0f, 1.0f, 1.0f },
    };

    public static final float[][] NUM_3 = {
        { 0.0f, 0.0f, 1.0f, 0.0f },
        { 1.0f, 0.0f, 1.0f, 0.5f },
        { 0.0f, 0.5f, 1.0f, 0.5f },
        { 1.0f, 0.5f, 1.0f, 1.0f },
        { 1.0f, 1.0f, 0.0f, 1.0f },
    };

    public static final float[][] NUM_4 = {
        { 0.0f, 0.0f, 0.0f, 0.5f },
        { 0.0f, 0.5f, 1.0f, 0.5f },
        { 1.0f, 0.0f, 1.0f, 1.0f },
    };

    public static final float[][] NUM_5 = {
        { 1.0f, 0.0f, 0.0f, 0.0f },
        { 0.0f, 0.0f, 0.0f, 0.5f },
        { 0.0f, 0.5f, 1.0f, 0.5f },
        { 1.0f, 0.5f, 1.0f, 1.0f },
        { 1.0f, 1.0f, 0.0f, 1.0f },
    };

    public static final float[][] NUM_6 = {
        { 0.0f, 0.0f, 0.0f, 1.0f },
        { 0.0f, 1.0f, 1.0f, 1.0f },
        { 1.0f, 1.0f, 1.0f, 0.5f },
        { 1.0f, 0.5f, 0.0f, 0.5f },
    };

    public static final float[][] NUM_7 = {
        { 0.0f, 0.0f, 1.0f, 0.0f },
        { 1.0f, 0.0f, 0.0f, 1.0f },
    };

    public static final float[][] NUM_8 = {
        { 0.0f, 0.0f, 0.0f, 1.0f },
        { 0.0f, 1.0f, 1.0f, 1.0f },
        { 1.0f, 1.0f, 1.0f, 0.0f },
        { 1.0f, 0.0f, 0.0f, 0.0f },
        { 0.0f, 0.5f, 1.0f, 0.5f },
    };

    public static final float[][] NUM_9 = {
        { 1.0f, 0.0f, 0.0f, 0.0f },
        { 0.0f, 0.0f, 0.0f, 0.5f },
        { 0.0f, 0.5f, 1.0f, 0.5f },
        { 1.0f, 0.0f, 1.0f, 1.0f },
    };
}
