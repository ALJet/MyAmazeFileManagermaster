package indi.aljet.myamazefilemanager_master.adapter.glide.apkimage;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class ApkImageDataFetcher
        implements DataFetcher<Drawable> {

    private PackageManager packageManager;
    private String model;

    public ApkImageDataFetcher(PackageManager packageManager, String model) {
        this.packageManager = packageManager;
        this.model = model;
    }


    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Drawable> callback) {
        PackageInfo pi = packageManager
                .getPackageArchiveInfo(model,0);
        pi.applicationInfo.sourceDir = model;
        callback.onDataReady(pi.applicationInfo.loadIcon(
                packageManager));
    }


    @Override
    public void cleanup() {
        // Intentionally empty only because we're not opening an InputStream or another I/O resource!
    }

    @Override
    public void cancel() {
        //No cancelation procedure
    }

    @NonNull
    @Override
    public Class<Drawable> getDataClass() {
        return Drawable.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

}
