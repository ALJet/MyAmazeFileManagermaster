package indi.aljet.myamazefilemanager_master.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by PC-LJL on 2018/1/22.
 */

public class TinyDB {

    private static final String DIVIDER = "‚‗‚";

    public static void putBooleanArray(SharedPreferences
                                       preferences,
                                       String key,
                                       Boolean[] array){
        preferences.edit().putString(key,
                TextUtils.join(DIVIDER,
                        array)).apply();
    }


    public static Boolean[] getBooleanArray(SharedPreferences preferences,
                                            String key,
                                            Boolean[] defaultValue){
        String prefValue = preferences
                .getString(key,"");
        if(preferences.equals("")){
            return defaultValue;
        }
        String[] temp = TextUtils.split(prefValue,DIVIDER);
        Boolean[] newArray = new Boolean[temp.length];
        for(int i = 0;i < temp.length;i++){
            newArray[i] = Boolean.valueOf(temp[i]);
        }
        return newArray;
    }



}
