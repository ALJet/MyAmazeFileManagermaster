package indi.aljet.myamazefilemanager_master.filesystem.compressed;

import android.content.Context;

import java.io.File;

/**
 * Created by PC-LJL on 2018/1/16.
 */

public class CompressedHelper {
    public static CompressedInterface
    getCompressedInterfaceInstance(Context context,
                                   File file){
        CompressedInterface compressedInterface;

        String path = file.getPath().toLowerCase();
        boolean isZip = path.endsWith(".zip") ||
                path.endsWith(".jar") || path
                .endsWith(".apk");
        boolean isTar = path.endsWith(".tar") ||
                path.endsWith(".tar.gz");
        boolean isRar = path.endsWith(".rar");

        if(isZip || isTar){
            compressedInterface = new ZipHelper(context);
        }else if (isRar) {
            compressedInterface = new RarHelper(context);
        } else {
            return null;
        }
        compressedInterface.setFilePath(file.getPath());
        return compressedInterface;
    }
}
