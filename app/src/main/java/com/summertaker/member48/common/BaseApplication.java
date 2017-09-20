package com.summertaker.member48.common;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    public static final String TAG = BaseApplication.class.getSimpleName();

    public static String MOBILE_USER_AGENT;

    public static String SAVE_PATH;

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>)";
        MOBILE_USER_AGENT += " AppleWebKit/<WebKit Rev> (KHTML, like Gecko)";
        MOBILE_USER_AGENT += " Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";

        SAVE_PATH = Environment.getExternalStorageDirectory().toString();
        SAVE_PATH += java.io.File.separator + "android";
        SAVE_PATH += java.io.File.separator + "data";
        SAVE_PATH += java.io.File.separator + getApplicationContext().getPackageName();
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public static String getMobileUserAgent() {
        return MOBILE_USER_AGENT;
    }

    public static void setMobileUserAgent(String mobileUserAgent) {
        MOBILE_USER_AGENT = mobileUserAgent;
    }

    public static String getSavePath() {
        return SAVE_PATH;
    }

    public static void setSavePath(String savePath) {
        SAVE_PATH = savePath;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
