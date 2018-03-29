package com.zh.zhvideoplayer.file;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.vov.vitamio.utils.Log.TAG;

/**
 * Created by lybly on 2018/3/18.
 */

public class FindVideo {


    public static void getFileDir(String infilePath, SQLiteDatabase db) {
        String  filename, filepath, parentdir, currentDir;
        StringBuilder  dirinfostr=new StringBuilder(1000);
        StringBuilder tmp=new StringBuilder(1000);
        StringBuilder videoinfostr=new StringBuilder(1000);

        File f = new File(infilePath);
        File[] files = f.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    getFileDir(file.getPath(), db);
                } else {
                    if (checkVideo(file.getName())) {
                        //文件名  中国山寨机无可超越的史诗传说！无法匹敌的国产神话！.flv
                        filename = file.getName();
                        //文件路径  /storage/sdcard1/Download/中国山寨机无可超越的史诗传说！无法匹敌的国产神话！.flv
                        filepath = file.getPath();
                        //父路径   /storage/sdcard1/Download
                        parentdir = file.getParent().toString();
                        //当前路径   Download
                        currentDir = file.toString().substring(f.toString().lastIndexOf("/") + 1, f.toString().length());
                        tmp.setLength(0);
                        videoinfostr.setLength(0);
                        tmp.append("insert into dirinfo(parentdir,currentdir)values(\"" + parentdir + "\",\"" + currentDir + "\")");
                        if (!tmp.equals(dirinfostr)) {
                            dirinfostr = tmp;
                            db.execSQL(dirinfostr.toString());
                        }
                        videoinfostr.append("insert into videoinfo(filepath,filename,parentdir)values(\"" + filepath + "\",\"" + filename + "\",\"" + parentdir + "\")") ;
//                        Log.d(TAG, "getFileDir: "+videoinfostr.length());
                        db.execSQL(videoinfostr.toString());
                    }
                }
            }
        }
    }

    private static String[] suffixname = {"3gp", "amv", "flv", "mp4", "wmv", "rmvb", "mkv","avi","f4v"};

    //判断后缀名
    private static boolean checkVideo(String path) {
        for (int i = 0; i < suffixname.length; i++) {
            if ((path.substring(path.lastIndexOf(".") + 1).equals(suffixname[i]))) {
                return true;
            }
            continue;
        }
        return false;
    }

    //获取内置或外置存储；路径
    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
