package com.dhivi.inc.topo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;

import butterknife.ButterKnife;

/**
 * Created by Manoj on 11/6/2017.
 */

public class ReferFragment extends Fragment {

    APIInterface apiInterface;
    String TAG = SettingsFragment.class.getSimpleName();

    public static ReferFragment newInstance() {
        return new ReferFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refer_fragment, container, false);

        ButterKnife.bind(this, view);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        return view;
    }
}
