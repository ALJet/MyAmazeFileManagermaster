package indi.aljet.myamazefilemanager_master.utils.application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import org.litepal.LitePalApplication;

/**
 * Created by PC-LJL on 2018/1/12.
 */

public class LeakCanaryApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(this);
    }
}
