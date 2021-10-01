package kr.or.mrhi.mp3player.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

import kr.or.mrhi.mp3player.MusicViewModel;
import kr.or.mrhi.mp3player.R;
import kr.or.mrhi.mp3player.databinding.MainActivityBinding;
import kr.or.mrhi.mp3player.fragment.FragmentMain;
import kr.or.mrhi.mp3player.fragment.FragmentMyPlayList;
import kr.or.mrhi.mp3player.fragment.FragmentSearchMusic;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private MainActivityBinding binding;
    private int fragmentPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                MODE_PRIVATE);

        setFragment();
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        binding.bottomNav.setOnItemSelectedListener(this);
    }

    private void setFragment() {
        Intent intent = getIntent();
        fragmentPos = intent.getIntExtra("fragmentPos", 100);
        if (fragmentPos!=100 && fragmentPos==1||fragmentPos==100||intent==null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, FragmentMain.newInstance())
                    .commitNow();
        } else if (fragmentPos!=100 && fragmentPos==2) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, new FragmentMyPlayList())
                    .commitNow();
        }else if (fragmentPos!=100 && fragmentPos==3) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, new FragmentSearchMusic())
                    .commitNow();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_musicAll:
                showMusicAll();
                break;
            case R.id.btn_musicMyList:
                showMyList();
                break;
            case R.id.btn_musicSearch:
                showMusicSeach();
                break;
        }
        return true;
    }

    private void showMusicAll() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, FragmentMain.newInstance())
                .commitNow();
    }

    private void showMyList() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, new FragmentMyPlayList())
                .commitNow();
    }

    private void showMusicSeach() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, new FragmentSearchMusic())
                .commitNow();
    }




}
