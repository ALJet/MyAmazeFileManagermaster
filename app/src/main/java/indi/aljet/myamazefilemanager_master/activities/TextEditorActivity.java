package indi.aljet.myamazefilemanager_master.activities;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.superclasses.ThemedActivity;
import indi.aljet.myamazefilemanager_master.asynchronous.asynctasks.SearchTextTask;
import indi.aljet.myamazefilemanager_master.exceptions.ShellNotRunningException;
import indi.aljet.myamazefilemanager_master.exceptions.StreamNotFoundException;
import indi.aljet.myamazefilemanager_master.filesystem.FileUtil;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.PrefFrag;
import indi.aljet.myamazefilemanager_master.ui.dialogs.GeneralDialogCreation;
import indi.aljet.myamazefilemanager_master.utils.MapEntry;
import indi.aljet.myamazefilemanager_master.utils.PreferenceUtils;
import indi.aljet.myamazefilemanager_master.utils.RootUtils;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.color.ColorUsage;
import indi.aljet.myamazefilemanager_master.utils.files.FileUtils;
import indi.aljet.myamazefilemanager_master.utils.files.GenericCopyUtil;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PC-LJL on 2018/2/12.
 */

public class TextEditorActivity extends ThemedActivity
        implements TextWatcher
        , View.OnClickListener {

    public EditText mInput, searchEditText;
    private HybridFileParcelable mFile;
    private String mOriginal;
    private Timer mTimer;
    private boolean mModified, isEditAllowed = true;
    private Typeface mInputTypefaceDefault, mInputTypefaceMono;
    private Toolbar toolbar;
    //ArrayList<StringBuilder> texts;
    //static final int maxlength = 200;
    //int index = 0;
    ScrollView scrollView;

    /*
     * List maintaining the searched text's start/end index as key/value pair
     */
    public ArrayList<MapEntry> nodes = new ArrayList<>();

    /*
     * variable to maintain the position of index
     * while pressing next/previous button in the searchBox
     */
    private int mCurrent = -1;

    /*
     * variable to maintain line number of the searched phrase
     * further used to calculate the scroll position
     */
    public int mLine = 0;

    private SearchTextTask searchTextTask;
    private static final String KEY_MODIFIED_TEXT = "modified";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ORIGINAL_TEXT = "original";
    private static final String KEY_MONOFONT = "monofont";

    private RelativeLayout searchViewLayout;

    Uri uri = null;
    public ImageButton upButton, downButton, closeButton;
    // input stream associated with the file
    private InputStream inputStream;
    private ParcelFileDescriptor parcelFileDescriptor;
    private File cacheFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getAppTheme().equals(AppTheme.DARK))
            getWindow().getDecorView().setBackgroundColor(Utils.getColor(this, R.color.holo_dark_background));
        else if (getAppTheme().equals(AppTheme.BLACK))
            getWindow().getDecorView().setBackgroundColor(Utils.getColor(this, android.R.color.black));

        setContentView(R.layout.search);
        searchViewLayout = findViewById(R.id.searchview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //findViewById(R.id.lin).setBackgroundColor(Color.parseColor(skin));
        toolbar.setBackgroundColor(getColorPreference().getColor(ColorUsage.getPrimary(MainActivity.currentTab)));
        searchViewLayout.setBackgroundColor(getColorPreference().getColor(ColorUsage.getPrimary(MainActivity.currentTab)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("Amaze",
                    ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap(),
                    getColorPreference().getColor(ColorUsage.getPrimary(MainActivity.currentTab)));
            ((Activity) this).setTaskDescription(taskDescription);
        }

        searchEditText = (EditText) searchViewLayout.findViewById(R.id.search_box);
        upButton = (ImageButton) searchViewLayout.findViewById(R.id.prev);
        downButton = (ImageButton) searchViewLayout.findViewById(R.id.next);
        closeButton = (ImageButton) searchViewLayout.findViewById(R.id.close);

        searchEditText.addTextChangedListener(this);

        upButton.setOnClickListener(this);
        //upButton.setEnabled(false);
        downButton.setOnClickListener(this);
        //downButton.setEnabled(false);
        closeButton.setOnClickListener(this);

        getSupportActionBar().setBackgroundDrawable(getColorPreference().getDrawable(ColorUsage.
                getPrimary(MainActivity.currentTab)));

        boolean useNewStack = getPrefs().getBoolean(PrefFrag.PREFERENCE_TEXTEDITOR_NEWSTACK, false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(!useNewStack);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getColorPreference().
                    getColor(ColorUsage.getPrimary(MainActivity.currentTab)));
            @SuppressLint("WrongViewCast") FrameLayout.MarginLayoutParams
                    p = (ViewGroup.MarginLayoutParams)
                    findViewById(R.id.texteditor).getLayoutParams();
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            p.setMargins(0, config.getStatusBarHeight(), 0, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean colourednavigation = getPrefs().getBoolean("colorednavigation", true);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(PreferenceUtils.getStatusColor(getColorPreference().
                    getColorAsString(ColorUsage.getPrimary(MainActivity.currentTab))));
            if (colourednavigation)
                window.setNavigationBarColor(PreferenceUtils.getStatusColor(getColorPreference()
                        .getColorAsString(ColorUsage.getPrimary(MainActivity.currentTab))));

        }

        mInput = findViewById(R.id.fname);
        scrollView = findViewById(R.id.editscroll);
        if (getIntent().getData() != null) {
            uri = getIntent().getData();
            mFile = new HybridFileParcelable(Utils
                    .sanitizeInput(getIntent().getData()
                            .getPath()));
        }
        String fileName;
        fileName = mFile.getName();
        try {
            if (fileName == null && uri != null) {
                if (uri.getScheme().equals("file")) {
                    fileName = uri.getLastPathSegment();
                }

                ContentProviderClient client = null;
                Cursor cursor = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        client = getContentResolver().acquireUnstableContentProviderClient(uri);
                    } else {
                        throw new Exception();
                    }

                    cursor = client.query(uri, new String[]{
                            MediaStore.Images.ImageColumns.DISPLAY_NAME
                    }, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    }
                } finally {

                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileName = getString(R.string.error);
        }
        getSupportActionBar().setTitle(fileName);

        mInput.addTextChangedListener(this);
        if (getAppTheme().equals(AppTheme.DARK))
            mInput.setBackgroundColor(Utils.getColor(this, R.color.holo_dark_background));
        else if (getAppTheme().equals(AppTheme.BLACK))
            mInput.setBackgroundColor(Utils.getColor(this, android.R.color.black));

        mInputTypefaceDefault = mInput.getTypeface();
        mInputTypefaceMono = Typeface.MONOSPACE;
        if (savedInstanceState != null) {

            mOriginal = savedInstanceState.getString(KEY_ORIGINAL_TEXT);
            int index = savedInstanceState.getInt(KEY_INDEX);
            mInput.setText(savedInstanceState.getString(KEY_MODIFIED_TEXT));
            mInput.setScrollY(index);
            if (savedInstanceState.getBoolean(KEY_MONOFONT)) mInput.setTypeface(mInputTypefaceMono);
        } else {

            load(uri, mFile);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_MODIFIED_TEXT, mInput.getText().toString());
        outState.putInt(KEY_INDEX, mInput.getScrollY());
        outState.putString(KEY_ORIGINAL_TEXT, mOriginal);
        outState.putBoolean(KEY_MONOFONT, mInput.getTypeface().equals(mInputTypefaceMono));
    }


    private void checkUnsavedChanges() {
        if (mOriginal != null && mInput
                .isShown() && !mOriginal
                .equals(mInput.getText().toString())) {
            new MaterialDialog.Builder(this)
                    .title(R.string.unsavedchanges)
                    .content(R.string.unsavedchangesdesc)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .positiveColor(getColorPreference().getColor(ColorUsage.ACCENT))
                    .negativeColor(getColorPreference().getColor(ColorUsage.ACCENT))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            saveFile(uri, new File(mFile.getPath()), mInput.getText().toString());
                            finish();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            finish();
                        }
                    })
                    .build().show();
        } else {
            finish();
        }
    }


    private void saveFile(final Uri uri, final File file,
                          final String editTextString) {
        Toast.makeText(this, R.string.saving,
                Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                writeTextFile(uri, file,
                        editTextString);
            } catch (StreamNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.error_file_not_found,
                            Toast.LENGTH_SHORT).show();
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.error_io,
                            Toast.LENGTH_SHORT).show();
                });
            } catch (ShellNotRunningException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.rootfailure,
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    private void writeTextFile(final Uri uri,
                               final File file,
                               String inputText)
            throws StreamNotFoundException, IOException,
            ShellNotRunningException {
        OutputStream outputStream = null;

        if (uri.toString().contains("file://")) {
            // dealing with files
            try {
                outputStream = FileUtil.getOutputStream(file, this);
            } catch (Exception e) {
                outputStream = null;
            }

            if (ThemedActivity.rootMode && outputStream == null) {
                // try loading stream associated using root
                try {

                    if (cacheFile != null && cacheFile.exists())
                        outputStream = new FileOutputStream(cacheFile);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    outputStream = null;
                }
            }
        } else if (uri.toString().contains("content://")) {

            if (parcelFileDescriptor != null) {
                File descriptorFile = new File(GenericCopyUtil.
                        PATH_FILE_DESCRIPTOR + parcelFileDescriptor.getFd());
                try {
                    outputStream = new FileOutputStream(descriptorFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    outputStream = null;
                }
            }

            if (outputStream == null) {
                try {
                    outputStream = getContentResolver().openOutputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    outputStream = null;
                }
            }
        }

        if (outputStream == null) throw new StreamNotFoundException();

        // saving data to file
        outputStream.write(inputText.getBytes());
        outputStream.close();

        mOriginal = inputText;
        mModified = false;
        invalidateOptionsMenu();

        if (cacheFile != null && cacheFile.exists()) {
            // cat cache content to original file and delete cache file
            RootUtils.cat(cacheFile.getPath(), mFile.getPath());

            cacheFile.delete();
        }

        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), getString(R.string.done), Toast.LENGTH_SHORT).show();
        });

    }


    private void setProgress(boolean show) {

    }

    private void load(final Uri uri,
                      final HybridFileParcelable mFile) {
        setProgress(true);
        this.mFile = mFile;
        mInput.setHint(R.string.loading);
        new Thread(() -> {
            try {
                inputStream = getInputStream(uri, mFile);

                String str;

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                if (bufferedReader != null) {
                    while ((str = bufferedReader.readLine()) != null) {
                        stringBuilder.append(str).append("\n");
                    }
                }
                mOriginal = stringBuilder.toString();
                inputStream.close();

                runOnUiThread(() -> {
                    try {
                        mInput.setText(mOriginal);
                        if (mOriginal.isEmpty()) {

                            mInput.setHint(R.string.file_empty);
                        } else
                            mInput.setHint(null);
                    } catch (OutOfMemoryError e) {
                        mInput.setHint(R.string.error);
                    }
                    setProgress(false);
                });

            } catch (StreamNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    mInput.setHint(R.string.error_file_not_found);
                });
            } catch (IOException e) {

                e.printStackTrace();
                runOnUiThread(() -> {
                    mInput.setHint(R.string.error_io);
                });
            }
        }).start();
    }


    @Override
    public void onBackPressed() {
        checkUnsavedChanges();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.save).setVisible(mModified);
        menu.findItem(R.id.monofont).setCheckable(
                mInput.getTypeface().equals(mInputTypefaceMono));
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                checkUnsavedChanges();
                break;
            case R.id.save:
                // Make sure EditText is visible before saving!
                saveFile(uri, new File(mFile.getPath()), mInput.getText().toString());
                break;
            case R.id.details:
                if (mFile.exists()) {
                    //HybridFile hFile = new HybridFile(OpenMode.FILE, mFile.getPath());
                    //hFile.generateMode(this);
                    GeneralDialogCreation.showPropertiesDialogWithoutPermissions(mFile, this, getAppTheme());
                } else Toast.makeText(this, R.string.not_allowed, Toast.LENGTH_SHORT).show();
                break;
            case R.id.openwith:
                if (mFile.exists()) {
                    boolean useNewStack = getPrefs().getBoolean(PrefFrag.PREFERENCE_TEXTEDITOR_NEWSTACK, false);
                    FileUtils.openunknown(new File(mFile.getPath()), this, false, useNewStack);
                } else Toast.makeText(this, R.string.not_allowed, Toast.LENGTH_SHORT).show();
                break;
            case R.id.find:
                if (searchViewLayout.isShown()) hideSearchView();
                else revealSearchView();
                break;
            case R.id.monofont:
                item.setChecked(!item.isChecked());
                mInput.setTypeface(item.isChecked() ? mInputTypefaceMono : mInputTypefaceDefault);
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parcelFileDescriptor != null){
            try{
                parcelFileDescriptor.close();
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(this,
                        getString(R.string.error_io), Toast.LENGTH_LONG).show();
            }
        }
        if (cacheFile != null && cacheFile.exists())
            cacheFile.delete();
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(searchEditText != null &&
                charSequence
                .hashCode() == searchEditText.getText()
                .hashCode()){
            if(searchEditText != null)
                searchTextTask.cancel(true);
            cleanSpans();
        }
    }


    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.hashCode() == mInput
                .getText().hashCode()){
            if(mTimer != null){
                mTimer.cancel();
                mTimer.purge();
                mTimer = null;
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                boolean modified;
                @Override
                public void run() {
                    modified = !mInput.getText()
                            .toString().equals(mOriginal);
                    if(mModified != modified){
                        mModified = modified;
                        invalidateOptionsMenu();
                    }
                }
            },250);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(searchEditText != null && editable
                .hashCode() == searchEditText
                .getText().hashCode()){
            searchTextTask = new SearchTextTask(this);
            searchTextTask.execute(editable);
        }
    }


    private InputStream getInputStream(Uri uri,
                                       HybridFileParcelable baseFile)
        throws StreamNotFoundException{
        InputStream stream = null;
        File file = new File(baseFile.getPath());
        if(uri.toString().contains("file://")){
            if(!file.canWrite() && ThemedActivity
                    .rootMode){
                try{
                    File cacheDir = getExternalCacheDir();
                    cacheFile = new File(cacheDir,
                            mFile.getName());
                    RootUtils.copy(mFile
                    .getPath(),cacheFile.getPath());
                    try{
                        stream = new FileInputStream(cacheFile);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                        stream = null;
                    }
                }catch (ShellNotRunningException e){
                    e.printStackTrace();
                    stream = null;
                }
            }else if(file.canRead()){
                try{
                    stream = new FileInputStream(file
                    .getPath());
                }catch (FileNotFoundException e){
                    stream = null;
                }
            }
        }else if(uri.toString().contains("content://")){
            try{
                parcelFileDescriptor = getContentResolver()
                        .openFileDescriptor(uri,"rw");
                File parcelFile = new File(GenericCopyUtil
                .PATH_FILE_DESCRIPTOR + parcelFileDescriptor
                .getFd());
                stream = new FileInputStream(parcelFile);
            }catch (FileNotFoundException e){
                try{
                    parcelFileDescriptor = getContentResolver()
                            .openFileDescriptor(uri,"r");
                    File parcelFile = new File(
                            GenericCopyUtil.PATH_FILE_DESCRIPTOR +
                                    parcelFileDescriptor.getFd());
                    stream = new FileInputStream(parcelFile);
                }catch (FileNotFoundException e1){
                    e1.printStackTrace();
                    stream = null;
                }
            }
            if(stream == null){
                try{
                    stream = getContentResolver()
                            .openInputStream(uri);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    stream = null;
                }
            }
        }
        if(stream == null)throw  new
                StreamNotFoundException();
        return stream;
    }


    void revealSearchView(){
        int startRadius = 4;
        int endRadius = Math.max(searchViewLayout
        .getWidth() ,searchViewLayout.getHeight());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        int cx = metrics.widthPixels - 160;
        int cy = toolbar.getBottom();
        Animator animator;

        if(Build.VERSION.SDK_INT >= Build
                .VERSION_CODES.LOLLIPOP){
            animator = ViewAnimationUtils
                    .createCircularReveal(searchViewLayout,
                            cx,cy,
                            startRadius,endRadius);
        }else{
            animator = ObjectAnimator
                    .ofFloat(searchViewLayout,
                            "alpha",
                            0f,1f);
        }
        animator.setInterpolator(new
                AccelerateDecelerateInterpolator());
        animator.setDuration(600);
        searchViewLayout.setVisibility(View.VISIBLE);
        searchEditText.setText("");
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                searchEditText.requestFocus();
                InputMethodManager imm =
                        (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText,
                        InputMethodManager.SHOW_IMPLICIT);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    void hideSearchView(){
        int endRadius = 4;
        int startRadius = Math.max(searchViewLayout.getWidth(), searchViewLayout.getHeight());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // hardcoded and completely random
        int cx = metrics.widthPixels - 160;
        int cy = toolbar.getBottom();

        Animator animator;
        // FIXME: 2016/11/18   ViewAnimationUtils Compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(searchViewLayout, cx, cy,
                    startRadius, endRadius);
        } else {
            animator = ObjectAnimator.ofFloat(searchViewLayout, "alpha", 0f, 1f);
        }

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(600);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                searchViewLayout.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(),
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev:
                // upButton
                if (mCurrent > 0) {

                    // setting older span back before setting new one
                    Map.Entry keyValueOld = (Map.Entry) nodes.get(mCurrent).getKey();
                    if (getAppTheme().equals(AppTheme.LIGHT)) {
                        mInput.getText().setSpan(new BackgroundColorSpan(Color.YELLOW), (Integer) keyValueOld.getKey(),
                                (Integer) keyValueOld.getValue(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    } else {
                        mInput.getText().setSpan(new BackgroundColorSpan(Color.LTGRAY), (Integer) keyValueOld.getKey(),
                                (Integer) keyValueOld.getValue(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    // highlighting previous element in list
                    Map.Entry keyValueNew = (Map.Entry) nodes.get(--mCurrent).getKey();
                    mInput.getText().setSpan(new BackgroundColorSpan(Utils.getColor(this, R.color.search_text_highlight)),
                            (Integer) keyValueNew.getKey(),
                            (Integer) keyValueNew.getValue(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    // scrolling to the highlighted element
                    scrollView.scrollTo(0, (Integer) keyValueNew.getValue()
                            + mInput.getLineHeight() + Math.round(mInput.getLineSpacingExtra())
                            - getSupportActionBar().getHeight());
                }
                break;
            case R.id.next:
                // downButton
                if (mCurrent < nodes.size() - 1) {

                    // setting older span back before setting new one
                    if (mCurrent != -1) {

                        Map.Entry keyValueOld = (Map.Entry) nodes.get(mCurrent).getKey();
                        if (getAppTheme().equals(AppTheme.LIGHT)) {
                            mInput.getText().setSpan(new BackgroundColorSpan(Color.YELLOW), (Integer) keyValueOld.getKey(),
                                    (Integer) keyValueOld.getValue(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        } else {
                            mInput.getText().setSpan(new BackgroundColorSpan(Color.LTGRAY), (Integer) keyValueOld.getKey(),
                                    (Integer) keyValueOld.getValue(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                    }

                    Map.Entry keyValueNew = (Map.Entry) nodes.get(++mCurrent).getKey();
                    mInput.getText().setSpan(new BackgroundColorSpan(Utils.getColor(this, R.color.search_text_highlight)),
                            (Integer) keyValueNew.getKey(),
                            (Integer) keyValueNew.getValue(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    // scrolling to the highlighted element
                    scrollView.scrollTo(0, (Integer) keyValueNew.getValue()
                            + mInput.getLineHeight() + Math.round(mInput.getLineSpacingExtra())
                            - getSupportActionBar().getHeight());
                }
                break;
            case R.id.close:
                // closeButton
                findViewById(R.id.searchview).setVisibility(View.GONE);
                cleanSpans();
                break;
        }
    }

    private void cleanSpans(){
        nodes.clear();
        mCurrent = -1;
        mLine = 0;

        BackgroundColorSpan[] colorSpans = mInput
                .getText().getSpans(0,
                        mInput.length(),BackgroundColorSpan
                .class);
        for(BackgroundColorSpan colorSpan : colorSpans){
            mInput.getText().removeSpan(colorSpan);
        }
    }


    public int getLineNumber(){
        return this.mLine;
    }

    public void setLineNumber(int lineNumber){
        this.mLine = lineNumber;
    }
}
