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
import android.app.Service;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kr.or.mrhi.mp3player.MusicData;
import kr.or.mrhi.mp3player.MusicPLayerController;
import kr.or.mrhi.mp3player.MusicService;
import kr.or.mrhi.mp3player.MusicViewModel;
import kr.or.mrhi.mp3player.R;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPLayerController.OnMusicPlayerStatusListener {


    private ImageButton btn_start, btn_pre, btn_next;
    private ToggleButton btn_addPlayList;
    private int btnCount = 0;
    private MusicData musicData;
    private List<MusicData> list;
    private SeekBar seekBar;
    private ImageView imageView;
    private TextView tv_title, tv_nowTime, tv_totalTime, tv_artist;
    private int musicDataPosition, fragmentPos, progressPos, maxTime;
    private Intent intentService;
    private boolean isFirstCheckFlag;

    private MusicViewModel mViewModel;
    private MusicPLayerController controller = MusicPLayerController.getInstance();
    private Long musicId = Long.MAX_VALUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music);
        mViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirstCheckFlag = false;
        controller.setDestroyActiviry(false);
        initMusicData();

        Log.i("데이터", musicId.toString() + ":" + musicData.getId().toString());
        if (!(musicId.equals(musicData.getId())^isFirstCheckFlag)) {
            startMyService(ACTION_MUSIC_SET);
            startMyService(ACTION_MUSIC_PLAY);
        }
    }

    private void initMusicData() {

        List<Long> uriList = mViewModel.getMusicIdList();
        Intent intent = getIntent();
        list = (List<MusicData>) intent.getSerializableExtra("MusicList");
        musicDataPosition = intent.getIntExtra("position", 0);
        fragmentPos = intent.getIntExtra("fragmentPos", 100);
        musicData = list.get(musicDataPosition);
        for (Long id : uriList) {
            Log.e("데이터베이스", id.toString() + musicData.getId().toString());
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
        controller.setOnChangeProgressPositionListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:

                if (btnCount % 2 == 1) {
                    btn_start.setImageResource(R.mipmap.play);
                    startMyService(ACTION_MUSIC_PAUSE);
                } else if (btnCount % 2 == 0) {
                    btn_start.setImageResource(R.mipmap.pause);
                    startMyService(ACTION_MUSIC_PLAY);
                }
                break;

            case R.id.btn_pre:
                startMyService(ACTION_MUSIC_PREV);
                break;

            case R.id.btn_next:
                startMyService(ACTION_MUSIC_NEXT);
                break;

            case R.id.btn_add_playlist:
                if (btn_addPlayList.isChecked()) {
                    mViewModel.enterMyPlayList(musicData.getId());
                } else {
                    mViewModel.deleteMyPlayList(musicData.getId());
                }
                break;
        }
    }

    private void startMyService(String str) {
        intentService = new Intent(this, MusicService.class);
        intentService.setAction(str);
        intentService.putExtra("CurrentMusicPos", musicDataPosition);
        intentService.putExtra("MusicList", (Serializable) list);
        Log.i("데이터", "startService" + musicDataPosition + list.get(musicDataPosition).toString());
        startService(intentService);

    }


    private void startMyActivity(int num) {
        Intent prevIntent = new Intent(MusicActivity.this, MusicActivity.class);
        prevIntent.putExtra("MusicList", (ArrayList<MusicData>) list);
        musicDataPosition += num;
        if (musicDataPosition<0){
            musicDataPosition= list.size()-1;
        }else if (musicDataPosition>list.size()-1){
            musicDataPosition=0;
        }
        prevIntent.putExtra("position", musicDataPosition);
        startActivity(prevIntent);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
        tv_nowTime.setText(timeDataFometer(position));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        startMyService(ACTION_MUSIC_PAUSE);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        intentService = new Intent(this, MusicService.class);
        intentService.setAction(ACTION_MUSIC_PROGRESS_CHANGE);
        intentService.putExtra("progressPos", seekBar.getProgress());
        startService(intentService);

        startMyService(ACTION_MUSIC_PLAY);
    }

    private String timeDataFometer(int time) {
        int nowTime = time / 1000;
        return String.format("%d : %02d", nowTime / 60, nowTime % 60);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstCheckFlag = true;
        if (!controller.getPlaying()) {
            Intent intent = new Intent(this, MusicService.class);
            stopService(intent);
            Log.i("서비스 종료", "종료됨" + controller.getPlaying());
        }
        controller.setDestroyActiviry(true);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fragmentPos", fragmentPos);
        startActivity(intent);

    }

    @Override
    public void onChangeProgressPosition(int progressPosition, int maxTime, Long musicId) {
        this.progressPos = progressPosition;
        this.maxTime = maxTime;
        this.musicId = musicId;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(progressPosition);
                tv_totalTime.setText(String.format("%d : %02d", maxTime / 1000 / 60, maxTime / 1000 % 60));
                seekBar.setMax(maxTime);
            }
        });
    }

    @Override
    public void onChangeStatus(String status) {
        switch (status) {
            case ACTION_MUSIC_PLAY:
                btn_start.setImageResource(R.mipmap.pause);
                btnCount++;
                break;
            case ACTION_MUSIC_PAUSE:
                btnCount++;
                btn_start.setImageResource(R.mipmap.play);
                break;
            case ACTION_MUSIC_PREV:
                startMyActivity(-1);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                finish();
                break;
            case ACTION_MUSIC_NEXT:
                startMyActivity(1);
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
                finish();
                break;

        }

    }
}