package com.kingberry.liuhao;

import android.util.Log;

/**
 * Created by Administrator on 2017/7/17.
 */

public class Lg {
    private final static int LEVEL = 5;

    private final static String DEFAULT_TAG = "llk";

    private Lg() {
        throw new UnsupportedOperationException("AnyLink mLog cannot be instantiated");
    }

    public static void v(String msg) {
        if(LEVEL >= 5){
            Log.v(DEFAULT_TAG, msg);
        }
    }

    public static void d(String msg) {
        if(LEVEL >= 4){
            Log.d(DEFAULT_TAG, msg);
        }
    }

    public static void i(String msg) {
        if(LEVEL >= 3){
            Log.i(DEFAULT_TAG, msg);
        }
    }

    public static void w(String msg) {
        if(LEVEL >= 2){
            Log.w(DEFAULT_TAG, msg);
        }
    }

    public static void e(String msg) {
        if(LEVEL >= 1){
            Log.e(DEFAULT_TAG, msg);
        }
    }
}
