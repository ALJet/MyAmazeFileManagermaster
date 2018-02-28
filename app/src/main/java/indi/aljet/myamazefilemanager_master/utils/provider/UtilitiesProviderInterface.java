package indi.aljet.myamazefilemanager_master.utils.provider;

import indi.aljet.myamazefilemanager_master.utils.color.ColorPreference;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;
import indi.aljet.myamazefilemanager_master.utils.theme.AppThemeManager;

/**
 * Created by PC-LJL on 2018/1/23.
 */

public interface UtilitiesProviderInterface {

    ColorPreference getColorPreference();
    AppTheme getAppTheme();

    AppThemeManager getThemeManager();

}
