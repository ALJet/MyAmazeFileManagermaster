package indi.aljet.myamazefilemanager_master.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;

import indi.aljet.myamazefilemanager_master.utils.files.CryptUtil;

/**
 * Created by PC-LJL on 2018/1/18.
 */

public class SmbUtil {

    public static String getSmbDecryptedPath(Context context,
                                             String path)
    throws GeneralSecurityException{
        if(!(path.contains(":") && path.contains("@"))){
            return path;
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(path.substring(0,path.indexOf(":",
                4) + 1));
        String encryptedPassword = path.substring(path
        .indexOf(":",4) + 1,path
        .lastIndexOf("@"));
        if(!TextUtils.isEmpty(encryptedPassword)){
            String decryptedPassword = null;
            try {
                decryptedPassword = CryptUtil
                        .decryptPassword(context,
                                encryptedPassword);
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer.append(decryptedPassword);
        }
        buffer.append(path.substring(path.lastIndexOf("@"),
                path.length()));
        return buffer.toString();

    }

    public static String getSmbEncryptedPath(Context context,
                                             String path)
        throws GeneralSecurityException,IOException{
        if (!(path.contains(":") && path.contains("@"))) {
            // smb path doesn't have any credentials
            return path;
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(path.substring(0, path.indexOf(":", 4)+1));
        String decryptedPassword = path.substring(path.indexOf(":", 4)+1, path.lastIndexOf("@"));

        if (!TextUtils.isEmpty(decryptedPassword)) {
            String encryptPassword =  CryptUtil.encryptPassword(context, decryptedPassword);
            buffer.append(encryptPassword);
        }
        buffer.append(path.substring(path.lastIndexOf("@"), path.length()));

        return buffer.toString();
    }
}
