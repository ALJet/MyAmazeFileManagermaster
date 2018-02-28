package indi.aljet.myamazefilemanager_master.activities;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.superclasses.BasicActivity;
import indi.aljet.myamazefilemanager_master.utils.PreferenceUtils;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;


/**
 * Created by PC-LJL on 2018/2/12.
 */

public class AboutActivity extends BasicActivity
        implements View.OnClickListener {


    private static final int HEADER_HEIGHT = 1024;
    private static final int HEADER_WIDTH = 500;

    @BindView(R.id.appBarLayout)
     AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsing_toolbar_layout)
     CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.toolBar)
    Toolbar mToolbar;

    @BindView(R.id.image_view_license)
    ImageView mLicensesIcon;



    @BindView(R.id.text_view_title)
     TextView mtitleTextView;
    private int mCount = 0;



    private Snackbar snackbar;
    private SharedPreferences mSharedPref;

    @BindView(R.id.view_divider_authors)
     View mAuthorsDivider;

    private static final String KEY_PREF_STUDIO = "studio";
    private static final String URL_AUTHOR_1_G_PLUS = "https://plus.google.com/u/0/110424067388738907251/";
    private static final String URL_AUTHOR_1_PAYPAL = "arpitkh96@gmail.com";
    private static final String URL_AUTHOR_2_G_PLUS = "https://plus.google.com/+VishalNehra/";
    private static final String URL_AUTHOR_2_PAYPAL = "https://www.paypal.me/vishalnehra";
    private static final String URL_DEVELOPER1_GITHUB = "https://github.com/EmmanuelMess";
    private static final String URL_DEVELOPER1_BITCOIN = "bitcoin:12SRnoDQvDD8aoCy1SVSn6KSdhQFvRf955?amount=0.0005";
    private static final String URL_REPO_CHANGELOG = "https://github.com/TeamAmaze/AmazeFileManager/commits/master";
    private static final String URL_REPO_ISSUES = "https://github.com/TeamAmaze/AmazeFileManager/issues";
    private static final String URL_REPO_TRANSLATE = "https://www.transifex.com/amaze/amaze-file-manager-1/";
    private static final String URL_REPO_G_PLUS_COMMUNITY = "https://plus.google.com/communities/113997576965363268101";
    private static final String URL_REPO_XDA = "http://forum.xda-developers.com/android/apps-games/app-amaze-file-managermaterial-theme-t2937314";
    private static final String URL_REPO_RATE = "market://details?id=com.amaze.filemanager";
    private static final String TAG_CLIPBOARD_DONATE = "donate_id";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getAppTheme().equals(AppTheme.DARK)){
            setTheme(R.style.aboutDark);
        }else if(getAppTheme().equals(AppTheme.BLACK)){
            setTheme(R.style.aboutBlack);
        }else{
            setTheme(R.style.aboutLight);
        }

        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        mSharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        mAppBarLayout.
                setLayoutParams(calculateHeaderViewParams());

        getSupportActionBar().
                setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().
                getDrawable(R.drawable.md_nav_back));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        switchIcons();

        Random random = new Random();
        if(random.nextInt(2) == 0){
            mLicensesIcon.setImageDrawable(getResources()
            .getDrawable(R.drawable
            .ic_apple_ios_grey600_24dp));
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources()
        ,R.drawable.about_header);

        Palette.from(bitmap).generate( palette -> {
           int mutedColor = palette.getMutedColor(Utils
           .getColor(AboutActivity.this,R.color
           .primary_blue));
           int darkMutedColor = palette.getDarkMutedColor
                   (Utils.getColor(AboutActivity.this,
                           R.color.primary_blue));
           mCollapsingToolbarLayout.setContentScrimColor
                   (mutedColor);
           mCollapsingToolbarLayout.setStatusBarScrimColor
                   (darkMutedColor);
        });

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout,
                verticalOffset) ->{
            mtitleTextView.setAlpha(Math.abs(verticalOffset
             / (float)appBarLayout.getTotalScrollRange()));
        });
    }


    private CoordinatorLayout.LayoutParams calculateHeaderViewParams(){
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) mAppBarLayout
                .getLayoutParams();
        float vidAspectRation = (float) HEADER_WIDTH /
                (float) HEADER_HEIGHT;
        Log.d(getClass().getSimpleName(),
                vidAspectRation + "");
        int screenWidth = getResources().getDisplayMetrics()
                .widthPixels;
        float reqHeightAsPerAspectRatio = (float)
        screenWidth * vidAspectRation;
        Log.d(getClass().getSimpleName()
        ,reqHeightAsPerAspectRatio + "");
        Log.d(getClass().getSimpleName(), "new width: " + screenWidth +
                " and height: " + reqHeightAsPerAspectRatio);

        layoutParams.width = screenWidth;
        layoutParams.height = (int)
        reqHeightAsPerAspectRatio;
        return layoutParams;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchIcons(){
        if(getAppTheme().equals(AppTheme
        .DARK) || getAppTheme().equals(AppTheme.BLACK)){
            mAuthorsDivider.setBackgroundColor(Utils
            .getColor(this,R.color.divider_dark_card));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_layout_version:
                mCount++;
                if (mCount >= 5) {
                    String text = getResources().getString(R.string.easter_egg_title) + " : " + mCount;

                    if(snackbar != null && snackbar.isShown()) {
                        snackbar.setText(text);
                    } else {
                        snackbar = Snackbar.make(v, text, Snackbar.LENGTH_SHORT);
                    }

                    snackbar.show();
                    mSharedPref.edit().putInt(KEY_PREF_STUDIO, Integer.parseInt(Integer.toString(mCount) + "000")).apply();
                } else {
                    mSharedPref.edit().putInt(KEY_PREF_STUDIO, 0).apply();
                }
                break;

            case R.id.relative_layout_issues:
                openURL(URL_REPO_ISSUES);
                break;

            case R.id.relative_layout_changelog:
                openURL(URL_REPO_CHANGELOG);
                break;

            case R.id.relative_layout_licenses:
                Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View dialog_view = getLayoutInflater().inflate(R.layout.open_source_licenses, null);
                WebView wv = (WebView) dialog_view.findViewById(R.id.webView1);
                dialog.setContentView(dialog_view);
                wv.loadData(PreferenceUtils.LICENCE_TERMS, "text/html", null);
                dialog.show();
                break;

            case R.id.text_view_author_1_g_plus:
                openURL(URL_AUTHOR_1_G_PLUS);
                break;

            case R.id.text_view_author_1_donate:
                ClipboardManager clipManager1 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip1 = ClipData.newPlainText(TAG_CLIPBOARD_DONATE, URL_AUTHOR_1_PAYPAL);
                clipManager1.setPrimaryClip(clip1);
                Snackbar.make(v, R.string.paypal_copy_message, Snackbar.LENGTH_LONG).show();
                break;

            case R.id.text_view_author_2_g_plus:
                openURL(URL_AUTHOR_2_G_PLUS);
                break;

            case R.id.text_view_author_2_donate:
                openURL(URL_AUTHOR_2_PAYPAL);
                break;

            case R.id.text_view_developer_1_github:
                openURL(URL_DEVELOPER1_GITHUB);
                break;

            case R.id.text_view_developer_1_donate:
                try {
                    openURL(URL_DEVELOPER1_BITCOIN);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(v, R.string.nobitcoinapp, Snackbar.LENGTH_LONG).show();
                }
                break;

            case R.id.relative_layout_translate:
                openURL(URL_REPO_TRANSLATE);
                break;

            case R.id.relative_layout_g_plus_community:
                openURL(URL_REPO_G_PLUS_COMMUNITY);
                break;

            case R.id.relative_layout_xda:
                openURL(URL_REPO_XDA);
                break;

            case R.id.relative_layout_rate:
                openURL(URL_REPO_RATE);
                break;
        }
    }


    private void openURL(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
