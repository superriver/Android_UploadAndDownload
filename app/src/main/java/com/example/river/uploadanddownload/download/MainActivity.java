package com.example.river.uploadanddownload.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.river.uploadanddownload.R;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private Button restart;

    private NumberProgressBar mProgressBar;
    private FileInfo fileInfo;
    private ProgressBroadcast receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        restart = (Button) findViewById(R.id.restart);
        mProgressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);

        fileInfo = checkDB();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
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
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
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
            mProgressBar.setProgress(progress);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private FileInfo checkDB(){
         DBHelper dbHelper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
         fileInfo= dbHelper.queryData(db,"http://183.230.81.12/cache/www.21yey.com/clientdownload/android/family.apk?ich_args2=91-26160505055589_e397871097a3544eecf5cc68b13fd45e_10001002_9c896425d7c3f4d0953d518939a83798_11e53421bdc95c93f9bfa93fadb17663");
            if (fileInfo.getFinished() > 0) {
                mProgressBar.setProgress(fileInfo.getFinished() * 100 / fileInfo.getLen());
                start.setText("继续");
            }else {
                fileInfo = new FileInfo("family.apk", "http://183.230.81.12/cache/www.21yey.com/clientdownload/android/family.apk?ich_args2=91-26160505055589_e397871097a3544eecf5cc68b13fd45e_10001002_9c896425d7c3f4d0953d518939a83798_11e53421bdc95c93f9bfa93fadb17663");

            }
        return fileInfo;

    }
}
