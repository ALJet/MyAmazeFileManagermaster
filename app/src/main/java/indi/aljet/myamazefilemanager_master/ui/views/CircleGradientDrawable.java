package indi.aljet.myamazefilemanager_master.ui.views;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.DisplayMetrics;

import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

/**
 * Created by PC-LJL on 2018/2/6.
 */

public class CircleGradientDrawable extends GradientDrawable {

    private static final int STROKE_WIDTH = 2;
    private static final String STROKE_COLOR_LIGHT = "#EEEEEE";
    private static final String STROKE_COLOR_DARK = "#424242";
    private DisplayMetrics mDisplayMetrics;

    /**
     * Constructor
     *
     * @param color    the hex color of circular icon
     * @param appTheme current theme light/dark which will determine the boundary color
     * @param metrics  to convert the boundary width for {@link #setStroke} method from dp to px
     */
    public CircleGradientDrawable(String color, AppTheme appTheme, DisplayMetrics metrics) {
        this(appTheme, metrics);
        setColor(Color.parseColor(color));
    }

    /**
     * Constructor
     *
     * @param color    the color of circular icon
     * @param appTheme current theme light/dark which will determine the boundary color
     * @param metrics  to convert the boundary width for {@link #setStroke} method from dp to px
     */
    public CircleGradientDrawable(@ColorInt int color, AppTheme appTheme, DisplayMetrics metrics) {
        this(appTheme, metrics);
        setColor(color);
    }

    public CircleGradientDrawable(AppTheme appTheme, DisplayMetrics metrics) {
        this.mDisplayMetrics = metrics;

        setShape(OVAL);
        setSize(1, 1);
        setStroke(dpToPx(STROKE_WIDTH), (appTheme.equals(AppTheme.DARK) || appTheme.equals(AppTheme.BLACK)) ? Color.parseColor(STROKE_COLOR_DARK)
                : Color.parseColor(STROKE_COLOR_LIGHT));
    }

    private int dpToPx(int dp) {
        int px = Math.round(mDisplayMetrics.density * dp);
        return px;
    }

}