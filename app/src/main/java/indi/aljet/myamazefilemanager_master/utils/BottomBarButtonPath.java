package indi.aljet.myamazefilemanager_master.utils;

import android.support.annotation.DrawableRes;

/**
 * Created by PC-LJL on 2018/1/12.
 */

public interface BottomBarButtonPath {
    void changePath(String path);
    String getPath();

    @DrawableRes
    int getRootDrawable();
}
