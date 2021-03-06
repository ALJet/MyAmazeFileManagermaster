package indi.aljet.myamazefilemanager_master.asynchronous.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Path;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Pair;
import android.util.Patterns;

import com.cloudrail.si.interfaces.CloudStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.superclasses.ThemedActivity;
import indi.aljet.myamazefilemanager_master.adapter.data.LayoutElementParcelable;
import indi.aljet.myamazefilemanager_master.database.UtilsHandler;
import indi.aljet.myamazefilemanager_master.exceptions.CloudPluginException;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFile;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.filesystem.RootHelper;
import indi.aljet.myamazefilemanager_master.fragments.CloudSheetFragment;
import indi.aljet.myamazefilemanager_master.fragments.MainFragment;
import indi.aljet.myamazefilemanager_master.utils.DataUtils;
import indi.aljet.myamazefilemanager_master.utils.OTGUtil;
import indi.aljet.myamazefilemanager_master.utils.OnAsyncTaskFinished;
import indi.aljet.myamazefilemanager_master.utils.OnFileFound;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.application.AppConfig;
import indi.aljet.myamazefilemanager_master.utils.cloud.CloudUtil;
import indi.aljet.myamazefilemanager_master.utils.files.FileListSorter;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by PC-LJL on 2018/1/30.
 */

public class LoadFilesListTask extends AsyncTask<Void,
        Void,Pair<OpenMode,ArrayList<LayoutElementParcelable>>> {


    private String path;
    private MainFragment ma;
    private Context c;
    private OpenMode openmode;
    private DataUtils dataUtils = DataUtils.getInstance();
    private OnAsyncTaskFinished<Pair<OpenMode,ArrayList<LayoutElementParcelable>>>
     listener;


    public LoadFilesListTask(Context c, String path, MainFragment ma, OpenMode openmode,
                             OnAsyncTaskFinished<Pair<OpenMode, ArrayList<LayoutElementParcelable>>> l) {
        this.path = path;
        this.ma = ma;
        this.openmode = openmode;
        this.c = c;
        this.listener = l;
    }

    @Override
    protected Pair<OpenMode, ArrayList<LayoutElementParcelable>> doInBackground(Void... voids) {
        HybridFile hFile = null;
        if(openmode == OpenMode.UNKNOWN){
            hFile = new HybridFile(OpenMode.UNKNOWN,path);
            hFile.generateMode(ma.getActivity());
            openmode = hFile.getMode();
            if(hFile.isSmb()){
                ma.smbPath = path;
            }else if(Patterns.EMAIL_ADDRESS.matcher
                    (path).matches()){
                openmode = OpenMode.ROOT;
            }
        }
        if(isCancelled()){
            return null;
        }

        ma.folder_count = 0;
        ma.file_count = 0;
        final ArrayList<LayoutElementParcelable> list;
        switch (openmode) {
            case SMB:
                if (hFile == null) {
                    hFile = new HybridFile(OpenMode.SMB, path);
                }

                try {
                    SmbFile[] smbFile = hFile.getSmbFile(5000).listFiles();
                    list = ma.addToSmb(smbFile, path);
                    openmode = OpenMode.SMB;
                } catch (SmbAuthException e) {
                    if (!e.getMessage().toLowerCase().contains("denied")) {
                        ma.reauthenticateSmb();
                    }
                    return null;
                } catch (SmbException | NullPointerException e) {
                    e.printStackTrace();
                    return null;
                }
                break;
            case CUSTOM:
                switch (Integer.parseInt(path)) {
                    case 0:
                        list = listImages();
                        break;
                    case 1:
                        list = listVideos();
                        break;
                    case 2:
                        list = listaudio();
                        break;
                    case 3:
                        list = listDocs();
                        break;
                    case 4:
                        list = listApks();
                        break;
                    case 5:
                        list = listRecent();
                        break;
                    case 6:
                        list = listRecentFiles();
                        break;
                    default:
                        throw new IllegalStateException();
                }

                break;
            case OTG:
                list = new ArrayList<>();
                listOtg(path, new OnFileFound() {
                    @Override
                    public void onFileFound(HybridFileParcelable file) {
                        LayoutElementParcelable elem = createListParcelables(file);
                        if(elem != null) list.add(elem);
                    }
                });
                openmode = OpenMode.OTG;
                break;
            case DROPBOX:
            case BOX:
            case GDRIVE:
            case ONEDRIVE:
                CloudStorage cloudStorage = dataUtils.getAccount(openmode);
                list = new ArrayList<>();

                try {
                    listCloud(path, cloudStorage, openmode, new OnFileFound() {
                        @Override
                        public void onFileFound(HybridFileParcelable file) {
                            LayoutElementParcelable elem = createListParcelables(file);
                            if(elem != null) list.add(elem);
                        }
                    });
                } catch (CloudPluginException e) {
                    e.printStackTrace();
                    AppConfig.toast(c, c.getResources().getString(R.string.failed_no_connection));
                    return new Pair<>(openmode, list);
                }
                break;
            default:
                // we're neither in OTG not in SMB, load the list based on root/general filesystem
                list = new ArrayList<>();
                RootHelper.getFiles(path, ThemedActivity.rootMode, ma.SHOW_HIDDEN,
                        new RootHelper.GetModeCallBack() {
                            @Override
                            public void getMode(OpenMode mode) {
                                openmode = mode;
                            }
                        }, new OnFileFound() {
                            @Override
                            public void onFileFound(HybridFileParcelable file) {
                                LayoutElementParcelable elem = createListParcelables(file);
                                if(elem != null) list.add(elem);
                            }
                        });
                break;
        }

        if (list != null && !(openmode == OpenMode.CUSTOM && ((path).equals("5") || (path).equals("6")))) {
            Collections.sort(list, new FileListSorter(ma.dsort, ma.sortby, ma.asc));
        }

        return new Pair<>(openmode, list);
    }


    @Override
    protected void onPostExecute(Pair<OpenMode, ArrayList<LayoutElementParcelable>> openModeArrayListPair) {
        super.onPostExecute(openModeArrayListPair);
        listener.onAsyncTaskFinished(openModeArrayListPair);
    }


    private LayoutElementParcelable createListParcelables(HybridFileParcelable baseFile) {
        if (!dataUtils.isFileHidden(baseFile.getPath())) {
            String size = "";
            long longSize= 0;

            if (baseFile.isDirectory()) {
                ma.folder_count++;
            } else {
                if (baseFile.getSize() != -1) {
                    try {
                        longSize = baseFile.getSize();
                        size = Formatter.formatFileSize(c, longSize);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                ma.file_count++;
            }

            LayoutElementParcelable layoutElement = new LayoutElementParcelable(
                    baseFile.getPath(), baseFile.getPermission(), baseFile.getLink(), size,
                    longSize, baseFile.isDirectory(), false, baseFile.getDate() + "", ma.SHOW_THUMBS);
            layoutElement.setMode(baseFile.getMode());
            return layoutElement;
        }

        return null;
    }

    private ArrayList<LayoutElementParcelable> listImages() {
        ArrayList<LayoutElementParcelable> songs = new ArrayList<>();
        final String[] projection = {MediaStore.Images.Media.DATA};
        final Cursor cursor = c.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                HybridFileParcelable strings = RootHelper.generateBaseFile(new File(path), ma.SHOW_HIDDEN);
                if (strings != null) {
                    LayoutElementParcelable parcelable = createListParcelables(strings);
                    if(parcelable != null) songs.add(parcelable);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }

    private ArrayList<LayoutElementParcelable> listVideos() {
        ArrayList<LayoutElementParcelable> songs = new ArrayList<>();
        final String[] projection = {MediaStore.Images.Media.DATA};
        final Cursor cursor = c.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                HybridFileParcelable strings = RootHelper.generateBaseFile(new File(path), ma.SHOW_HIDDEN);
                if (strings != null) {
                    LayoutElementParcelable parcelable = createListParcelables(strings);
                    if(parcelable != null) songs.add(parcelable);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }

    private ArrayList<LayoutElementParcelable> listaudio() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.DATA
        };

        Cursor cursor = c.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        ArrayList<LayoutElementParcelable> songs = new ArrayList<>();
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                HybridFileParcelable strings = RootHelper.generateBaseFile(new File(path), ma.SHOW_HIDDEN);
                if (strings != null) {
                    LayoutElementParcelable parcelable = createListParcelables(strings);
                    if(parcelable != null) songs.add(parcelable);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }

    private ArrayList<LayoutElementParcelable> listDocs() {
        ArrayList<LayoutElementParcelable> songs = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = c.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, null, null, null);
        String[] types = new String[]{".pdf", ".xml", ".html", ".asm", ".text/x-asm", ".def", ".in", ".rc",
                ".list", ".log", ".pl", ".prop", ".properties", ".rc",
                ".doc", ".docx", ".msg", ".odt", ".pages", ".rtf", ".txt", ".wpd", ".wps"};
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                if (path != null && Arrays.asList(types).contains(path)) {
                    HybridFileParcelable strings = RootHelper.generateBaseFile(new File(path), ma.SHOW_HIDDEN);
                    if (strings != null) {
                        LayoutElementParcelable parcelable = createListParcelables(strings);
                        if(parcelable != null) songs.add(parcelable);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(songs, (lhs, rhs) -> -1 * Long.valueOf(lhs.date).compareTo(rhs.date));
        if (songs.size() > 20)
            for (int i = songs.size() - 1; i > 20; i--) {
                songs.remove(i);
            }
        return songs;
    }

    private ArrayList<LayoutElementParcelable> listApks() {
        ArrayList<LayoutElementParcelable> songs = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA};

        Cursor cursor = c.getContentResolver()
                .query(MediaStore.Files.getContentUri("external"), projection, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                if (path != null && path.endsWith(".apk")) {
                    HybridFileParcelable strings = RootHelper.generateBaseFile(new File(path), ma.SHOW_HIDDEN);
                    if (strings != null) {
                        LayoutElementParcelable parcelable = createListParcelables(strings);
                        if(parcelable != null) songs.add(parcelable);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }

    private ArrayList<LayoutElementParcelable> listRecent() {
        UtilsHandler utilsHandler = new UtilsHandler(c);
        final LinkedList<String> paths = utilsHandler.getHistoryLinkedList();
        ArrayList<LayoutElementParcelable> songs = new ArrayList<>();
        for (String f : paths) {
            if (!f.equals("/")) {
                HybridFileParcelable hybridFileParcelable = RootHelper.generateBaseFile(new File(f), ma.SHOW_HIDDEN);
                if (hybridFileParcelable != null) {
                    hybridFileParcelable.generateMode(ma.getActivity());
                    if (!hybridFileParcelable.isSmb() && !hybridFileParcelable.isDirectory() && hybridFileParcelable.exists()) {
                        LayoutElementParcelable parcelable = createListParcelables(hybridFileParcelable);
                        if (parcelable != null) songs.add(parcelable);
                    }
                }
            }
        }
        return songs;
    }

    private ArrayList<LayoutElementParcelable> listRecentFiles() {
        ArrayList<LayoutElementParcelable> songs = new ArrayList<>();
        final String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_MODIFIED};
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 2);
        Date d = c.getTime();
        Cursor cursor = this.c.getContentResolver().query(MediaStore.Files
                        .getContentUri("external"), projection,
                null,
                null, null);
        if (cursor == null) return songs;
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex
                        (MediaStore.Files.FileColumns.DATA));
                File f = new File(path);
                if (d.compareTo(new Date(f.lastModified())) != 1 && !f.isDirectory()) {
                    HybridFileParcelable strings = RootHelper.generateBaseFile(new File(path), ma.SHOW_HIDDEN);
                    if (strings != null) {
                        LayoutElementParcelable parcelable = createListParcelables(strings);
                        if(parcelable != null) songs.add(parcelable);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(songs, (lhs, rhs) -> -1 * Long.valueOf(lhs.date).compareTo(rhs.date));
        if (songs.size() > 20)
            for (int i = songs.size() - 1; i > 20; i--) {
                songs.remove(i);
            }
        return songs;
    }

    /**
     * Lists files from an OTG device
     *
     * @param path the path to the directory tree, starts with prefix {@link }
     *             Independent of URI (or mount point) for the OTG
     */
    private void listOtg(String path, OnFileFound fileFound) {
        OTGUtil.getDocumentFiles(path, c, fileFound);
    }

    private void listCloud(String path, CloudStorage cloudStorage, OpenMode openMode,
                           OnFileFound fileFoundCallback) throws CloudPluginException {
        if (!CloudSheetFragment.isCloudProviderAvailable(c)) {
            throw new CloudPluginException();
        }

        CloudUtil.getCloudFiles(path, cloudStorage, openMode, fileFoundCallback);
    }
}
