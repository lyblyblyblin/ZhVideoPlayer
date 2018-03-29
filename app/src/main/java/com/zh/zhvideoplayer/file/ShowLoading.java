package com.zh.zhvideoplayer.file;

import android.content.Context;
import android.os.AsyncTask;
import static com.zh.zhvideoplayer.DirActivity.olddb;
import static com.zh.zhvideoplayer.DirActivity.dialog;
import static com.zh.zhvideoplayer.file.FindVideo.getFileDir;
import static com.zh.zhvideoplayer.file.FindVideo.getStoragePath;

/**
 * Created by lybly on 2018/3/21.
 */

public class ShowLoading extends AsyncTask<Object, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Object... objects) {
        Context context = (Context) objects[0];

        //可以遍历内置存储下的external_sd
        String rempath = getStoragePath(context, true);//外置存储
        String norempath = getStoragePath(context, false);//内置存储
        if (rempath != null) getFileDir(rempath, olddb);
        if (norempath != null) getFileDir(norempath, olddb);
        return true;
    }

    @Override
    protected void onPreExecute() {

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.cancel();
        this.onSuccessListener.onSuccess();
        super.onPostExecute(aBoolean);
    }

    //创建接口，成功时候回调
    private OnSuccessListener onSuccessListener;

    public interface OnSuccessListener {
        void onSuccess();
    }

    public void setOnSuccessListener(OnSuccessListener onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
    }
}