package indi.aljet.myamazefilemanager_master.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.adapter.holders.HiddenViewHolder;
import indi.aljet.myamazefilemanager_master.asynchronous.asynctasks.DeleteTask;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFile;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.fragments.MainFragment;
import indi.aljet.myamazefilemanager_master.utils.DataUtils;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.files.FileUtils;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class HiddenAdapter extends RecyclerView.Adapter
<HiddenViewHolder>{

    private SharedPreferences sharedPrefs;
    private MainFragment context;
    private Context c;
    private ArrayList<HybridFile> items;
    private MaterialDialog materialDialog;
    private boolean hide;
    private DataUtils dataUtils = DataUtils.getInstance();


    public HiddenAdapter(Context context, MainFragment mainFrag, SharedPreferences sharedPreferences,
                         ArrayList<HybridFile> items, MaterialDialog materialDialog, boolean hide) {
        this.c = context;
        this.context = mainFrag;
        sharedPrefs = sharedPreferences;
        this.items = new ArrayList<>(items);
        this.hide = hide;
        this.materialDialog = materialDialog;
    }

    @Override
    public HiddenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) c.getSystemService
                (Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.bookmarkrow,
                parent,false);
        return new HiddenViewHolder(view);

    }

    @Override
    public void onBindViewHolder(HiddenViewHolder holder, int position) {
        HybridFile file = items.get(position);
        holder.txtTitle.setText(file.getName());
        String a = file.getReadablePath(file.getPath());
        holder.txtDesc.setText(a);

        if(hide){
            holder.image.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener( view ->{
            if(!file.isSmb() && file.isDirectory()){
                ArrayList<HybridFileParcelable> al =
                        new ArrayList<>();
                HybridFileParcelable baseFile = new
                        HybridFileParcelable(items.get(position)
                .getPath() + "/.nomedia");
                baseFile.setMode(OpenMode.FILE);
                al.add(baseFile);
                new DeleteTask(context.getActivity()
                .getContentResolver(),c).execute((al));
            }
            dataUtils.removeHiddenFile(items.get(position)
            .getParent());
            items.remove(items.get(position));
            notifyDataSetChanged();
        });
        holder.row.setOnClickListener( view -> {
            materialDialog.dismiss();
            new Thread(() -> {
                if (file.isDirectory()) {
                    context.getActivity()
                            .runOnUiThread(() -> {
                                context.loadlist(file
                                                .getPath(),
                                        false,
                                        OpenMode.UNKNOWN);
                            });
                } else {
                    if (!file.isSmb()) {
                        context.getActivity().runOnUiThread(
                                () -> {
                                    FileUtils.openFile(
                                            new File(file
                                                    .getPath()),
                                            (MainActivity)
                                                    context.getActivity(),
                                            sharedPrefs);
                                });
                    }
                }
            }).start();
        });
    }



    public void updateDialog(MaterialDialog dialog){
        materialDialog = dialog;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
