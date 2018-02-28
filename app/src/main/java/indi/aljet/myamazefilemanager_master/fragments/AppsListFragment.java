package indi.aljet.myamazefilemanager_master.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.superclasses.ThemedActivity;
import indi.aljet.myamazefilemanager_master.adapter.AppsAdapter;
import indi.aljet.myamazefilemanager_master.adapter.glide.AppsAdapterPreloadModel;
import indi.aljet.myamazefilemanager_master.asynchronous.loaders.AppListLoader;
import indi.aljet.myamazefilemanager_master.utils.GlideConstants;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.application.GlideApp;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

/**
 * Created by PC-LJL on 2018/2/8.
 */

public class AppsListFragment extends
        ListFragment implements
        LoaderManager.LoaderCallbacks<AppListLoader.AppsDataPair> {

    UtilitiesProviderInterface utilsProvider;
    AppsListFragment app = this;
    AppsAdapter adapter;

    public SharedPreferences Sp;
    ListView vl;
    int asc,sortby;

    int index = 0,top = 0;
    public static final int ID_LOADER_APP_LIST = 0;
    private AppsAdapterPreloadModel modelProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilsProvider = (UtilitiesProviderInterface)
                getActivity();
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        MainActivity mainActivity = (MainActivity)
                getActivity();
        mainActivity.getAppbar().setTitle(R.string.apps);
        mainActivity.floatingActionButton.getMenuButton().hide();
        mainActivity.buttonBarFrame.setVisibility(View.GONE);
        mainActivity.supportInvalidateOptionsMenu();
        vl = getListView();
        Sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getSortModes();
        ListView vl = getListView();
        vl.setDivider(null);
        if(utilsProvider.getAppTheme().equals(AppTheme
        .DARK)){
            getActivity().getWindow().getDecorView()
                    .setBackgroundColor(Utils
                    .getColor(getContext(),R.color
                    .holo_dark_background));
        }else if(utilsProvider.getAppTheme()
                .equals(AppTheme.BLACK)){
            getActivity().getWindow().getDecorView().
                    setBackgroundColor(Utils.getColor
                            (getContext(), android.R.
                                    color.black));
        }
        modelProvider = new AppsAdapterPreloadModel(app);
        ViewPreloadSizeProvider<String> sizeProvider = new
                ViewPreloadSizeProvider<>();
        ListPreloader<String> preloader = new ListPreloader<>
                (GlideApp.with(app),modelProvider,
                        sizeProvider, GlideConstants
                .MAX_PRELOAD_APPSADAPTER);

        adapter = new AppsAdapter(getContext(),
                (ThemedActivity)getActivity(),
                utilsProvider
        ,modelProvider,sizeProvider,R.layout.rowlayout,
                app);
        getListView().setOnScrollListener(preloader);
        setListAdapter(adapter);
        setListShown(false);
        setEmptyText(getResources().getString(R.string
        .no_applications));
        getLoaderManager().initLoader(ID_LOADER_APP_LIST,
                null,this);
        if(savedInstanceState != null){
            index = savedInstanceState.getInt("index");
            top = savedInstanceState.getInt("top");
        }


    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(vl != null){
            int index = vl.getFirstVisiblePosition();
            View vi = vl.getChildAt(0);
            int top = (vi == null) ? 0 : vi.getTop();
            outState.putInt("index",index);
            outState.putInt("top",top);
        }
    }


    public boolean unin(String pkg){
        try{
            Intent intent = new Intent(Intent
            .ACTION_DELETE);
            intent.setData(Uri.parse("package:" + pkg));
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void getSortModes(){
        int t = Integer.parseInt(Sp.getString("sortbyApps",
                "0"));
        if(t <= 2){
            sortby = t;
            asc = 1;
        }else{
            asc = -1;
            sortby = t - 3;
        }
    }


    @Override
    public Loader<AppListLoader.AppsDataPair> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(getContext()
        ,sortby,asc);
    }

    @Override
    public void onLoadFinished(Loader<AppListLoader.AppsDataPair> loader, AppListLoader.AppsDataPair data) {
        adapter.setData(data.first);
        modelProvider.setItemList(data.second);
        if (isResumed()){
            setListShown(true);
        }else{
            setListShownNoAnimation(true);
        }
        if(vl != null){
            vl.setSelectionFromTop(index,top);
        }
    }

    @Override
    public void onLoaderReset(Loader<AppListLoader.AppsDataPair> loader) {
        adapter.setData(null);
    }
}
