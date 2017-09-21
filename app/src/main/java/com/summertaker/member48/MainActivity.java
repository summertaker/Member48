package com.summertaker.member48;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.summertaker.member48.common.BaseApplication;
import com.summertaker.member48.util.SlidingTabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MemberFragment.MemberFragmentListener {

    //static final String LOG_TAG = "TAG";

    private static final int REQUEST_PERMISSION_CODE = 100;

    private ProgressBar mProgressBar;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.toolbar_progress_bar);

        //----------------------------------------------------------------------------
        // 런타임에 권한 요청
        // https://developer.android.com/training/permissions/requesting.html?hl=ko
        //----------------------------------------------------------------------------
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    // permission denied
                    onPermissionDenied();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void onPermissionDenied() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("권한이 거부되었습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    public void init() {
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                runFragment("goBack");
            }
        });
        */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runFragment("goTop");
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        mSiteData = new ArrayList<>();
        String[] akb48Urls = {"http://sp.akb48.co.jp/profile/member/index.php?g_code=all"};
        mSiteData.add(new SiteData("AKB48", akb48Urls));
        String[] ske48Urls = {"http://sp.ske48.co.jp"};
        mSiteData.add(new SiteData("SKE48", ske48Urls));
        */

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(sectionsPagerAdapter);

        //-------------------------------------------------------------------------------------------------------
        // 뷰페이저 간 이동 시 프레그먼트 자동으로 새로고침 방지
        // https://stackoverflow.com/questions/28494637/android-how-to-stop-refreshing-fragments-on-tab-change
        //-------------------------------------------------------------------------------------------------------
        mViewPager.setOffscreenPageLimit(BaseApplication.getInstance().getSiteList().size());

        SlidingTabLayout slidingTabLayout = findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                runFragment("refresh");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /*
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //SiteData siteData = BaseApplication.getInstance().getSiteData(position);
            //String title = siteData.getTitle();
            return MemberFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return BaseApplication.getInstance().getSiteList().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return BaseApplication.getInstance().getSiteData(position).getTitle();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //runFragment("goBack");
            super.onBackPressed();
        }
    }

    public void runFragment(String command) {
        //--------------------------------------------------------------------------------------------
        // 프레그먼트에 이벤트 전달하기
        // https://stackoverflow.com/questions/34861257/how-can-i-set-a-tag-for-viewpager-fragments
        //--------------------------------------------------------------------------------------------
        Fragment f = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());

        // based on the current position you can then cast the page to the correct Fragment class
        // and call some method inside that fragment to reload the data:
        //if (0 == mViewPager.getCurrentItem() && null != f) {
        if (f != null) {
            MemberFragment wf = (MemberFragment) f;

            switch (command) {
                case "goBack":
                    wf.goBack();
                    break;
                case "goTop":
                    wf.goTop();
                    break;
                case "refresh":
                    wf.refresh();
                    break;
                case "open_in_new":
                    wf.openInNew();
                    break;
            }
        }
    }

    @Override
    public void onMemberFragmentEvent(String event) {
        switch (event) {
            case "onRefreshStarted":
                if (mProgressBar != null) {
                    //mProgressBar.setVisibility(View.VISIBLE);
                }

                // 툴바 햄버거 아이콘을 기본으로 설정
                //mDrawerToggle.setDrawerIndicatorEnabled(true);

                break;
            case "onRefreshFinished":
                if (mProgressBar != null) {
                    //mProgressBar.setVisibility(View.GONE);
                }

                /*
                // 툴바 햄버거 아이콘을 뒤로 가기 아이콘으로 변경
                if (canGoBack) {
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                    mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            runFragment("goBack");
                        }
                    });
                } else {
                    mDrawerToggle.setDrawerIndicatorEnabled(true);
                }
                */
                break;
        }
    }
}
