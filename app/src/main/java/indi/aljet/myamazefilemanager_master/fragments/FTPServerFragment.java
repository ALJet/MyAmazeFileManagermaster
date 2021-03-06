package indi.aljet.myamazefilemanager_master.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.asynchronous.ftpservice.FTPService;
import indi.aljet.myamazefilemanager_master.utils.Utils;
import indi.aljet.myamazefilemanager_master.utils.color.ColorUsage;
import indi.aljet.myamazefilemanager_master.utils.files.CryptUtil;
import indi.aljet.myamazefilemanager_master.utils.theme.AppTheme;

/**
 * Created by PC-LJL on 2018/2/9.
 */

public class FTPServerFragment extends Fragment {

    private TextView statusText, username, password, port, sharedPath;
    private AppCompatEditText usernameEditText, passwordEditText;
    private TextInputLayout usernameTextInput, passwordTextInput;
    private AppCompatCheckBox mAnonymousCheckBox, mSecureCheckBox;
    private Button ftpBtn;
    private MainActivity mainActivity;
    private View rootView, startDividerView, statusDividerView;
    private int skin_color, skinTwoColor, accentColor;
    private Spanned spannedStatusNoConnection, spannedStatusConnected;
    private Spanned spannedStatusSecure, spannedStatusNotRunning;
    private ImageButton ftpPasswordVisibleButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivity = (MainActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ftp
        ,container,false);
        statusText =(TextView) rootView.findViewById(R.id.text_view_ftp_status);
        username = (TextView) rootView.findViewById(R.id.text_view_ftp_username);
        password = (TextView) rootView.findViewById(R.id.text_view_ftp_password);
        port = (TextView) rootView.findViewById(R.id.text_view_ftp_port);
        sharedPath = (TextView) rootView.findViewById(R.id.text_view_ftp_path);
        ftpBtn = (Button) rootView.findViewById(R.id.startStopButton);
        startDividerView = rootView.findViewById(R.id.divider_ftp_start);
        statusDividerView = rootView.findViewById(R.id.divider_ftp_status);
        ftpPasswordVisibleButton = (ImageButton) rootView.findViewById(R.id.ftp_password_visible);

        skin_color = mainActivity.getColorPreference().getColor(ColorUsage.PRIMARY);
        skinTwoColor = mainActivity.getColorPreference().getColor(ColorUsage.PRIMARY_TWO);
        accentColor = mainActivity.getColorPreference().getColor(ColorUsage.ACCENT);

        updateSpans();
        updateStatus();


        //light theme
        if (mainActivity.getAppTheme().equals(AppTheme.LIGHT)) {
            startDividerView.setBackgroundColor(Utils.getColor(getContext(), R.color.divider));
            statusDividerView.setBackgroundColor(Utils.getColor(getContext(), R.color.divider));
        } else {
            //dark
            startDividerView.setBackgroundColor(Utils.getColor(getContext(), R.color.divider_dark_card));
            statusDividerView.setBackgroundColor(Utils.getColor(getContext(), R.color.divider_dark_card));
        }

        ftpBtn.setOnClickListener(v -> {
            if (!FTPService.isRunning()) {
                if (FTPService.isConnectedToWifi(getContext())
                        || FTPService.isConnectedToLocalNetwork(getContext())
                        || FTPService.isEnabledWifiHotspot(getContext()))
                    startServer();
                else {
                    // no wifi and no eth, we shouldn't be here in the first place, because of broadcast
                    // receiver, but just to be sure
                    statusText.setText(spannedStatusNoConnection);
                }
            } else {
                stopServer();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mainActivity.getAppbar().setTitle(R.string.ftp);
        mainActivity.floatingActionButton.getMenuButton().hide();
        mainActivity.buttonBarFrame.setVisibility(View.GONE);
        mainActivity.supportInvalidateOptionsMenu();

        mainActivity.updateViews(new ColorDrawable(MainActivity.currentTab==1 ?
                skinTwoColor : skin_color));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_ftp_port:
                int currentFtpPort = getDefaultPortFromPreferences();

                new MaterialDialog.Builder(getActivity())
                        .input(getString(R.string.ftp_port_edit_menu_title), Integer.toString(currentFtpPort), true, (dialog, input) -> {})
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .onPositive((dialog, which) -> {
                            EditText editText = dialog.getInputEditText();
                            if (editText != null) {
                                String name = editText.getText().toString();

                                int portNumber = Integer.parseInt(name);
                                if (portNumber < 1024) {
                                    Toast.makeText(getActivity(), R.string.ftp_port_change_error_invalid, Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    changeFTPServerPort(portNumber);
                                    Toast.makeText(getActivity(), R.string.ftp_port_change_success, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        })
                        .positiveText(getResources().getString(R.string.change).toUpperCase())
                        .negativeText(R.string.cancel)
                        .build()
                        .show();
                return true;
            case R.id.ftp_path:
                MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity());
                dialogBuilder.title(getString(R.string.ftp_path));
                dialogBuilder.input(getString(R.string.ftp_path_hint),
                        getDefaultPathFromPreferences(),
                        false, (dialog, input) -> {});
                dialogBuilder.onPositive((dialog, which) -> {
                    EditText editText = dialog.getInputEditText();
                    if (editText != null) {
                        String path = editText.getText().toString();

                        File pathFile = new File(path);
                        if (pathFile.exists() && pathFile.isDirectory()) {

                            changeFTPServerPath(pathFile.getPath());

                            Toast.makeText(getActivity(), R.string.ftp_path_change_success,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            // try to get parent
                            File pathParentFile = new File(pathFile.getParent());
                            if (pathParentFile.exists() && pathParentFile.isDirectory()) {

                                changeFTPServerPath(pathParentFile.getPath());
                                Toast.makeText(getActivity(), R.string.ftp_path_change_success,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                // don't have access, print error

                                Toast.makeText(getActivity(), R.string.ftp_path_change_error_invalid,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                });

                dialogBuilder.positiveText(getResources().getString(R.string.change).toUpperCase())
                        .negativeText(R.string.cancel)
                        .build()
                        .show();
                return true;
            case R.id.ftp_login:
                MaterialDialog.Builder loginDialogBuilder = new MaterialDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View rootView = inflater.inflate(R.layout.dialog_ftp_login, null);
                initLoginDialogViews(rootView);

                loginDialogBuilder.customView(rootView, true);

                loginDialogBuilder.title(getString(R.string.ftp_login));

                loginDialogBuilder.onPositive((dialog, which) -> {
                    if (mAnonymousCheckBox.isChecked()) {

                        // remove preferences
                        setFTPUsername("");
                        setFTPPassword("");
                    } else {

                        if (passwordEditText.getText().toString().equals("")) {
                            passwordTextInput.setError(getResources().getString(R.string.field_empty));
                        } else if (usernameEditText.getText().toString().equals("")) {
                            usernameTextInput.setError(getResources().getString(R.string.field_empty));
                        } else {

                            // password and username field not empty, let's set them to preferences
                            setFTPUsername(usernameEditText.getText().toString());
                            setFTPPassword(passwordEditText.getText().toString());
                        }
                    }

                    if (mSecureCheckBox.isChecked()) {
                        setSecurePreference(true);
                    } else setSecurePreference(false);

                    // TODO: Fix secure connection certification
                    mSecureCheckBox.setEnabled(false);
                    setSecurePreference(false);
                    // TODO: Fix secure connection certification
                });

                loginDialogBuilder.positiveText(getResources().getString(R.string.set).toUpperCase())
                        .negativeText(getResources().getString(R.string.cancel))
                        .build()
                        .show();

                return true;
            case R.id.ftp_timeout:
                MaterialDialog.Builder timeoutBuilder = new MaterialDialog.Builder(getActivity());

                timeoutBuilder.title(getResources().getString(R.string.ftp_timeout) + " (" +
                        getResources().getString(R.string.ftp_seconds) + ")");
                timeoutBuilder.input(String.valueOf(FTPService.DEFAULT_TIMEOUT + " " +
                                getResources().getString(R.string.ftp_seconds)), String.valueOf(getFTPTimeout()),
                        true, (dialog, input) -> {
                            boolean isInputInteger;
                            try {
                                // try parsing for integer check
                                Integer.parseInt(input.toString());
                                isInputInteger = true;
                            } catch (NumberFormatException e) {
                                isInputInteger = false;
                            }

                            if (input.length()==0 || !isInputInteger)
                                setFTPTimeout(FTPService.DEFAULT_TIMEOUT);
                            else
                                setFTPTimeout(Integer.valueOf(input.toString()));
                        });
                timeoutBuilder.positiveText(getResources().getString(R.string.set).toUpperCase())
                        .negativeText(getResources().getString(R.string.cancel))
                        .build()
                        .show();
                return true;
        }

        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mainActivity.getMenuInflater()
                .inflate(R.menu.ftp_server_ment,menu);
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if ((netInfo != null && (netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_ETHERNET))
                    || FTPService.isEnabledWifiHotspot(getContext())) {
                // connected to wifi or eth
                ftpBtn.setEnabled(true);
            } else {
                // wifi or eth connection lost
                stopServer();
                statusText.setText(spannedStatusNoConnection);
                ftpBtn.setEnabled(true);
                ftpBtn.setEnabled(false);
                ftpBtn.setText(getResources().getString(R.string.start_ftp).toUpperCase());
            }
        }
    };

    private BroadcastReceiver ftpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            updateSpans();
            if (action.equals(FTPService.ACTION_STARTED)) {
                if (getSecurePreference()) {
                    statusText.setText(spannedStatusSecure);
                } else {
                    statusText.setText(spannedStatusConnected);
                }
                ftpBtn.setText(getResources().getString(R.string.stop_ftp).toUpperCase());

            } else if (action.equals(FTPService.ACTION_FAILEDTOSTART)) {
                statusText.setText(spannedStatusNotRunning);

                Toast.makeText(getContext(),
                        getResources().getString(R.string.unknown_error), Toast.LENGTH_LONG).show();

                ftpBtn.setText(getResources().getString(R.string.start_ftp).toUpperCase());
            } else if (action.equals(FTPService.ACTION_STOPPED)) {
                statusText.setText(spannedStatusNotRunning);
                ftpBtn.setText(getResources().getString(R.string.start_ftp).toUpperCase());
            }
        }
    };



    private void startServer(){
        getContext().sendBroadcast(new Intent(
                FTPService.ACTION_START_FTPSERVER));
    }


    private void stopServer(){
        getContext().sendBroadcast(new Intent((FTPService
        .ACTION_STOP_FTPSERVER)));
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(mWifiReceiver, wifiFilter);
        IntentFilter ftpFilter = new IntentFilter();
        ftpFilter.addAction(FTPService.ACTION_STARTED);
        ftpFilter.addAction(FTPService.ACTION_STOPPED);
        ftpFilter.addAction(FTPService.ACTION_FAILEDTOSTART);
        getContext().registerReceiver(ftpReceiver, ftpFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mWifiReceiver);
        getContext().unregisterReceiver(ftpReceiver);
    }

    private void updateStatus(){
        if (!FTPService.isRunning()) {
            if (!FTPService.isConnectedToWifi(getContext())
                    && !FTPService.isConnectedToLocalNetwork(getContext())
                    && !FTPService.isEnabledWifiHotspot(getContext())) {
                statusText.setText(spannedStatusNoConnection);
                ftpBtn.setEnabled(false);
            } else {

                statusText.setText(spannedStatusNotRunning);
                ftpBtn.setEnabled(true);
            }

            ftpBtn.setText(getResources().getString(R.string.start_ftp).toUpperCase());

        } else {
            statusText.setText(spannedStatusConnected);
            ftpBtn.setEnabled(true);
            ftpBtn.setText(getResources().getString(R.string.stop_ftp).toUpperCase());
        }

        final String passwordDecrypted = getPasswordFromPreferences();
        final String passwordBulleted = passwordDecrypted.replaceAll(".", "\u25CF");

        username.setText(getResources().getString(R.string.username) + ": " +
                getUsernameFromPreferences());
        password.setText(getResources().getString(R.string.password) + ": " +
                passwordBulleted);

        ftpPasswordVisibleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_grey600_24dp));

        if (passwordDecrypted.equals("")) {
            ftpPasswordVisibleButton.setVisibility(View.GONE);
        } else {
            ftpPasswordVisibleButton.setVisibility(View.VISIBLE);
        }

        ftpPasswordVisibleButton.setOnClickListener(v -> {
            if (password.getText().toString().contains("\u25CF")) {
                // password was not visible, let's make it visible
                password.setText(getResources().getString(R.string.password) + ": " +
                        passwordDecrypted);
                ftpPasswordVisibleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off_grey600_24dp));
            } else {
                // password was visible, let's hide it
                password.setText(getResources().getString(R.string.password) + ": " + passwordBulleted);
                ftpPasswordVisibleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_grey600_24dp));
            }
        });

        port.setText(getResources().getString(R.string.ftp_port) + ": " +
                getDefaultPortFromPreferences());
        sharedPath.setText(getResources().getString(R.string.ftp_path) + ": " +
                getDefaultPathFromPreferences());
    }

    /**
     * Updates the status spans
     */
    private void updateSpans(){
        String ftpAddress;

        try{
            ftpAddress = getFTPAddressString();
        } catch (NullPointerException npe){
            npe.printStackTrace();
            ftpAddress = "";
            Toast.makeText(getContext(), getResources().getString(R.string.local_inet_addr_error), Toast.LENGTH_SHORT).show();
        }

        String statusHead = getResources().getString(R.string.ftp_status_title) + ": ";

        spannedStatusConnected = Html.fromHtml(statusHead + "<b>&nbsp;&nbsp;" +
                "<font color='" + accentColor + "'>"
                + getResources().getString(R.string.ftp_status_running) + "</font></b>" +
                "&nbsp;<i>(" + ftpAddress + ")</i>");
        spannedStatusNoConnection = Html.fromHtml(statusHead + "<b>&nbsp;&nbsp;&nbsp;&nbsp;" +
                "<font color='" + Utils.getColor(getContext(), android.R.color.holo_red_light) + "'>"
                + getResources().getString(R.string.ftp_status_no_connection) + "</font></b>");

        spannedStatusNotRunning = Html.fromHtml(statusHead + "<b>&nbsp;&nbsp;&nbsp;&nbsp;" +
                getResources().getString(R.string.ftp_status_not_running) + "</b>");
        spannedStatusSecure = Html.fromHtml(statusHead + "<b>&nbsp;&nbsp;&nbsp;&nbsp;" +
                "<font color='" + Utils.getColor(getContext(), android.R.color.holo_green_light) + "'>"
                + getResources().getString(R.string.ftp_status_secure_connection) + "</font></b>" +
                "&nbsp;<i>(" + ftpAddress + ")</i>");
    }

    private void initLoginDialogViews(View loginDialogView) {

        usernameEditText = (AppCompatEditText) loginDialogView.findViewById(R.id.edit_text_dialog_ftp_username);
        passwordEditText = (AppCompatEditText) loginDialogView.findViewById(R.id.edit_text_dialog_ftp_password);
        usernameTextInput = (TextInputLayout) loginDialogView.findViewById(R.id.text_input_dialog_ftp_username);
        passwordTextInput = (TextInputLayout) loginDialogView.findViewById(R.id.text_input_dialog_ftp_password);
        mAnonymousCheckBox = (AppCompatCheckBox) loginDialogView.findViewById(R.id.checkbox_ftp_anonymous);
        mSecureCheckBox = (AppCompatCheckBox) loginDialogView.findViewById(R.id.checkbox_ftp_secure);

        mAnonymousCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                usernameEditText.setEnabled(false);
                passwordEditText.setEnabled(false);
            } else {
                usernameEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
            }
        });

        // init dialog views as per preferences
        if (getUsernameFromPreferences().equals(FTPService.DEFAULT_USERNAME)) {
            mAnonymousCheckBox.setChecked(true);
        } else {

            usernameEditText.setText(getUsernameFromPreferences());
            passwordEditText.setText(getPasswordFromPreferences());
        }

        if (getSecurePreference()) {
            mSecureCheckBox.setChecked(true);
        } else mSecureCheckBox.setChecked(false);

        // check if we have a keystore
        InputStream stream = getResources().openRawResource(R.raw.key);
        if (stream == null) {
            mSecureCheckBox.setEnabled(false);
            mSecureCheckBox.setChecked(false);
            setSecurePreference(false);
        }
    }


    /**
     * @return address at which server is running
     */
    private String getFTPAddressString() {
        InetAddress ia = FTPService.getLocalInetAddress(getContext());
        if(ia == null){
            throw new NullPointerException("getLocalInetAddress returned a null value");
        }
        return (getSecurePreference() ? FTPService.INITIALS_HOST_SFTP : FTPService.INITIALS_HOST_FTP)
                + ia.getHostAddress()  + ":" + getDefaultPortFromPreferences();
    }

    private int getDefaultPortFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getInt(FTPService.PORT_PREFERENCE_KEY, FTPService.DEFAULT_PORT);
    }

    private String getUsernameFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(FTPService.KEY_PREFERENCE_USERNAME, FTPService.DEFAULT_USERNAME);
    }

    private String getPasswordFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            String encryptedPassword = preferences.getString(FTPService.KEY_PREFERENCE_PASSWORD, "");

            if (encryptedPassword.equals("")) {
                return "";
            } else {
                return CryptUtil.decryptPassword(getContext(), encryptedPassword);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();

            Toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
            // can't decrypt the password saved in preferences, remove the preference altogether
            preferences.edit().putString(FTPService.KEY_PREFERENCE_PASSWORD, "").apply();
            return "";
        }
    }

    private String getDefaultPathFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return preferences.getString(FTPService.KEY_PREFERENCE_PATH, FTPService.DEFAULT_PATH);
    }

    private void changeFTPServerPort(int port) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        preferences.edit()
                .putInt(FTPService.PORT_PREFERENCE_KEY, port)
                .apply();

        // first update spans which will point to an updated status
        updateSpans();
        updateStatus();
    }

    private void changeFTPServerPath(String path) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putString(FTPService.KEY_PREFERENCE_PATH, path).apply();

        updateStatus();

    }

    private void setFTPUsername(String username) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        preferences.edit().putString(FTPService.KEY_PREFERENCE_USERNAME, username).apply();
        updateStatus();
    }

    private void setFTPPassword(String password) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            preferences.edit().putString(FTPService.KEY_PREFERENCE_PASSWORD, CryptUtil.encryptPassword(getContext(), password)).apply();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
        }
        updateStatus();
    }

    /**
     * Returns timeout from preferences
     * @return timeout in seconds
     */
    private int getFTPTimeout() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getInt(FTPService.KEY_PREFERENCE_TIMEOUT, FTPService.DEFAULT_TIMEOUT);
    }

    private void setFTPTimeout(int seconds) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putInt(FTPService.KEY_PREFERENCE_TIMEOUT, seconds).apply();
    }

    private boolean getSecurePreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getBoolean(FTPService.KEY_PREFERENCE_SECURE, FTPService.DEFAULT_SECURE);
    }

    private void setSecurePreference(boolean isSecureEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putBoolean(FTPService.KEY_PREFERENCE_SECURE, isSecureEnabled).apply();
    }



}
