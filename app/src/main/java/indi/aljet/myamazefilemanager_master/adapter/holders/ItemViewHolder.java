package indi.aljet.myamazefilemanager_master.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.ui.views.RoundedImageView;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.picture_icon)
    public RoundedImageView pictureIcon;

    @BindView(R.id.generic_icon)
    public ImageView genericIcon;

    @BindView(R.id.apk_icon)
    public ImageView apkIcon;

    @BindView(R.id.icon_thumb)
    public ImageView imageView1;

    @BindView(R.id.firstline)
    public TextView txtTitle;

    @BindView(R.id.secondLine)
    public TextView txtDesc;

    @BindView(R.id.date)
    public TextView date;

    @BindView(R.id.permis)
    public TextView perm;

    @BindView(R.id.second)
    public View rl;

    @BindView(R.id.generictext)
    public TextView genericText;

    @BindView(R.id.properties)
    public ImageButton about;

    @BindView(R.id.check_icon)
    public ImageView checkImageView;

    @BindView(R.id.check_icon_grid)
    public ImageView checkImageViewGrid;


    public ItemViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(itemView);
    }
}
