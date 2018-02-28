package indi.aljet.myamazefilemanager_master.asynchronous.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Formatter;
import android.support.v4.util.Pair;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import indi.aljet.myamazefilemanager_master.adapter.data.AppDataParcelable;
import indi.aljet.myamazefilemanager_master.utils.InterestingConfigChange;
import indi.aljet.myamazefilemanager_master.utils.broadcast_receiver.PackageReceiver;


/**
 * Created by PC-LJL on 2018/2/1.
 */

public class AppListLoader extends AsyncTaskLoader<AppListLoader
        .AppsDataPair> {

    private PackageManager packageManager;
    private PackageReceiver packageReceiver;
    private AppsDataPair mApps;
    private int sortBy,asc;


    public AppListLoader(Context context, int sortBy, int asc) {
        super(context);

        this.sortBy = sortBy;
        this.asc = asc;

        /*
         * using global context because of the fact that loaders are supposed to be used
         * across fragments and activities
         */
        packageManager = getContext().getPackageManager();
    }

    @Override
    public AppsDataPair loadInBackground() {
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(
                PackageManager.MATCH_UNINSTALLED_PACKAGES |
                        PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS);

        if (apps == null)
            return new AppsDataPair(Collections.emptyList(), Collections.emptyList());

        mApps = new AppsDataPair(new ArrayList<>(apps.size()), new ArrayList<>(apps.size()));

        for (ApplicationInfo object : apps) {
            File sourceDir = new File(object.sourceDir);

            String label = object.loadLabel(packageManager).toString();
            PackageInfo info;

            try {
                info = packageManager.getPackageInfo(object.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                info = null;
            }

            AppDataParcelable elem = new AppDataParcelable(
                    label == null ? object.packageName : label,
                    object.sourceDir, object.packageName,
                    object.flags + "_" + (info!=null ? info.versionName:""),
                    Formatter.formatFileSize(getContext(), sourceDir.length()),
                    sourceDir.length(), sourceDir.lastModified());

            mApps.first.add(elem);

            Collections.sort(mApps.first, new AppDataParcelable.AppDataSorter(sortBy, asc));

            for (AppDataParcelable p : mApps.first) {
                mApps.second.add(p.path);
            }
        }

        return mApps;

    }

    @Override
    public void deliverResult(AppsDataPair data) {
        if (isReset()) {

            if (data != null)
                onReleaseResources(data);//TODO onReleaseResources() is empty
        }

        // preserving old data for it to be closed
        AppsDataPair oldData = mApps;
        mApps = data;
        if (isStarted()) {
            // loader has been started, if we have data, return immediately
            super.deliverResult(mApps);
        }

        // releasing older resources as we don't need them now
        if (oldData != null) {
            onReleaseResources(oldData);//TODO onReleaseResources() is empty
        }
    }

    @Override
    protected void onStartLoading() {
        if (mApps != null) {
            // we already have the results, load immediately
            deliverResult(mApps);
        }

        if (packageReceiver != null) {
            packageReceiver = new PackageReceiver(this);
        }

        boolean didConfigChange = InterestingConfigChange.isConfigChanged(getContext().getResources());

        if (takeContentChanged() || mApps == null || didConfigChange) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(AppsDataPair data) {
        super.onCanceled(data);
        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if(mApps != null){
            onReleaseResources(mApps);
            mApps = null;
        }
        if(packageReceiver != null){
            getContext().unregisterReceiver(packageReceiver);
            packageReceiver = null;
        }
        InterestingConfigChange.recycle();
    }

    private void onReleaseResources(AppsDataPair layoutElementList){
    }

    public static class AppsDataPair extends
            Pair<List<AppDataParcelable>,List<String>>{
        public AppsDataPair(List<AppDataParcelable> first,
                            List<String> second){
            super(first,second);
        }
    }
}
