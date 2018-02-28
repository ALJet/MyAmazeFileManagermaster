package indi.aljet.myamazefilemanager_master.filesystem.compressed;

import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.adapter.data.CompressedObjectParcelable;
import indi.aljet.myamazefilemanager_master.utils.OnAsyncTaskFinished;

/**
 * Created by PC-LJL on 2018/1/16.
 */

public interface CompressedInterface {

    void setFilePath(String path);

    void changePath(String path, boolean addGoBackItem,
                    OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>>
                    onFinish);


    void decompress(String whereToDecompress);


    void decompress(String whereToDecompress,
                    String[] subDirectories);
}
