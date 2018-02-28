package indi.aljet.myamazefilemanager_master.utils.application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

/**
 * Created by PC-LJL on 2018/1/12.
 */

public class GlideApplication extends LeakCanaryApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
    }
}
