package indi.aljet.myamazefilemanager_master.adapter.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by PC-LJL on 2018/1/16.
 */

public class CompressedObjectParcelable
        implements Parcelable {

    public static final int TYPE_GOBACK = -1,
    TYPE_NORMAL = 0;

    public final boolean directory;
    public final int type;
    public final String name;
    public final long date,size;

    public CompressedObjectParcelable(String name, long date, long size, boolean directory) {
        this.directory = directory;
        this.type = TYPE_NORMAL;
        this.name = name;
        this.date = date;
        this.size = size;
    }

    /**
     * TYPE_GOBACK instance
     */
    public CompressedObjectParcelable() {
        this.directory = true;
        this.type = TYPE_GOBACK;
        this.name = null;
        this.date = 0;
        this.size = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel p1, int p2) {
        p1.writeInt(type);
        if(type != TYPE_GOBACK) {
            p1.writeInt(directory? 1:0);
            p1.writeString(name);
            p1.writeLong(size);
            p1.writeLong(date);
        }
    }

    public static final Parcelable.Creator<CompressedObjectParcelable> CREATOR =
            new Parcelable.Creator<CompressedObjectParcelable>() {
                public CompressedObjectParcelable createFromParcel(Parcel in) {
                    return new CompressedObjectParcelable(in);
                }

                public CompressedObjectParcelable[] newArray(int size) {
                    return new CompressedObjectParcelable[size];
                }
            };



    private CompressedObjectParcelable(Parcel in){
        type = in.readInt();
        if(type == TYPE_GOBACK){
            directory = true;
            name = null;
            date = 0;
            size  = 0;
        }else{
            directory = in.readInt() == 1;
            name = in.readString();
            size = in.readLong();
            date = in.readLong();
        }
    }


    public static class Sorter implements
            Comparator<CompressedObjectParcelable>{

        @Override
        public int compare
                (CompressedObjectParcelable
                         compressedObjectParcelable,
                 CompressedObjectParcelable t1) {
            if(compressedObjectParcelable.type ==
                    CompressedObjectParcelable.TYPE_GOBACK){
                return -1;
            }else if(t1.type == CompressedObjectParcelable
                    .TYPE_GOBACK){
                return 1;
            }else if(compressedObjectParcelable.directory &&
                    !t1.directory){
                return -1;
            }else if(t1.directory && (compressedObjectParcelable)
                    .directory){
                return 1;
            }else {
                return compressedObjectParcelable
                        .name.compareToIgnoreCase(t1.name);
            }

        }
    }

}
