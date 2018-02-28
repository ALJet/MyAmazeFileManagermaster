package indi.aljet.myamazefilemanager_master.utils;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import indi.aljet.myamazefilemanager_master.R;
import indi.aljet.myamazefilemanager_master.ui.notifications.NotificationConstants;

/**
 * Created by PC-LJL on 2018/1/18.
 */

public class ServiceWatcherUtil {

    private Handler handler;
    private static HandlerThread handlerThread;
    private ProgressHandler progressHandler;
    long totalSize;
    private Runnable runnable;

    private static ArrayList<Intent> pendingIntents =
            new ArrayList<>();

    public static long POSITION = 0L;

    public static int HAULT_COUNTER = -1;
    public static final int ID_NOTIFICATION_WAIT = 9248 ;


    public ServiceWatcherUtil(ProgressHandler progressHandler, long totalSize) {
        this.progressHandler = progressHandler;
        this.totalSize = totalSize;
        POSITION = 0L;
        HAULT_COUNTER = -1;

        handlerThread = new HandlerThread("service_progress_watcher");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }


    public void watch(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if(progressHandler.getFileName() == null){
                    handler.postDelayed(this,1000);
                    progressHandler.addWrittenLength(POSITION);
                    if(POSITION == totalSize ||
                            progressHandler.getCancelled()){
                        handler.removeCallbacks(this);
                        handlerThread.quit();
                        return;
                    }
                    if(POSITION == progressHandler
                            .getWrittenSize()){
                        HAULT_COUNTER++;
                        if(HAULT_COUNTER > 10){
                            progressHandler.addWrittenLength(totalSize);
                            handler.removeCallbacks(this);
                            handlerThread.quit();
                            return;
                        }
                    }
                    handler.postDelayed(this,1000);
                }
            }
        };
        handler.postDelayed(runnable,1000);
    }


    public void stopWatch(){
        if(handlerThread.isAlive())
            handler.post(runnable);
    }


    public static synchronized void runService(final
                                               Context context,
                                               final Intent intent){
        if(pendingIntents.size() == 0){
            init(context);
        }
        pendingIntents.add(intent);
    }


    public static synchronized void init(final Context context){
        final HandlerThread waitingThread = new
                HandlerThread("service_startup_watcher");
        waitingThread.start();
        final Handler handler = new Handler(waitingThread
        .getLooper());
        final NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new
                NotificationCompat.Builder(context,
                NotificationConstants.CHANNEL_NORMAL_ID)
                .setContentTitle(context
                .getString(R.string.waiting_title))
                .setContentText(context.getString(R.string
                .waiting_content))
                .setAutoCancel(false)
                .setSmallIcon(R.drawable
                .ic_all_inclusive_white_36dp)
                .setProgress(0,0,true);
        NotificationConstants.setMetadata(context, mBuilder);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(handlerThread == null ||
                        !handlerThread.isAlive()){
                    context.startService(pendingIntents
                    .remove(pendingIntents.size() -1));
                    if(pendingIntents.size() == 0){
                        notificationManager.cancel(ID_NOTIFICATION_WAIT);
                        handler.removeCallbacks(this);
                        waitingThread.quit();
                        return;
                    }else{
                        notificationManager.notify(ID_NOTIFICATION_WAIT,
                                mBuilder.build());
                    }
                }
                handler.postDelayed(this,5000);
            }
        };
        handler.postDelayed(runnable,0);
    }



}
