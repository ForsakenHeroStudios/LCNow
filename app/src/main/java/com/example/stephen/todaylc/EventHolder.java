package com.example.stephen.todaylc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EventHolder extends RecyclerView.ViewHolder {
    private TextView eventTitle, eventTime, eventLocation;
    private WebView  eventDescription;
    private ImageView imageView;

    public EventHolder(View itemView) {
        super(itemView);
        eventTitle = itemView.findViewById(R.id.event_title);
        eventDescription = itemView.findViewById(R.id.event_description_web);
        eventTime = itemView.findViewById(R.id.event_time);
        imageView = itemView.findViewById(R.id.imageView);
        eventLocation = itemView.findViewById(R.id.event_location);
    }

    public void setDetails(Event event) {
        eventTitle.setText(event.getTitle());
        eventDescription.getSettings().setJavaScriptEnabled(true);
        eventDescription.setWebViewClient(new WebViewClient());
        eventDescription.loadData(event.getDescription(),"text/html","UTF-8");
        if (event.getStartEnd() == null || event.getStartEnd().equals("")) {
            eventTime.setText(event.getMonthDay());
        } else {
            eventTime.setText(event.getMonthDay()+", "+event.getStartEnd());
        }
        eventLocation.setText(event.getLocation());
        ImageDownloader imageDownloader = new ImageDownloader();
        if (event.getImageURL()!=null) {
            try {
                Bitmap newImage = imageDownloader.execute(event.getImageURL()).get();
                imageView.setImageBitmap(newImage);
            } catch (Exception e) { }
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }
}
