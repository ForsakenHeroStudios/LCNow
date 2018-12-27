package com.example.stephen.todaylc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EventCardView extends CardView {
    private int myheight;
//    private TextView notifyView;
    private Event event;
    private int position;

    public int getMyheight() {
        return myheight;
    }

    public EventCardView(@NonNull Context context) {
        super(context);
    }

    public EventCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EventCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public void addText(TextView textView) {
//        notifyView = textView;
//    }
//
//    public TextView getNotifyView() {
//        return notifyView;
//    }


    public void setPosition(int position) {
        this.position = position;
    }

    public void setEvent(Event event) {
        this.event=event;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        myheight = h;
//        notifyView.setHeight(h);
//        Log.d("changer", ""+event.getTitle()+" "+h);
        MainActivity.eventAdapter.getViewHolders().get(position).sizeChanged();
//        for (EventHolder holder : MainActivity.eventAdapter.getViewHolders()) {
//            holder.sizeChanged();
//        }
    }

}
