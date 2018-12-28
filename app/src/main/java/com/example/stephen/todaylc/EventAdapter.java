package com.example.stephen.todaylc;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

    private ArrayList<Event> events;
    private Context context;
    public static final String CHANNEL_ID = "1";
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private ArrayList<EventHolder> viewHolders;





    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
        viewBinderHelper.setOpenOnlyOne(true);
        viewHolders = new ArrayList<>();
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item_row,parent,false);
        EventHolder eventHolder = new EventHolder(view);
        viewHolders.add(eventHolder);
        eventHolder.setPosition(viewHolders.size()-1);
        return eventHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final EventHolder holder, final int position) {
        final Event event = events.get(position);
        holder.setDetails(event, position);
        if (MainActivity.getSubList().contains(event)) {
            holder.setNotify(true);
        }
        viewBinderHelper.bind(holder.getSwipeRevealLayout(),event.toString());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public ArrayList<EventHolder> getViewHolders() {
        return viewHolders;
    }

    public void saveStates(Bundle outState) {
        viewBinderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        viewBinderHelper.restoreStates(inState);
    }

}
