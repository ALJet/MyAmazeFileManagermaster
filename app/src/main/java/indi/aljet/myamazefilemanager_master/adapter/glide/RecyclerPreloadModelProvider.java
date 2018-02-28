package indi.aljet.myamazefilemanager_master.adapter.glide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import java.util.Collections;
import java.util.List;

import indi.aljet.myamazefilemanager_master.adapter.data.IconDataParcelable;
import indi.aljet.myamazefilemanager_master.utils.application.GlideApp;
import indi.aljet.myamazefilemanager_master.utils.application.GlideRequest;


/**
 * Created by PC-LJL on 2018/2/7.
 */

public class RecyclerPreloadModelProvider implements
        ListPreloader.PreloadModelProvider<IconDataParcelable> {

    private Fragment fragment;
    private List<IconDataParcelable> urisToLoad;
    private boolean showThumbs;

    public RecyclerPreloadModelProvider(@NonNull Fragment fragment, @NonNull List<IconDataParcelable> uris,
                                        boolean showThumbs) {
        this.fragment = fragment;
        urisToLoad = uris;
        this.showThumbs = showThumbs;
    }

    @Override
    @NonNull
    public List<IconDataParcelable> getPreloadItems(int position) {
        IconDataParcelable iconData = urisToLoad.get(position);
        if (iconData == null) return Collections.emptyList();
        return Collections.singletonList(iconData);
    }

    @Override
    @Nullable
    public RequestBuilder getPreloadRequestBuilder(IconDataParcelable iconData) {
        GlideRequest request;

        if(!showThumbs) {
            request = GlideApp.with(fragment).asDrawable().fitCenter().load(iconData.image);
        } else {
            if (iconData.type == IconDataParcelable.IMAGE_FROMFILE) {
                request = GlideApp.with(fragment).asDrawable().centerCrop().load(iconData.path).fallback(iconData.image);
            } else {
                request = GlideApp.with(fragment).asDrawable().centerCrop().load(iconData.image);
            }
        }

        return request;
    }


}
