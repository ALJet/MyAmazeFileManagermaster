package indi.aljet.myamazefilemanager_master.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PC-LJL on 2018/1/15.
 */

public class ComputerParcelable implements Parcelable {

    public final String addr;
    public final String name;

    public ComputerParcelable(String str,String str2){
        this.name  = str;
        this.addr = str2;
    }

    private ComputerParcelable(Parcel parcel){
        this.name = parcel.readString();
        this.addr = parcel.readString();
    }

    public static final Creator<ComputerParcelable> CREATOR
            = new Creator<ComputerParcelable>() {
        @Override
        public ComputerParcelable createFromParcel(Parcel parcel) {
            return new ComputerParcelable(parcel);
        }

        @Override
        public ComputerParcelable[] newArray(int i) {
            return new ComputerParcelable[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.addr);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]",this.name,this.addr);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ComputerParcelable
                && (this == obj || (this.name.equals
                (((ComputerParcelable) obj).name) && this.addr
                .equals(((ComputerParcelable)obj).addr)));
    }


    @Override
    public int hashCode() {
        return this.name.hashCode() + this.addr
                .hashCode();
    }
}
