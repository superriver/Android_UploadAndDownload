package com.example.river.uploadanddownload.download;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/10/18.
 */

public class DownloadService extends IntentService{
    public static final String ACTION_START = "start";
    public static final String ACTION_RESTART = "restart";
    public static final String ACTION_UPDATE = "update";

    public DownloadService() {
        super("download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TaskManager task =  TaskManager.getInstance();
        FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
        if(intent.getAction().equals(ACTION_START)){
            if(fileInfo.isDownloading()){
                task.start(DownloadService.this,fileInfo);
            }else {
                task.stop();
            }
        }
        if(intent.getAction().equals(ACTION_RESTART)){
            task.restart(DownloadService.this,fileInfo);
        }

    }


}
