package indi.aljet.myamazefilemanager_master.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.widget.EditText;


import indi.aljet.myamazefilemanager_master.R;

/**
 * Created by PC-LJL on 2018/1/15.
 */

public class EditTextColorStateUtil {

    @SuppressLint("RestrictedApi")
    public static void setTint(Context context,
                               EditText editText,
                               int color){
        if(Build.VERSION.SDK_INT >= 21)
            return;
        ColorStateList editTextColorStateList =
                createEditTextColorStateList(context,color);
        if(editText instanceof AppCompatEditText){
            ((AppCompatEditText) editText).
                    setSupportBackgroundTintList(editTextColorStateList);
        }else if(Build.VERSION.SDK_INT >= Build
                .VERSION_CODES.LOLLIPOP){
            editText.setBackgroundTintList(editTextColorStateList);
        }
    }


    private static ColorStateList createEditTextColorStateList(Context context
    ,int color){
        int[] [] states = new int[3][];
        int[] colors = new int[3];
        int i= 0;
        states[i] = new int[] { -android.R.attr.state_enabled};
        colors[i] = Utils.getColor(context, R.color.text_disabled);
        i++;
        states[i] = new int[] {-android.R.attr.state_pressed,
        -android.R.attr.state_focused};
        colors[i] = Utils.getColor(context,R.color.grey);
        i++;
        states[i] = new int[]{};
        colors[i] = color;
        return new ColorStateList(states,colors);
    }
}
