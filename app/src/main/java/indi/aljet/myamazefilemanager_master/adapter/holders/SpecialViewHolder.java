package indi.aljet.myamazefilemanager_master.adapter.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class SpecialViewHolder extends RecyclerView.ViewHolder {

    public static final int HEADER_FILES = 0,
    HEADER_FOLDERS = 1;


    @BindView(R.id.text)
    public TextView txtTitle;

    public final int type;

    public SpecialViewHolder(Context c,
                             View view,
                             UtilitiesProviderInterface utilsProvider,
                             int type) {
        super(view);
        this.type = type;
        ButterKnife.bind(view);

        switch (type){
            case HEADER_FILES:
                txtTitle.setText(R.string.text);
                break;
            case HEADER_FOLDERS:
                txtTitle.setText(R.string.folders);
                break;
            default:
                throw new IllegalStateException(": " + type);
        }

        if(utilsProvider.getAppTheme().equals(AppTheme
        .LIGHT)){
            txtTitle.setTextColor(Utils.getColor(c,
                    R.color.text_light));
        }else {
            txtTitle.setTextColor(Utils.getColor(c, R.color.text_dark));
        }

    }
}
