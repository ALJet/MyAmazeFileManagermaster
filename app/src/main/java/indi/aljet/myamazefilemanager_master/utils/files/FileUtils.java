package indi.aljet.myamazefilemanager_master.utils.files;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.types.CloudMetaData;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

import indi.aljet.myamazefilemanager_master.activities.DatabaseViewerActivity;
import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFile;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.PrefFrag;
import indi.aljet.myamazefilemanager_master.ui.dialogs.GeneralDialogCreation;
import indi.aljet.myamazefilemanager_master.ui.icons.Icons;
import indi.aljet.myamazefilemanager_master.ui.icons.MimeTypes;
import indi.aljet.myamazefilemanager_master.utils.DataUtils;
import indi.aljet.myamazefilemanager_master.utils.OTGUtil;
import indi.aljet.myamazefilemanager_master.utils.OnFileFound;
import indi.aljet.myamazefilemanager_master.utils.OnProgressUpdate;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.application.AppConfig;
import indi.aljet.myamazefilemanager_master.utils.cloud.CloudUtil;
import indi.aljet.myamazefilemanager_master.utils.share.ShareTask;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;
import jcifs.smb.SmbFile;

/**
 * Created by PC-LJL on 2018/1/26.
 */

public class FileUtils {
    public static long folderSize(File directory,
                                  OnProgressUpdate<Long> updateState) {
        long length = 0;
        try {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += folderSize(file, null);
                }
                if (updateState != null) {
                    updateState.onUpdate(length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }


    public static long folderSize(HybridFile directory,
                                  OnProgressUpdate<Long> updateState) {
        return folderSize(new File(directory.getPath()),
                updateState);
    }

    public static long folderSize(SmbFile directory) {
        long length = 0;
        try {
            for (SmbFile file : directory.listFiles()) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += folderSize(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }


    public static long folderSizeCloud(OpenMode openMode,
                                       CloudMetaData sourceFileMeta) {
        DataUtils dataUtils = DataUtils.getInstance();
        long length = 0;
        CloudStorage cloudStorage = dataUtils
                .getAccount(openMode);
        for (CloudMetaData metaData : cloudStorage
                .getChildren(CloudUtil.stripPath(openMode
                        , sourceFileMeta.getPath()))) {
            if (metaData.getFolder()) {
                length += folderSizeCloud(openMode,
                        metaData);
            } else {
                length += metaData.getSize();
            }
        }
        return length;
    }


    public static long otgFolderSize(String path,
                                     final Context context) {
        final AtomicLong totalBytes = new AtomicLong(0);
        OTGUtil.getDocumentFiles(path, context, new
                OnFileFound() {
                    @Override
                    public void onFileFound(HybridFileParcelable file) {
                        totalBytes.addAndGet(getBaseFileSize(file,
                                context));
                    }
                });
        return totalBytes.longValue();
    }


    /**
     * Helper method to calculate source files size
     */
    public static long getTotalBytes(ArrayList<HybridFileParcelable> files, Context context) {
        long totalBytes = 0L;
        for (HybridFileParcelable file : files) {
            totalBytes += getBaseFileSize(file, context);
        }
        return totalBytes;
    }

    private static long getBaseFileSize(HybridFileParcelable baseFile, Context context) {
        if (baseFile.isDirectory(context)) {
            return baseFile.folderSize(context);
        } else {
            return baseFile.length(context);
        }
    }

    public static void scanFile(String path,
                                Context c) {
        System.out.println(path + " " + Build.VERSION.SDK_INT);
        Uri contentUri = Uri.fromFile(new File(path));
        Intent mediaScanIntent = new Intent(Intent
                .ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        c.sendBroadcast(mediaScanIntent);
    }

    /**
     * Starts a media scanner to let file system know changes done to files
     */
    public static void scanFile(final Context context, final
    MediaScannerConnection mediaScannerConnection,
                                final String[] paths) {

        Log.d("SCAN started", paths[0]);

        AppConfig.runInBackground(() -> {
            mediaScannerConnection.connect();
            mediaScannerConnection.scanFile(context, paths, null, null);
        });
    }


    public static void crossfade(View buttons,
                                final View pathbar) {
        buttons.setAlpha(0f);
        buttons.setVisibility(View.VISIBLE);
        buttons.animate()
                .alpha(0f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pathbar.setVisibility(View.GONE);
                    }
                });

    }


    public static void revealShow(final View view,
                                  boolean reveal) {
        if (reveal) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
            animator.setDuration(300); //ms
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        } else {

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
            animator.setDuration(300); //ms
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
            animator.start();
        }
    }


    public static void crossfadeInverse(final View buttons, final View pathbar) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.

        pathbar.setAlpha(0f);
        pathbar.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        pathbar.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
        buttons.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        buttons.setVisibility(View.GONE);
                    }
                });
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
    }


    public static void shareCloudFile(String path,
                                      final OpenMode openMode,
                                      final Context context) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String shareFilePath = strings[0];
                CloudStorage cloudStorage = DataUtils
                        .getInstance().getAccount(openMode);
                return cloudStorage.createShareLink(
                        CloudUtil.stripPath(openMode,
                                shareFilePath));
            }
        }.execute(path);
    }


    public static void shareFiles(ArrayList<File> a, Activity c, AppTheme appTheme, int fab_skin) {

        ArrayList<Uri> uris = new ArrayList<>();
        boolean b = true;
        for (File f : a) {
            uris.add(Uri.fromFile(f));
        }

        String mime = MimeTypes.getMimeType(a.get(0));
        if (a.size() > 1)
            for (File f : a) {
                if (!mime.equals(MimeTypes.getMimeType(f))) {
                    b = false;
                }
            }

        if (!b || mime == (null))
            mime = "*/*";
        try {

            new ShareTask(c, uris, appTheme, fab_skin).execute(mime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static float readableFileSizeFloat(long size) {
        if (size <= 0) {
            return 0;
        }
        return (float) (size / (1024 * 1024));
    }


    public static void openunknown(File f,
                                   Context c,
                                   boolean forcechooser,
                                   boolean useNewStack) {
        Intent chooserIntent = new Intent();
        chooserIntent.setAction(Intent.ACTION_VIEW);

        String type = MimeTypes.getMimeType(f);
        if (type != null && type.trim().length() != 0 &&
                !type.equals("*/*")) {
            Uri uri = fileToContentUri(c, f);
            if (uri == null) {
                uri = Uri.fromFile(f);
            }
            chooserIntent.setDataAndType(uri, type);

            Intent activityintent;

            if (forcechooser) {
                if (useNewStack) {
                    applyNewDocFlag(chooserIntent);
                }
                activityintent = Intent
                        .createChooser(chooserIntent,
                                c.getResources()
                                        .getString(R.string
                                                .openwith));
            } else {
                activityintent = chooserIntent;
                if (useNewStack) {
                    applyNewDocFlag(activityintent);
                }
            }
            try {
                c.startService(activityintent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            openWith(f, c, useNewStack);
        }
    }


    public static void openunknown(DocumentFile f,
                                   Context c,
                                   boolean forcechooser,
                                   boolean useNewStack) {
        Intent chooserIntent = new Intent();
        chooserIntent.setAction(Intent.ACTION_VIEW);
        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        String type = f.getType();
        if (type != null && type.trim().length() != 0 && !type.equals("*/*")) {
            chooserIntent.setDataAndType(f.getUri(), type);
            Intent activityIntent;
            if (forcechooser) {
                if (useNewStack) applyNewDocFlag(chooserIntent);
                activityIntent = Intent.createChooser(chooserIntent, c.getResources().getString(R.string.openwith));
            } else {
                activityIntent = chooserIntent;
                if (useNewStack) applyNewDocFlag(chooserIntent);
            }

            try {
                c.startActivity(activityIntent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(c, R.string.noappfound, Toast.LENGTH_SHORT).show();
                openWith(f, c, useNewStack);
            }
        } else {
            openWith(f, c, useNewStack);
        }
    }


    private static void applyNewDocFlag(Intent i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME
                    | Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
        }
    }

    private static final String INTERNAL_VOLUME = "internal";
    public static final String EXTERNAL_VOLUME = "external";
    private static final String EMULATED_STORAGE_SOURCE = System.getenv("EMULATED_STORAGE_SOURCE");
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");
    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");


    public static String normalizeMediapath(String path) {
        if (TextUtils.isEmpty(EMULATED_STORAGE_SOURCE) ||
                TextUtils.isEmpty(EMULATED_STORAGE_TARGET) ||
                TextUtils.isEmpty(EXTERNAL_STORAGE)) {
            return path;
        }
        if (path.startsWith(EMULATED_STORAGE_SOURCE)) {
            path = path.replace(EMULATED_STORAGE_SOURCE, EMULATED_STORAGE_TARGET);
        }
        return path;

    }

    public static Uri fileToContentUri(Context context,
                                       File file) {
        final String normalizedPath =
                normalizeMediapath(file
                        .getAbsolutePath());
        Uri uri = fileToContentUri(context, normalizedPath,
                EXTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        uri = fileToContentUri(context, normalizedPath,
                INTERNAL_VOLUME);
        if (uri != null) {
            return uri;
        }
        return null;
    }


    private static Uri fileToContentUri(Context context,
                                        String path,
                                        String volume) {
        final String where = MediaStore
                .MediaColumns.DATA + " = ?";
        Uri baseUri;
        String[] projection;
        int mimeType = Icons.getTypeOfFile(new File(path));
        switch (mimeType) {
            case Icons.IMAGE:
                baseUri = MediaStore.Images.Media
                        .EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.VIDEO:
                baseUri = MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            case Icons.AUDIO:
                baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                projection = new String[]{BaseColumns._ID};
                break;
            default:
                baseUri = MediaStore.Files.getContentUri(volume);
                projection = new String[]{BaseColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        }

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(baseUri, projection,
                where, new String[]{path}, null);
        try {
            if (c != null && c.moveToNext()) {
                boolean isValid = false;
                if (mimeType == Icons.IMAGE || mimeType == Icons.VIDEO || mimeType == Icons.AUDIO) {
                    isValid = true;
                } else {
                    int type = c.getInt(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
                    isValid = type != 0;
                }

                if (isValid) {
                    // Do not force to use content uri for no media files
                    long id = c.getLong(c.getColumnIndexOrThrow(BaseColumns._ID));
                    return Uri.withAppendedPath(baseUri, String.valueOf(id));
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;

    }


    public static void openWith(final File f,
                                final Context c,
                                final boolean useNewStack) {
        MaterialDialog.Builder a = new MaterialDialog.Builder(c);
        a.title(c.getResources().getString(R.string
                .openas));
        String[] items = new String[]{c.getResources().getString(R.string.text), c.getResources().getString(R.string.image), c.getResources().getString(R.string.video), c.getResources().getString(R.string.audio), c.getResources().getString(R.string.database), c.getResources().getString(R.string.other)};
        a.items(items).itemsCallback((materialDialog, view, i,
                                      charSequence) -> {
            Uri uri = fileToContentUri(c, f);
            if (uri == null)
                uri = Uri.fromFile(f);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            switch (i) {
                case 0:
                    if (useNewStack) applyNewDocFlag(intent);
                    intent.setDataAndType(uri, "text/*");
                    break;
                case 1:
                    intent.setDataAndType(uri, "image/*");
                    break;
                case 2:
                    intent.setDataAndType(uri, "video/*");
                    break;
                case 3:
                    intent.setDataAndType(uri, "audio/*");
                    break;
                case 4:
                    intent = new Intent(c, DatabaseViewerActivity.class);
                    intent.putExtra("path", f.getPath());
                    break;
                case 5:
                    intent.setDataAndType(uri, "*/*");
                    break;
            }
            try {
                c.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(c, R.string.noappfound, Toast.LENGTH_SHORT).show();
                openWith(f, c, useNewStack);
            }
        });
        try {
            a.build().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openWith(final DocumentFile f, final Context c, final boolean useNewStack) {
        MaterialDialog.Builder a = new MaterialDialog.Builder(c);
        a.title(c.getResources().getString(R.string.openas));
        String[] items = new String[]{c.getResources().getString(R.string.text), c.getResources().getString(R.string.image), c.getResources().getString(R.string.video), c.getResources().getString(R.string.audio), c.getResources().getString(R.string.database), c.getResources().getString(R.string.other)};

        a.items(items).itemsCallback((materialDialog, view, i, charSequence) -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            switch (i) {
                case 0:
                    if (useNewStack) applyNewDocFlag(intent);
                    intent.setDataAndType(f.getUri(), "text/*");
                    break;
                case 1:
                    intent.setDataAndType(f.getUri(), "image/*");
                    break;
                case 2:
                    intent.setDataAndType(f.getUri(), "video/*");
                    break;
                case 3:
                    intent.setDataAndType(f.getUri(), "audio/*");
                    break;
                case 4:
                    intent = new Intent(c, DatabaseViewerActivity.class);
                    intent.putExtra("path", f.getUri());
                    break;
                case 5:
                    intent.setDataAndType(f.getUri(), "*/*");
                    break;
            }
            try {
                c.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(c, R.string.noappfound, Toast.LENGTH_SHORT).show();
                openWith(f, c, useNewStack);
            }
        });

        a.build().show();
    }


    public static boolean canGoBack(Context context,
                                    HybridFile currentFile) {
        switch (currentFile.getMode()) {

            // we're on main thread and can't list the cloud files
            case DROPBOX:
            case BOX:
            case GDRIVE:
            case ONEDRIVE:
            case OTG:
                return true;
            default:
                return true;// TODO: 29/9/2017 there might be nothing to go back to (check parent)
        }
    }


    public static long[] getSpaces(HybridFile hFile,
                                   Context context,
                                   final OnProgressUpdate<Long[]>
                                           updateState) {
        long totalSpace = hFile.getTotal(context);
        long freeSpace = hFile.getUsableSpace();
        long fileSize = 0l;

        if (hFile.isDirectory(context)) {
            fileSize = hFile.folderSize(context);
        } else {
            fileSize = hFile.length(context);
        }
        return new long[]{totalSpace, freeSpace, fileSize};
    }


    public static boolean copyToClipboard(Context context,
                                          String text) {
        try {
            ClipboardManager clipboard = (ClipboardManager)
                    context.getSystemService(context
                            .CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(context
                    .getString(R.string.clipboard_path_copy), text);
            clipboard.setPrimaryClip(clip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String[] getFolderNamesInPath(String path) {
        if (!path.endsWith("/"))
            path += "/";
        return ("root" + path).split("/");
    }


    public static String[] getPathsInPath(String path) {
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        ArrayList<String> paths = new ArrayList<>();
        while (path.length() > 0) {
            paths.add(path);
            path = path.substring(0, path
                    .lastIndexOf("/"));
        }
        paths.add("/");
        Collections.reverse(paths);

        return paths.toArray(new String[paths.size()]);

    }

    public static boolean canListFiles(File f) {
        return f.canRead() && f.isDirectory();
    }


    public static void openFile(final File f,
                                final MainActivity m,
                                SharedPreferences sharedPrefs) {
        boolean useNewStack = sharedPrefs.getBoolean(PrefFrag.PREFERENCE_TEXTEDITOR_NEWSTACK, false);
        boolean defaultHandler = isSelfDefault(f, m);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(m);
        final Toast[] studioCount = {null};

        if (defaultHandler && f.getName().toLowerCase().endsWith(".zip") ||
                f.getName().toLowerCase().endsWith(".jar") ||
                f.getName().toLowerCase().endsWith(".rar") ||
                f.getName().toLowerCase().endsWith(".tar") ||
                f.getName().toLowerCase().endsWith(".tar.gz")) {
            GeneralDialogCreation.showArchiveDialog(f, m);
        } else if (f.getName().toLowerCase().endsWith(".apk")) {
            GeneralDialogCreation.showPackageDialog(sharedPrefs, f, m);
        } else if (defaultHandler && f.getName().toLowerCase().endsWith(".db")) {
            Intent intent = new Intent(m, DatabaseViewerActivity.class);
            intent.putExtra("path", f.getPath());
            m.startActivity(intent);
        } else if (Icons.getTypeOfFile(f) == Icons.AUDIO) {
            final int studio_count = sharedPreferences.getInt("studio", 0);
            Uri uri = Uri.fromFile(f);
            final Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "audio/*");

            // Behold! It's the  legendary easter egg!
            if (studio_count != 0) {
                new CountDownTimer(studio_count, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int sec = (int) millisUntilFinished / 1000;
                        if (studioCount[0] != null)
                            studioCount[0].cancel();
                        studioCount[0] = Toast.makeText(m, sec + "", Toast.LENGTH_LONG);
                        studioCount[0].show();
                    }

                    @Override
                    public void onFinish() {
                        if (studioCount[0] != null)
                            studioCount[0].cancel();
                        studioCount[0] = Toast.makeText(m, m.getString(R.string.opening),
                                Toast.LENGTH_LONG);
                        studioCount[0].show();
                        m.startActivity(intent);
                    }
                }.start();
            } else
                m.startActivity(intent);
        } else {
            try {
                openunknown(f, m, false, useNewStack);
            } catch (Exception e) {
                Toast.makeText(m, m.getResources().getString(R.string.noappfound), Toast.LENGTH_LONG).show();
                openWith(f, m, useNewStack);
            }
        }
    }


    private static boolean isSelfDefault(File f,
                                         Context c) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f), MimeTypes
                .getMimeType(f));
        String s = "";
        ResolveInfo rii = c.getPackageManager()
                .resolveActivity(intent, PackageManager
                        .MATCH_DEFAULT_ONLY);
        if (rii != null && rii.activityInfo != null)
            s = rii.activityInfo.packageName;
        return s.equals("indi.aljet.myamazefilemanager_master")
                || rii == null;
    }

    public static void openFile(final DocumentFile f, final MainActivity m, SharedPreferences sharedPrefs) {
        boolean useNewStack = sharedPrefs.
                getBoolean(PrefFrag.PREFERENCE_TEXTEDITOR_NEWSTACK, false);
        try {
            openunknown(f, m, false, useNewStack);
        } catch (Exception e) {
            Toast.makeText(m, m.getResources().getString(R.string.noappfound), Toast.LENGTH_LONG).show();
            openWith(f, m, useNewStack);
        }
    }


    public static ArrayList<HybridFile> toHybridFileConcurrentRadixTree
            (ConcurrentRadixTree<VoidValue> a) {
        ArrayList<HybridFile> b = new ArrayList<>();
        for (CharSequence o : a.getKeysStartingWith("")) {
            HybridFile hFile = new HybridFile(OpenMode.UNKNOWN, o.toString());
            hFile.generateMode(null);
            b.add(hFile);
        }
        return b;
    }

    public static ArrayList<HybridFile> toHybridFileArrayList(LinkedList<String> a) {
        ArrayList<HybridFile> b = new ArrayList<>();
        for (String s : a) {
            HybridFile hFile = new HybridFile(OpenMode.UNKNOWN, s);
            hFile.generateMode(null);
            b.add(hFile);
        }
        return b;
    }

    public static HybridFileParcelable parseName(String line) {
        boolean linked = false;
        StringBuilder name = new StringBuilder();
        StringBuilder link = new StringBuilder();
        String size = "-1";
        String date = "";
        String[] array = line.split(" ");
        if (array.length < 6)
            return null;
        for (String anArray : array) {
            if (anArray.contains("->") && array[0]
                    .startsWith("1")) {
                linked = true;
            }
        }
        int p = getColonPosition(array);
        if (p != -1){
            date = array[p -1]+ " | "+ array[p];
            size = array[p -2];
        }
        if (!linked) {
            for (int i = p + 1; i < array.length; i++) {
                name.append(" ").append(array[i]);
            }
            name = new StringBuilder(name.toString().trim());
        } else {
            int q = getLinkPosition(array);
            for (int i = p + 1; i < q; i++) {
                name.append(" ").append(array[i]);
            }
            name = new StringBuilder(name.toString().trim());
            for (int i = q + 1; i < array.length; i++) {
                link.append(" ").append(array[i]);
            }
        }
        long Size = (size==null || size.trim().length()==0)?-1:Long.parseLong(size);
        if(date.trim().length()>0) {
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd | HH:mm");
            Date stringDate = simpledateformat.parse(date, pos);
            HybridFileParcelable baseFile=new HybridFileParcelable(name.toString(),array[0],stringDate.getTime(),Size,true);
            baseFile.setLink(link.toString());
            return baseFile;
        }else {
            HybridFileParcelable baseFile= new HybridFileParcelable(name.toString(),array[0],new File("/").lastModified(),Size,true);
            baseFile.setLink(link.toString());
            return baseFile;
        }
    }


    private static int getLinkPosition(String[] array){
        for(int i=0;i<array.length;i++){
            if(array[i].contains("->"))return i;
        }
        return  0;
    }

    private static int getColonPosition(String[] array){
        for(int i=0;i<array.length;i++){
            if(array[i].contains(":"))return i;
        }
        return  -1;
    }


    public static ArrayList<Boolean[]> parse(String permLine) {
        ArrayList<Boolean[]> arrayList= new ArrayList<>(3);
        Boolean[] read =new Boolean[]{permLine.charAt(1) == 'r',
                permLine.charAt(4) == 'r',
                permLine.charAt(7) == 'r'};

        Boolean[] write=new Boolean[]{permLine.charAt(2) == 'w',
                permLine.charAt(5) == 'w',
                permLine.charAt(8) == 'w'};

        Boolean[] execute=new Boolean[]{permLine.charAt(3) == 'x',
                permLine.charAt(6) == 'x',
                permLine.charAt(9) == 'x'};

        arrayList.add(read);
        arrayList.add(write);
        arrayList.add(execute);
        return arrayList;
    }

    public static boolean isStorage(String path) {
        for (String s : DataUtils.getInstance().getStorages())
            if (s.equals(path)) return true;
        return false;
    }

    public static boolean isPathAccesible(String dir, SharedPreferences pref) {
        File f = new File(dir);
        boolean showIfHidden = pref.getBoolean(PrefFrag.PREFERENCE_SHOW_HIDDENFILES, false),
                isDirSelfOrParent = dir.endsWith("/.") || dir.endsWith("/.."),
                showIfRoot = pref.getBoolean(PrefFrag.PREFERENCE_ROOTMODE, false);

        return f.exists() && f.isDirectory()
                && (!f.isHidden() || (showIfHidden && !isDirSelfOrParent))
                && (!isRoot(dir) || showIfRoot);

        // TODO: 2/5/2017 use another system that doesn't create new object
    }

    public static boolean isRoot(String dir) {// TODO: 5/5/2017 hardcoding root might lead to problems down the line
        return !dir.contains(OTGUtil.PREFIX_OTG) && !dir.startsWith("/storage");
    }

}
