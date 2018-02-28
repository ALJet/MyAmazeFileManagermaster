package indi.aljet.myamazefilemanager_master.adapter.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import java.util.Collections;
import java.util.List;

import indi.aljet.myamazefilemanager_master.utils.application.GlideRequest;


/**
 * Created by PC-LJL on 2018/2/7.
 */

public class AppsAdapterPreloadModel implements
        ListPreloader.PreloadModelProvider<String> {

    private GlideRequest<Drawable> request;
    private List<String> items;

    public AppsAdapterPreloadModel(Fragment f) {

        request = (GlideRequest<Drawable>)
                Glide.with(f).asDrawable();

    }

    public void setItemList(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        if(items == null) return Collections.emptyList();
        else return Collections.singletonList(items.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder getPreloadRequestBuilder(String item) {
        return request.clone().load(item);
    }

    public void loadApkImage(String item, ImageView v) {
        request.load(item).into(v);
    }
}
