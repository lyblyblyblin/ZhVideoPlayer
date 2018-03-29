package com.zh.zhvideoplayer;

import android.content.Context;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import com.zh.zhvideoplayer.player.VideoActivity;

/**
 * Created by lybly on 2018/3/21.
 */

public class TTSTool {
    public static boolean ttsiswork;
    public static TextToSpeech textspeaker;

    private static Handler handler;

    //判断tts是否可用
    public static boolean ttsIsWork(Context context) {
        handler = new Handler();
        ttsiswork = true;
        textspeaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (textspeaker == null) ttsiswork = false;
            }
        });
        return ttsiswork;
    }

    //使用tts转换
    public static void ttsToSpe(Context context, final String ttsString) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //do somthing
                //更新UI
                textspeaker.speak(ttsString, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 50);

    }

    public static void stopSpeak() {
        textspeaker.stop();
    }

    public static void shutdownSpeak() {
        if (textspeaker!=null)textspeaker.shutdown();
    }

    public static void stopHandle(){
        if (handler!=null)handler.removeCallbacksAndMessages(null);
    }
}
