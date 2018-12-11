package com.example.stephen.todaylc;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;

public class LoadEvents {

    static ArrayList<Event> result = new ArrayList<>();

    public static ArrayList<Event> getEvents() throws JSONException {

        LiveLoader.get("", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                //load stuff into event objects

                for(int i = 0; i < timeline.length(); i++){


                    JSONObject eve = null;


                    try {
                        eve = timeline.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String title = null;
                    try {
                        title = eve.getString("title");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String description = null;
                    try {
                        description = eve.getString("description");
                    } catch (JSONException e) {
                        e.printStackTrace();
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

//                    Event tempEvent = new Event(title, description, time, img);

                    Log.d("webIt", "" + result.size());
//                    result.add(tempEvent);
                }

            }
        });


        return result;
    }
}
