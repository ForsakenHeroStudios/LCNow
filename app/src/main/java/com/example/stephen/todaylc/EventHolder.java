package com.example.stephen.todaylc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;


public class EventHolder extends RecyclerView.ViewHolder {
    private TextView eventTitle, eventTime, eventLocation, dividerTextView, notifyText;
    private WebView  eventDescription;
    private ImageView imageView;
    private EventCardView card;
    private SuperSwipeRevealLayout swipeRevealLayout;
    private LinearLayout swipeLinearLayout;
    private View itView;
    private int position;
    private boolean notify;

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
        if (notify) {
            notifyText.setText("Cancel\n Notification");
            notifyText.setBackgroundColor(Color.RED);
        } else {
            notifyText.setText("Add\n Notification");
            notifyText.setBackgroundColor(App.getApplication().getResources().getColor(R.color.colorPrimary));
        }
    }




    public EventHolder(View itemView) {
        super(itemView);
        itView = itemView;
        eventTitle = itemView.findViewById(R.id.event_title);
        eventDescription = itemView.findViewById(R.id.event_description_web);
        eventTime = itemView.findViewById(R.id.event_time);
        imageView = itemView.findViewById(R.id.imageView);
        eventLocation = itemView.findViewById(R.id.event_location);
        dividerTextView = itemView.findViewById(R.id.dividerTextView);
        card = itemView.findViewById(R.id.eventCard);
        swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
        notifyText = itemView.findViewById(R.id.notifyText);
        swipeLinearLayout = itemView.findViewById(R.id.swipeLinearLayout);
        notify = false;
    }



    public void setDetails(final Event event, int position) {
        this.position = position;
        card.setEvent(event);
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                sendToEventDetail(event);
                return true;
            }
        };
        eventTitle.setText(event.getTitle());


        //separate method for the webview is used here because it cannot detect clicks, only touch events
        // TODO: if a url appears in the webview, go to that instead when clicked on.
        eventDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    sendToEventDetail(event);
                }
                return true;
            }
        });
        Calendar calendar = Calendar.getInstance();
        String time = event.getTime();
        calendar.set(Integer.parseInt(time.substring(0, 4)), Integer.parseInt(time.substring(5, 7)) - 1, Integer.parseInt(time.substring(8, 10)));
        dividerTextView.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.US)+" "+calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.US)+" "+calendar.get(Calendar.DATE)+", "+calendar.get(Calendar.YEAR));
        if (event.isFirstOfDay()) {
            dividerTextView.setVisibility(View.VISIBLE);
        } else {
            dividerTextView.setVisibility(View.GONE);
        }
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
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() != R.id.event_description_web) {
                    sendToEventDetail(event);
                }
            }
        });
        eventDescription.getSettings().setJavaScriptEnabled(true);
        eventDescription.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                sizeChanged();
            }
        });
        eventDescription.loadData(event.getDescription(),"text/html","UTF-8");
        notifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notify) {
                    MainActivity.manualCancelSub(event);
                } else {
                    MainActivity.manualAddSub(event);
                }
                swipeRevealLayout.close(true);
                notify = !notify;
            }
        });
        swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                changeNotifyText();
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {

            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {

            }
        });
        notify = MainActivity.getSubList().contains(event);
    }

    private void changeNotifyText() {
        if (notify) {
            notifyText.setText("Cancel\n Notification");
            notifyText.setBackgroundColor(Color.RED);
        } else {
            notifyText.setText("Add\n Notification");
            notifyText.setBackgroundColor(App.getApplication().getResources().getColor(R.color.colorPrimary));
        }
    }

    public void close() {
        swipeRevealLayout.close(true);
    }


    public SuperSwipeRevealLayout getSwipeRevealLayout() {
        return swipeRevealLayout;
    }

    public void sizeChanged() {
        ViewGroup.LayoutParams linearParams = swipeRevealLayout.getChildAt(0).getLayoutParams();
        linearParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        swipeRevealLayout.getChildAt(0).setLayoutParams(linearParams);
    }

    public EventCardView getCard() {
        return card;
    }

    public void setHeight(int height) {
        ViewGroup.LayoutParams layoutParams = notifyText.getLayoutParams();
        layoutParams.height = height;
        notifyText.setLayoutParams(layoutParams);
        notifyText.requestLayout();
        notifyText.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY));
        Log.d("changer4","height: "+height+", "+eventTitle.getText()+", "+notifyText.getMeasuredHeight()+", "+layoutParams.height);
    }

    private void sendToEventDetail(Event event) {
        Intent intent = new Intent(App.getContext(), EventDetailActivity.class);
        intent.putExtra("url",event.getUrl());
        App.getApplication().startActivity(intent);
    }

    public void setPosition(int i) {
        card.setPosition(i);
    }

    public static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

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
