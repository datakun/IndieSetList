package com.kimjunu.indiesetlist.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kimjunu.indiesetlist.App;
import com.kimjunu.indiesetlist.R;
import com.kimjunu.indiesetlist.model.ArtistModel;
import com.kimjunu.indiesetlist.model.PerformModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistFragment extends Fragment implements TextWatcher {
    private static final String TAG = "ArtistFragment";

    @BindView(R.id.rvArtist)
    RecyclerView rvArtist;

    @BindView(R.id.etSearch)
    EditText etSearch;

    ArtistAdapter mArtistAdapter = null;

    RecyclerView.LayoutManager mLayoutManager = null;

    ArrayList<PerformModel> mPerformList = new ArrayList<>();
    ArrayList<ArtistModel> mArtistList = new ArrayList<>();

    public static ArtistFragment newInstance(Bundle bundle) {
        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artists, container, false);

        ButterKnife.bind(this, root);

//        Bundle bundle = getArguments();
//
//        mVenueList = bundle.getParcelableArrayList(App.ARGS_ARTIST_LIST);

        rvArtist.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        rvArtist.setLayoutManager(mLayoutManager);

        mArtistAdapter = new ArtistAdapter();
        rvArtist.setAdapter(mArtistAdapter);

        etSearch.addTextChangedListener(this);

        updateArtistList(etSearch.getText().toString());

        return root;
    }

    public void setPerformList(ArrayList<PerformModel> itemList) {
        mPerformList = itemList;

        updateArtistList(etSearch.getText().toString());
    }

    public void setArtistList(ArrayList<ArtistModel> itemList) {
        mArtistList = itemList;

        updateArtistList(etSearch.getText().toString());
    }

    private void updateArtistList(String artistName) {
        ArrayList<ArtistModel> artistList = new ArrayList<>();

        for (ArtistModel artist : mArtistList) {
            if ("".equals(artistName)) {
                artistList.add(artist);
            } else if (artist.name.contains(artistName)) {
                artistList.add(artist);
            }
        }

        mArtistAdapter.setArtistList(artistList);
        mArtistAdapter.setPerformList(mPerformList);
        mArtistAdapter.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String artistName = etSearch.getText().toString();
        updateArtistList(artistName);
    }
}
