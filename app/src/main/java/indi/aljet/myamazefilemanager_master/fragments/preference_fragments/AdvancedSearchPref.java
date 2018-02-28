package indi.aljet.myamazefilemanager_master.fragments.preference_fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import indi.aljet.myamazefilemanager_master.R;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class AdvancedSearchPref extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml
        .advancedsearch_prefs);
    }
}
