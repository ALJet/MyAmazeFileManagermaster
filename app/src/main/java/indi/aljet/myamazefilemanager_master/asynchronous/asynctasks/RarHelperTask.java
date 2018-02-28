package indi.aljet.myamazefilemanager_master.asynchronous.asynctasks;

import android.os.AsyncTask;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import indi.aljet.myamazefilemanager_master.adapter.data.CompressedObjectParcelable;
import indi.aljet.myamazefilemanager_master.filesystem.compressed.RarHelper;
import indi.aljet.myamazefilemanager_master.utils.OnAsyncTaskFinished;

/**
 * Created by PC-LJL on 2018/1/31.
 */

public class RarHelperTask extends AsyncTask<Void,
        Void,ArrayList<CompressedObjectParcelable>> {

    private String fileLocation;
    private String relativeDirectory;

    private boolean createBackItem;
    private OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>>
     onFinish;


    public RarHelperTask(String realFileDirectory, String dir, boolean goBack,
                         OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> l) {
        fileLocation = realFileDirectory;
        relativeDirectory = dir;
        createBackItem = goBack;
        onFinish = l;
    }

    @Override
    protected ArrayList<CompressedObjectParcelable> doInBackground(Void... voids) {
        ArrayList<CompressedObjectParcelable> elements = new ArrayList<>();

        try{
            if(createBackItem){
                elements.add(0,new
                        CompressedObjectParcelable());
            }

            Archive zipfile = new Archive(new File(fileLocation));
            String relativeDirDiffSeparator =
                    relativeDirectory.replace("/","\\");
            for(FileHeader header : zipfile.getFileHeaders()){
                String name = header.getFileNameString();
                boolean isInBaseDir = (relativeDirDiffSeparator == null || relativeDirDiffSeparator.equals("")) && !name.contains("\\");
                boolean isInRelativeDir = relativeDirDiffSeparator != null && name.contains("\\")
                        && name.substring(0, name.lastIndexOf("\\")).equals(relativeDirDiffSeparator);
                if (isInBaseDir || isInRelativeDir) {
                    elements.add(new CompressedObjectParcelable(RarHelper.convertName(header), 0, header.getDataSize(), header.isDirectory()));
                }
            }
            Collections.sort(elements,new CompressedObjectParcelable.Sorter());

        }catch (RarException | IOException e){
            e.printStackTrace();
        }

        return elements;
    }

    @Override
    protected void onPostExecute(ArrayList<CompressedObjectParcelable> compressedObjectParcelables) {
        super.onPostExecute(compressedObjectParcelables);
        onFinish.onAsyncTaskFinished(compressedObjectParcelables);
    }
}
