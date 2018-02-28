package indi.aljet.myamazefilemanager_master.adapter.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by PC-LJL on 2018/2/7.
 */

public class IconDataParcelable implements Parcelable {

    public static final int IMAGE_RES = 0, IMAGE_FROMFILE = 1;

    public final int type;
    public final String path;
    public final @DrawableRes
    int image;

    public IconDataParcelable(int type, @DrawableRes int img) {
        if(type == IMAGE_FROMFILE) throw new IllegalArgumentException();
        this.type = type;
        this.image = img;
        this.path = null;
    }

    public IconDataParcelable(int type, String path, @DrawableRes int img) {
        if(type == IMAGE_RES) throw new IllegalArgumentException();
        this.type = type;
        this.path = path;
        this.image = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeString(path);
        parcel.writeInt(image);
    }

    public IconDataParcelable(Parcel im) {
        type = im.readInt();
        path = im.readString();
        image = im.readInt();
    }

    public static final Parcelable.Creator<IconDataParcelable> CREATOR =
            new Parcelable.Creator<IconDataParcelable>() {
                public IconDataParcelable createFromParcel(Parcel in) {
                    return new IconDataParcelable(in);
                }

                public IconDataParcelable[] newArray(int size) {
                    return new IconDataParcelable[size];
                }
            };


}
