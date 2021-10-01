package kr.or.mrhi.mp3player;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MusicViewModel extends AndroidViewModel {

    Context context;
    private MutableLiveData<List<MusicData>> musicListData;
    private MutableLiveData<List<MusicData>> myPlayListLiveData;
    private MusicDBController musicDBController;

    public MusicViewModel(@NonNull Application application) {
        super(application);
        this.context=application.getApplicationContext();
        musicDBController = new MusicDBController(context);
        getMusicListData();
        getMyPlayListData();
    }


    public MutableLiveData<List<MusicData>> getMyPlayListData() {
        if (myPlayListLiveData == null) {
            myPlayListLiveData = new MutableLiveData<>();
        }
        refrashMyPlayListData();

        return myPlayListLiveData;
    }

    private void refrashMyPlayListData() {
        List<MusicData> myMusicDataList = new ArrayList<>();
        List<Long> myMusicID = musicDBController.getMyPlayList();

        for (Long id : myMusicID){
            for (MusicData musicData : musicListData.getValue()){
                if (id.equals(musicData.getId())){
                    myMusicDataList.add(musicData);
                    Log.i("플레이리스트", musicData.toString());
                }
            }
        }
        myPlayListLiveData.setValue(myMusicDataList);
    }

    public List<MusicData> getMusicListData() {
        if (musicListData == null) {
            musicListData = new MutableLiveData<>();
        }
        refrashMusicData();

        return musicListData.getValue();
    }



    @SuppressLint("Range")
    public void refrashMusicData() {
        List<MusicData> list = makeMusicDataList();
        musicListData.setValue(list);
    }

    private List<MusicData> makeMusicDataList() {
        List<MusicData> list = new ArrayList<>();
        Cursor cursor = null;
        String[] projectionMedia = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST
        };

        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projectionMedia, null, null, null);

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

            while(cursor.moveToNext()){
                Long id = cursor.getLong(idColumn);
                Long albumId = cursor.getLong(albumIdColumn);
                String displayName = cursor.getString(displayNameColumn);
                String album = cursor.getString(albumColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                String duration = cursor.getString(durationColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);

                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri imageUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                MusicData musicData = new MusicData(
                        title, artist,displayName, album, duration, contentUri, imageUri, id);
                list.add(musicData);
            }
        }catch (Exception e){
            Log.e("음악 가져오기", "컨텐트리져브 에러"+e.toString());
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return list;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Long> getMusicIdList(){
        return musicDBController.getMyPlayList();

    }

    public void enterMyPlayList(Long id){
        musicDBController.enterMyPlayList(id);
        refrashMyPlayListData();
        Log.i("라이브데이터", "했음");

    }

    public void deleteMyPlayList(Long id){
        musicDBController.deleteMyPlayList(id);
        refrashMyPlayListData();
        Log.i("라이브데이터", "했음");
    }

}