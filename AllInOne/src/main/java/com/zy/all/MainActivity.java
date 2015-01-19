package com.zy.all;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.zy.all.activity.BaseActivity;
import com.zy.all.fragment.TabFragment;
import com.zy.all.imageloader.fragment.AllPicturesFragment;
import com.zy.all.jazzyviewpager.JazzyViewPager;
import com.zy.all.ui.ChangeColorButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private static final String TAG = "zy";

    private JazzyViewPager mViewPager;
    private String[] mTitle = new String[]{"first", "second", "third", "fourth"};

    Context mContext;
    private PagerAdapter mAdapt;
    private ChangeColorButton first;
    private ChangeColorButton second;
    private ChangeColorButton third;
    private ChangeColorButton fourth;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        initActionbar();

        setupView();

        drawer = (DrawerLayout) findViewById(R.id.drawer);


    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private void setupView() {
        initData();
        //viewpager
        mViewPager = (JazzyViewPager) findViewById(R.id.content);
        mViewPager.setAdapter(mAdapt);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.RotateUp
        );

        first = (ChangeColorButton) findViewById(R.id.first);
        second = (ChangeColorButton) findViewById(R.id.second);
        third = (ChangeColorButton) findViewById(R.id.third);
        fourth = (ChangeColorButton) findViewById(R.id.fourth);

        first.setOnClickListener(this);
        second.setOnClickListener(this);
        third.setOnClickListener(this);
        fourth.setOnClickListener(this);
        first.setIconAlpha(1.0f);

        mTabIndicator.add(first);
        mTabIndicator.add(second);
        mTabIndicator.add(third);
        mTabIndicator.add(fourth);
    }

    private ArrayList<ChangeColorButton> mTabIndicator = new ArrayList<>();
    private ArrayList<Fragment> mTabs = new ArrayList<>();
    private void initData() {
        int N = mTitle.length;
        for(int i=0; i<N; i++){
            String title = mTitle[i];
            TabFragment tabFragment = new TabFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            tabFragment.setArguments(args);
           /* if (i == 0){
                mTabs.add(new AllPicturesFragment());
            }else{
            }*/
                mTabs.add(tabFragment);
        }

        mAdapt = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                Object obj = super.instantiateItem(container, position);
                mViewPager.setObjectForPosition(obj, position);
                return obj;
            }
        };

    }

    private void initActionbar() {
        //actionbar
        /*setOverflowShowingAlways();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.navigate_list,
                android.R.layout.simple_spinner_dropdown_item);
        ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener(){

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                Toast.makeText(mContext, "itemPosition=" + itemPosition + "; itemId=" + itemId,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, onNavigationListener);*/


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(Gravity.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Log.d(TAG, "featureId=" + featureId);
        //actionbar overflow 中显示图标
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if (menu.getClass().getSimpleName().equals("MenuBuilder")){

                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    //设置不存在menu键, 始终显示overflow button
    private void setOverflowShowingAlways()
    {
        try
        {
            // true if a permanent menu key is present, false otherwise.
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.d("zy", String.format("position=%d; positionOffset=%f", position, positionOffset));

        if (positionOffset >0 ){
            ChangeColorButton start = mTabIndicator.get(position);
            ChangeColorButton end = mTabIndicator.get(position +1 );

            start.setIconAlpha(1-positionOffset);
            end.setIconAlpha(positionOffset);

        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("zy", "onPageSelected=" + position);
        clearTabColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        clearTabColor(-1);
        switch (view.getId()){
            case R.id.first:
                mViewPager.setCurrentItem(0, false);
                first.setIconAlpha(1.0f);
                break;
            case R.id.second:
                mViewPager.setCurrentItem(1, false);
                second.setIconAlpha(1.0f);
                break;
            case R.id.third:
                mViewPager.setCurrentItem(2, false);
                third.setIconAlpha(1.0f);
                break;
            case R.id.fourth:
                mViewPager.setCurrentItem(3, false);
                fourth.setIconAlpha(1.0f);
                break;
        }
    }

    private void clearTabColor(int currentTabId) {
        first.setIconAlpha(0f);
        second.setIconAlpha(0f);
        third.setIconAlpha(0f);
        fourth.setIconAlpha(0f);
        if (currentTabId >=0 ){
            mTabIndicator.get(currentTabId).setIconAlpha(1.0f);
        }
    }
}
