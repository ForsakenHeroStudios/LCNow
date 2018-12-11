package com.example.stephen.todaylc;
import com.loopj.android.http.*;

public class LiveLoader {
    private static final String BASE_URL = "https://www.lclark.edu/live/json/events";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
