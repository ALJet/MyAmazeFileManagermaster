package indi.aljet.myamazefilemanager_master.adapter.glide.apkimage;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class ApkImageModelLoaderFactory implements
        ModelLoaderFactory<String,Drawable> {

    private PackageManager packageManager;

    public ApkImageModelLoaderFactory(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    @NonNull
    @Override
    public ModelLoader<String, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new ApkImageModelLoader(packageManager);
    }

    @Override
    public void teardown() {

    }
}
