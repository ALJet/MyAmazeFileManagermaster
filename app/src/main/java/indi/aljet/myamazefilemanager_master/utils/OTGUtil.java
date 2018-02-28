package indi.aljet.myamazefilemanager_master.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.filesystem.RootHelper;

/**
 * Created by PC-LJL on 2018/1/18.
 */

public class OTGUtil {

    public static final String PREFIX_OTG = "otg:/";



    public static ArrayList<HybridFileParcelable>
    getDocumentFilesList(String path, Context context){
        final ArrayList<HybridFileParcelable> files = new
                ArrayList<>();
        getDocumentFiles(path,context,files::add);
        return files;
    }


    public static void getDocumentFiles(String path,
                                        Context context,
                                        OnFileFound fileFound){
        SharedPreferences manager = PreferenceManager
                .getDefaultSharedPreferences(context);
        String rootUriString = manager
                .getString(MainActivity.KEY_PREF_OTG,null);
        DocumentFile rootUri = DocumentFile.fromTreeUri
                (context, Uri.parse(rootUriString));
        String[] parts = path.split("/");
        for(String part : parts){
            if(part.equals(OTGUtil.PREFIX_OTG + "/"))
                break;
            if(part.equals("otg:") || part.equals("")){
                continue;
            }
            rootUri = rootUri.findFile(part);
        }
        for(DocumentFile file : rootUri.listFiles()){
            if(file.exists()){
                long size = 0;
                if(!file.isDirectory()){
                    size = file.length();
                }
                Log.d(context.getClass().getSimpleName(),
                        "Found file : "+ file.getName());
                HybridFileParcelable baseFile = new
                        HybridFileParcelable(path + "/" +
                file.getName(), RootHelper
                .parseDocumentFilePermission(file),
                        file.lastModified(),size,file.isDirectory());
                baseFile.setName(file.getName());
                baseFile.setMode(OpenMode.OTG);
                fileFound.onFileFound(baseFile);
            }
        }
    }


    public static DocumentFile getDocumentFile(String path,
                                               Context context,
                                               boolean createRecursive){
        SharedPreferences manager = PreferenceManager
                .getDefaultSharedPreferences(context);
        String rootUriString = manager.getString(
                MainActivity.KEY_PREF_OTG,null);

        DocumentFile rootUri = DocumentFile.fromTreeUri(context
        ,Uri.parse(rootUriString));
        String[] parts = path.split("/");
        for(String part : parts){
            if(part.equals("otg:/"))
                break;
            if(part.equals("otg:") || part.equals("")){
                continue;
            }

            DocumentFile nextDocument = rootUri.findFile(part);
            if(createRecursive && (nextDocument == null ||
            !nextDocument.exists()) ){
                nextDocument = rootUri.createFile(part
                .substring(part.lastIndexOf(".")),part);
            }
            rootUri = nextDocument;
        }
        return rootUri;
    }

}
