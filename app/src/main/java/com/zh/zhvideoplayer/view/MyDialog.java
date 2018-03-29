package com.zh.zhvideoplayer.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.zh.zhvideoplayer.R;

/**
 * Created by lybly on 2018/3/21.
 */

public class MyDialog extends Dialog {
    Context mContext;

    public MyDialog(Context context) {
        super(context,R.style.dialog);
        mContext = context;
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.mydialog, null);
        this.setContentView(layout);
    }
}
