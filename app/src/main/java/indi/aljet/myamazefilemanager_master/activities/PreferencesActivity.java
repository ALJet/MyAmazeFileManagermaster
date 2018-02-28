package indi.aljet.myamazefilemanager_master.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.superclasses.ThemedActivity;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.AdvancedSearchPref;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.ColorPref;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.FoldersPref;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.PrefFrag;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.QuickAccessPref;
import indi.aljet.myamazefilemanager_master.utils.PreferenceUtils;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.color.ColorUsage;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by PC-LJL on 2018/2/12.
 */

public class PreferencesActivity extends ThemedActivity {

    //Start is the first activity you see
    public static final int START_PREFERENCE = 0;
    public static final int COLORS_PREFERENCE = 1;
    public static final int FOLDERS_PREFERENCE = 2;
    public static final int QUICKACCESS_PREFERENCE = 3;
    public static final int ADVANCEDSEARCH_PREFERENCE = 4;

    private boolean changed = false;
    //The preference fragment currently selected
    private int selectedItem = 0;

    private static final String KEY_CURRENT_FRAG_OPEN = "current_frag_open";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences Sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        setContentView(R.layout.prefsfrag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(SDK_INT >= 21){
            ActivityManager.TaskDescription
                    taskDescription = new
                    ActivityManager.TaskDescription("Amaze",
                    ((BitmapDrawable) getResources().
                            getDrawable(R.mipmap.ic_launcher)).
                            getBitmap(),
                    getColorPreference().
                            getColor(ColorUsage.
                                    getPrimary(MainActivity.
                                            currentTab)));
            setTaskDescription(taskDescription);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP |
                        ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setBackgroundDrawable(
                getColorPreference().getDrawable(ColorUsage
                .getPrimary(MainActivity.currentTab)));

        if (SDK_INT == 20 || SDK_INT == 19) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getColorPreference().getColor(ColorUsage.getPrimary
                    (MainActivity.currentTab)));

            @SuppressLint("WrongViewCast") FrameLayout.MarginLayoutParams p =
                    (ViewGroup.MarginLayoutParams) findViewById(R.id.preferences).getLayoutParams();
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            p.setMargins(0, config.getStatusBarHeight(), 0, 0);
        } else if (SDK_INT >= 21) {
            boolean colourednavigation = Sp.getBoolean("colorednavigation", false);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(PreferenceUtils.getStatusColor(getColorPreference().
                    getColorAsString(ColorUsage.getPrimary(MainActivity.currentTab))));
            if (colourednavigation)
                window.setNavigationBarColor(PreferenceUtils.getStatusColor(getColorPreference().
                        getColorAsString(ColorUsage.getPrimary(MainActivity.currentTab))));

        }
        if (getAppTheme().equals(AppTheme.BLACK)) getWindow().getDecorView().setBackgroundColor
                (Utils.getColor(this, android.R.color.black));
        if (savedInstanceState != null){
            selectedItem = savedInstanceState.getInt(KEY_CURRENT_FRAG_OPEN, 0);
        }
        selectItem(selectedItem);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_FRAG_OPEN,selectedItem);
    }

    @Override
    public void onBackPressed() {
        if(selectedItem != START_PREFERENCE && changed){
            restartPC(this);
        }else if(selectedItem != START_PREFERENCE){
            selectItem(START_PREFERENCE);
        }else{
            Intent in = new Intent(
                    PreferencesActivity
            .this,MainActivity.class);
            in.setAction(Intent.ACTION_MAIN);
            in.setAction(Intent.CATEGORY_LAUNCHER);
            this.startActivity(in);
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.home:
                if(selectedItem != START_PREFERENCE &&
                        changed){
                    restartPC(this);
                }else if(selectedItem != START_PREFERENCE){
                    selectItem(START_PREFERENCE);
                }else {
                    Intent in = new Intent(
                            PreferencesActivity.this,
                            MainActivity.class);
                    in.setAction(Intent.ACTION_MAIN);
                    in.setAction(Intent.CATEGORY_LAUNCHER);
                    final int enter_anim = android.R.anim
                            .fade_in;
                    final int exit_anim = android.R.anim.fade_out;
                    Activity activity = this;
                    activity.overridePendingTransition(enter_anim, exit_anim);
                    activity.finish();
                    activity.overridePendingTransition(enter_anim, exit_anim);
                    activity.startActivity(in);
                }
                return true;
        }
        return true;
    }


    public void setChanged(){
        changed = true;
    }

    public void restartPC(final Activity activity){
        if(activity == null)
            return;
        final int enter_anim = android.R.anim.fade_in;
        final int exit_anim = android.R.anim.fade_out;
        activity.overridePendingTransition(enter_anim
        ,exit_anim);
        activity.overridePendingTransition(enter_anim,
                exit_anim);
        activity.startActivity(activity.getIntent());

    }


    public void selectItem(int i ){
        selectedItem = i;
        switch (i){
            case START_PREFERENCE:
                loadPrefFragment(new PrefFrag(),
                        R.string.setting);
                break;
            case COLORS_PREFERENCE:
                loadPrefFragment(new ColorPref()
                ,R.string.color_title);
                break;
            case FOLDERS_PREFERENCE:
                loadPrefFragment(new FoldersPref(),
                        R.string.sidebarfolders_title);
                break;
            case QUICKACCESS_PREFERENCE:
                loadPrefFragment(new QuickAccessPref(),
                        R.string.sidebarfolders_title);
                break;
            case ADVANCEDSEARCH_PREFERENCE:
                loadPrefFragment(new AdvancedSearchPref(),
                        R.string.advanced_search);
                break;

        }
    }

    private void loadPrefFragment(PreferenceFragment
                                  fragment,
                                  @StringRes int titleBarName){
        FragmentTransaction t = getFragmentManager()
                .beginTransaction();
        t.replace(R.id.prefsfragment,
                fragment);
        t.commit();
        getSupportActionBar().setTitle(titleBarName);
    }



}
