package indi.aljet.myamazefilemanager_master.activities.superclasses;

import android.support.v7.app.AppCompatActivity;

import indi.aljet.myamazefilemanager_master.utils.application.AppConfig;
import indi.aljet.myamazefilemanager_master.utils.color.ColorPreference;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;
import indi.aljet.myamazefilemanager_master.utils.theme.AppThemeManager;

/**
 * Created by PC-LJL on 2018/2/12.
 */

public class BasicActivity extends AppCompatActivity
implements UtilitiesProviderInterface{

    private boolean initialized = false;
    private UtilitiesProviderInterface utilsProvider;

    private void initialize(){
        utilsProvider = getAppConfig()
                .getUtilsProvider();
        initialized = true;
    }

    public AppConfig getAppConfig(){
        return (AppConfig) getApplication();
    }

    public ColorPreference getColorPreference(){
        if(!initialized)
            initialize();
        return utilsProvider.getColorPreference();
    }

    @Override
    public AppTheme getAppTheme() {
        if (!initialized)
            initialize();

        return utilsProvider.getAppTheme();
    }

    @Override
    public AppThemeManager getThemeManager() {
        if (!initialized)
            initialize();

        return utilsProvider.getThemeManager();
    }
}
