package indi.aljet.myamazefilemanager_master.utils.theme;

import android.content.res.Resources;

import com.afollestad.materialdialogs.Theme;

import java.util.Calendar;

/**
 * Created by PC-LJL on 2018/1/12.
 */

public enum AppTheme {


    LIGHT(0),
    DARK(1),
    TIMED(2),
    BLACK(3);

    public static final int LIGHT_INDEX = 0;
    public static final int DARK_INDEX = 1;
    public static final int TIME_INDEX = 2;
    public static final int BLACK_INDEX = 3;

    private int id;


    AppTheme(int id){
        this.id = id;
    }


    public static AppTheme getTheme(int index){
        switch (index){
            default:
            case LIGHT_INDEX:
                    return LIGHT;
            case DARK_INDEX:
                return DARK;
            case TIME_INDEX:
                return TIMED;
            case BLACK_INDEX:
                return BLACK;
        }
    }


    public Theme getMaterialDialogTheme(){
        switch (id){
            default:
            case LIGHT_INDEX:
                return Theme.LIGHT;
            case DARK_INDEX:
            case BLACK_INDEX:
                return Theme.DARK;
            case TIME_INDEX:
                int hour = Calendar.getInstance()
                        .get(Calendar.HOUR_OF_DAY);
                if(hour <= 6 || hour >= 18){
                    return Theme.DARK;
                }else{
                    return Theme.LIGHT;
                }

        }
    }


    public AppTheme getSimpleTheme(){
        switch (id){
            default:
            case LIGHT_INDEX:
                return LIGHT;
            case DARK_INDEX:
                return DARK;
            case TIME_INDEX:
                int hour = Calendar.getInstance()
                        .get(Calendar.HOUR_OF_DAY);
                if(hour <= 6 || hour >= 18){
                    return DARK;
                }else{
                    return LIGHT;
                }
            case BLACK_INDEX:
                return BLACK;
        }
    }


    public int getId(){
        return id;
    }
}
