package indi.aljet.myamazefilemanager_master.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

/**
 * Created by PC-LJL on 2018/1/17.
 */

public class LruBitmapCache extends LruCache<String,Bitmap>
 implements ImageLoader.ImageCache{
     public LruBitmapCache(int maxSize){
         super(maxSize);
     }

     public LruBitmapCache(){
         this(getDefaultCacheSize());
     }

     private static int getDefaultCacheSize(){
         int memory = (int) (Runtime
         .getRuntime().maxMemory() / 1024);
         return memory / 8;
     }


    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
         put(url,bitmap);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount();
    }
}
