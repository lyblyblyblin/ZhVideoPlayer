package com.zh.zhvideoplayer.player;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zh.zhvideoplayer.R;

import java.io.File;
import java.util.ArrayList;

import static com.zh.zhvideoplayer.TTSTool.stopSpeak;
import static com.zh.zhvideoplayer.TTSTool.ttsToSpe;
import static com.zh.zhvideoplayer.TTSTool.ttsiswork;

public class VideoActivity extends AppCompatActivity {
    private ListView mListView;
    private LocalVideoListAdapter videoadapter;
    private boolean first_speaker;//是否去读
    private int num_speaker;//点击的是第几个
    ArrayList<String> filenameArr;
    ArrayList<String> filepathArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filenameArr = (ArrayList) getIntent().getSerializableExtra("filenameArr");
        filepathArr = (ArrayList) getIntent().getSerializableExtra("filepathArr");

        setContentView(R.layout.activity_main);
        first_speaker = false;
        num_speaker = -1;
        videoadapter = new LocalVideoListAdapter(this, filenameArr, filepathArr, ttsiswork);

        mListView = (ListView) findViewById(R.id.lv_localvideo);
        mListView.setAdapter(videoadapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!ttsiswork) {
                    startplayAct(VideoActivity.this, filenameArr.get(position), filepathArr.get(position));
                } else {
                    //第一次点击情况，或第二次点击了其实视频
                    if ((first_speaker == false && num_speaker == -1) ||
                            ((num_speaker != (position - 1)) && first_speaker == true)) {
                        videoadapter.setSelectPosition(position);
                        videoadapter.setSpeakPosition(position);
                        first_speaker = true;
                        num_speaker = position - 1;
                    } else {
                        if (num_speaker == (position - 1) && first_speaker == true) {
                            first_speaker = false;
                            num_speaker = -1;
                            stopSpeak();
                            if ((new File(filepathArr.get(position))).exists()) {
                                startplayAct(VideoActivity.this, filenameArr.get(position), filepathArr.get(position));
                            } else {
                                ttsToSpe(VideoActivity.this, "视频找不到，可能已经被删除");
                                videoadapter.removePosition(position);
                            }
                        }
                    }
                }
            }
        });
    }

    //关闭阅读
    @Override
    protected void onDestroy() {
        if (ttsiswork) stopSpeak();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (ttsiswork) stopSpeak();
        super.onPause();
    }

    private void startplayAct(Context context, String name, String path) {
        Intent intent = new Intent(VideoActivity.this, PlayerActivity.class);
        // 添加了setEmptyView(footerView)后，position-1要减1，防止数组越界
        intent.putExtra("videoname", name);
        intent.putExtra("videopath", path);
        intent.putExtra("videoType", 1);
        startActivity(intent);
    }


}
