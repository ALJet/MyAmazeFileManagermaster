package indi.aljet.myamazefilemanager_master.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.superclasses.ThemedActivity;
import indi.aljet.myamazefilemanager_master.adapter.data.AppDataParcelable;
import indi.aljet.myamazefilemanager_master.adapter.glide.AppsAdapterPreloadModel;
import indi.aljet.myamazefilemanager_master.adapter.holders.AppHolder;
import indi.aljet.myamazefilemanager_master.asynchronous.asynctasks.DeleteTask;
import indi.aljet.myamazefilemanager_master.asynchronous.services.CopyService;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.filesystem.RootHelper;
import indi.aljet.myamazefilemanager_master.fragments.AppsListFragment;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.ServiceWatcherUtil;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.color.ColorUsage;
import indi.aljet.myamazefilemanager_master.utils.files.FileUtils;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class AppsAdapter extends
        ArrayAdapter<AppDataParcelable>{

    private UtilitiesProviderInterface utilsProvider;
    private Context context;
    private AppsAdapterPreloadModel modelProvider;
    private ViewPreloadSizeProvider<String> sizeProvider;
    private SparseBooleanArray myChecked = new SparseBooleanArray();
    private AppsListFragment app;

    private ThemedActivity themedActivity;

    public AppsAdapter(Context context, ThemedActivity ba, UtilitiesProviderInterface utilsProvider,
                       AppsAdapterPreloadModel modelProvider, ViewPreloadSizeProvider<String> sizeProvider,
                       int resourceId, AppsListFragment app) {
        super(context, resourceId);
        themedActivity = ba;
        this.utilsProvider = utilsProvider;
        this.modelProvider = modelProvider;
        this.sizeProvider = sizeProvider;
        this.context = context;
        this.app = app;

        /*for (int i = 0; i < items.size(); i++) {
            myChecked.put(i, false);
        }*/
    }

    public void setData(List<AppDataParcelable> data) {
        clear();

        if (data != null) {
            addAll(data);
        }
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final AppDataParcelable rowItem = getItem(position);
        View view;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.rowlayout, null);
            final AppHolder vholder = new AppHolder(view);
            view.findViewById(R.id.generic_icon).setVisibility(View.GONE);
            view.findViewById(R.id.picture_icon).setVisibility(View.GONE);
            view.setTag(vholder);
            sizeProvider.setView(view.findViewById(R.id.apk_icon));
        } else {
            view = convertView;
        }

        final AppHolder holder = (AppHolder) view.getTag();

        modelProvider.loadApkImage(rowItem.path, holder.apkIcon);

        if (holder.about != null) {
            if(utilsProvider.getAppTheme().equals(AppTheme.LIGHT))
                holder.about.setColorFilter(Color.parseColor("#ff666666"));
            showPopup(holder.about, rowItem);
        }
        holder.txtTitle.setText(rowItem.label);
        //	File f = new File(rowItem.getDesc());
        holder.txtDesc.setText(rowItem.fileSize);
        holder.rl.setClickable(true);
        holder.rl.setOnClickListener(p1 -> {
            Intent i1 = app.getActivity().getPackageManager().getLaunchIntentForPackage(rowItem.packageName);
            if (i1 != null)
                app.startActivity(i1);
            else
                Toast.makeText(app.getActivity(), app.getResources().getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
            // TODO: Implement this method
        });


        if (myChecked.get(position)) {
            holder.rl.setBackgroundColor(Utils.getColor(context, R.color.appsadapter_background));
        } else {
            if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT)) {
                holder.rl.setBackgroundResource(R.drawable.safr_ripple_white);
            } else {
                holder.rl.setBackgroundResource(R.drawable.safr_ripple_black);
            }
        }

        return view;
    }


    private void showPopup(View v,
                           final AppDataParcelable rowItem){
        v.setOnClickListener( view -> {
            PopupMenu popupMenu = new PopupMenu(app
            .getActivity(),view);
            popupMenu.setOnMenuItemClickListener(item -> {
                int colorAccent = themedActivity
                        .getColorPreference()
                        .getColor(ColorUsage.ACCENT);
                switch (item.getItemId()) {
                    case R.id.open:
                        Intent i1 = app.getActivity().getPackageManager().getLaunchIntentForPackage(rowItem.packageName);
                        if (i1!= null)
                            app.startActivity(i1);
                        else
                            Toast.makeText(app.getActivity(),app.getResources().getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.share:
                        ArrayList<File> arrayList2=new ArrayList<File>();
                        arrayList2.add(new File(rowItem.path));
                        themedActivity.getColorPreference();
                        FileUtils.shareFiles(arrayList2, app.getActivity(), utilsProvider.getAppTheme(), colorAccent);
                        return true;
                    case R.id.unins:
                        final HybridFileParcelable f1 = new HybridFileParcelable(rowItem.path);
                        f1.setMode(OpenMode.ROOT);

                        if ((Integer.valueOf(rowItem.data.substring(0,
                                rowItem.data.indexOf("_"))) & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            // system package
                            if(app.Sp.getBoolean("rootmode",false)) {
                                MaterialDialog.Builder builder1 = new MaterialDialog.Builder(app.getActivity());
                                builder1.theme(utilsProvider.getAppTheme().getMaterialDialogTheme())
                                        .content(app.getResources().getString(R.string.unin_system_apk))
                                        .title(app.getResources().getString(R.string.warning))
                                        .negativeColor(colorAccent)
                                        .positiveColor(colorAccent)
                                        .negativeText(app.getResources().getString(R.string.no))
                                        .positiveText(app.getResources().getString(R.string.yes))
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onNegative(MaterialDialog materialDialog) {

                                                materialDialog.cancel();
                                            }

                                            @Override
                                            public void onPositive(MaterialDialog materialDialog) {

                                                ArrayList<HybridFileParcelable> files = new ArrayList<>();
                                                if (Build.VERSION.SDK_INT >= 21) {
                                                    String parent = f1.getParent();
                                                    if (!parent.equals("app") && !parent.equals("priv-app")) {
                                                        HybridFileParcelable baseFile=new HybridFileParcelable(f1.getParent());
                                                        baseFile.setMode(OpenMode.ROOT);
                                                        files.add(baseFile);
                                                    }
                                                    else files.add(f1);
                                                } else {
                                                    files.add(f1);
                                                }
                                                new DeleteTask(app.getActivity().getContentResolver(), app.getActivity()).execute((files));
                                            }
                                        }).build().show();
                            } else {
                                Toast.makeText(app.getActivity(),app.getResources().getString(R.string.enablerootmde),Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            app.unin(rowItem.packageName);
                        }
                        return true;
                    case R.id.play:
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setData(Uri.parse("market://details?id=" + rowItem.packageName));
                        app.startActivity(intent1);
                        return true;
                    case R.id.properties:

                        app.startActivity(new Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + rowItem.packageName)));
                        return true;
                    case R.id.backup:
                        Toast.makeText(app.getActivity(), app.getResources().getString( R.string.copyingapk) + Environment.getExternalStorageDirectory().getPath() + "/app_backup", Toast.LENGTH_LONG).show();
                        File f = new File(rowItem.path);
                        ArrayList<HybridFileParcelable> ab = new ArrayList<>();
                        File dst = new File(Environment.getExternalStorageDirectory().getPath() + "/app_backup");
                        if(!dst.exists() || !dst.isDirectory())dst.mkdirs();
                        Intent intent = new Intent(app.getActivity(), CopyService.class);
                        HybridFileParcelable baseFile= RootHelper.generateBaseFile(f,true);
                        baseFile.setName(rowItem.label + "_" +
                                rowItem.packageName.substring(rowItem.packageName.indexOf("_")+1) + ".apk");
                        ab.add(baseFile);

                        intent.putParcelableArrayListExtra(CopyService.TAG_COPY_SOURCES, ab);
                        intent.putExtra(CopyService.TAG_COPY_TARGET, dst.getPath());
                        intent.putExtra(CopyService.TAG_COPY_OPEN_MODE, 0);

                        ServiceWatcherUtil.runService(app.getActivity(), intent);
                        return true;
                }

                return false;
            });
            popupMenu.inflate(R.menu.app_options);
            popupMenu.show();
        });
    }

}
