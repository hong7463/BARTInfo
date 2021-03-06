package com.honghaisen.bartinfo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    private Button getInfo;
    private LinearLayout info;
    private RequestQueue requestQueue;
    private FragmentManager fragmentManager;
    private List<InfoFragment> fragments = new ArrayList<InfoFragment>();
    private HashMap<String, String> map;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getInfo = (Button) findViewById(R.id.getInfo);
        info = (LinearLayout) findViewById(R.id.container);
        requestQueue = Volley.newRequestQueue(this);
        fragmentManager = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rdGrp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        map = (HashMap<String, String>)getIntent().getSerializableExtra("map");

        TreeSet<String> treeSet = new TreeSet<String>();
        for(String key : map.keySet()) {
            treeSet.add(key);
        }
        for(String key : treeSet) {
            RadioButton radioButton = new RadioButton(MainActivity.this);
            radioButton.setText(key);
            radioGroup.addView(radioButton);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String check = null;
                for(int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton child = (RadioButton)radioGroup.getChildAt(i);
                    if(child.isChecked()) {
                        check = child.getText().toString();
                        break;
                    }
                }
                if(check == null) {
                    Toast.makeText(MainActivity.this, "please select a station name", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                String uri = "http://api.bart.gov/api/etd.aspx?cmd=etd&orig=" + map.get(check) + "&key=MW9S-E7SL-26DU-VV8V";
                StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final List<Info> list = new ArrayList<Info>();
                        FragmentTransaction clearTransaction = fragmentManager.beginTransaction();
                        for(InfoFragment fragment : fragments) {
                            clearTransaction.remove(fragment);
                        }
                        clearTransaction.commit();
                        XmlPullParser pullParser = null;
                        XmlPullParserFactory factory = null;
                        try {
                            factory = XmlPullParserFactory.newInstance();
                            pullParser = factory.newPullParser();
                            pullParser.setInput(new StringReader(response));
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        }
                        String text = null;
                        int event;
                        try {
                            event = pullParser.getEventType();
                            Info infoObj = null;
                            while(event != XmlPullParser.END_DOCUMENT) {
                                String name = pullParser.getName();
                                switch (event) {
                                    case XmlPullParser.START_TAG:
                                        break;
                                    case XmlPullParser.TEXT:
                                        text = pullParser.getText();
                                        break;
                                    case XmlPullParser.END_TAG:
                                        if(name.equals("minutes")) {
                                            infoObj = new Info();
                                            infoObj.minutes = text;
                                            Log.d("min", "min");
                                        }
                                        if(name.equals("platform")) {
                                            infoObj.platform = text;
                                            Log.d("plat", "platform");
                                        }
                                        if(name.equals("direction")) {
                                            infoObj.direction = text;
                                            Log.d("direction", "direction");
                                        }
                                        if(name.equals("length")) {
                                            infoObj.length = text;
                                            Log.d("length", "length");
                                        }
                                        if(name.equals("color")) {
                                            infoObj.color = text;
                                            Log.d("color", "color");
                                        }
                                        if(name.equals("bikeflag")) {
                                            infoObj.bikeflag = text;
                                            Log.d("bikeflag", "bikeflag");
                                            list.add(infoObj);
                                        }
                                        break;
                                }
                                event = pullParser.next();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        int count = 1;
                        fragments.clear();
                        Collections.sort(list, new Comparator<Info>() {

                            @Override
                            public int compare(Info lhs, Info rhs) {
                                if(lhs.minutes.equals("Leaving")) {
                                    return -1;
                                }
                                if(rhs.minutes.equals("Leaving")) {
                                    return 1;
                                }
                                return Integer.parseInt(lhs.minutes) - Integer.parseInt(rhs.minutes);
                            }
                        });
                        if(list.size() == 0) {
                            Toast.makeText(MainActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                        }
                        for(Info info : list) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            InfoFragment infoFragment = new InfoFragment();
                            infoFragment.minutes = info.minutes;
                            infoFragment.platform = info.platform;
                            infoFragment.direction = info.direction;
                            infoFragment.length = info.length;
                            infoFragment.color = info.color;
                            infoFragment.bikeflag = info.bikeflag;
                            infoFragment.num = String.valueOf(count++);
                            fragments.add(infoFragment);
                            fragmentTransaction.add(R.id.container, infoFragment);
                            fragmentTransaction.commit();
                        }
                        Log.d("response", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(request);
            }
        });
    }
}
