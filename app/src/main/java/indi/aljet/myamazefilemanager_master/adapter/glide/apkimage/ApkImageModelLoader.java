package indi.aljet.myamazefilemanager_master.adapter.glide.apkimage;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class ApkImageModelLoader implements
        ModelLoader<String,Drawable> {

    private PackageManager packageManager;

    public ApkImageModelLoader(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    @Nullable
    @Override
    public LoadData<Drawable> buildLoadData(@NonNull String s, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(s),new
                ApkImageDataFetcher(packageManager,s));
    }

    @Override
    public boolean handles(@NonNull String s) {
        return s.substring(s.length() - 4,
                s.length())
                .toLowerCase().equals(".apk");
    }
}
