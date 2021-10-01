package kr.or.mrhi.mp3player;

import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_NEXT;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PAUSE;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PLAY;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PREV;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import kr.or.mrhi.mp3player.activity.MusicActivity;

public class MusicPLayerController {
    private MediaPlayer mediaPlayer;
    private List<MusicData> musicDataList;
    private Context context;
    private static MusicPLayerController instance = new MusicPLayerController() ;
    private OnMusicPlayerStatusListener onMusicPlayerStatusListener;
    private String currentStatus;
    private Boolean isPlaying;
    private int position;



    private MusicPLayerController() {

    }

    public static MusicPLayerController getInstance() {
        return instance;
    }


    public void initPlayer(){
        mediaPlayer = MediaPlayer.create(context, musicDataList.get(position).getUri());
        mediaPlayer.setOnCompletionListener((mediaPlayer)->{
            mediaPlayer.pause();
            onMusicPlayerStatusListener.onChangeStatus(ACTION_MUSIC_NEXT);
        });



    }

    public void play() {
        mediaPlayer.start();
        currentStatus=ACTION_MUSIC_PLAY;
        onMusicPlayerStatusListener.onChangeStatus(currentStatus);
        seekBarThreadStart();
        Log.i("컨트롤러", "음악시작");
    }

    public void pause(){
        mediaPlayer.pause();
        currentStatus=ACTION_MUSIC_PAUSE;
        onMusicPlayerStatusListener.onChangeStatus(currentStatus);
    }

    public void next(){
        mediaPlayer.stop();
        currentStatus=ACTION_MUSIC_NEXT;
        mediaPlayer = MediaPlayer.create(context, musicDataList.get(++position).getUri());
        mediaPlayer.start();
        onMusicPlayerStatusListener.onChangeStatus(currentStatus);
    }

    public void prev(){
        mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(context, musicDataList.get(--position).getUri());
        mediaPlayer.start();
        currentStatus=ACTION_MUSIC_PREV;
        onMusicPlayerStatusListener.onChangeStatus(currentStatus);
    }

    public void stop(){
        mediaPlayer.stop();
        currentStatus=ACTION_MUSIC_PAUSE;

    }

    public void setSeekTo(int progress){
        mediaPlayer.seekTo(progress);
    }

    public Boolean getPlaying() {
        return mediaPlayer.isPlaying();
    }


    private void seekBarThreadStart() {
        new Thread(() -> {
            while (mediaPlayer.isPlaying()) {
                onMusicPlayerStatusListener.onChangeProgressPosition(
                        mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration(), musicDataList.get(position).getId());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }



    public interface OnMusicPlayerStatusListener{
        void onChangeProgressPosition(int progressPosition, int maxTime, Long musicId);
        void onChangeStatus(String status);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setMusicDataList(List<MusicData> musicDataList) {
        this.musicDataList = musicDataList;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public void setOnChangeProgressPositionListener(OnMusicPlayerStatusListener onMusicPlayerStatusListener) {
        this.onMusicPlayerStatusListener = onMusicPlayerStatusListener;
    }
}
