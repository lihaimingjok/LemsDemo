package com.pcjz.lems.ui.homepage;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.common.downloadapk.DownloadApk;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.VersionUtil;
import com.pcjz.lems.business.common.view.CircleImageView;
import com.pcjz.lems.business.common.view.MyViewPager;
import com.pcjz.lems.business.common.view.dialog.MyDialog;
import com.pcjz.lems.business.common.view.dialog.OnMyNegativeListener;
import com.pcjz.lems.business.common.view.dialog.OnMyPositiveListener;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.homepage.bean.UpdateResponModel;
import com.pcjz.lems.ui.myinfo.AboutActivity;
import com.pcjz.lems.ui.myinfo.FeedBackActivity;
import com.pcjz.lems.ui.myinfo.MyInfoActivity;
import com.pcjz.lems.ui.myinfo.SetupActivity;
import com.squareup.otto.BasicBus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomePageActivity extends BaseActivity {

    /**
     * 内容ViewPager
     */
    private MyViewPager mViewPager;
    /**
     * 底部导航
     */
    private TabLayout mTabLayout;
    /**
     * 底部导航标题
     */
    //public final int[] TAB_TITLES = new int[]{R.string.tab_realname_work, R.string.tab_equipment_protect, R.string.tab_work_control, R.string.tab_reportforms, R.string.tab_apps};
    public final int[] TAB_TITLES = new int[]{R.string.tab_realname_work, R.string.tab_reportforms, R.string.tab_apps};
    /**
     * 底部导航图片
     */
    //private final int[] TAB_IMGS = new int[]{R.drawable.tab_workbench_selector, R.drawable.tab_acceptance_selector, R.drawable.tab_provincestatisticsforms_selector, R.drawable.tab_reportforms_selector, R.drawable.tab_tools_selector};
    private final int[] TAB_IMGS = new int[]{R.drawable.tab_workbench_selector, R.drawable.tab_reportforms_selector, R.drawable.tab_tools_selector};
    /**
     * 页面数目
     */
    private final int COUNT = TAB_TITLES.length;
    /**
     * ViewPager适配器
     */
    private MyViewPagerAdapter myViewPagerAdapter;


    private LinearLayout llHomePage;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.my_leftbar_pho_header)
            .showImageForEmptyUri(R.drawable.my_leftbar_pho_header)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private CircleImageView mIvPortrait;
    public static final int RELOAD_DATA = 1;
    private int mWorkbenchCurrentIndex = 0;
    public Handler mHandler;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;


    @Override
    public void setView() {
        setContentView(R.layout.activity_home_page);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        try {
            mBasicBus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView() {
        super.initView();
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_homepage);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        llHomePage = (LinearLayout) findViewById(R.id.ll_home_page);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headView = getLayoutInflater().from(this).inflate(R.layout.navigation_headerlayout, null);
        navigationView.addHeaderView(headView);
        navigationView.setItemIconTintList(null);
        navigationView.getMenu().getItem(0).setVisible(false);
        String poritrait = SharedPreferencesManager.getString(ResultStatus.PORTRAIT_IMG);
        String realName = SharedPreferencesManager.getString(ResultStatus.REALNAME);
        mIvPortrait = (CircleImageView) headView.findViewById(R.id.iv_head_portrait);
        TextView tvRealName = (TextView) headView.findViewById(R.id.tv_person_name);
        if (poritrait != null) {
            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + poritrait, mIvPortrait, mOption);
        }
        tvRealName.setText(realName);
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabsFromPagerAdapter(myViewPagerAdapter);
        mViewPager.setAdapter(myViewPagerAdapter);
        setTabs(mTabLayout, this.getLayoutInflater(), TAB_TITLES, TAB_IMGS);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(2);
    }

    @Subscribe
    public void excuteAction(String action) {
        if (SysCode.CHANGE_PORTRAIT_SUCCESS.equals(action)) {
            String poritrait = SharedPreferencesManager.getString(ResultStatus.PORTRAIT_IMG);
            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + poritrait, mIvPortrait, mOption);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断存储权限，如果没有就申请，用户拒绝就提示某些功能不可用
        setPermission();
    }

    private void setPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获取到存储权限
            } else {
                // Permission Denied
                //拒绝了权限
                AppContext.showToastText(getResources().getString(R.string.denied_permission));
                //AppContext.showToast(R.string.denied_permission);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setListener() {
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //ivPersonCenter.setImageResource(R.mipmap.tab_contacts_normal);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //ivPersonCenter.setImageResource(R.drawable.home_nav_icon_back);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //ivPersonCenter.setImageResource(R.drawable.home_toolbar_leftbar__icon_user);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_modify_task:
                                //startActivity(new Intent(HomePageActivity.this, ModifyTaskActivity.class));
                                break;
                            case R.id.action_settings:
                                startActivity(new Intent(HomePageActivity.this, SetupActivity.class));
                                break;
                            case R.id.action_feedback:
                                startActivity(new Intent(HomePageActivity.this, FeedBackActivity.class));
                                break;
                            case R.id.action_update:
                                String versionCode = VersionUtil.getVersionCode(HomePageActivity.this) + "";
                                requestVersion(versionCode);
                                break;
                            case R.id.action_about:
                                startActivity(new Intent(HomePageActivity.this, AboutActivity.class));
                                break;
                            default:
                                break;
                        }
                        menuItem.setCheckable(false);//设置选项可选
                        menuItem.setChecked(false);//设置选项被选中
                        drawerLayout.closeDrawers();//关闭侧边菜单栏
                        return true;
                    }
                });

        mIvPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MyInfoActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });


    }

    private void requestVersion(String versionCode) {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("appPackageName", "com.pcjz.lems");
            obj.put("versionCode", versionCode);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this, AppConfig.GET_LAST_VERSION, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    Type type = new TypeToken<BaseData<UpdateResponModel>>() {
                    }.getType();
                    BaseData<UpdateResponModel> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), SysCode.SUCCESS)) {
                        UpdateResponModel updateResponModel = datas.getData();
                        int versionCode = VersionUtil.getVersionCode(HomePageActivity.this);
                        if (datas.getCode().equals("5006")) {
                            Toast.makeText(HomePageActivity.this, datas.getMsg(), Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(datas.getData().getVersionCode()) > versionCode) {
                            showUpdateDialog(HomePageActivity.this, updateResponModel);
                        } else {
                            Toast.makeText(HomePageActivity.this, "更新失败，请卸载后，重新安装", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    } else {
                        AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private int maxLevel = 0;

    /*private int calLevel(List<ProjectTreeMultiInfo> projectTreeCalInfoArrayList) {
        int Max = 1;
        for (int i = 0; i < projectTreeCalInfoArrayList.size(); i++) {
            List<ProjectTreeMultiInfo> projectTreeMultiInfoList = projectTreeCalInfoArrayList.get(i).getList();
            if (projectTreeMultiInfoList != null && projectTreeMultiInfoList.size() > 0) {
                calLevel(projectTreeMultiInfoList);
            }
        }
        return Max;
    }*/

    public void showUpdateDialog(final Context mContext, UpdateResponModel updateResponModel) {
        String versionName = updateResponModel.getVersionName();
        String updateContent = updateResponModel.getUpdateContent();
        final String url = AppConfig.APK_URL + updateResponModel.getAppAndroidPath();
        final MyDialog updateDialog = new MyDialog(mContext, "发现" + versionName + "新版本啦", updateContent, "更新", "暂不更新",
                new OnMyPositiveListener() {
                    @Override
                    public void onClick() {
                        DownloadApk.downLoadApk(HomePageActivity.this, url);
                    }
                }, new OnMyNegativeListener() {
            @Override
            public void onClick() {
                super.onClick();
            }
        });
        updateDialog.show();
    }

    private int setDrawerGravity() {
        return Gravity.LEFT;
    }

    private ArrayList<String> list;

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 设置底部导航
     *
     * @param tabLayout
     * @param layoutInflater
     * @param tabTitles
     * @param tabImgs
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater layoutInflater, int[] tabTitles, int[] tabImgs) {
        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            View view = View.inflate(this, R.layout.tab_custom, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_tab);
            textView.setText(tabTitles[i]);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_tab);
            imageView.setImageResource(tabImgs[i]);
            tab.setCustomView(view);
        }

    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SysCode.ACTION_JPUSH_NOTIFICATION_RECEIVED);
        intentFilter.addAction(SysCode.JUMP_CURRENTINDEX);
        registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (SysCode.ACTION_JPUSH_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                TabLayout.Tab tab = mTabLayout.getTabAt(0);
                View view = tab.getCustomView();
                RelativeLayout rlInfoPoint = (RelativeLayout) view.findViewById(R.id.rl_info_point);
                rlInfoPoint.setVisibility(View.VISIBLE);
            } else if (StringUtils.equals(SysCode.JUMP_CURRENTINDEX, intent.getAction())) {
                /*int currentIndex = intent.getExtras().getInt("currentIndex");
                if (AuthManager.getFragment(0) instanceof WorkRealNameFragment) {
                    ((WorkRealNameFragment) AuthManager.getFragment(0)).setCurrentIndex(currentIndex);
                }*/
            }
        }
    };

    public BasicBus mBasicBus = MessageBus.getBusInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
        registerBroadcastReceiver();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * ViewPager适配器
     */
    private class MyViewPagerAdapter extends FragmentPagerAdapter {


        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return AuthManager.getFragment(position);
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void initWebData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        //by zhengyuye
        //WorkRealNameFragment.mCurrentIndex = 0;
    }


    public void openCloseDrawer() {
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    public void clearMsg(int position) {
        TabLayout.Tab tab = mTabLayout.getTabAt(position);
        View view = tab.getCustomView();
        RelativeLayout rlInfoPoint = (RelativeLayout) view.findViewById(R.id.rl_info_point);
        //   if (rlInfoPoint.getVisibility() == View.VISIBLE) {
        rlInfoPoint.setVisibility(View.GONE);
        //  }
    }

    public void setMsgView() {
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        View view = tab.getCustomView();
        RelativeLayout rlInfoPoint = (RelativeLayout) view.findViewById(R.id.rl_info_point);
        if (rlInfoPoint.getVisibility() == View.GONE) {
            rlInfoPoint.setVisibility(View.VISIBLE);
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                AppContext.showToast(R.string.press_again_exit_app);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
