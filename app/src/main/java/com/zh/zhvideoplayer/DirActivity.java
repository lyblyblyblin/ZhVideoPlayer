package com.zh.zhvideoplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.zh.zhvideoplayer.Data.DatabaseHelper;
import com.zh.zhvideoplayer.file.GetVideoAsync;
import com.zh.zhvideoplayer.file.ShowLoading;
import com.zh.zhvideoplayer.permisson.PermissionHelper;
import com.zh.zhvideoplayer.permisson.PermissionInterface;
import com.zh.zhvideoplayer.player.VideoActivity;
import com.zh.zhvideoplayer.view.MyDialog;

import java.io.File;
import java.util.ArrayList;


import static com.zh.zhvideoplayer.TTSTool.shutdownSpeak;
import static com.zh.zhvideoplayer.TTSTool.stopHandle;
import static com.zh.zhvideoplayer.TTSTool.ttsIsWork;
import static com.zh.zhvideoplayer.TTSTool.ttsToSpe;
import static com.zh.zhvideoplayer.TTSTool.ttsiswork;

public class DirActivity extends AppCompatActivity implements PermissionInterface, ShowLoading.OnSuccessListener,GetVideoAsync.OnSuccessListener {
    private static ListView lv_Dir;
    private ShowLoading showLoading;
    private GetVideoAsync getVideoAsync;

    @Override
    public int getPermissionsRequestCode() {
        return 200;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("用户同意权限", "user granted the permission!");
                    requestPermissionsSuccess();
                } else {
                    // permission denied, boo! Disable the
                    // f用户不同意 可以给一些友好的提示
                    Log.i("用户不同意权限", "user denied the permission!");
                    requestPermissionsFail();
                }
                return;
            }

        }
    }

    @Override
    public void requestPermissionsSuccess() {


    }

    @Override
    public void requestPermissionsFail() {
        finish();
    }


    private DatabaseHelper dbHelp;
    public static SQLiteDatabase olddb;
    public static SQLiteDatabase newdb;
    File fileOldDB;
    File fileNewDB;
    private ArrayList<String> parentdir,// /storage/sdcard1/Download
            currentdir,                 // Download
            filepathArr,             // /storage/sdcard1/Download/中国山寨机无可超越的史诗传说！无法匹敌的国产神话！.flv
            filenameArr;            //中国山寨机无可超越的史诗传说！无法匹敌的国产神话！.flv
    Cursor cursor;//用于遍历数据库
    private PermissionHelper mPermissionHelper;

    public static MyDialog dialog;
    private static String DB_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newdbOK=false;
        DB_PATH = "/data/data/" + this.getPackageName() + "/databases/";
        fileOldDB = new File(DB_PATH + "old.db");
        fileNewDB = new File(DB_PATH + "new.db");
        //防止错误
        fileNewDB.delete();
        setContentView(R.layout.activity_dir);
        if (fileOldDB.exists()) {
            Log.d("old.db ", "have");
            olddb = SQLiteDatabase.openOrCreateDatabase(fileOldDB, null);
            onSuccess();
            dbHelp = new DatabaseHelper(DirActivity.this, "new.db", null, 1);
            newdb = dbHelp.getWritableDatabase();
            getVideoAsync = new GetVideoAsync();
            getVideoAsync.setOnSuccessListener(this);
            getVideoAsync.execute(DirActivity.this.getApplication());
        } else {
            dialog = new MyDialog(DirActivity.this);
            dbHelp = new DatabaseHelper(DirActivity.this, "old.db", null, 1);
            olddb = dbHelp.getWritableDatabase();
            //初始化并发起权限申请
            mPermissionHelper = new PermissionHelper(this, this);
            mPermissionHelper.requestPermissions();

            showLoading = new ShowLoading();
            showLoading.setOnSuccessListener(this);
            showLoading.execute(DirActivity.this.getApplication());
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandle();
        olddb.close();
        shutdownSpeak();

        if (fileNewDB.exists() && newdbOK) {
            fileOldDB.delete();
            fileNewDB.renameTo(fileOldDB);
        }
    }
    private boolean newdbOK;
    @Override
    public void onSuccess() {

        currentdir = new ArrayList<>();
        parentdir = new ArrayList<>();
        filepathArr = new ArrayList<>();
        filenameArr = new ArrayList<>();
        //遍历数据库 dir部分
        cursor = olddb.rawQuery("select * from dirinfo", null);
        if (cursor.moveToFirst()) {
            do {
                currentdir.add(cursor.getString(cursor.getColumnIndex("currentdir")));
                parentdir.add(cursor.getString(cursor.getColumnIndex("parentdir")));
            } while (cursor.moveToNext());
        }
        ttsIsWork(DirActivity.this);

        if (currentdir.size() == 0) {
            if (ttsiswork) {
                ttsToSpe(DirActivity.this, "找不到视频,将自动退出");
            } else {
                Toast.makeText(DirActivity.this, "找不到视频,将自动退出", Toast.LENGTH_LONG).show();
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //do somthing
                    stopHandle();
                    finish();
                }
            }, 9000);
        } else {
            lv_Dir = (ListView) findViewById(R.id.lv_localvideo);
            lv_Dir.setVisibility(View.VISIBLE);
            //使用系统默认的listview
            ArrayAdapter<String> diradapter = new ArrayAdapter<String>(DirActivity.this,
                    android.R.layout.simple_expandable_list_item_1,
                    currentdir
            );
            lv_Dir.setAdapter(diradapter);
            lv_Dir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(DirActivity.this, parentdir.get(position), Toast.LENGTH_LONG).show();
                    filenameArr.clear();
                    filepathArr.clear();
                    //遍历数据库 video部分
                    cursor = olddb.rawQuery("select * from videoinfo where parentdir like '" + parentdir.get(position) + "'", null);
                    if (cursor.moveToFirst()) {
                        do {
                            filepathArr.add(cursor.getString(cursor.getColumnIndex("filepath")));
                            filenameArr.add(cursor.getString(cursor.getColumnIndex("filename")));
                        } while (cursor.moveToNext());
                    }
                    Intent intent = new Intent(DirActivity.this, VideoActivity.class);
                    intent.putExtra("filenameArr", filenameArr);
                    intent.putExtra("filepathArr", filepathArr);
                    startActivity(intent);

                }
            });
        }
    }


    @Override
    public void newDBonSuccess() {
        newdbOK=true;
    }
}
