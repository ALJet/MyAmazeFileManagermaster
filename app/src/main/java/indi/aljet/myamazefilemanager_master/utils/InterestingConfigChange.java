package indi.aljet.myamazefilemanager_master.utils;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * Created by PC-LJL on 2018/1/17.
 */

public class InterestingConfigChange {

    private static Configuration lastConfiguration = new
            Configuration();
    private static int lastDensity = -1;

    public static boolean isConfigChanged(Resources
                                          resources){
        int changedFieldsMask = lastConfiguration
                .updateFrom(resources.getConfiguration());
        boolean densityChanged = lastDensity !=
                resources.getDisplayMetrics().densityDpi;
        int mode = ActivityInfo.CONFIG_SCREEN_LAYOUT |
                ActivityInfo.CONFIG_UI_MODE | ActivityInfo
                .CONFIG_LOCALE;
        return densityChanged || (changedFieldsMask
        & mode) != 0;
    }


    public static void recycle(){
        lastConfiguration = new Configuration();
        lastDensity = -1;
    }


}
