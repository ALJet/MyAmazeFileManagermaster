package indi.aljet.myamazefilemanager_master.utils.share;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.R;

/**
 * Created by PC-LJL on 2018/1/24.
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter
        .ViewHolder> {

    private ArrayList<Intent> items;
    private MaterialDialog dialog;
    private ArrayList<String> labels;
    private ArrayList<Drawable> drawables;
    private Context context;

    void updateMatDialog(MaterialDialog b){
        this.dialog = b;
    }

    ShareAdapter(Context context,
                 ArrayList<Intent> intents,
                 ArrayList<String> labels,
                 ArrayList<Drawable> arrayList1){
        items = new ArrayList<>(intents);
        this.context = context;
        this.labels = labels;
        this.drawables = arrayList1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent
        .getContext()).inflate(R.layout.simplerow,parent
        ,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.render(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private View rootView;
        private TextView textView;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = ((TextView) view.findViewById(R.id.firstline));
            imageView = (ImageView) view.findViewById(R.id.icon);
        }

        void render(final int position){
            if(drawables.get(position) != null){
                imageView.setImageDrawable(drawables
                .get(position));
            }
            textView.setVisibility(View.VISIBLE);
            textView.setText(labels.get(position));
            rootView.setOnClickListener( v -> {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                context.startActivity(items.get(position));
            });
        }
    }
}
