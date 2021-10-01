package kr.or.mrhi.mp3player.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.or.mrhi.mp3player.MusicData;
import kr.or.mrhi.mp3player.MusicViewModel;
import kr.or.mrhi.mp3player.MyAdapter;
import kr.or.mrhi.mp3player.R;
import kr.or.mrhi.mp3player.activity.MainActivity;
import kr.or.mrhi.mp3player.activity.MusicActivity;

public class FragmentMyPlayList extends Fragment implements MyAdapter.OnStartActivityListener {

    private MusicViewModel mViewModel;
    private MyAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);

        View view = inflater.inflate(R.layout.my_playlist_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        adapter = new MyAdapter(mViewModel.getMyPlayListData().getValue(), this.getContext());
        adapter.setOnStartActivityListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mViewModel.getMyPlayListData().observe(requireActivity(),MusicData ->{
            Log.i("라이브데이터", "값변경");
            adapter.notifyDataSetChanged();
        });
        return view;
    }




    @Override
    public void onStartActivity(List<MusicData> list, int position) {
        Intent intent = new Intent(getActivity(), MusicActivity.class);
        intent.putExtra("MusicList", (ArrayList<MusicData>) list);
        intent.putExtra("position", position);
        intent.putExtra("fragmentPos", 2);
        startActivity(intent);
    }


}