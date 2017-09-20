package com.summertaker.member48;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.summertaker.member48.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberFragment extends Fragment implements MemberInterface {

    private String mTag = this.getClass().getSimpleName();
    private String mVolleyTag = mTag;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshMode = false;

    private MemberFragmentListener mListener;

    private String mRequestUrl;
    private String mUserAgent;

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

        View rootView = inflater.inflate(R.layout.member_fragment, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);

        mGridView = rootView.findViewById(R.id.gridView);

        mMemberList = new ArrayList<>();
        mAdapter = new MemberAdapter(getContext(), mMemberList, this);
        mGridView.setAdapter(mAdapter);

        //String title = getArguments().getString("title");
        mRequestUrl = getArguments().getString("url");
        mUserAgent = BaseApplication.getMobileUserAgent();

        mListener.onMemberFragmentEvent("onRefreshStarted");

        String fileName = Util.getUrlToFileName(mRequestUrl);
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
                parseData(builder.toString());
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }
        } else {
            //BaseApplication baseApplication = ((BaseApplication) getActivity().getApplicationContext());
            requestData();
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

    private void requestData() {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);


        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, mRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                //mCacheManager.save(cacheId, response);
                writeData(mRequestUrl, response);
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

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void writeData(String url, String response) {
        Util.writeToFile(BaseApplication.getSavePath(), Util.getUrlToFileName(url), response);
        parseData(response);
    }

    private void parseData(String response) {
        mMemberList.clear();
        //mAdapter.notifyDataSetChanged();

        if (mRequestUrl.contains("akb48")) {
            Akb48Parser akb48Parser = new Akb48Parser();
            akb48Parser.parseMemberList(response, mMemberList);
        }
        renderData();
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
            //gridView.setOnItemClickListener(itemClickListener);
        }
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
        requestData();
    }

    public void openInNew() {
        //String url = mWebView.getUrl();
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //startActivity(intent);
    }

    @Override
    public void onPictureClick(int position, String imageUrl) {

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