package indi.aljet.myamazefilemanager_master.filesystem.compressed;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.adapter.data.CompressedObjectParcelable;
import indi.aljet.myamazefilemanager_master.asynchronous.asynctasks.ZipHelperTask;
import indi.aljet.myamazefilemanager_master.asynchronous.services.ExtractService;
import indi.aljet.myamazefilemanager_master.utils.OnAsyncTaskFinished;
import indi.aljet.myamazefilemanager_master.utils.ServiceWatcherUtil;

/**
 * Created by PC-LJL on 2018/1/16.
 */

public class ZipHelper implements CompressedInterface {

    private String filePath;
    private Context context;

    public ZipHelper(Context context) {
        this.context = context;
    }

    @Override
    public void setFilePath(String path) {
        filePath = path;
    }

    @Override
    public void changePath(String path, boolean addGoBackItem, OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        new ZipHelperTask(context,
                filePath,path,addGoBackItem,onFinish)
                .execute();
    }

    @Override
    public void decompress(String whereToDecompress) {
        Intent intent = new Intent(context,
                ExtractService.class);
        intent.putExtra(ExtractService.KEY_PATH_ZIP, filePath);
        intent.putExtra(ExtractService.KEY_ENTRIES_ZIP, new String[0]);
        intent.putExtra(ExtractService.KEY_PATH_EXTRACT, whereToDecompress);
        ServiceWatcherUtil.runService(context, intent);
    }

    @Override
    public void decompress(String whereToDecompress, String[] subDirectories) {
        Intent intent = new Intent(context, ExtractService.class);
        intent.putExtra(ExtractService.KEY_PATH_ZIP, filePath);
        intent.putExtra(ExtractService.KEY_ENTRIES_ZIP, subDirectories);
        intent.putExtra(ExtractService.KEY_PATH_EXTRACT, whereToDecompress);
        ServiceWatcherUtil.runService(context, intent);
    }
}
