package kr.or.mrhi.mp3player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.target.NotificationTarget;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.or.mrhi.mp3player.activity.MusicActivity;

public class MusicService extends Service {
    private String currentStatus;
    public static final String ACTION_MUSIC_SET = "SET", ACTION_MUSIC_PLAY = "PLAY",
            ACTION_MUSIC_PAUSE = "PAUSE", ACTION_MUSIC_NEXT = "NEXT", ACTION_MUSIC_PREV = "PREV",
            ACTION_MUSIC_PROGRESS_CHANGE = "CHANGE", ACTION_STOP_MUSIC_SERVICE = "STOP";

    public static int NOTIFICATION_ID = 1001;
    public static String CHANNEID = "mp3ID";

    private String title, content;
    private List<MusicData> musicDataList;
    private MusicPLayerController controller;
    private NotificationManager notificationManager;
    private int musicDataPosition;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("서비스", "넘어옴");
        controller = MusicPLayerController.getInstance();
        controller.setContext(this);
        currentStatus = intent.getAction();
        switch (currentStatus) {
            case ACTION_MUSIC_SET:
                currentStatus = ACTION_MUSIC_PLAY;
                musicDataList = (List<MusicData>) intent.getSerializableExtra("MusicList");
                musicDataPosition = intent.getIntExtra("CurrentMusicPos", 1000);

                Log.i("데이터", "startService" +musicDataPosition + musicDataList.get(musicDataPosition).toString());
                Log.i("서비스", "서비스 들어옴" +musicDataPosition);
                controller.setMusicDataList(musicDataList);
                controller.setPosition(musicDataPosition);
                controller.initPlayer();

                break;
            case ACTION_MUSIC_PLAY:
                controller.play();
                currentStatus = ACTION_MUSIC_PLAY;
                break;

            case ACTION_MUSIC_PAUSE:
                controller.pause();

                currentStatus = ACTION_MUSIC_PAUSE;
                break;

            case ACTION_MUSIC_NEXT:
                controller.next();
                break;

            case ACTION_MUSIC_PREV:
                controller.prev();
                break;

            case ACTION_MUSIC_PROGRESS_CHANGE:
                int progressPos = intent.getIntExtra("progressPos", 0);
                controller.setSeekTo(progressPos);
                break;

            case ACTION_STOP_MUSIC_SERVICE:
                Intent stopService = new Intent(this, MusicService.class);
                stopService(stopService);
                break;

        }
        startForeground(NOTIFICATION_ID, setNotifycationBar());
        return START_NOT_STICKY;
    }


    private Notification setNotifycationBar() {
        createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_LOW, false, getString(R.string.app_name), "App notification channel");

        Intent intent = new Intent(this, MusicActivity.class);
        intent.putExtra("MusicList", (ArrayList<MusicData>) musicDataList);
        intent.putExtra("position", musicDataPosition);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.i("데이터", "startService" +musicDataPosition + musicDataList.get(musicDataPosition).toString());

        title = musicDataList.get(musicDataPosition).getTitle();
        content = musicDataList.get(musicDataPosition).getArtist();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEID);
        builder.setSmallIcon(R.mipmap.music);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.addAction(makeBtnNotifiCation(ACTION_MUSIC_PREV));
        builder.addAction(makeBtnNotifiCation(currentStatus));
        builder.addAction(makeBtnNotifiCation(ACTION_MUSIC_NEXT));

        builder.setContentIntent(PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);

        return builder.build();

    }

    private NotificationCompat.Action makeBtnNotifiCation(String currentStatus) {
        String btnTitle = null;

        switch (currentStatus) {
            case ACTION_MUSIC_PLAY:
                btnTitle = ACTION_MUSIC_PAUSE;
                break;
            case ACTION_MUSIC_PAUSE:
                btnTitle = ACTION_MUSIC_PLAY;
                break;

            case ACTION_MUSIC_NEXT:
                btnTitle = ACTION_MUSIC_NEXT;
                break;
            case ACTION_MUSIC_PREV:
                btnTitle = ACTION_MUSIC_PREV;
                break;

        }

        Log.i("데이터", "startService" +musicDataPosition + musicDataList.get(musicDataPosition).toString());
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("MusicList", (ArrayList<MusicData>) musicDataList);
        intent.putExtra("position", musicDataPosition);
        intent.setAction(btnTitle);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);


        NotificationCompat.Action notification = new NotificationCompat.Action.Builder(0, btnTitle, pendingIntent).build();

        return notification;
    }

    private void createNotificationChannel(Context context, int importance, Boolean showBadge,
                                           String name, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEID = this.getPackageName();
            NotificationChannel channel = new NotificationChannel(CHANNEID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(showBadge);

            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancelAll();
        Log.i("서비스", "서비스 죽음");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
