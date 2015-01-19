package com.zy.all;


import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * Created by Simon on 2014/12/16.
 */
public class AddInActionBar extends ActionProvider {

    public AddInActionBar(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        return null;
    }


    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        subMenu.add(R.string.start_multi_user_chat)
                .setIcon(R.drawable.ic_menu_start_conversation)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        return true;
                    }
                });
        subMenu.add(R.string.add_friends)
                .setIcon(R.drawable.ic_menu_allfriends)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        return true;
                    }
                });

    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
