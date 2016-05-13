package com.honghaisen.bartinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.HashMap;
import java.util.TreeSet;

public class Splash extends AppCompatActivity {

    private RequestQueue requestQueue;
    HashMap<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestQueue = Volley.newRequestQueue(this);
        String uri = "http://api.bart.gov/api/stn.aspx?cmd=stns&key=MW9S-E7SL-26DU-VV8V";
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                    String key = null;
                    while(event != XmlPullParser.END_DOCUMENT) {
                        String name = pullParser.getName();
                        switch (event) {
                            case XmlPullParser.START_TAG:
                                break;
                            case XmlPullParser.TEXT:
                                text = pullParser.getText();
                                break;
                            case XmlPullParser.END_TAG:
                                if(name.equals("name")) {
                                    key = text;
                                }
                                if(name.equals("abbr")) {
                                    map.put(key, text);
                                    Log.d("name", key);
                                    Log.d("abbr", text);
                                }
                                break;
                        }
                        event = pullParser.next();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(Splash.this, MainActivity.class);
                i.putExtra("map", map);
                Splash.this.startActivity(i);
                Splash.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "error");
            }
        });
        requestQueue.add(request);
    }
}
