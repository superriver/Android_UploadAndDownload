package com.example.river.uploadanddownload.download;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/20.
 */

public class TaskManager {
    private Map<String,FileInfo>  map = new HashMap<>();
    private boolean isPause;
    public static boolean isCancel;
    public static class TaskHolder{
        private static final TaskManager instance = new TaskManager();
    }
    public static TaskManager getInstance(){
        return TaskHolder.instance;
    }


    //恢复任务
    public void start(Context context,FileInfo fileInfo){
        if(map.get(fileInfo.getUrl())==null){
            map.put(fileInfo.getUrl(),fileInfo);
        }
        DownloadTask task = new DownloadTask(map.get(fileInfo.getUrl()),context);
        isPause = false;
        task.start();
    }

    public void stop(){
        isPause =true;
    }
    public void cancel(){
        isCancel = true;
    }
    public void restart(Context context,FileInfo fileInfo){
        try {
            map.clear();
            File file = new File(DownloadTask.FILE_PATH,fileInfo.getFileName());
            if (file.exists()){
                file.delete();
            }
            Thread.sleep(100);
        }catch (Exception e){
            return;
        }
        start(context,fileInfo);
    }
    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

}
