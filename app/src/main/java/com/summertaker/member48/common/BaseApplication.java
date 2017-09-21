package com.summertaker.member48.common;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.summertaker.member48.SiteData;

import java.util.ArrayList;
import java.util.List;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    public static final String TAG = BaseApplication.class.getSimpleName();

    public static String USER_AGENT_WEB;
    public static String USER_AGENT_MOBILE;

    public static String DATA_PATH;

    private RequestQueue mRequestQueue;

    private List<SiteData> mSiteList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        USER_AGENT_WEB = "Mozilla/5.0 (Macintosh; U; Mac OS X 10_6_1; en-US) ";
        USER_AGENT_WEB += "AppleWebKit/530.5 (KHTML, like Gecko) ";
        USER_AGENT_WEB += "Chrome/ Safari/530.5";

        USER_AGENT_MOBILE = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) ";
        USER_AGENT_MOBILE += "AppleWebKit/528.18 (KHTML, like Gecko) ";
        USER_AGENT_MOBILE += "Version/4.0 Mobile/7A341 Safari/528.16";

        DATA_PATH = Environment.getExternalStorageDirectory().toString();
        DATA_PATH += java.io.File.separator + "android";
        DATA_PATH += java.io.File.separator + "data";
        DATA_PATH += java.io.File.separator + getApplicationContext().getPackageName();

        List<String> akb48urls = new ArrayList<>();
        akb48urls.add("http://sp.akb48.co.jp/profile/member/index.php?g_code=all");
        mSiteList.add(new SiteData("AKB48", USER_AGENT_MOBILE, akb48urls));

        List<String> ske48urls = new ArrayList<>();
        ske48urls.add("http://sp.ske48.co.jp/");
        mSiteList.add(new SiteData("SKE48", USER_AGENT_MOBILE, ske48urls));

        List<String> nmb48urls = new ArrayList<>();
        nmb48urls.add("http://spn2.nmb48.com/profile/list.php");
        nmb48urls.add("http://spn2.nmb48.com/profile/team-m.php");
        nmb48urls.add("http://spn2.nmb48.com/profile/team-b2.php");
        nmb48urls.add("http://spn2.nmb48.com/profile/kenkyu.php");
        mSiteList.add(new SiteData("NMB48", USER_AGENT_MOBILE, nmb48urls));

        List<String> hkt48urls = new ArrayList<>();
        hkt48urls.add("http://sp.hkt48.jp/qhkt48_list");
        mSiteList.add(new SiteData("HKT48", USER_AGENT_MOBILE, hkt48urls));

        List<String> ngt48urls = new ArrayList<>();
        ngt48urls.add("http://ngt48.jp/profile");
        mSiteList.add(new SiteData("NGT48", USER_AGENT_WEB, ngt48urls));

        List<String> stu48urls = new ArrayList<>();
        stu48urls.add("http://www.stu48.com/feature/profile");
        mSiteList.add(new SiteData("STU48", USER_AGENT_WEB, stu48urls));

        /*
        List<String> jkt48urls = new ArrayList<>();
        jkt48urls.add("http://sp.ske48.co.jp/");
        mSiteList.add(new SiteData("JKT48", jkt48urls));
        */
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public static String getMobileUserAgent() {
        return USER_AGENT_MOBILE;
    }

    public static void setMobileUserAgent(String mobileUserAgent) {
        USER_AGENT_MOBILE = mobileUserAgent;
    }

    public static String getDataPath() {
        return DATA_PATH;
    }

    public static void setDataPath(String dataPath) {
        DATA_PATH = dataPath;
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

    public List<SiteData> getSiteList() {
        return mSiteList;
    }

    public SiteData getSiteData(int position) {
        return mSiteList.get(position);
    }
}
