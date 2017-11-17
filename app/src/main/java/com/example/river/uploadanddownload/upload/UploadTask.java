package com.example.river.uploadanddownload.upload;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/10/19.
 */

public class UploadTask {
    private UploadThread uploadThread;
    public UploadTask(UploadThread uploadThread){
        this.uploadThread = uploadThread;
    }

    public void download(){
        uploadThread.start();
    }




}
