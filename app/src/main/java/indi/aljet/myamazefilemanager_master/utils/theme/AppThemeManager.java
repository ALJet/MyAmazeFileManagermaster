package indi.aljet.myamazefilemanager_master.utils.theme;

import android.content.SharedPreferences;

/**
 * Created by PC-LJL on 2018/1/12.
 */

public class AppThemeManager {
    private SharedPreferences preferences;

    private AppTheme appTheme;


    public AppThemeManager(SharedPreferences preferences){
        this.preferences = preferences;
        String themeId = preferences.getString("theme","0");
        appTheme = AppTheme.getTheme(Integer.parseInt(themeId
        )).getSimpleTheme();
    }


    public AppTheme getAppTheme(){
        return appTheme.getSimpleTheme();
    }


    public AppThemeManager setAppTheme(AppTheme appTheme){
        this.appTheme = appTheme;
        preferences.edit().putString("theme",Integer
        .toString(appTheme.getId()))
                .apply();
        return this;
    }

}
