package com.example.river.multipletask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private String[] urls = {
            "http://www.21yey.com/clientdownload/android/family.apk", "http://www.21yey.com/clientdownload/android/kindergarten.apk", "http://www.21yey.com/clientdownload/android/kindergartenPresident.apk"};
    private Map<String, FileInfo> map = new HashMap<>();
    private ListView lv;

    private int id = 0;
    private ProgressBroadcast receiver;
    private List<FileInfo> fileInfos = new ArrayList<>();
    private FileAdapter adapter;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.list_item);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        //  toolbar.setM
        //initData();
        // fileInfo = checkDB();

        adapter = new FileAdapter(MainActivity.this);
        receiver = new ProgressBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ProgressBroadcast");
//注册receiver
        registerReceiver(receiver, filter);

    }


    public void add(View view) {
        editText = new EditText(MainActivity.this);
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("添加任务")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = editText.getText().toString().trim();
                        if(checkTask(url)){
                            Toast.makeText(MainActivity.this, "任务已存在", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FileInfo info = new FileInfo(id++, url.substring(url.lastIndexOf("/") + 1), url);
                        info.setDownloading(true);
                        fileInfos.add(info);
                        map.put(url, info);
                        lv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Intent intent = new Intent(MainActivity.this, DownloadService.class);
                        intent.setAction("start");
                        intent.putExtra("fileInfo", info);
                        startService(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();

    }


    public boolean checkTask(String url) {
        return  map.containsKey(url);
    }

    public class FileAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public FileAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return fileInfos.size();
        }

        @Override
        public FileInfo getItem(int position) {
            return fileInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            final FileInfo fileInfo = fileInfos.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_view, parent, false);
                holder = new ViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.start = (Button) convertView.findViewById(R.id.start);
                holder.restart = (Button) convertView.findViewById(R.id.restart);
                holder.delete = (Button) convertView.findViewById(R.id.delete);
                holder.progressBar = (NumberProgressBar) convertView.findViewById(R.id.number_progress_bar);
                holder.fileName.setText(fileInfos.get(position).getFileName());
                holder.progressBar.setProgress(fileInfos.get(position).getFinished());
                if (fileInfo.isDownloading()) {
                    holder.start.setText("暂停");
                } else {
                    holder.start.setText("继续");
                }

                holder.start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (fileInfo.isDownloading()) {
                            fileInfo.setPause(true);
                            fileInfo.setDownloading(false);
                            holder.start.setText("继续");
                        } else {
                            fileInfo.setDownloading(true);
                            holder.start.setText("暂停");
                        }
                        Intent intent = new Intent(MainActivity.this, DownloadService.class);
                        intent.setAction("start");
                        intent.putExtra("fileInfo", fileInfo);
                        startService(intent);
                    }
                });
                holder.restart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.start.setText("暂停");
                        fileInfo.setDownloading(true);
                        Intent intent = new Intent(MainActivity.this, DownloadService.class);
                        fileInfo.setFinished(0);
                        intent.setAction("restart");
                        intent.putExtra("fileInfo", fileInfo);
                        startService(intent);
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileInfos.remove(position);
                        notifyDataSetChanged();

                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.progressBar.setProgress(fileInfos.get(position).getFinished());
            return convertView;
        }

        public void updateProgress(int id, int progress) {
            FileInfo info = fileInfos.get(id);
            info.setFinished(progress);
            notifyDataSetChanged();

        }

        class ViewHolder {
            TextView fileName;
            Button start;
            Button restart;
            Button delete;
            NumberProgressBar progressBar;
        }
    }

    public class ProgressBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int finished = intent.getIntExtra("finished", 0);
            int id = intent.getIntExtra("id", 0);
            adapter.updateProgress(id, finished);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    // private FileInfo checkDB() {
//        DBHelper dbHelper = new DBHelper(MainActivity.this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        fileInfo = dbHelper.queryData(db, "http://183.230.81.12/cache/www.21yey.com/clientdownload/android/family.apk?ich_args2=91-26160505055589_e397871097a3544eecf5cc68b13fd45e_10001002_9c896425d7c3f4d0953d518939a83798_11e53421bdc95c93f9bfa93fadb17663");
//        if (fileInfo.getFinished() > 0) {
//            //mProgressBar.setProgress(fileInfo.getFinished() * 100 / fileInfo.getLen());
//            //start.setText("继续");
//        } else {
//            fileInfo = new FileInfo("family.apk", "http://183.230.81.12/cache/www.21yey.com/clientdownload/android/family.apk?ich_args2=91-26160505055589_e397871097a3544eecf5cc68b13fd45e_10001002_9c896425d7c3f4d0953d518939a83798_11e53421bdc95c93f9bfa93fadb17663");
//
//        }
//        return fileInfo;

    // }
}
