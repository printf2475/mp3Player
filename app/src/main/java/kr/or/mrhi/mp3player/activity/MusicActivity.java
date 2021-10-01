package kr.or.mrhi.mp3player.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_NEXT;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PAUSE;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PLAY;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PREV;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PROGRESS_CHANGE;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_SET;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import kr.or.mrhi.mp3player.MusicData;
import kr.or.mrhi.mp3player.MusicPLayerController;
import kr.or.mrhi.mp3player.MusicService;
import kr.or.mrhi.mp3player.MusicViewModel;
import kr.or.mrhi.mp3player.R;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener , MusicPLayerController.OnChangeProgressPositionListener {

    public static int NOTIFICATION_ID = 1001;
    public static String CHANNEID = "mp3ID";
    Context context = this;
    ImageButton btn_start, btn_pre, btn_next;
    ToggleButton btn_addPlayList;
    int count = 0;
    MediaPlayer mediaPlayer;
    MusicData musicData;
    List<MusicData> list;
    SeekBar seekBar;
    ImageView imageView;
    TextView tv_title, tv_nowTime, tv_totalTime, tv_artist;
    int position, fragmentPos;
    String title, content;
    Intent intentService;

    private MusicViewModel mViewModel;
    private MusicPLayerController controller = MusicPLayerController.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        mViewModel = new ViewModelProvider(this).get(MusicViewModel.class);
        controller.setOnChangeProgressPositionListener(this);
        initView();
        initMusicData();
        initMusic();

        startService(ACTION_MUSIC_SET);
        //setNotifycationBar();

    }


    private void initMusicData() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Long> uriList = mViewModel.getMusicIdList();
        Intent intent = getIntent();
        list = (List<MusicData>) intent.getSerializableExtra("MusicList");
        position = intent.getIntExtra("position", 0);
        fragmentPos = intent.getIntExtra("fragmentPos", 100);
        musicData = list.get(position);
        for (Long id : uriList) {
            Log.e("데이터베이스",id.toString()+musicData.getId().toString() );
            if (musicData.getId().equals(id)) {
                Log.e("데이터베이스", "select 로 값 비교");
                btn_addPlayList.toggle();
            }
        }
        Log.i("데이터", musicData.toString());
        tv_title.setText(musicData.getTitle());
        tv_artist.setText(musicData.getArtist());
        Glide.with(this)
                .load(musicData.getImageuri())
                .error(R.mipmap.music)
                .fitCenter()
                .into(imageView);

        Log.i("데이터", musicData.toString());
    }

    private void initView() {
        btn_start = findViewById(R.id.btn_start);
        btn_pre = findViewById(R.id.btn_pre);
        btn_next = findViewById(R.id.btn_next);
        btn_addPlayList = findViewById(R.id.btn_add_playlist);

        tv_title = findViewById(R.id.tv_titlePlayer);
        tv_nowTime = findViewById(R.id.tv_nowTime);
        tv_totalTime = findViewById(R.id.tv_totalTime);
        tv_artist = findViewById(R.id.tv_artistPlayer);

        seekBar = findViewById(R.id.seekBar);
        imageView = findViewById(R.id.imageView);

        btn_start.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_addPlayList.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);

    }

    private void initMusic() {
        mediaPlayer = MediaPlayer.create(this, musicData.getUri());
        seekBar.setMax(mediaPlayer.getDuration());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                count++;
                if (count % 2 == 1) {
//                    mediaPlayer.pause();
                    btn_start.setImageResource(R.mipmap.play);
                    startService(ACTION_MUSIC_PAUSE);
                } else if (count % 2 == 0) {
//                    mediaPlayer.start();
                    btn_start.setImageResource(R.mipmap.pause);
                    startService(ACTION_MUSIC_PLAY);
                }
                break;

            case R.id.btn_pre:
                startService(ACTION_MUSIC_PREV);
                startMyActivity(-1);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);

                finish();
                break;

            case R.id.btn_next:
                startService(4);
                startMyActivity(ACTION_MUSIC_NEXT);
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
                finish();
                break;

            case R.id.btn_add_playlist:
                if(btn_addPlayList.isChecked()){
                    mViewModel.enterMyPlayList(musicData.getId());
                }else{
                    mViewModel.deleteMyPlayList(musicData.getId());
                }
                break;
        }
    }

    private void  startService(int i){
        intentService = new Intent(this, MusicService.class);
        intentService.setAction(String.valueOf(i));
        intentService.putExtra("currentMusic", musicData);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(intentService);
        }

    }


    private void startMyActivity(int num) {
        Intent prevIntent = new Intent(MusicActivity.this, MusicActivity.class);
        prevIntent.putExtra("MusicList", (ArrayList<MusicData>) list);
        prevIntent.putExtra("position", position + num);
        startActivity(prevIntent);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
        tv_nowTime.setText(timeDataFometer(position));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        startService(ACTION_MUSIC_PAUSE);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        intentService = new Intent(this, MusicService.class);
        intentService.setAction(String.valueOf(ACTION_MUSIC_PROGRESS_CHANGE));
        intentService.putExtra("currentMusic", musicData);
        intentService.putExtra("progressPos", seekBar.getProgress());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentService);
        }
        startService(ACTION_MUSIC_PLAY);
    }


    private String timeDataFometer(int time) {
        int nowTime = time / 1000;
        return String.format("%d : %02d", nowTime / 60, nowTime % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fragmentPos", fragmentPos);
        startActivity(intent);

    }

    private Notification setNotifycationBar(){
        Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
        createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
                getString(R.string.app_name), "App notification channel");

        title = list.get(position).getTitle();
        content = list.get(position).getArtist();


        Intent intent = new Intent(this, MusicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEID);
        builder.setSmallIcon(R.mipmap.music);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setProgress(mediaPlayer.getDuration()/1000, seekBar.getProgress(), false);
        builder.addAction(new NotificationCompat.Action(
                R.mipmap.pause,"prev", pendingIntent));
        builder.addAction(new NotificationCompat.Action(
                R.mipmap.pause,"pause", pendingIntent));
        builder.addAction(new NotificationCompat.Action(
                R.mipmap.pause,"next", pendingIntent));
        //builder.setStyle(mediaStyle.setMediaSession()) // 5

        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.i("노티피케이션" , "동작");

        return builder.build();

    }

    private void createNotificationChannel(Context context, int importance, Boolean showBadge,
                                           String name,String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEID = this.getPackageName();
            NotificationChannel channel = new NotificationChannel(CHANNEID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(showBadge);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }else{
            Log.i("노티피케이션" , "버전안맞음");
        }
    }


    @Override
    public void onChangeProgressPosition(int position, int maxTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(position);
                tv_totalTime.setText(String.valueOf(maxTime));
            }
        });
    }
}