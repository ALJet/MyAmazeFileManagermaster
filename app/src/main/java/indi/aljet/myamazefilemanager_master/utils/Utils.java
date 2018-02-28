package indi.aljet.myamazefilemanager_master.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;

/**
 * Created by PC-LJL on 2018/1/22.
 */

public class Utils {
    private static final int INDEX_NOT_FOUND = -1;
    private static final SimpleDateFormat DATE_NO_MINUTES =
            new SimpleDateFormat("MMM dd,yyyy");
    private static final SimpleDateFormat DATE_WITH_MINUTES =
            new SimpleDateFormat("MMMM dd yyyy | KK:mm a");
    private static final String INPUT_INTENT_BLACKLIST_COLON = ";";
    private static final String INPUT_INTENT_BLACKLIST_PIPE = "\\|";
    private static final String INPUT_INTENT_BLACKLIST_AMP = "&&";
    private static final String INPUT_INTENT_BLACKLIST_DOTS = "\\.\\.\\.";


    public static float clamp(float min,float max,float value){
        float minimun = Math.max(min,value);
        return Math.min(minimun,max);
    }


    public static float getViewRawY(View view){
        int[] location = new int[2];
        location[0] = 0;
        location[1] = (int)view.getY();
        ((View)view.getParent()).
                getLocationInWindow(location);
        return location[1];
    }


    public static void setTint(Context context,
                               CheckBox box,
                               int color){
        if(Build.VERSION.SDK_INT >= 21){
            return;
        }
        ColorStateList sl = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        }, new int[]{getColor(context, R.color.grey), color});
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES
                .LOLLIPOP){
            box.setButtonTintList(sl);
        }else{
            Drawable drawable = DrawableCompat
                    .wrap(ContextCompat
                    .getDrawable(box.getContext(),
                            R.drawable.abc_btn_check_material));
            DrawableCompat.setTintList(drawable,sl);
            box.setButtonDrawable(drawable);
        }
    }

    public static String getDate(long f){
        return DATE_WITH_MINUTES.format(f);
    }

    public static String getDate(long f,
                                 String year){
        String date = DATE_NO_MINUTES
                .format(f);
        if(date.substring(date.length() - 4,
                date.length()).equals(year)){
            date = date.substring(0,
                    date.length() - 6);
        }
        return date;
    }


    public static int getColor(Context c,
                               @ColorRes int color){
        if(Build.VERSION.SDK_INT >= Build
                .VERSION_CODES.M){
            return c.getColor(color);
        }else{
            return c.getResources().getColor(color);
        }
    }


    public static int dpToPx(Context c,
                             int dp){
        DisplayMetrics displayMetrics =
                c.getResources().getDisplayMetrics();
        return Math.round(dp *
                (displayMetrics.xdpi) /
        DisplayMetrics.DENSITY_DEFAULT);
    }


    public static String differenceStrings(String str1, String str2) {
        if (str1 == null) return str2;
        if (str2 == null) return str1;

        int at = indexOfDifferenceStrings(str1, str2);

        if (at == INDEX_NOT_FOUND) return "";

        return str2.substring(at);
    }


    private static int indexOfDifferenceStrings(CharSequence cs1,
                                                CharSequence cs2){
        if(cs1 == cs2)
            return INDEX_NOT_FOUND;
        if(cs1 == null || cs2 == null){
            return 0;
        }
        int i ;
        for( i = 0;i < cs1.length() &&
                i < cs2.length();++i){
            if(cs1.charAt(i) != cs2.charAt(i))
                break;
        }
        if(i < cs2.length() || i < cs1.length())
            return i;
        return INDEX_NOT_FOUND;
    }

    public static void disableScreenRotation(MainActivity
                                             mainActivity){
        int screenOrientation = mainActivity
                .getResources().getConfiguration()
                .orientation;

        if(screenOrientation == Configuration
                .ORIENTATION_LANDSCAPE){
            mainActivity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else if(screenOrientation ==
                Configuration.ORIENTATION_PORTRAIT){
            mainActivity.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            );
        }
    }

    public static String sanitizeInput(String input){
        String sanitizedInput;
        String sanitizedInputTemp = input;

        while(true){
            sanitizedInput = sanitizeInputOnce
                    (sanitizedInputTemp);
            if(sanitizedInput.equals(sanitizedInputTemp)){
                break;
            }
            sanitizedInputTemp = sanitizedInput;
        }
        return sanitizedInput;
    }


    private static String sanitizeInputOnce(String input){
        return input.replaceAll(INPUT_INTENT_BLACKLIST_PIPE, "").
                replaceAll(INPUT_INTENT_BLACKLIST_AMP, "").
                replaceAll(INPUT_INTENT_BLACKLIST_DOTS, "").
                replaceAll(INPUT_INTENT_BLACKLIST_COLON, "");
    }


    public static Uri getUriForBaseFile(Context context,
                                        HybridFileParcelable baseFile){
        switch (baseFile.getMode()) {
            case FILE:
            case ROOT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    return GenericFileProvider.getUriForFile(context, GenericFileProvider.PROVIDER_NAME,
                            new File(baseFile.getPath()));
                } else {
                    return Uri.fromFile(new File(baseFile.getPath()));
                }
            case OTG:
                return OTGUtil.getDocumentFile(baseFile.getPath(), context, true).getUri();
            case SMB:
            case DROPBOX:
            case GDRIVE:
            case ONEDRIVE:
            case BOX:
                Toast.makeText(context, context.getResources().getString(R.string.smb_launch_error),
                        Toast.LENGTH_LONG).show();
                return null;
            default:
                return null;
        }
    }






}
