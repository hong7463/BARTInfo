package com.honghaisen.bartinfo;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;


public class RadioFragment extends Fragment {

    private RadioButton radioButton;
    String fullName;

    public RadioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_radio, container, false);

        RadioButton radioButton = (RadioButton) view.findViewById(R.id.rdBtn);
        radioButton.setText(fullName);
        return view;
    }

}
