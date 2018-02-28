package indi.aljet.myamazefilemanager_master.asynchronous.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cloudrail.si.interfaces.CloudStorage;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.activities.superclasses.ThemedActivity;
import indi.aljet.myamazefilemanager_master.asynchronous.services.CopyService;
import indi.aljet.myamazefilemanager_master.database.CryptHandler;
import indi.aljet.myamazefilemanager_master.db.DBHelper;
import indi.aljet.myamazefilemanager_master.db.tables.Encrypted;
import indi.aljet.myamazefilemanager_master.exceptions.ShellNotRunningException;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.fragments.MainFragment;
import indi.aljet.myamazefilemanager_master.utils.DataUtils;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.RootUtils;
import indi.aljet.myamazefilemanager_master.utils.ServiceWatcherUtil;
import indi.aljet.myamazefilemanager_master.utils.application.AppConfig;
import indi.aljet.myamazefilemanager_master.utils.cloud.CloudUtil;
import indi.aljet.myamazefilemanager_master.utils.files.CryptUtil;
import indi.aljet.myamazefilemanager_master.utils.files.FileUtils;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by PC-LJL on 2018/1/31.
 */

public class MoveFiles extends AsyncTask
<ArrayList<String>,Void,Boolean>{
    private ArrayList<ArrayList<HybridFileParcelable>> files;
    private MainFragment mainFrag;
    private ArrayList<String> paths;
    private Context context;
    private OpenMode mode;

    public MoveFiles(ArrayList<ArrayList<HybridFileParcelable>> files, MainFragment ma, Context context, OpenMode mode) {
        mainFrag = ma;
        this.context = context;
        this.files = files;
        this.mode = mode;
    }


    @Override
    protected Boolean doInBackground(ArrayList<String>[] strings) {
        paths = strings[0];
        if(files.size() == 0)
            return true;
        switch (mode) {
            case SMB:
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable f : files.get(i)) {
                        try {
                            SmbFile source = new SmbFile(f.getPath());
                            SmbFile dest = new SmbFile(paths.get(i) + "/" + f.getName());
                            source.renameTo(dest);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            return false;
                        } catch (SmbException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                break;
            case FILE:
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable f : files.get(i)) {
                        File dest = new File(paths.get(i) + "/" + f.getName());
                        File source = new File(f.getPath());
                        if (!source.renameTo(dest)) {

                            // check if we have root
                            if (ThemedActivity.rootMode) {
                                try {
                                    if (!RootUtils.rename(f.getPath(), paths.get(i) + "/" + f.getName()))
                                        return false;
                                } catch (ShellNotRunningException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            } else return false;
                        }
                    }
                }
                break;
            case DROPBOX:
            case BOX:
            case ONEDRIVE:
            case GDRIVE:
                for (int i=0; i<paths.size(); i++) {
                    for (HybridFileParcelable baseFile : files.get(i)) {

                        DataUtils dataUtils = DataUtils.getInstance();

                        CloudStorage cloudStorage = dataUtils.getAccount(mode);
                        String targetPath = paths.get(i) + "/" + baseFile.getName();
                        if (baseFile.getMode() == mode) {
                            // source and target both in same filesystem, use API method
                            try {

                                cloudStorage.move(CloudUtil.stripPath(mode, baseFile.getPath()),
                                        CloudUtil.stripPath(mode, targetPath));
                            } catch (Exception e) {
                                return false;
                            }
                        }  else {
                            // not in same filesystem, execute service
                            return false;
                        }
                    }
                }
            default:
                return false;
        }

        return true;
    }


    @Override
    protected void onPostExecute(Boolean movedCorrectly) {
        if (movedCorrectly) {
            if (mainFrag != null && mainFrag.getCurrentPath().equals(paths.get(0))) {
                // mainFrag.updateList();
                Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);

                intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, paths.get(0));
                context.sendBroadcast(intent);
            }

            for (int i = 0; i < paths.size(); i++) {
                for (HybridFileParcelable f : files.get(i)) {
                    FileUtils.scanFile(f.getPath(), context);
                    FileUtils.scanFile(paths.get(i) + "/" + f.getName(), context);
                }
            }

            // updating encrypted db entry if any encrypted file was moved
            AppConfig.runInBackground(() -> {
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable file : files.get(i)) {
                        if (file.getName().endsWith(CryptUtil.CRYPT_EXTENSION)) {
                            try {

                                //更新数据

                                CryptHandler cryptHandler = new CryptHandler(context);
                                Encrypted oldEntrypted = DBHelper.findEntrypted(file.getPath());
                                Encrypted newEntry = new Encrypted();
                                newEntry.set_id(oldEntrypted.get_id());
                                newEntry.setPassword(oldEntrypted.getPassword());
                                newEntry.setPath(paths.get(i) + "/" + file.getName());
                                DBHelper.updateEntrypted(oldEntrypted,newEntry);


//                                CryptHandler cryptHandler = new CryptHandler(context);
//                                EncryptedEntry oldEntry = cryptHandler.findEntry(file.getPath());
//                                EncryptedEntry newEntry = new EncryptedEntry();
//                                newEntry.setId(oldEntry.getId());
//                                newEntry.setPassword(oldEntry.getPassword());
//                                newEntry.setPath(paths.get(i) + "/" + file.getName());
//                                cryptHandler.updateEntry(oldEntry, newEntry);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // couldn't change the entry, leave it alone
                            }
                        }
                    }
                }
            });

        } else {
            for (int i = 0; i < paths.size(); i++) {
                Intent intent = new Intent(context, CopyService.class);
                intent.putExtra(CopyService.TAG_COPY_SOURCES, files.get(i));
                intent.putExtra(CopyService.TAG_COPY_TARGET, paths.get(i));
                intent.putExtra(CopyService.TAG_COPY_MOVE, true);
                intent.putExtra(CopyService.TAG_COPY_OPEN_MODE, mode.ordinal());

                ServiceWatcherUtil.runService(context, intent);
            }
        }
    }
}
