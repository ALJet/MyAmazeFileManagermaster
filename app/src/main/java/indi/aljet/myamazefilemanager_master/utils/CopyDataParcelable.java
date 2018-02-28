package indi.aljet.myamazefilemanager_master.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PC-LJL on 2018/1/15.
 */

public class CopyDataParcelable implements Parcelable {
    public final int sourceProgress;

    public final long byteProgress;

    public final int sourceFiles;

    public final long totalSize;

    public final int speedRaw;

    public final boolean completed,move;

    public final String name;


    public CopyDataParcelable(String name, int amountOfSourceFiles, long totalSize, boolean move) {
        this.name = name;
        sourceFiles = amountOfSourceFiles;
        this.totalSize = totalSize;
        this.move = move;

        speedRaw = 0;
        sourceProgress = 0;
        byteProgress = 0;
        completed = false;
    }


    public CopyDataParcelable(String name, int amountOfSourceFiles, int sourceProgress,
                              long totalSize, long byteProgress, int speedRaw, boolean move,
                              boolean completed) {
        this.name = name;
        sourceFiles = amountOfSourceFiles;
        this.sourceProgress = sourceProgress;
        this.totalSize = totalSize;
        this.byteProgress = byteProgress;
        this.speedRaw = speedRaw;
        this.move = move;
        this.completed = completed;
    }


    protected CopyDataParcelable(Parcel in){
        sourceProgress = in.readInt();
        byteProgress = in.readLong();
        sourceFiles = in.readInt();
        totalSize = in.readLong();
        completed = in.readByte() != 0;
        move = in.readByte() != 0;
        name = in.readString();
        speedRaw = in.readInt();

    }


    public static final Creator<CopyDataParcelable>
    CREATOR = new Creator<CopyDataParcelable>() {
        @Override
        public CopyDataParcelable createFromParcel(Parcel parcel) {
            return new CopyDataParcelable(parcel);
        }

        @Override
        public CopyDataParcelable[] newArray(int i) {
            return new CopyDataParcelable[i];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sourceProgress);
        parcel.writeLong(byteProgress);
        parcel.writeInt(sourceFiles);
        parcel.writeLong(totalSize);
        parcel.writeByte((byte) (completed ? 1 : 0));
        parcel.writeByte((byte) (move ? 1 : 0));
        parcel.writeString(name);
        parcel.writeInt(speedRaw);
    }
}
