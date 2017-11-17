package com.example.river.uploadanddownload.upload;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/20.
 */

public class TaskManager {
    private Map<String,FileInfo>  map = new HashMap<>();
    private static final String requestPath = "";
    private boolean isPause;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    public TaskManager(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
        db =dbHelper.getReadableDatabase();
    }

    private LinkedList<UploadTask> tasks = new LinkedList<>();
    //添加任务
    public void addTask(UploadTask task){
        tasks.add(task);
    }

    public LinkedList<UploadTask> getTasks(){
        return tasks;
    }
    //删除任务


    //恢复任务
    public void start(Context context,FileInfo fileInfo){
        new InitThread(fileInfo).start();
        UploadThread uploadThread = new UploadThread(fileInfo,context);
        UploadTask uploadTask = new UploadTask(uploadThread);
        uploadTask.download();
    }

    public void stop(){
        isPause =true;
    }
    public void restart(Context context,FileInfo fileInfo){
        try {
            map.clear();
            File file = new File(UploadThread.FILE_PATH,fileInfo.getFileName());
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
              //  UploadThread uploadThread = new UploadThread("aa",context);
//                UploadTask uploadTask = new UploadTask(uploadThread);
//                uploadTask.download();
            }
        }
    };

    class InitThread extends Thread{
        private FileInfo fileInfo;
        public InitThread(  FileInfo fileInfo){
            this.fileInfo = fileInfo;
        }
        @Override
        public void run() {
            if(fileInfo!=null){
                dbHelper.insert(db,fileInfo);

            }
            handler.obtainMessage(1).sendToTarget();

        }
    }
}
