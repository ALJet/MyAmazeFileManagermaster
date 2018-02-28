package indi.aljet.myamazefilemanager_master.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.aljet.myamazefilemanager_master.R;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class AppHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.firstline)
    public ImageView apkIcon;

    @BindView(R.id.apk_icon)
    public TextView txtTitle;

    @BindView(R.id.second)
    public RelativeLayout rl;

    @BindView(R.id.date)
    public TextView txtDesc;

    @BindView(R.id.properties)
    public ImageButton about;


    public AppHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(itemView);
        apkIcon.setVisibility(View.VISIBLE);
    }
}
