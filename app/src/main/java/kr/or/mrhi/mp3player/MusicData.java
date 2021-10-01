package kr.or.mrhi.mp3player;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class MusicData implements Serializable {
    private String title;
    private String artist;
    private String displayName;
    private String album;
    private String duration ;
    private String uri;
    private String imageuri;
    private Long id;




    public MusicData(String title, String artist, String displayName, String album, String duration, Uri uri, Uri imageuri, Long id) {
        this.title = title;
        this.artist = artist;
        this.displayName = displayName;
        this.album = album;
        this.duration = duration;
        this.uri = uri.toString();
        this.imageuri = imageuri.toString();
        this.id=id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Uri getUri() {
        return Uri.parse(uri);
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public Uri getImageuri() {
        return Uri.parse(imageuri);
    }

    public void setImageuri(Uri imageuri) {
        this.imageuri = imageuri.toString();
    }

    @Override
    public String toString() {
        return "MusicData{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", displayName='" + displayName + '\'' +
                ", album='" + album + '\'' +
                ", duration='" + duration + '\'' +
                ", uri='" + uri + '\'' +
                ", imageuri='" + imageuri + '\'' +
                '}';
    }
}
