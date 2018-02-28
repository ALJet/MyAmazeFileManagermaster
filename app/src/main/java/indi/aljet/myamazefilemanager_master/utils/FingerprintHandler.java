package indi.aljet.myamazefilemanager_master.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import indi.aljet.myamazefilemanager_master.utils.files.EncryptDecryptUtils;

/**
 * Created by PC-LJL on 2018/1/15.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private EncryptDecryptUtils.
            DecryptButtonCallbackInterface
            decryptButtonCallbackInterface;
    private Intent decryptIntent;

    private MaterialDialog materialDialog;


    public FingerprintHandler(Context mContext, Intent intent, MaterialDialog materialDialog,
                              EncryptDecryptUtils.DecryptButtonCallbackInterface decryptButtonCallbackInterface) {
        context = mContext;
        this.decryptIntent = intent;
        this.materialDialog = materialDialog;
        this.decryptButtonCallbackInterface = decryptButtonCallbackInterface;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void authenticate(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    public FingerprintHandler() {
        super();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        materialDialog.cancel();
        decryptButtonCallbackInterface.confirm(decryptIntent);
    }

    @Override
    public void onAuthenticationFailed() {
        materialDialog.cancel();
        decryptButtonCallbackInterface.failed();
    }
}
