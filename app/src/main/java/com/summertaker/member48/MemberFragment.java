package com.summertaker.member48;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.member48.common.BaseApplication;
import com.summertaker.member48.common.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberFragment extends Fragment {

    String mTag = "MemberFragment";
    String mVolleyTag = mTag;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshMode = false;

    private WebView mWebView;

    WebFragmentListener mCallback;

    RequestQueue queue;

    private GridView gridView;
    ArrayList<MemberData> mMemberList = new ArrayList<>();

    // Container Activity must implement this interface
    public interface WebFragmentListener {
        public void onWebFragmentEvent(String event, String url, boolean canGoBack);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (WebFragmentListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
            }
        }
    }

    public MemberFragment() {
    }

    public static MemberFragment newInstance(int position, String title, String url) {
        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("title", title);
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gridview_fragment, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);

        gridView = rootView.findViewById(R.id.gridView);

        //String title = getArguments().getString("title");
        String url = getArguments().getString("url");

        String fileName = Util.getUrlToFileName(url);
        //Log.d(mTag, fileName);
        File file = new File(BaseApplication.getSavePath(), fileName);
        if (file.exists()) {
            //String data = Util.readFile(getContext(), BaseApplication.getSavePath(), Util.getUrlToFileName(url));
            //parseData(url, data);

            //boolean isSuccess = file.delete();
            //Log.d("==", fileName + " deleted.");

            try {
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    //builder.append('\n');
                }
                reader.close();
                parseData(url, builder.toString());
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }
        } else {
            //BaseApplication baseApplication = ((BaseApplication) getActivity().getApplicationContext());
            requestData(url, BaseApplication.getMobileUserAgent());
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsRefreshMode = true;
                refresh();
            }
        });
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);


        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                //mCacheManager.save(cacheId, response);
                writeData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseData(url, "");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", userAgent);
                return headers;
            }
        };

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void writeData(String url, String response) {
        Util.writeToFile(BaseApplication.getSavePath(), Util.getUrlToFileName(url), response);
        parseData(url, response);
    }

    private void parseData(String url, String response) {
        if (url.contains("akb48")) {
            Akb48Parser akb48Parser = new Akb48Parser();
            akb48Parser.parseMemberList(response, mMemberList);
            renderData();
        }
    }

    private void renderData() {
        //if (!isDataLoaded || !isWikiLoaded) {
        //    return;
        //}

        //mPbLoading.setVisibility(View.GONE);
        //Log.e(mTag, "mMemberList.size(): " + mMemberList.size());

        if (mMemberList.size() == 0) {
            //alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            if (gridView != null) {

                MemberAdapter adapter = new MemberAdapter(getContext(), mMemberList);
                gridView.setAdapter(adapter);
                //gridView.setOnItemClickListener(itemClickListener);
            }
        }
    }

    private void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshMode = false;
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mCallback.onWebFragmentEvent("onPageStarted", mWebView.getUrl(), mWebView.canGoBack());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mCallback.onWebFragmentEvent("onPageFinished", mWebView.getUrl(), mWebView.canGoBack());
            if (mIsRefreshMode) {
                onRefreshComplete();
            }
        }
    }

    public void goBack() {

    }

    public void refresh() {
        //mWebView.reload();
    }

    public void openInNew() {
        //String url = mWebView.getUrl();
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        // cancel request
        if (queue != null) {
            queue.cancelAll(mVolleyTag);
        }
    }
}