package com.summertaker.member48;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.member48.common.BaseApplication;
import com.summertaker.member48.parser.Akb48Parser;
import com.summertaker.member48.parser.Hkt48Parser;
import com.summertaker.member48.parser.Ngt48Parser;
import com.summertaker.member48.parser.Nmb48Parser;
import com.summertaker.member48.parser.Ske48Parser;
import com.summertaker.member48.parser.Stu48Parser;
import com.summertaker.member48.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberFragment extends Fragment implements MemberInterface {

    private String mTag = "== " + this.getClass().getSimpleName();
    private String mVolleyTag = mTag;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshMode = false;

    private MemberFragmentListener mListener;

    private String mUserAgent;
    private List<String> mRequestUrls;
    private int mLoadCount = 0;

    private GridView mGridView;
    private MemberAdapter mAdapter;
    private ArrayList<MemberData> mMemberList;

    // Container Activity must implement this interface
    public interface MemberFragmentListener {
        public void onMemberFragmentEvent(String event);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mListener = (MemberFragmentListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
            }
        }
    }

    public MemberFragment() {
    }

    public static MemberFragment newInstance(int position) {
        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        //args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.member_fragment, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);

        mGridView = rootView.findViewById(R.id.gridView);

        mMemberList = new ArrayList<>();
        mAdapter = new MemberAdapter(getContext(), mMemberList, this);
        mGridView.setAdapter(mAdapter);

        int position = getArguments().getInt("position");

        SiteData siteData = BaseApplication.getInstance().getSiteList().get(position);
        mRequestUrls = siteData.getUrls();
        mUserAgent = siteData.getUserAgent();

        loadData();

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

    /**
     * 데이터 로드하기
     */
    private void loadData() {
        mListener.onMemberFragmentEvent("onRefreshStarted");

        if (mLoadCount < mRequestUrls.size()) {
            //--------------------------------
            // 로드할 데이터가 남은 경우
            //--------------------------------
            String fileName = Util.getUrlToFileName(mRequestUrls.get(mLoadCount)) + ".html";
            //Log.d(mTag, "fileName: " + fileName);

            File file = new File(BaseApplication.getDataPath(), fileName);
            if (file.exists()) {
                parseData(Util.readFile(fileName));
            } else {
                requestData();
            }
        } else {
            //--------------------------------
            // 데이터 로드가 완료된 경우
            //--------------------------------
            mLoadCount = 0;

            renderData();
        }
    }

    private void requestData() {
        final String url = mRequestUrls.get(mLoadCount);
        Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                writeData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseData("");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", mUserAgent);
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void writeData(String url, String response) {
        Util.writeToFile(Util.getUrlToFileName(url) + ".html", response);
        parseData(response);
    }

    private void parseData(String response) {
        if (!response.isEmpty()) {
            if (mRequestUrls.get(mLoadCount).contains("akb48")) {
                Akb48Parser akb48Parser = new Akb48Parser();
                akb48Parser.parseMemberList(response, mMemberList);
            } else if (mRequestUrls.get(mLoadCount).contains("ske48")) {
                Ske48Parser ske48Parser = new Ske48Parser();
                ske48Parser.parseMemberList(response, mMemberList);
            } else if (mRequestUrls.get(mLoadCount).contains("nmb48")) {
                Nmb48Parser nmb48Parser = new Nmb48Parser();
                nmb48Parser.parseMemberList(response, mMemberList);
            } else if (mRequestUrls.get(mLoadCount).contains("hkt48")) {
                Hkt48Parser hkt48Parser = new Hkt48Parser();
                hkt48Parser.parseMemberList(response, mMemberList);
            } else if (mRequestUrls.get(mLoadCount).contains("ngt48")) {
                Ngt48Parser ngt48Parser = new Ngt48Parser();
                ngt48Parser.parseMemberList(response, mMemberList);
            } else if (mRequestUrls.get(mLoadCount).contains("stu48")) {
                Stu48Parser stu48Parser = new Stu48Parser();
                stu48Parser.parseMemberList(response, mMemberList);
            }
        }

        mLoadCount++;

        if (mLoadCount == mRequestUrls.size()) {
            mLoadCount = 0;
            renderData();
        } else {
            loadData();
        }
    }

    private void renderData() {
        //Log.d(mTag, "mMemberList.size(): " + mMemberList.size());

        //if (mMemberList.size() == 0) {
        //    //alertNetworkErrorAndFinish(mErrorMessage);
        //} else {
        //    //gridView.setOnItemClickListener(itemClickListener);
        //}
        mAdapter.notifyDataSetChanged();

        if (mIsRefreshMode) {
            onRefreshComplete();
        }
        mListener.onMemberFragmentEvent("onRefreshFinished");
    }

    private void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshMode = false;
    }

    public void goBack() {

    }

    public void goTop() {
        //mGridView.smoothScrollToPosition(0);
        mGridView.setSelection(0);
    }

    public void refresh() {
        //mSwipeRefreshLayout.setRefreshing(true);
        //mIsRefreshMode = true;

        mListener.onMemberFragmentEvent("onRefreshStarted");

        mMemberList.clear();
        //mAdapter.notifyDataSetChanged();

        loadData();
    }

    public void openInNew() {
        //String url = mWebView.getUrl();
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //startActivity(intent);
    }

    @Override
    public void onPictureClick(int position, String imageUrl) {
        Log.d(mTag, imageUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivity(intent);
    }

    @Override
    public void onNameClick(int position) {

    }

    @Override
    public void onCloseClick(int position) {
        mMemberList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.getInstance().cancelPendingRequests(mVolleyTag);
    }
}