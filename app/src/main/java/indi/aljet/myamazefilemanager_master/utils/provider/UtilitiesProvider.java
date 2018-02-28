package indi.aljet.myamazefilemanager_master.utils.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import indi.aljet.myamazefilemanager_master.utils.color.ColorPreference;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;
import indi.aljet.myamazefilemanager_master.utils.theme.AppThemeManager;

/**
 * Created by PC-LJL on 2018/1/23.
 */

public class UtilitiesProvider implements UtilitiesProviderInterface {


    private ColorPreference colorPreference;
    private AppThemeManager appThemeManager;

    public UtilitiesProvider(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        colorPreference = ColorPreference.loadFromPreferences
                (context,sharedPreferences);
        appThemeManager = new AppThemeManager(sharedPreferences);
    }

    public ColorPreference getColorPreference() {
        return colorPreference;
    }


    @Override
    public AppTheme getAppTheme() {
        return appThemeManager.getAppTheme();
    }

    @Override
    public AppThemeManager getThemeManager() {
        return appThemeManager;
    }
}
