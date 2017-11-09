package com.example.river.uploadanddownload.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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

        new InitThread(fileInfo).start();
        if(intent.getAction().equals("start")){
            if(fileInfo.isDownloading()){
                task.start(DownloadService.this,fileInfo);
            }else {
                task.stop();
            }

        }
       if(intent.getAction().equals("restart")){
           task.restart(DownloadService.this,fileInfo);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    class InitThread extends Thread{
        private   FileInfo fileInfo;
        public InitThread(  FileInfo fileInfo){
            this.fileInfo = fileInfo;
        }
        @Override
        public void run() {

        }
    }
}
