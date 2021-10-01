package kr.or.mrhi.mp3player.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.or.mrhi.mp3player.MusicData;
import kr.or.mrhi.mp3player.MusicViewModel;
import kr.or.mrhi.mp3player.MyAdapter;
import kr.or.mrhi.mp3player.R;
import kr.or.mrhi.mp3player.activity.MusicActivity;

public class FragmentSearchMusic extends Fragment implements MyAdapter.OnStartActivityListener, TextWatcher {
    private MusicViewModel mViewModel;
    private RecyclerView recyclerView;
    private TextView edt_search;
    private MyAdapter adapter;
    private List<MusicData> list = new ArrayList<>();
    private List<MusicData> tempList = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        tempList=mViewModel.getMusicListData();
        adapter = new MyAdapter(list, this.getContext());
        View view = inflater.inflate(R.layout.search_music_fragment, container, false);
        recyclerView = view.findViewById(R.id.searchRecyclerView);
        edt_search = view.findViewById(R.id.edt_search);


        adapter.setOnStartActivityListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(adapter);

        edt_search.addTextChangedListener(this);

        return view;

    }


    @Override
    public void onStartActivity(List<MusicData> list, int position) {
        Intent intent = new Intent(getActivity(), MusicActivity.class);
        intent.putExtra("MusicList", (ArrayList<MusicData>) list);
        intent.putExtra("position", position);
        intent.putExtra("fragmentPos", 3);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = edt_search.getText().toString();
        search(text);
    }


    public void search(String charText) {
        list.clear();

        if (charText.length() == 0) {
            list.addAll(tempList);
        } else {
            for(int i = 0;i < tempList.size(); i++)
            {
                if (tempList.get(i).getDisplayName().contains(charText))
                {
                    list.add(tempList.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}