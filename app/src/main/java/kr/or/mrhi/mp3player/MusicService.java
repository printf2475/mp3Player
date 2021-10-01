package kr.or.mrhi.mp3player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.security.Provider;
import java.util.Timer;
import java.util.TimerTask;

import kr.or.mrhi.mp3player.activity.MusicActivity;

public class MusicService extends Service {
    private int currentStatus;
    public static final int ACTION_MUSIC_SET = 0, ACTION_MUSIC_PLAY = 1,
            ACTION_MUSIC_PAUSE = 2, ACTION_MUSIC_NEXT = 3, ACTION_MUSIC_PREV = 4, ACTION_MUSIC_PROGRESS_CHANGE = 5;

    private MusicData musicData;
    private MusicPLayerController controller;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("서비스", "넘어옴");
        controller=MusicPLayerController.getInstance();
        controller.setContext(this);
        currentStatus = Integer.parseInt(intent.getAction());
        switch (currentStatus) {
            case ACTION_MUSIC_SET:
                musicData= (MusicData) intent.getSerializableExtra("currentMusic");
                controller.setMusicData(musicData);
                controller.initPlayer();
                controller.play();
                break;
            case ACTION_MUSIC_PLAY:
                controller.play();
                break;

            case ACTION_MUSIC_PAUSE:
                controller.pause();
                break;

            case ACTION_MUSIC_NEXT:
                controller.stop();
                controller.next();
                break;

            case ACTION_MUSIC_PREV:
                controller.stop();
                controller.prev();
                break;

            case ACTION_MUSIC_PROGRESS_CHANGE:
                int progressPos = intent.getIntExtra("progressPos", 0);
                controller.setSeekTo(progressPos);
                break;

        }
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
