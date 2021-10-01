package kr.or.mrhi.mp3player;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MusicDBController {

    Context context;
    OpenHelper openHelper;

    public MusicDBController(Context context) {
        this.context = context;
        openHelper = new OpenHelper(context);
    }


    class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(@Nullable Context context) {
            super(context, "MusicDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE playlistTBL ( musicID var(30) PRIMARY KEY);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            onCreate(sqLiteDatabase);
        }
    }


    public void enterMyPlayList(Long id) {
        SQLiteDatabase sqlDB = openHelper.getWritableDatabase();
        try {
            sqlDB.execSQL("INSERT INTO playlistTBL VALUES ('" + id + "')");
            Log.e("데이터베이스", "insert성공");
        } catch (Exception e) {
            Log.e("데이터베이스", "insert에러" + e.toString());
        } finally {
            sqlDB.close();
        }
    }

    public List<Long> getMyPlayList() {
        List<Long> idList = new ArrayList<>();
        SQLiteDatabase sqlDB = openHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqlDB.rawQuery("SELECT * FROM playlistTBL;", null);
            while (cursor.moveToNext()) {
                idList.add(cursor.getLong(0));
            }
            Log.e("데이터베이스", "select성공");
        } catch (Exception e) {
            Log.e("데이터베이스", "select에러" + e.toString());
        } finally {
            sqlDB.close();
            if (cursor != null) {
                cursor.close();
            }
        }
        return idList;
    }

    public void deleteMyPlayList(Long id) {
        SQLiteDatabase sqlDB = openHelper.getWritableDatabase();
        try {
            sqlDB.execSQL("delete from playlistTBL where musicID = " + id + ";");
            Log.e("데이터베이스", "delete성공");
        } catch (Exception e) {
            Log.e("데이터베이스", "delete에러" + e.toString());
        } finally {
            sqlDB.close();
        }
    }

}
