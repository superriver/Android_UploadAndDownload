package com.example.river.multipletask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/10/18.
 */

public class DownloadService extends Service{
    public static final String ACTION_UPDATE = "update";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TaskManager task =  TaskManager.getInstance();
        FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
        if(intent.getAction().equals("start")){
            if(fileInfo.isDownloading()){
                task.start(DownloadService.this,fileInfo);
            }else {
                Toast.makeText(DownloadService.this, "暂停", Toast.LENGTH_SHORT).show();
               task.stop();
            }

        }
       if(intent.getAction().equals("restart")){
           task.restart(DownloadService.this,fileInfo);
        }

        return super.onStartCommand(intent, flags, startId);
    }

}
