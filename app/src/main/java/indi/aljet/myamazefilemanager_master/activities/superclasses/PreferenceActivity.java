package indi.aljet.myamazefilemanager_master.activities.superclasses;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * Created by PC-LJL on 2018/2/12.
 */

public class PreferenceActivity extends BasicActivity {
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
    }

    public SharedPreferences getPrefs(){
        return sharedPrefs;
    }
}
