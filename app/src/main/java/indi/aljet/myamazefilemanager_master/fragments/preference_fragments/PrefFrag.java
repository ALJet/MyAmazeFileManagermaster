package indi.aljet.myamazefilemanager_master.fragments.preference_fragments;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.activities.AboutActivity;
import indi.aljet.myamazefilemanager_master.activities.PreferencesActivity;
import indi.aljet.myamazefilemanager_master.ui.views.preference.CheckBox;
import indi.aljet.myamazefilemanager_master.utils.color.ColorUsage;
import indi.aljet.myamazefilemanager_master.utils.files.CryptUtil;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

import static indi.aljet.myamazefilemanager_master.R.string.feedback;
/**
 * Created by PC-LJL on 2018/2/8.
 */

public class PrefFrag extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    private static final String PREFERENCE_KEY_ABOUT = "about";
    private static final String[] PREFERENCE_KEYS =
            {"columns", "theme", "rootmode", "showHidden", "feedback", PREFERENCE_KEY_ABOUT,
                    "colors", "sidebar_folders", "sidebar_quickaccess", "advancedsearch"};

    public static final String PREFERENCE_SHOW_SIDEBAR_FOLDERS = "sidebar_folders_enable";
    public static final String PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES = "sidebar_quickaccess_enable";

    public static final String PREFERENCE_TEXTEDITOR_NEWSTACK = "texteditor_newstack";

    public static final String PREFERENCE_SHOW_HIDDENFILES = "showHidden";

    public static final String PREFERENCE_ROOTMODE = "rootmode";

    public static final String PREFERENCE_CHANGEPATHS = "typeablepaths";

    public static final String PREFERENCE_CRYPT_MASTER_PASSWORD = "crypt_password";
    public static final String PREFERENCE_CRYPT_FINGERPRINT = "crypt_fingerprint";
    public static final String PREFERENCE_CRYPT_WARNING_REMEMBER = "crypt_remember";

    public static final String PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT = "";
    public static final boolean PREFERENCE_CRYPT_FINGERPRINT_DEFAULT = false;
    public static final boolean PREFERENCE_CRYPT_WARNING_REMEMBER_DEFAULT = false;
    public static final String ENCRYPT_PASSWORD_FINGERPRINT = "fingerprint";
    public static final String ENCRYPT_PASSWORD_MASTER = "master";

    private UtilitiesProviderInterface utilsProvider;
    private SharedPreferences sharedPref;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilsProvider = (UtilitiesProviderInterface) getActivity();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        for (String PREFERENCE_KEY : PREFERENCE_KEYS) {
            findPreference(PREFERENCE_KEY).setOnPreferenceClickListener(this);
    }

        // crypt master password
        final Preference masterPasswordPreference = findPreference(PREFERENCE_CRYPT_MASTER_PASSWORD);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ||
                sharedPref.getBoolean(PREFERENCE_CRYPT_FINGERPRINT, false)) {
            // encryption feature not available
            masterPasswordPreference.setEnabled(false);
        }
        masterPasswordPreference.setOnPreferenceClickListener(this);

        CheckBox checkBoxFingerprint = (CheckBox) findPreference(PREFERENCE_CRYPT_FINGERPRINT);

        try {

            // finger print sensor
            final FingerprintManager fingerprintManager = (FingerprintManager)
                    getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

            final KeyguardManager keyguardManager = (KeyguardManager)
                    getActivity().getSystemService(Context.KEYGUARD_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManager.isHardwareDetected()) {

                checkBoxFingerprint.setEnabled(true);
            }

            checkBoxFingerprint.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.crypt_fingerprint_no_permission),
                                Toast.LENGTH_LONG).show();
                        return false;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            !fingerprintManager.hasEnrolledFingerprints()) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.crypt_fingerprint_not_enrolled),
                                Toast.LENGTH_LONG).show();
                        return false;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            !keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.crypt_fingerprint_no_security),
                                Toast.LENGTH_LONG).show();
                        return false;
                    }

                    masterPasswordPreference.setEnabled(false);
                    return true;
                }
            });
        } catch (NoClassDefFoundError error) {
            error.printStackTrace();

            // fingerprint manager class not defined in the framework
            checkBoxFingerprint.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String[] sort;
        MaterialDialog.Builder builder;

        switch (preference.getKey()) {
            case "columns":
                sort = getResources().getStringArray(R.array.columns);
                builder = new MaterialDialog.Builder(getActivity());
                builder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
                builder.title(R.string.gridcolumnno);
                int current = Integer.parseInt(sharedPref.getString("columns", "-1"));
                current = current == -1 ? 0 : current;
                if (current != 0) current = current - 1;
                builder.items(sort).itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        sharedPref.edit().putString("columns", "" + (which != 0 ? sort[which] : "" + -1)).commit();
                        dialog.dismiss();
                        return true;
                    }
                });
                builder.build().show();
                return true;
            case "theme":
                sort = getResources().getStringArray(R.array.theme);
                current = Integer.parseInt(sharedPref.getString("theme", "0"));
                builder = new MaterialDialog.Builder(getActivity());
                //builder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
                builder.items(sort).itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        utilsProvider.getThemeManager().setAppTheme(AppTheme.getTheme(which));
                        dialog.dismiss();
                        restartPC(getActivity());
                        return true;
                    }
                });
                builder.title(R.string.theme);
                builder.build().show();
                return true;
            case "feedback":
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "vishalmeham2@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback : Amaze File Manager");

                PackageManager packageManager = getActivity().getPackageManager();
                List activities = packageManager.queryIntentActivities(emailIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe)
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(feedback)));
                else
                    Toast.makeText(getActivity(), getResources().getString(R.string.send_email_to)
                            + " vishalmeham2@gmail.com", Toast.LENGTH_LONG).show();
                return false;
            case PREFERENCE_KEY_ABOUT:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return false;
            /*FROM HERE BE FRAGMENTS*/
            case "colors":
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.COLORS_PREFERENCE);
                return true;
            case "sidebar_folders":
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.FOLDERS_PREFERENCE);
                return true;
            case "sidebar_quickaccess":
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.QUICKACCESS_PREFERENCE);
                return true;
            case "advancedsearch":
                ((PreferencesActivity) getActivity())
                        .selectItem(PreferencesActivity.ADVANCEDSEARCH_PREFERENCE);
                return true;
            case PREFERENCE_CRYPT_MASTER_PASSWORD:
                MaterialDialog.Builder masterPasswordDialogBuilder = new MaterialDialog.Builder(getActivity());
                masterPasswordDialogBuilder.title(getResources().getString(R.string.crypt_pref_master_password_title));

                String decryptedPassword = null;
                try {
                    String preferencePassword = sharedPref.getString(PREFERENCE_CRYPT_MASTER_PASSWORD,
                            PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT);
                    if (!preferencePassword.equals(PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT)) {

                        // password is set, try to decrypt
                        decryptedPassword = CryptUtil.decryptPassword(getActivity(), preferencePassword);
                    } else {
                        // no password set in preferences, just leave the field empty
                        decryptedPassword = "";
                    }
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }

                masterPasswordDialogBuilder.input(getResources().getString(R.string.authenticate_password),
                        decryptedPassword, false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            }
                        });
                masterPasswordDialogBuilder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
                masterPasswordDialogBuilder.positiveText(getResources().getString(R.string.ok));
                masterPasswordDialogBuilder.negativeText(getResources().getString(R.string.cancel));
                masterPasswordDialogBuilder.positiveColor(utilsProvider.getColorPreference().getColor(ColorUsage.ACCENT));
                masterPasswordDialogBuilder.negativeColor(utilsProvider.getColorPreference().getColor(ColorUsage.ACCENT));

                masterPasswordDialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {

                            String inputText = dialog.getInputEditText().getText().toString();
                            if (!inputText.equals(PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT)) {

                                sharedPref.edit().putString(PREFERENCE_CRYPT_MASTER_PASSWORD,
                                        CryptUtil.encryptPassword(getActivity(),
                                                dialog.getInputEditText().getText().toString())).apply();
                            } else {
                                // empty password, remove the preference
                                sharedPref.edit().putString(PREFERENCE_CRYPT_MASTER_PASSWORD,
                                        "").apply();
                            }
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                            sharedPref.edit().putString(PREFERENCE_CRYPT_MASTER_PASSWORD,
                                    PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT).apply();
                        }
                    }
                });

                masterPasswordDialogBuilder.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                });

                masterPasswordDialogBuilder.build().show();
                return true;
        }

        return false;
    }



    public static void restartPC(final Activity activity) {
        if (activity == null) return;

        final int enter_anim = android.R.anim.fade_in;
        final int exit_anim = android.R.anim.fade_out;
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.finish();
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.startActivity(activity.getIntent());
    }
}
