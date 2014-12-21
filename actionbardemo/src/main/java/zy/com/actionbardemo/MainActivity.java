package zy.com.actionbardemo;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;


public class MainActivity extends FragmentActivity
            implements View.OnClickListener, ActionBar.TabListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.toggle_home_as_up).setOnClickListener(this);
        findViewById(R.id.toggle_show_home).setOnClickListener(this);
        findViewById(R.id.toggle_use_log).setOnClickListener(this);
        findViewById(R.id.toggle_show_title).setOnClickListener(this);
        findViewById(R.id.toggle_show_custom).setOnClickListener(this);
        findViewById(R.id.toggle_navigation_tabs).setOnClickListener(this);
        findViewById(R.id.toggle_navigation_list).setOnClickListener(this);
        findViewById(R.id.add_subtitle).setOnClickListener(this);
        findViewById(R.id.change_log_icon).setOnClickListener(this);
        findViewById(R.id.cycle_custom_gravity).setOnClickListener(this);

        ActionBar actionBar = getActionBar();
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setSubtitle("subTitle");
        actionBar.setTitle("mainTitle");
        actionBar.setIcon(R.drawable.ic_icon);
//        actionBar.setHomeAsUpIndicator(R.drawable.);

        Button customView = new Button(this);
        customView.setText("custom");
        actionBar.setCustomView(customView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        actionBar.addTab(actionBar.newTab().setText("Tab1").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Tab2").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Tab3").setTabListener(this));

        String[] navigationListData = new String[]{"first", "second", "third"};
        SpinnerAdapter spinnerAdapt = ArrayAdapter.createFromResource(this, R.array.navigate_list,
                android.R.layout.simple_spinner_dropdown_item);
        ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                return true;
            }
        };
        actionBar.setListNavigationCallbacks(spinnerAdapt, onNavigationListener);

    }

    @Override
    public void onClick(View view) {
       ActionBar actionBar = getActionBar();
        int flag = 0;
        switch (view.getId() ){
            case R.id.toggle_home_as_up :
                flag = ActionBar.DISPLAY_HOME_AS_UP;
                break;
            case R.id.toggle_show_home :
                flag = ActionBar.DISPLAY_SHOW_HOME;
                break;
            case R.id.toggle_use_log :
                flag = ActionBar.DISPLAY_USE_LOGO;
                break;
            case R.id.toggle_show_title :
                flag = ActionBar.DISPLAY_SHOW_TITLE;
                break;
            case R.id.toggle_show_custom:
                flag = ActionBar.DISPLAY_SHOW_CUSTOM;
                break;
            case R.id.change_home_as_up_icon:
                break;
            case R.id.change_log_icon:
                break;
            case R.id.cycle_custom_gravity:
                break;
            case R.id.add_subtitle:
                break;
            case R.id.toggle_navigation_tabs:
                actionBar.setNavigationMode(actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD ?
                    ActionBar.NAVIGATION_MODE_LIST : ActionBar.NAVIGATION_MODE_STANDARD );
                break;
            case R.id.toggle_navigation_list:
                actionBar.setNavigationMode(actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD ?
                        ActionBar.NAVIGATION_MODE_TABS : ActionBar.NAVIGATION_MODE_STANDARD );
                break;
        }

        int change = actionBar.getDisplayOptions() ^ flag;
        actionBar.setDisplayOptions(change, flag);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //tabListener need
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {    }
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {    }
}
