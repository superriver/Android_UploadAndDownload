package com.example.river.uploadanddownload.upload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2017/10/18.
 */

public class UploadService extends Service{
    public static final String ACTION_UPDATE = "update";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TaskManager task =  new TaskManager(UploadService.this);
        FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
        if(intent.getAction().equals("start")){
            if(fileInfo.isDownloading()){
                Log.d("huang","start->");
                task.start(UploadService.this,fileInfo);
            }else {
                Log.d("huang","stop->");
                task.stop();
            }

        }
       if(intent.getAction().equals("restart")){
           task.restart(UploadService.this,fileInfo);
        }

        return super.onStartCommand(intent, flags, startId);
    }



}
