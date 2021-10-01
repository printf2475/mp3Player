package kr.or.mrhi.mp3player.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kr.or.mrhi.mp3player.MusicData;
import kr.or.mrhi.mp3player.MusicViewModel;
import kr.or.mrhi.mp3player.R;
import kr.or.mrhi.mp3player.MyAdapter;
import kr.or.mrhi.mp3player.activity.MusicActivity;

public class FragmentMain extends Fragment implements MyAdapter.OnStartActivityListener{

    MusicViewModel mViewModel;

    public static FragmentMain newInstance() {
        return new FragmentMain();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel= new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        mViewModel.setContext(getContext().getApplicationContext());
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        MyAdapter adapter = new MyAdapter(mViewModel.getMusicListData(), this.getContext());
        adapter.setOnStartActivityListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStartActivity(List<MusicData> list, int position) {

        Intent intent = new Intent(getActivity(), MusicActivity.class);
        intent.putExtra("MusicList", (ArrayList<MusicData>) list);
        intent.putExtra("position", position);
        intent.putExtra("fragmentPos", 1);
        startActivity(intent);
    }
}