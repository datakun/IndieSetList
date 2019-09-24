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

import com.kimjunu.indiesetlist.R;
import com.kimjunu.indiesetlist.model.ArtistModel;
import com.kimjunu.indiesetlist.model.PerformModel;
import com.kimjunu.indiesetlist.model.VenueModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VenueFragment extends Fragment implements TextWatcher {
    private static final String TAG = "VenueFragment";

    @BindView(R.id.rvVenue)
    RecyclerView rvVenue;

    @BindView(R.id.etSearch)
    EditText etSearch;

    VenueAdapter mVenueAdapter = null;

    RecyclerView.LayoutManager mLayoutManager = null;

    ArrayList<PerformModel> mPerformList = new ArrayList<>();
    ArrayList<VenueModel> mVenueList = new ArrayList<>();

    public static VenueFragment newInstance(Bundle bundle) {
        VenueFragment fragment = new VenueFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_venues, container, false);

        ButterKnife.bind(this, root);

        Bundle bundle = getArguments();

        rvVenue.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        rvVenue.setLayoutManager(mLayoutManager);

        mVenueAdapter = new VenueAdapter();
        rvVenue.setAdapter(mVenueAdapter);

        etSearch.addTextChangedListener(this);

        updateVenueList(etSearch.getText().toString());

        return root;
    }

    public void setPerformList(ArrayList<PerformModel> itemList) {
        mPerformList = itemList;

        updateVenueList(etSearch.getText().toString());
    }

    public void setVenueList(ArrayList<VenueModel> itemList) {
        mVenueList = itemList;

        updateVenueList(etSearch.getText().toString());
    }

    private void updateVenueList(String artistName) {
        ArrayList<VenueModel> venueList = new ArrayList<>();

        for (VenueModel venue : mVenueList) {
            if ("".equals(artistName)) {
                venueList.add(venue);
            } else if (venue.name.contains(artistName)) {
                venueList.add(venue);
            }
        }

        mVenueAdapter.setVenueList(venueList);
        mVenueAdapter.setPerformList(mPerformList);
        mVenueAdapter.notifyDataSetChanged();
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
        updateVenueList(artistName);
    }
}
