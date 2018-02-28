package indi.aljet.myamazefilemanager_master.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.superclasses.ThemedActivity;
import indi.aljet.myamazefilemanager_master.exceptions.ShellNotRunningException;
import indi.aljet.myamazefilemanager_master.fragments.DbViewerFragment;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.PrefFrag;
import indi.aljet.myamazefilemanager_master.utils.PreferenceUtils;
import indi.aljet.myamazefilemanager_master.utils.RootUtils;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.color.ColorUsage;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by PC-LJL on 2018/2/12.
 */

public class DatabaseViewerActivity extends ThemedActivity {

    private String path;
    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private Cursor c;


    private File pathFile;
    boolean delete = false;
    private Toolbar toolbar;
    public SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.checkStorage = false;
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPre = PreferenceManager
                .getDefaultSharedPreferences(this);
        if(getAppTheme().equals(AppTheme.DARK)){
            setTheme(R.style.appCompatDark);
            getWindow().getDecorView()
                    .setBackgroundColor(Utils.getColor
                            (this,R.color
                            .holo_dark_background));
        }else if(getAppTheme().equals(AppTheme
        .BLACK)){
            setTheme(R.style.appCompatBlack);
            getWindow().getDecorView()
                    .setBackgroundColor(Utils.getColor(this,
                           android.R.color.black));
        }
        setContentView(R.layout.activity_db_viewer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(SDK_INT >= 21){
            ActivityManager.TaskDescription taskDescription =
                    new ActivityManager.TaskDescription("Amaze",
                            ((BitmapDrawable)
                                    ContextCompat.getDrawable(this,
                                            R.mipmap.ic_launcher)).getBitmap(),
                            getColorPreference().getColor(ColorUsage.getPrimary
                                    (MainActivity.currentTab)));
            setTaskDescription(taskDescription);
        }
        getSupportActionBar()
                .setBackgroundDrawable(getColorPreference()
                .getDrawable(ColorUsage.getPrimary(
                        MainActivity.currentTab)));
        boolean useNewStack = sharedPre.getBoolean(
                PrefFrag.PREFERENCE_TEXTEDITOR_NEWSTACK,false);
        getSupportActionBar().setDisplayHomeAsUpEnabled
                (!useNewStack);
        if(SDK_INT == 20 || SDK_INT == 19){
            SystemBarTintManager tintManager = new
                    SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(
                    getColorPreference().getColor(
                            ColorUsage
                            .getPrimary(MainActivity
                            .currentTab)));
            @SuppressLint("WrongViewCast")
            FrameLayout.MarginLayoutParams p =
                    (ViewGroup.MarginLayoutParams)
                            findViewById(R.id.parentdb).
                                    getLayoutParams();
            SystemBarTintManager.SystemBarConfig config =
                    tintManager.getConfig();
            p.setMargins(0,config.getStatusBarHeight(),
                    0
            ,0);

        }else if(SDK_INT >= 21){
            boolean colourednavigation = getPrefs()
                    .getBoolean("colorednavigation",true);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams
            .FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager
            .LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(PreferenceUtils
            .getStatusColor(getColorPreference()
            .getColorAsString(ColorUsage.getPrimary
                    (MainActivity.currentTab))));
            if(colourednavigation){
                window.setNavigationBarColor(PreferenceUtils
                .getStatusColor(getColorPreference()
                .getColorAsString(ColorUsage.getPrimary(MainActivity
                .currentTab))));
            }
            path = getIntent().getStringExtra("path");
            pathFile = new File(path);
            listView = findViewById(R.id.listView);
            load(pathFile);
            listView.setOnItemClickListener((path,
                    view,position,id) ->{
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager()
                        .beginTransaction();
                DbViewerFragment fragment = new
                        DbViewerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("table",arrayList
                .get(position));
                fragment.setArguments(bundle);
                fragmentTransaction.add(R.id.content_frame
                ,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
        }
    }

    private ArrayList<String> getDbTableNames(Cursor c){
        ArrayList<String> result = new ArrayList<>();
        for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            for(int i = 0;i < c.getColumnCount();i++){
                result.add(c.getString(i));
            }
        }
        return result;
    }

    private void load(final File file){
        new Thread(() -> {
            File file1 = getExternalCacheDir();
            if(!file.canRead() && ThemedActivity.rootMode){
                try{
                    RootUtils.copy(pathFile.getPath(),
                            new File(file1.getPath()
                            ,file.getName())
                    .getPath());
                    pathFile = new File(file1.getPath(),
                            file.getName());
                }catch (ShellNotRunningException e){
                    e.printStackTrace();
                }
                delete = true;
            }
            try{
                sqLiteDatabase = SQLiteDatabase
                        .openDatabase(pathFile
                        .getPath(),null,
                                SQLiteDatabase
                        .OPEN_READONLY);
                c = sqLiteDatabase.rawQuery(
                        "SELECT name FROM sqlite_master WHERE type='table'",
                        null);
                arrayList = getDbTableNames(c);
                arrayAdapter = new ArrayAdapter(
                        DatabaseViewerActivity.this,
                        android.R.layout.simple_list_item_1,
                        arrayList);
            }catch (Exception e){
                e.printStackTrace();
                finish();
            }

        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        if (c != null)
            c.close();
        if (delete)
            pathFile.delete();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        toolbar.setTitle(pathFile.getName());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            onBackPressed();
            toolbar.setTitle(pathFile.getName());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toolbar.setTitle(pathFile.getName());
    }
}
