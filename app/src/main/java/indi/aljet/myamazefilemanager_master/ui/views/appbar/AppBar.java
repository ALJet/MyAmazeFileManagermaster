package indi.aljet.myamazefilemanager_master.ui.views.appbar;

import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;


import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by PC-LJL on 2018/2/2.
 */

public class AppBar {

    private int TOOLBAR_START_INSET;

    private Toolbar toolbar;
    private SearchView searchView;
    private BottomBar bottomBar;

    private AppBarLayout appbarLayout;

    public AppBar(MainActivity a, SharedPreferences sharedPref, SearchView.SearchListener searchListener) {
        toolbar = (Toolbar) a.findViewById(R.id.action_bar);
        searchView = new SearchView(this, a, searchListener);
        bottomBar = new BottomBar(this, a);

        appbarLayout = (AppBarLayout) a.findViewById(R.id.lin);

        if (SDK_INT >= 21) toolbar.setElevation(0);
        /* For SearchView, see onCreateOptionsMenu(Menu menu)*/
        TOOLBAR_START_INSET = toolbar.getContentInsetStart();

        if (!sharedPref.getBoolean("intelliHideToolbar", true)) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
            appbarLayout.setExpanded(true, true);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public BottomBar getBottomBar() {
        return bottomBar;
    }

    public AppBarLayout getAppbarLayout() {
        return appbarLayout;
    }

    public void setTitle(String title) {
        if (toolbar != null) toolbar.setTitle(title);
    }

    public void setTitle(@StringRes int title) {
        if (toolbar != null) toolbar.setTitle(title);
    }

}
