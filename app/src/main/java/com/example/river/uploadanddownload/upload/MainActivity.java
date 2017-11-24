package com.example.river.uploadanddownload.upload;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.river.uploadanddownload.R;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private Button restart;
    private List<String> images = new ArrayList<>();
    private List<Bitmap> bitmapList = new ArrayList<>();
    private NumberProgressBar mProgressBar;
    private FileInfo fileInfo;
    private ProgressBroadcast receiver;
    private TextView textView;
    private DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        restart = (Button) findViewById(R.id.restart);
        mProgressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);
        textView = (TextView) findViewById(R.id.path);
        //fileInfo = checkDB();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadService.class);
                if (fileInfo.isDownloading()) {
                    fileInfo.setPause(true);
                    fileInfo.setDownloading(false);
                    start.setText("继续");
                } else {
                    fileInfo.setDownloading(true);
                    start.setText("暂停");
                }

                intent.setAction("start");
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setText("暂停");
                fileInfo.setDownloading(true);
                Intent intent = new Intent(MainActivity.this, UploadService.class);
                fileInfo.setFinished(0);
                intent.setAction("restart");
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);
            }
        });
        receiver = new ProgressBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ProgressBroadcast");
//注册receiver
        registerReceiver(receiver, filter);
    }

    public class ProgressBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("finished", 0);
            Log.d("huang","progress->"+progress);
            mProgressBar.setProgress(progress);
        }
    }

    public void add(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*;image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    //   Cursor cursor = getContentResolver().query()
//                    String filePath = null;
//                    Uri originalUri = data.getData();
//                    if(originalUri.toString().startsWith("file://")){
//                        filePath = originalUri.getPath();
//                        if(!filePath.endsWith(".mp4")){
//                            Toast.makeText(MainActivity.this, "不支持该格式", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }else {
//
//                    }
                    Uri selectVideo = data.getData();
                    String[] filePath = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectVideo, filePath, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePath[0]);
                    String videoPath = cursor.getString(columnIndex);

                    if(videoPath==null||videoPath.equals("")){
                        textView.setText("不存在路径");
                        return;
                    }
                    textView.setText(videoPath);
                    File file = new File(videoPath);
                    fileInfo = new FileInfo(file.getName(),file.getAbsolutePath());
                    fileInfo.setLen(file.length());
                    Toast.makeText(MainActivity.this, videoPath, Toast.LENGTH_SHORT).show();
                    cursor.close();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getThumbnail() {
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] proj = {MediaStore.Video.Media._ID
                , MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = cr.query(
                uri, proj, MediaStore.Video.Media.MIME_TYPE + "=?",
                new String[]{"video/mp4"}, MediaStore.Video.Media.DATE_MODIFIED + "desc");
        if (cursor.moveToFirst()) {
            int _data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            do {
                String path = cursor.getString(_data);
                int anInt = cursor.getInt(columnIndex);
                images.add(path);
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, anInt, MediaStore.Video.Thumbnails.MINI_KIND, null);
                bitmapList.add(bitmap);

            } while (cursor.moveToNext());

        }
        cursor.close();
        handler.sendEmptyMessage(0);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


}
