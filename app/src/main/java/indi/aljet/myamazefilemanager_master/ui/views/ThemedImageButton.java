package indi.aljet.myamazefilemanager_master.ui.views;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by PC-LJL on 2018/2/6.
 */

public class ThemedImageButton extends ThemedImageView {

    public ThemedImageButton(Context context) {
        super(context);
    }

    public ThemedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemedImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
    }

    @Override
    protected boolean onSetAlpha(int alpha) {
        return false;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ImageButton.class.getName();
    }


}
