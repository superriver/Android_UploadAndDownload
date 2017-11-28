package com.example.river.uploadanddownload.upload;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.example.river.uploadanddownload.download.*;
import com.example.river.uploadanddownload.download.TaskManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Administrator on 2017/11/9.
 */
public class UploadThread extends Thread {
    public static String FILE_PATH = Environment.getExternalStorageDirectory() + "/river";//文件下载保存路径
    public static final  String BROADCAST_ACTION = "broadcast";
    private Context context;
    private DBManager dbManager;
    private String uploadPath;
    private int finished = 0;//当前已下载完成的进度
    private FileInfo fileInfo;
    public static boolean isPause;

    public UploadThread( FileInfo fileInfo,Context context,DBHelper dbHelper){
        this.fileInfo = fileInfo;
        this.context = context;
        dbManager = new DBManager(dbHelper);
    }

    @Override
    public void run() {
                    try{
                        //mProgressBar.setProgress((int) fileInfo.getLen());
                        String sourceid =dbManager.getBindId(fileInfo);
                        String head= "Content-Length="+fileInfo.getLen()+";filename="+fileInfo.getFileName()+";sourceid="+
                                (sourceid==null?"":sourceid)+"\r\n";
                        Socket socket = new Socket("192.168.1.29",7878);
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(head.getBytes());

                        PushbackInputStream pb = new PushbackInputStream(socket.getInputStream());
                        String response = StreamTool.readLine(pb);
                        String[] items = response.split(";");
                        String responseid = items[0].substring(items[0].indexOf("=")+1);
                        String position = items[1].substring(items[1].indexOf("=")+1);
                        if(sourceid==null){
                            fileInfo.setSourceId(responseid);
                            dbManager.save(fileInfo);
                        }
                        File uploadfile = new File(fileInfo.getUrl());
                        RandomAccessFile fileOutStream = new RandomAccessFile(uploadfile,"r");
                        fileOutStream.seek(Integer.valueOf(position));
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        long time = System.currentTimeMillis();
                        long length = Integer.valueOf(position);
                        long fileLen = fileInfo.getLen();

                        while (!isPause&&(len=fileOutStream.read(buffer))!=-1){
                            Log.d("huang","-->"+isPause);
                            outputStream.write(buffer,0,len);
                            length+=len;
//                            if(System.currentTimeMillis()-time>500) {
//
//                            }
//                            time = System.currentTimeMillis();
                            Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                            int progress = Math.round (length * 100 /fileLen );
                            intent.putExtra("finished", progress);
                            intent.setAction("android.intent.action.ProgressBroadcast");
                            context.sendBroadcast(intent);

                        }
                        fileOutStream.close();
                        outputStream.close();
                        pb.close();
                        socket.close();

                    }catch (Exception e){
                        System.err.print(e.getMessage());
                        e.fillInStackTrace();
                    }
    }
}
