package kr.or.mrhi.mp3player;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("서비스", "RestarterBroadcastReceiver.onReceive");
        context.startService(new Intent(context, MusicService.class));
    }
}

