package indi.aljet.myamazefilemanager_master.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.aljet.myamazefilemanager_master.R;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class HiddenViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text1)
    public TextView txtTitle;

    @BindView(R.id.delete_button)
    public ImageButton image;

    @BindView(R.id.text2)
    public TextView txtDesc;

    @BindView(R.id.bookmarkrow)
    public LinearLayout row;


    public HiddenViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(itemView);
    }
}
