package indi.aljet.myamazefilemanager_master.asynchronous.ftpservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by PC-LJL on 2018/1/31.
 */

public class FTPReceiver extends BroadcastReceiver {

    static final String TAG = FTPReceiver.class
            .getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG,"Received: " + intent.getAction());
        try{
            Intent service = new Intent(context,
                    FTPService.class);
            service.putExtras(intent);
            if(intent.getAction().equals(FTPService.
            ACTION_START_FTPSERVER) &&
                    !FTPService.isRunning()){
                context.startService(service);
            }else if(intent.getAction().equals(FTPService
            .ACTION_STOP_FTPSERVER)){
                context.stopService(service);

            }
        }catch (Exception e){
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }
}
