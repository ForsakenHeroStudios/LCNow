package com.example.stephen.todaylc;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {

    public static ArrayList<Event> result;
    public static ArrayList<String> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // displays splash animation gif using a web view
        WebView webView = findViewById(R.id.splashWeb);
        webView.loadUrl("file:///android_asset/splashhtml.html");
        result = new ArrayList<>();
        groups = new ArrayList<>();
        try {
            getEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getEvents() throws JSONException {

        LiveLoader.get("", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {

                //load stuff into event objects
                for (int i = 0; i < timeline.length(); i++) {
                    JSONObject eve = null;
                    try {
                        eve = timeline.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String title = null;
                    try {
                        title = eve.getString("title").replace("&amp;","&");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String description = null;
                    try {
                        description = eve.getString("description");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (description.equals("null")) {
                        description="";
                    } else {
                        // replace ’ with the html for the apostrophe, since we're displaying it that way
                        description = description.replace("’","&#8217;");
                    }

                    String time = null;
                    try {
                        time = eve.getString("date_utc");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String img = null;
                    try {
                        img = eve.getString("thumbnail");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String loc = null;
                    try {
                        loc = eve.getString("location").replace("&amp;","&");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (loc.equals("null")) {
                        loc = "";
                    }

                    String group = null;
                    try {
                        group = eve.getString("group").replace("&amp;","and");
                        if (!groups.contains(group)) {
                            groups.add(group);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String monthDay = null;
                    try {
                        monthDay = eve.getString("date");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // leftover from bad JSON object it seems causes poor appearance
                    monthDay = monthDay.replace("<span class=\"lw_date_year\">","");
                    monthDay = monthDay.replace("</span>","");

                    String startEnd = null;
                    try {
                        startEnd = eve.getString("date_time");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String[] tags = null;
                    try {

                        tags = eve.getString("tags").split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Event tempEvent = new Event(title, description, time, img, loc, group,monthDay,startEnd,tags,false);

                    Log.d("event", tempEvent.toString());
                    result.add(tempEvent);
                }
                // display the main activity now that all events have been loaded
                Intent intent = new Intent(getApplicationContext(),HomeMenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
