package indi.aljet.myamazefilemanager_master.asynchronous.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

import indi.aljet.myamazefilemanager_master.filesystem.HybridFile;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.fragments.SearchWorkerFragment;
import indi.aljet.myamazefilemanager_master.utils.OnFileFound;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;

/**
 * Created by PC-LJL on 2018/1/31.
 */

public class SearchAsyncTask extends AsyncTask
<String,HybridFileParcelable,Void>{

    private static final String TAG = "SearchAsyncTask";

    private WeakReference<Activity> activity;
    private SearchWorkerFragment.HelperCallbacks mCallbacks;
    private String mInput;
    private OpenMode mOpenMode;
    private boolean mRootMode,isRegexEnabled,
    isMatchesEnabled;


    public SearchAsyncTask(Activity a, SearchWorkerFragment.HelperCallbacks l,
                           String input, OpenMode openMode, boolean root, boolean regex,
                           boolean matches) {
        activity = new WeakReference<>(a);
        mCallbacks = l;
        mInput = input;
        mOpenMode = openMode;
        mRootMode = root;
        isRegexEnabled = regex;
        isMatchesEnabled = matches;
    }


    @Override
    protected void onPreExecute() {
        if (mCallbacks != null) {
            mCallbacks.onPreExecute(mInput);
        }
    }


    @Override
    protected Void doInBackground(String... params) {
        String path = params[0];
        HybridFile file = new HybridFile(mOpenMode, path);
        file.generateMode(activity.get());
        if (file.isSmb()) return null;

        // level 1
        // if regex or not
        if (!isRegexEnabled) {
            search(file, mInput);
        } else {
            // compile the regular expression in the input
            Pattern pattern = Pattern.compile(bashRegexToJava(mInput));
            // level 2
            if (!isMatchesEnabled) searchRegExFind(file, pattern);
            else searchRegExMatch(file, pattern);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mCallbacks != null) {
            mCallbacks.onPostExecute(mInput);
        }
    }

    @Override
    protected void onCancelled() {
        if (mCallbacks != null) mCallbacks.onCancelled();
    }

    @Override
    protected void onProgressUpdate(HybridFileParcelable... val) {
        if (!isCancelled() && mCallbacks != null) {
            mCallbacks.onProgressUpdate(val[0], mInput);
        }
    }

    private void search(HybridFile directory,final
                        SearchFilter filter){
        if (directory.isDirectory(activity.get())) {// do you have permission to read this directory?
            directory.forEachChildrenFile(activity.get(), mRootMode, new OnFileFound() {
                @Override
                public void onFileFound(HybridFileParcelable file) {
                    if (!isCancelled()) {
                        if (filter.searchFilter(file.getName())) {
                            publishProgress(file);
                        }
                        if (file.isDirectory() && !isCancelled()) {
                            search(file, filter);
                        }
                    }
                }
            });
        } else {
            Log.d(TAG, "Cannot search " + directory.getPath() + ": Permission Denied");
        }
    }


    private void search(HybridFile file,final String query){
        search(file,fileName -> fileName.toLowerCase()
        .contains(query.toLowerCase()));
    }



    private void searchRegExFind(HybridFile file, final Pattern pattern) {
        search(file, new SearchFilter() {
            @Override
            public boolean searchFilter(String fileName) {
                return pattern.matcher(fileName).find();
            }
        });
    }

    /**
     * Recursively match a java regex pattern {@link Pattern} with the file names and publish the result
     *
     * @param file    the current file
     * @param pattern the compiled java regex
     */
    private void searchRegExMatch(HybridFile file, final Pattern pattern) {
        search(file, fileName -> pattern.matcher(fileName).matches());
    }

    /**
     * method converts bash style regular expression to java. See {@link Pattern}
     *
     * @param originalString
     * @return converted string
     */
    private String bashRegexToJava(String originalString) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < originalString.length(); i++) {
            switch (originalString.charAt(i) + "") {
                case "*":
                    stringBuilder.append("\\w*");
                    break;
                case "?":
                    stringBuilder.append("\\w");
                    break;
                default:
                    stringBuilder.append(originalString.charAt(i));
                    break;
            }
        }

        Log.d(getClass().getSimpleName(), stringBuilder.toString());
        return stringBuilder.toString();
    }




    public interface SearchFilter{
        boolean searchFilter(String fileName);
    }

}
