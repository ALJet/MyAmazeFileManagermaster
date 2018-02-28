package indi.aljet.myamazefilemanager_master.utils.cloud;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.types.CloudMetaData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.database.CloudHandler;
import indi.aljet.myamazefilemanager_master.exceptions.CloudPluginException;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.ui.icons.MimeTypes;
import indi.aljet.myamazefilemanager_master.utils.DataUtils;
import indi.aljet.myamazefilemanager_master.utils.OnFileFound;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;

/**
 * Created by PC-LJL on 2018/1/25.
 */

public class CloudUtil {


    public static ArrayList<HybridFileParcelable> listFiles
            (String path, CloudStorage cloudStorage,
             OpenMode openMode)throws CloudPluginException{
        final ArrayList<HybridFileParcelable> baseFiles = new
                ArrayList<>();
        getCloudFiles(path,cloudStorage,openMode,new
                OnFileFound(){
                    @Override
                    public void onFileFound(HybridFileParcelable file) {
                        baseFiles.add(file);
                    }
                });
        return baseFiles;
    }


    public static void getCloudFiles(String path,
                                     CloudStorage cloudStorage,
                                     OpenMode openMode,
                                     OnFileFound fileFoundCallback)
    throws CloudPluginException{
        String strippedPath = stripPath(openMode,path);
        try{
            for(CloudMetaData cloudMetaData : cloudStorage
                    .getChildren(strippedPath)){
                HybridFileParcelable baseFile = new
                        HybridFileParcelable(path + "/" +
                cloudMetaData.getName() ,"",(cloudMetaData
                .getModifiedAt() == null) ? 01: cloudMetaData.getModifiedAt(),
                cloudMetaData.getSize(),cloudMetaData.getFolder());
                baseFile.setName(cloudMetaData.getName());
                baseFile.setMode(openMode);
                fileFoundCallback.onFileFound(baseFile);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new CloudPluginException();
        }
    }


    public static String stripPath(OpenMode openMode,
                                   String path){
        String strippedPath = path;
        switch (openMode){
            case DROPBOX:
                if(path.equals(CloudHandler
                .CLOUD_NAME_DROPBOX + "/")){
                    strippedPath = path.replace(
                            CloudHandler.CLOUD_NAME_DROPBOX,
                            "");
                }else{
                    strippedPath = path.replace(CloudHandler
                    .CLOUD_PREFIX_DROPBOX + "/","");
                }
                break;
            case BOX:
                if(path.equals(CloudHandler
                .CLOUD_PREFIX_BOX + "/")){
                    strippedPath = path.replace(CloudHandler
                    .CLOUD_PREFIX_BOX ,"");
                }else{
                    strippedPath = path.replace
                            (CloudHandler.CLOUD_PREFIX_BOX + "/", "");
                }
                break;
            case ONEDRIVE:
                if(path.equals(CloudHandler
                .CLOUD_PREFIX_ONE_DRIVE + "/")){
                    strippedPath = path.replace(
                            CloudHandler.CLOUD_PREFIX_ONE_DRIVE
                    ,"");
                }else{
                    strippedPath = path.replace(
                            CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/", "");
            }
            break;
            case GDRIVE:
                if (path.equals(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/")) {
                    // we're at root, just replace the prefix
                    strippedPath = path.replace(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE, "");
                } else {
                    // we're not at root, replace prefix + /
                    strippedPath = path.replace(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/", "");
                }
                break;
            default:
                break;
        }
        return strippedPath;
    }


    public static void launchCloud(final HybridFileParcelable baseFile,
                                   final OpenMode serviceType,
                                   final Activity activity){
        final CloudStreamer streamer = CloudStreamer
                .getInstance();

        new Thread(() -> {
            streamer.setStreamSrc(baseFile
            .getInputStream(activity),baseFile
            .getName(),baseFile.length(activity));
            activity.runOnUiThread(() ->{
                try{
                    File file = new File(Uri.parse
                            (CloudUtil.stripPath
                                    (serviceType, baseFile.
                                            getPath())).getPath());
                    Uri uri = Uri.parse(CloudStreamer
                    .URL + Uri.fromFile(file).getEncodedPath());
                    Intent i = new Intent(Intent
                    .ACTION_VIEW);
                    i.setDataAndType(uri, MimeTypes
                    .getMimeType(file));
                    PackageManager packageManager =
                            activity.getPackageManager();
                    List<ResolveInfo> resInfos = packageManager
                            .queryIntentActivities(i,0);
                    if(resInfos != null && resInfos
                            .size() > 0){
                        activity.startActivity(i);
                    }else{
                        Toast.makeText(activity,
                                activity.getResources().getString(R.string
                                .smb_launch_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }

            });

        }).start();
    }



    public static void checkToken(String path,
                                  final MainActivity mainActivity){
        new AsyncTask<String,Void,Boolean>(){

            OpenMode serviceType;
            private DataUtils dataUtils = DataUtils
                    .getInstance();

            @Override
            protected Boolean doInBackground(String... strings) {
                boolean isTokenValid = true;
                String path = strings[0];

                if(path.startsWith(CloudHandler
                .CLOUD_PREFIX_DROPBOX)){
                    serviceType = OpenMode.DROPBOX;
                    CloudStorage cloudStorage =
                            dataUtils.getAccount(OpenMode.DROPBOX);
                    try{
                        cloudStorage.getUserLogin();
                    }catch (Exception e){
                        e.printStackTrace();
                        isTokenValid = false;
                    }
                }else if(path.startsWith(CloudHandler
                .CLOUD_PREFIX_ONE_DRIVE)){
                    serviceType = OpenMode.ONEDRIVE;
                    CloudStorage cloudStorageOneDrive = dataUtils.getAccount(OpenMode.ONEDRIVE);

                    try {
                        cloudStorageOneDrive.getUserLogin();
                    } catch (Exception e) {
                        e.printStackTrace();

                        isTokenValid = false;
                    }
                }else if (path.startsWith(CloudHandler.CLOUD_PREFIX_BOX)) {

                    serviceType = OpenMode.BOX;
                    CloudStorage cloudStorageBox = dataUtils.getAccount(OpenMode.BOX);

                    try {
                        cloudStorageBox.getUserLogin();
                    } catch (Exception e) {
                        e.printStackTrace();

                        isTokenValid = false;
                    }
                } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE)) {

                    serviceType = OpenMode.GDRIVE;
                    CloudStorage cloudStorageGDrive = dataUtils.getAccount(OpenMode.GDRIVE);

                    try {
                        cloudStorageGDrive.getUserLogin();
                    } catch (Exception e) {
                        e.printStackTrace();

                        isTokenValid = false;
                    }
                }
                return isTokenValid;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(!aBoolean){
                    Toast.makeText(mainActivity, mainActivity.getResources()
                            .getString(R.string.cloud_token_lost), Toast.LENGTH_LONG).show();
                    mainActivity.deleteConnection(serviceType);
                    mainActivity.addConnection(serviceType);
                }
            }
        }.execute(path);
    }

}
