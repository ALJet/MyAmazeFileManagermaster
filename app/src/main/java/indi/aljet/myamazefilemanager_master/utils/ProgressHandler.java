package indi.aljet.myamazefilemanager_master.utils;

/**
 * Created by PC-LJL on 2018/1/18.
 */

public class ProgressHandler {

    long totalSize = 0L;

    long writtenSize = 0L;

    int sourceFiles = 0;

    int sourceFilesProcessed = 0;

    String fileName;

    int speedRaw = 0;

    private boolean isCancelled = false;

    ProgressListener progressListener;


    public ProgressHandler( int sourceFiles,long totalSize) {
        this.totalSize = totalSize;
        this.sourceFiles = sourceFiles;
    }


    public synchronized void addWrittenLength(long newPosition){
        this.speedRaw = (int) (newPosition - writtenSize);
        this.writtenSize = newPosition;
        progressListener.onProgressed(fileName,
                sourceFiles,sourceFilesProcessed,
                totalSize,writtenSize,speedRaw);
    }



    public synchronized void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public synchronized String getFileName() {
        return this.fileName;
    }

    public synchronized void setSourceFilesProcessed(int sourceFilesProcessed) {
        this.sourceFilesProcessed = sourceFilesProcessed;
    }

    // dynamically setting total size, useful in case files are compressed
    public synchronized void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public synchronized void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public synchronized boolean getCancelled() {
        return this.isCancelled;
    }

    public synchronized long getWrittenSize() {
        return writtenSize;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public interface ProgressListener{

        void onProgressed(String fileName,
                          int sourceFiles,
                          int sourceProgress,
                          long totalSize,
                          long writtenSize,
                          int speed);

    }

}
