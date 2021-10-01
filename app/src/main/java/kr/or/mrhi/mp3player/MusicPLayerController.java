package kr.or.mrhi.mp3player;

import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_NEXT;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PAUSE;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PLAY;
import static kr.or.mrhi.mp3player.MusicService.ACTION_MUSIC_PREV;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicPLayerController {
    private MediaPlayer mediaPlayer;
    private MusicData musicData =null;
    private Context context;
    private static MusicPLayerController instance = new MusicPLayerController() ;
    private OnChangeProgressPositionListener onChangeProgressPositionListener;
    private int currentStatus;

    private MusicPLayerController() {

    }

    public static MusicPLayerController getInstance() {
        return instance;
    }


    public void initPlayer(){
        mediaPlayer = MediaPlayer.create(context, musicData.getUri());
    }

    public void play() {

        currentStatus=ACTION_MUSIC_PLAY;
        mediaPlayer.start();
        seekBarThreadStart();
        Log.i("컨트롤러", "음악시작");
    }

    public void pause(){
        mediaPlayer.pause();
        currentStatus=ACTION_MUSIC_PAUSE;

    }

    public void next(){
        currentStatus=ACTION_MUSIC_NEXT;
    }

    public void prev(){
        currentStatus=ACTION_MUSIC_PREV;
    }

    public void stop(){
        mediaPlayer.stop();
        currentStatus=ACTION_MUSIC_PAUSE;

    }

    public void setSeekTo(int progress){
        mediaPlayer.seekTo(progress);
    }


    private void seekBarThreadStart() {
        new Thread(() -> {
            while (mediaPlayer.isPlaying()) {
                onChangeProgressPositionListener.onChangeProgressPosition(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public interface OnChangeProgressPositionListener{
        void onChangeProgressPosition(int position, int maxTime);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setMusicData(MusicData musicData) {
        this.musicData = musicData;
    }

    public void setOnChangeProgressPositionListener(OnChangeProgressPositionListener onChangeProgressPositionListener) {
        this.onChangeProgressPositionListener = onChangeProgressPositionListener;
    }
}
