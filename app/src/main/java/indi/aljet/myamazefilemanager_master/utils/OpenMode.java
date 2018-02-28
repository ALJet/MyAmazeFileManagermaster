package indi.aljet.myamazefilemanager_master.utils;

/**
 * Created by PC-LJL on 2018/1/9.
 */

public enum OpenMode {

    UNKNOWN,
    FILE,
    SMB,

    CUSTOM,

    ROOT,
    OTG,
    GDRIVE,
    DROPBOX,
    BOX,
    ONEDRIVE;


    public static OpenMode getOpenMode(int ordinal){
        return OpenMode.values()[ordinal];
    }
}
