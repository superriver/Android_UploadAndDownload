package com.example.river.multipletask;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2017/10/20.
 */

public class TaskManager {
    private Map<String,FileInfo>  map = new HashMap<>();
    //private Executor executor = new ThreadPoolExecutor()
    private ExecutorService es = Executors.newFixedThreadPool(3);
    private static final int DEFAULT_THREAD_COUNT = 4;//默认下载线程数
    private boolean isPause;
    public static class TaskHolder{
        private static final TaskManager instance = new TaskManager();
    }
    public static TaskManager getInstance(){
        return TaskHolder.instance;
    }

    private Map<Integer,DownloadTask> tasks = new HashMap<>();
    //新建任务
    public void addTask(int taskId,DownloadTask task){
       tasks.put(taskId,task);
    }

    public DownloadTask getTask(int taskId){
        return tasks.get(taskId);
    }
    //删除任务

    public void removeTask(int taskId){
        tasks.remove(taskId);
    }

    //恢复任务
    public void start(Context context,FileInfo fileInfo){
        if(!map.containsKey(fileInfo.getUrl())){
            map.put(fileInfo.getUrl(),fileInfo);
        }
        DownloadTask task = new DownloadTask(map.get(fileInfo.getUrl()),context);
        Toast.makeText(context, "下载开始", Toast.LENGTH_SHORT).show();
        tasks.put(fileInfo.getId(),task);
        //es.execute(task);
        task.start();
        isPause = false;
    }

    public void stop(){
        isPause =true;
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
