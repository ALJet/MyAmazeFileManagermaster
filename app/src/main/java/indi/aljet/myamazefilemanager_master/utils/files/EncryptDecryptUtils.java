package indi.aljet.myamazefilemanager_master.utils.files;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;

import indi.aljet.myamazefilemanager_master.activities.MainActivity;
import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.asynchronous.services.EncryptService;
import indi.aljet.myamazefilemanager_master.database.CryptHandler;
import indi.aljet.myamazefilemanager_master.database.models.EncryptedEntry;
import indi.aljet.myamazefilemanager_master.db.DBHelper;
import indi.aljet.myamazefilemanager_master.db.tables.Encrypted;
import indi.aljet.myamazefilemanager_master.filesystem.HybridFileParcelable;
import indi.aljet.myamazefilemanager_master.fragments.MainFragment;
import indi.aljet.myamazefilemanager_master.fragments.preference_fragments.PrefFrag;
import indi.aljet.myamazefilemanager_master.ui.dialogs.GeneralDialogCreation;
import indi.aljet.myamazefilemanager_master.utils.OpenMode;
import indi.aljet.myamazefilemanager_master.utils.ServiceWatcherUtil;
import indi.aljet.myamazefilemanager_master.utils.provider.UtilitiesProviderInterface;

/**
 * Created by PC-LJL on 2018/1/16.
 */

public class EncryptDecryptUtils {

    public static final String DECRYPT_BROADCAST =
            "decrypt_broadcast";

    public static void startEncryption(Context c,
                                       final String path,
                                       final String password,
                                       Intent intent)throws Exception{
        CryptHandler cryptHandler = new CryptHandler(c);
        EncryptedEntry encryptedEntry = new EncryptedEntry(
                path.concat(CryptUtil.CRYPT_EXTENSION),
                password);
//        cryptHandler.addEntry(encryptedEntry);
        //使用自己的方法
        DBHelper.addEntry(encryptedEntry);
        ServiceWatcherUtil.runService(c,intent);
    }


    public static void decryptFile(Context c,
                                   final MainActivity mainActivity,
                                   final MainFragment main,
                                   OpenMode openMode,
                                   HybridFileParcelable sourceFile,
                                   String decryptPath,
                                   UtilitiesProviderInterface utilsProvider,
                                   boolean broadcastResult){
        Intent decryptIntent = new Intent(main.getContext(),
                EncryptService.class);
        decryptIntent.putExtra(EncryptService.TAG_OPEN_MODE,
                openMode.ordinal());
        decryptIntent.putExtra(EncryptService.TAG_CRYPT_MODE,
                EncryptService.CryptEnum.DECRYPT.ordinal());
        decryptIntent.putExtra(EncryptService.TAG_SOURCE,
                sourceFile);
        decryptIntent.putExtra(EncryptService.TAG_DECRYPT_PATH,
                decryptPath);
        decryptIntent.putExtra(EncryptService.TAG_BROADCAST_RESULT,
                broadcastResult);
        SharedPreferences preferences1 = PreferenceManager
                .getDefaultSharedPreferences(main.getContext());
        EncryptedEntry encryptedEntry;

        try{
            encryptedEntry = findEncryptedEntry(main
            .getContext(),sourceFile.getPath());
        }catch (GeneralSecurityException | IOException e){
            e.printStackTrace();
            Toast.makeText(main.getContext(), main.getActivity().getResources().getString(R.string.crypt_decryption_fail), Toast.LENGTH_LONG).show();
            return;
        }


        DecryptButtonCallbackInterface decryptButtonCallbackInterface =
                new DecryptButtonCallbackInterface() {
                    @Override
                    public void confirm(Intent intent) {
                        ServiceWatcherUtil.runService(main.getContext(), intent);
                    }

                    @Override
                    public void failed() {
                        Toast.makeText(main.getContext(), main.getActivity().getResources().getString(R.string.crypt_decryption_fail_password), Toast.LENGTH_LONG).show();
                    }
                };

        if (encryptedEntry == null) {
            // couldn't find the matching path in database, we lost the password

            Toast.makeText(main.getContext(), main.getActivity().getResources().getString(R.string.crypt_decryption_fail), Toast.LENGTH_LONG).show();
            return;
        }

        switch (encryptedEntry.getPassword()) {
            case PrefFrag.ENCRYPT_PASSWORD_FINGERPRINT:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        GeneralDialogCreation.showDecryptFingerprintDialog(c,
                                mainActivity, decryptIntent, utilsProvider.getAppTheme(), decryptButtonCallbackInterface);
                    } else throw new IllegalStateException("API < M!");
                } catch (GeneralSecurityException | IOException | IllegalStateException e) {
                    e.printStackTrace();

                    Toast.makeText(main.getContext(), main.getResources().getString(R.string.crypt_decryption_fail), Toast.LENGTH_LONG).show();
                }
                break;
            case PrefFrag.ENCRYPT_PASSWORD_MASTER:
                try {
                    GeneralDialogCreation.showDecryptDialog(c,
                            mainActivity, decryptIntent, utilsProvider.getAppTheme(),
                            CryptUtil.decryptPassword(c, preferences1.getString(PrefFrag.PREFERENCE_CRYPT_MASTER_PASSWORD,
                                    PrefFrag.PREFERENCE_CRYPT_MASTER_PASSWORD_DEFAULT)), decryptButtonCallbackInterface);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                    Toast.makeText(main.getContext(), main.getResources().getString(R.string.crypt_decryption_fail), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                GeneralDialogCreation.showDecryptDialog(c, mainActivity, decryptIntent,
                        utilsProvider.getAppTheme(), encryptedEntry.getPassword(),
                        decryptButtonCallbackInterface);
                break;
        }

    }


    /**
     * DBHelper用自己 的数据库方法
     * @param context
     * @param path
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static EncryptedEntry findEncryptedEntry(Context context,
                                                     String path)
        throws GeneralSecurityException,IOException{
        CryptHandler handler = new CryptHandler(context);
        EncryptedEntry matched = null;
        //用自己 的数据库方法
        for(Encrypted encrypted : DBHelper
                .getAllEntrpteds()){
            if(path.contains(encrypted.getPassword())){
                if(matched == null || matched
                        .getPath().length() < encrypted.getPassword()
                        .length()){
                    matched.setPath(encrypted.getPath());
                    matched.setId(encrypted.get_id());
                    matched.setPassword(encrypted.getPassword());
                }
            }
        }
        return matched;


    }


    public interface EncryptButtonCallbackInterface{
        void onButtonPressed(Intent intent) throws Exception;

        void onButtonPressed(Intent intent,String password)
            throws Exception;

    }

    public interface DecryptButtonCallbackInterface{
        void confirm(Intent intent);

        void failed();
    }

}
