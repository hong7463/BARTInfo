package com.honghaisen.bartinfo;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfoFragment extends Fragment {

    String minutes;
    String platform;
    String direction;
    String length;
    String color;
    String bikeflag;
    String num;

    private TextView seperator;
    private TextView min;
    private TextView plat;
    private TextView dir;
    private TextView len;
    private TextView col;
    private TextView bf;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        seperator = (TextView) view.findViewById(R.id.seperator);
        min = (TextView) view.findViewById(R.id.min);
        plat = (TextView) view.findViewById(R.id.plat);
        dir = (TextView) view.findViewById(R.id.direction);
        len = (TextView) view.findViewById(R.id.length);
        col = (TextView) view.findViewById(R.id.color);
        bf = (TextView) view.findViewById(R.id.bikeflag);

        min.setText("minutes left: " + minutes);
        plat.setText("platform: " + platform);
        dir.setText("direction: " + direction);
        len.setText("length: " + length);
        col.setText("color: " + color);
        bf.setText("bikeflag: " + bikeflag);
        seperator.setText("===============result" + num + "================");

        return view;
    }

}
