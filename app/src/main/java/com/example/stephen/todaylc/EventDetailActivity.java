package com.example.stephen.todaylc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EventDetailActivity extends AppCompatActivity {

    private WebView eventDetailWebView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        eventDetailWebView = findViewById(R.id.eventDetailWebView);
        eventDetailWebView.getSettings().setJavaScriptEnabled(true);
        eventDetailWebView.setWebViewClient(new WebViewClient());
        eventDetailWebView.loadUrl(url);

    }
}
