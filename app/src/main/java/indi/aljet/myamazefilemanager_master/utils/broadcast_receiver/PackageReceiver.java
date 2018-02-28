package indi.aljet.myamazefilemanager_master.utils.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import indi.aljet.myamazefilemanager_master.asynchronous.loaders.AppListLoader;

/**
 * Created by PC-LJL on 2018/1/25.
 */

public class PackageReceiver extends BroadcastReceiver {

    private AppListLoader listLoader;


    public PackageReceiver(AppListLoader listLoader) {
        this.listLoader = listLoader;

        IntentFilter filter =  new IntentFilter(Intent
        .ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        listLoader.getContext().registerReceiver(this,
                filter);
        IntentFilter sdcardFilter = new IntentFilter(Intent
        .ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdcardFilter.addAction(Intent
        .ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        listLoader.getContext().registerReceiver(this,
                sdcardFilter);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listLoader.onContentChanged();
    }
}
