package kr.or.mrhi.mp3player;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import kr.or.mrhi.mp3player.activity.MusicActivity;


public class MyAdapter extends RecyclerView.Adapter<Holder> {
    private static List<MusicData> list;
    private View myView;
    private Context context;



    OnStartActivityListener onStartActivityListener;


    public interface OnStartActivityListener{
        void onStartActivity( List<MusicData> list, int position);
    }

    public void setOnStartActivityListener(OnStartActivityListener onStartActivityListener) {
        this.onStartActivityListener = onStartActivityListener;
    }

    public MyAdapter(List<MusicData> list ,Context context) {
        this.context=context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context= parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = inflater.inflate(R.layout.list_item, parent, false);

        return new Holder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.iv_music.setImageResource(R.mipmap.music);
        holder.tv_title.setText(list.get(position).getTitle());
        holder.tv_artist.setText(list.get(position).getArtist());
        holder.item_layout.setOnClickListener(view -> {
            onStartActivityListener.onStartActivity(list, position);
        });
        Glide.with(holder.itemView)
                .load(list.get(position).getImageuri())
                .override(200, 200)
                .error(R.mipmap.music)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(holder.iv_music);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class Holder extends RecyclerView.ViewHolder {
    TextView tv_title, tv_artist;
    ConstraintLayout item_layout;
    ImageView iv_music;
    public Holder(@NonNull View itemView) {
        super(itemView);
        iv_music = itemView.findViewById(R.id.iv_music);
        tv_title = itemView.findViewById(R.id.tv_title);
        tv_artist = itemView.findViewById(R.id.tv_artist);
        item_layout = itemView.findViewById(R.id.item_layout);
    }
}



