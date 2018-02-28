package indi.aljet.myamazefilemanager_master.ui.views.preference;

import android.content.Context;
import android.preference.Preference;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

import indi.aljet.myamazefilemanager_master.R;

/**
 * Created by PC-LJL on 2018/2/6.
 */

public class PathSwitchPreference extends Preference {

    public static final int EDIT = 0, DELETE = 1;

    private int lastItemClicked = -1;

    public PathSwitchPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        setWidgetLayoutResource(R.layout.namepathswitch_preference);
        return super.onCreateView(parent);
    }

    @Override
    protected void onBindView(View view) {
        setListener(view, R.id.edit, EDIT);
        setListener(view, R.id.delete, DELETE);

        view.setOnClickListener(null);

        super.onBindView(view);//Keep this before things that need changing what's on screen
    }

    public int getLastItemClicked() {
        return lastItemClicked;
    }

    private View.OnClickListener setListener(final View v, @IdRes int id, final int elem) {
        final PathSwitchPreference t = this;

        View.OnClickListener l = view -> {
            lastItemClicked = elem;
            getOnPreferenceClickListener().onPreferenceClick(t);
        };

        v.findViewById(id).setOnClickListener(l);

        return l;
    }

}
