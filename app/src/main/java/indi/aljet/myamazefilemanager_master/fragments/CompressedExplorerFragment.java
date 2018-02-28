package indi.aljet.myamazefilemanager_master.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.github.junrar.Archive;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.adapter.CompressedExplorerAdapter;
import indi.aljet.myamazefilemanager_master.adapter.data.CompressedObjectParcelable;
import indi.aljet.myamazefilemanager_master.asynchronous.asynctasks.DeleteTask;
import indi.aljet.myamazefilemanager_master.asynchronous.services.ExtractService;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.filesystem.compressed.CompressedHelper;
import indi.aljet.myamazefilemanager_master.filesystem.compressed.CompressedInterface;
import indi.aljet.myamazefilemanager_master.ui.views.DividerItemDecoration;
import indi.aljet.myamazefilemanager_master.ui.views.FastScroller;
import indi.aljet.myamazefilemanager_master.utils.BottomBarButtonPath;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.color.ColorUsage;
import indi.aljet.myamazefilemanager_master.utils.files.FileUtils;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

/**
 * Created by PC-LJL on 2018/2/8.
 */

public class CompressedExplorerFragment extends
        Fragment implements BottomBarButtonPath {


    public static final String KEY_PATH = "path";

    private static final String KEY_CACHE_FILES = "cache_files";
    private static final String KEY_URI = "uri";
    private static final String KEY_FILE = "file";
    private static final String KEY_WHOLE_LIST = "whole_list";
    private static final String KEY_ELEMENTS = "elements";
    private static final String KEY_OPEN = "is_open";


    public File compressedFile;

    /**
     * files to be deleted from cache
     * with a Map maintaining key - the root of directory created (for deletion purposes after we exit out of here
     * and value - the path of file to open
     */
    public ArrayList<HybridFileParcelable> files;
    public boolean selection = false;
    public String relativeDirectory = "";//Normally this would be "/" but for pathing issues it isn't
    public String skin, accentColor, iconskin, year;
    public CompressedExplorerAdapter compressedExplorerAdapter;
    public ActionMode mActionMode;
    public boolean coloriseIcons, showSize, showLastModified, gobackitem;
    public Archive archive;
    public ArrayList<CompressedObjectParcelable> elements = new ArrayList<>();
    public MainActivity mainActivity;
    public RecyclerView listView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public boolean isOpen = false;  // flag states whether to open file after service extracts it

    private UtilitiesProviderInterface utilsProvider;
    private CompressedInterface compressedInterface;
    private View rootView;
    private boolean addheader = true;
    private LinearLayoutManager mLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private boolean showDividers;
    private View mToolbarContainer;
    private boolean stopAnims = true;
    private int file = 0, folder = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilsProvider = (UtilitiesProviderInterface)
                getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_frag, container, false);
        mainActivity = (MainActivity) getActivity();
        listView = (RecyclerView) rootView.findViewById(R.id.listView);
        listView.setOnTouchListener((view, motionEvent) -> {
            if(stopAnims && !compressedExplorerAdapter
                    .stoppedAnimation){
                stopAnim();
            }
            compressedExplorerAdapter.stoppedAnimation = true;
            stopAnims = false;
            return false;

        });
        swipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this :: refresh);
        return rootView;
    }


    public void stopAnim(){
        for (int j = 0; j < listView.getChildCount(); j++) {
            View v = listView.getChildAt(j);
            if (v != null) v.clearAnimation();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        compressedFile = new File(Uri.parse(getArguments().getString(KEY_PATH)).getPath());

        mToolbarContainer = mainActivity.getAppbar().getAppbarLayout();
        mToolbarContainer.setOnTouchListener((view, motionEvent) ->
        {
            if(stopAnims)
            {
                if((!compressedExplorerAdapter.stoppedAnimation))
                {
                    stopAnim();
                }
                compressedExplorerAdapter.stoppedAnimation = true;
            }
            stopAnims = false;
            return false;
        });

        listView.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(mLayoutManager);

        if(utilsProvider.getAppTheme().equals(AppTheme.DARK))
        {
            rootView.setBackgroundColor(Utils.getColor(getContext(), R.color.holo_dark_background));
        } else if(utilsProvider.getAppTheme().equals(AppTheme.BLACK)) {
            listView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.black));
        } else {
            listView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.background_light));
        }

        gobackitem = sp.getBoolean("goBack_checkbox", false);
        coloriseIcons = sp.getBoolean("coloriseIcons", true);
        showSize = sp.getBoolean("showFileSize", false);
        showLastModified = sp.getBoolean("showLastModified", true);
        showDividers = sp.getBoolean("showDividers", true);
        year = ("" + Calendar.getInstance().get(Calendar.YEAR)).substring(2, 4);
        skin = mainActivity.getColorPreference().getColorAsString(ColorUsage.PRIMARY);
        accentColor = mainActivity.getColorPreference().getColorAsString(ColorUsage.ACCENT);
        iconskin = mainActivity.getColorPreference().getColorAsString(ColorUsage.ICON_SKIN);

        //mainActivity.findViewById(R.id.buttonbarframe).setBackgroundColor(Color.parseColor(skin));

        if (savedInstanceState == null && compressedFile != null) {
            files = new ArrayList<>();
            // adding a cache file to delete where any user interaction elements will be cached
            String fileName = compressedFile.getName().substring(0, compressedFile.getName().lastIndexOf("."));
            files.add(new HybridFileParcelable(getActivity().getExternalCacheDir().getPath() + "/" + fileName));
            compressedInterface = CompressedHelper.getCompressedInterfaceInstance(getContext(), compressedFile);

            changePath("");
        } else {
            onRestoreInstanceState(savedInstanceState);
        }
        mainActivity.supportInvalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_ELEMENTS, elements);
        outState.putString(KEY_PATH, relativeDirectory);
        outState.putString(KEY_URI, compressedFile.getPath());
        outState.putString(KEY_FILE, compressedFile.getPath());
        outState.putParcelableArrayList(KEY_CACHE_FILES, files);
        outState.putBoolean(KEY_OPEN, isOpen);
    }

    private void onRestoreInstanceState(Bundle savedInstanceState){
        compressedFile = new File(Uri.parse(
                savedInstanceState.getString(KEY_URI))
        .getPath());
        files = savedInstanceState.getParcelableArrayList(KEY_CACHE_FILES);
        isOpen = savedInstanceState.getBoolean(KEY_OPEN);
        elements = savedInstanceState.getParcelableArrayList(KEY_ELEMENTS);
        relativeDirectory = savedInstanceState.getString(KEY_PATH, "");

        compressedInterface = CompressedHelper.
                getCompressedInterfaceInstance(getContext(), compressedFile);
        createViews(elements, relativeDirectory);
    }

    public ActionMode.Callback mActionModeCallback = new
            ActionMode.Callback() {
                private void hideOption(int id,Menu menu){
                    MenuItem item = menu.findItem(id);
                    item.setVisible(false);
                }

                private void showOption(int id, Menu menu) {
                    MenuItem item = menu.findItem(id);
                    item.setVisible(true);
                }

                View v;


                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    v = getActivity().getLayoutInflater().inflate(R.layout.actionmode, null);
                    mode.setCustomView(v);
                    // assumes that you have "contexual.xml" menu resources
                    inflater.inflate(R.menu.contextual, menu);
                    hideOption(R.id.cpy, menu);
                    hideOption(R.id.cut, menu);
                    hideOption(R.id.delete, menu);
                    hideOption(R.id.addshortcut, menu);
                    hideOption(R.id.share, menu);
                    hideOption(R.id.openwith, menu);
                    showOption(R.id.all, menu);
                    hideOption(R.id.compress, menu);
                    hideOption(R.id.hide, menu);
                    showOption(R.id.ex, menu);
                    mode.setTitle(getResources().getString(R.string.select));
                    mainActivity.updateViews(new ColorDrawable(Utils.getColor(getContext(), R.color.holo_dark_action_mode)));
                    if (Build.VERSION.SDK_INT >= 21) {

                        Window window = getActivity().getWindow();
                        if (mainActivity.colourednavigation)
                            window.setNavigationBarColor(Utils.getColor(getContext(), android.R.color.black));
                    }
                    if (Build.VERSION.SDK_INT < 19) {
                        mainActivity.getAppbar().getToolbar().setVisibility(View.GONE);
                    }
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    ArrayList<Integer> positions = compressedExplorerAdapter.getCheckedItemPositions();
                    ((TextView) v.findViewById(R.id.item_count)).setText(positions.size() + "");

                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.all:
                            compressedExplorerAdapter.toggleChecked(true);
                            mode.invalidate();
                            return true;
                        case R.id.ex:
                            Toast.makeText(getActivity(), getResources().getString(R.string.extracting), Toast.LENGTH_SHORT).show();

                            String[] dirs = new String[compressedExplorerAdapter.getCheckedItemPositions().size()];
                            for (int i = 0; i < dirs.length; i++) {
                                dirs[i] = elements.get(compressedExplorerAdapter.getCheckedItemPositions().get(i)).name;
                            }

                            compressedInterface.decompress(null, dirs);

                            mode.finish();
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    if (compressedExplorerAdapter != null) compressedExplorerAdapter.toggleChecked(false);
                    selection = false;
                    mainActivity.updateViews(mainActivity.getColorPreference().getDrawable(ColorUsage.getPrimary(MainActivity.currentTab)));
                    if (Build.VERSION.SDK_INT >= 21) {

                        Window window = getActivity().getWindow();
                        if (mainActivity.colourednavigation)
                            window.setNavigationBarColor(mainActivity.skinStatusBar);
                    }
                    mActionMode = null;
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity.supportInvalidateOptionsMenu();
        if(files.get(0).exists()){
            new DeleteTask(getActivity().
                            getContentResolver(),
                            getActivity(), this).
                    execute(files);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.floatingActionButton.getMenuButton().hide();
        Intent intent = new Intent(getActivity(), ExtractService.class);
        getActivity().bindService(intent, mServiceConnection, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mServiceConnection);
    }

    private ServiceConnection mServiceConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    // open file if pending
                    if (isOpen) {
                        // open most recent entry added to files to be deleted from cache
                        File cacheFile = new File(files.get(files.size() - 1).getPath());
                        if (cacheFile.exists()) {
                            FileUtils.openFile(cacheFile, mainActivity, mainActivity.getPrefs());
                        }
                        // reset the flag and cache file, as it's root is already in the list for deletion
                        isOpen = false;
                        files.remove(files.size() - 1);
                    }
                }
            };


    @Override
    public void changePath(String folder) {
        if(folder == null) folder = "";
        if(folder.startsWith("/")) folder = folder.substring(1);

        boolean addGoBackItem = gobackitem && !isRoot(folder);
        String finalfolder = folder;
        compressedInterface.changePath(folder, addGoBackItem, data -> {
            elements = data;
            createViews(elements, finalfolder);

            swipeRefreshLayout.setRefreshing(false);
            updateBottomBar();
        });

        updateBottomBar();
    }


    @Override
    public String getPath() {
        if(!isRootRelativePath())
            return "/" + relativeDirectory;
        else
            return "";
    }

    @Override
    public int getRootDrawable() {
        return R.drawable.ic_compressed_white_24dp;
    }

    private void refresh() {
        changePath(relativeDirectory);
    }

    private void updateBottomBar() {
        String path = !isRootRelativePath()? compressedFile.getName() + "/" + relativeDirectory : compressedFile.getName();
        mainActivity.getAppbar().getBottomBar().updatePath(path, false, null, OpenMode.FILE, folder, file, this);
    }

    private void createViews(ArrayList<CompressedObjectParcelable> items, String dir) {
        if (compressedExplorerAdapter == null) {
            compressedExplorerAdapter = new CompressedExplorerAdapter(getActivity(), utilsProvider, items, this, compressedInterface);
            listView.setAdapter(compressedExplorerAdapter);
        } else {
            compressedExplorerAdapter.generateZip(items);
        }

        folder = 0;
        file = 0;
        for (CompressedObjectParcelable item : items) {
            if(item.type == CompressedObjectParcelable.TYPE_GOBACK) continue;

            if (item.directory) folder++;
            else file++;
        }

        stopAnims = true;
        if (!addheader) {
            listView.removeItemDecoration(dividerItemDecoration);
            //listView.removeItemDecoration(headersDecor);
            addheader = true;
        } else {
            dividerItemDecoration = new DividerItemDecoration(getActivity(), true, showDividers);
            listView.addItemDecoration(dividerItemDecoration);
            //headersDecor = new StickyRecyclerHeadersDecoration(compressedExplorerAdapter);
            //listView.addItemDecoration(headersDecor);
            addheader = false;
        }
        final FastScroller fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroll);
        fastScroller.setRecyclerView(listView, 1);
        fastScroller.setPressedHandleColor(mainActivity.getColorPreference().getColor(ColorUsage.ACCENT));
        ((AppBarLayout) mToolbarContainer).addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            fastScroller.updateHandlePosition(verticalOffset, 112);
        });
        listView.stopScroll();
        relativeDirectory = dir;
        updateBottomBar();
        swipeRefreshLayout.setRefreshing(false);
    }

    public boolean canGoBack() {
        return !isRootRelativePath();
    }

    public void goBack() {
        changePath(new File(relativeDirectory).getParent());
    }

    private boolean isRootRelativePath() {
        return isRoot(relativeDirectory);
    }

    private boolean isRoot(String folder) {
        return folder == null || folder.isEmpty();
    }
}
