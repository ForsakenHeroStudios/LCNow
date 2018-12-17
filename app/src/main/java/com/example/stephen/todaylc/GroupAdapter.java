package com.example.stephen.todaylc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {
    private ArrayList<Group> groups;
    private Context context;

    public GroupAdapter(ArrayList<Group> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item_row,parent,false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        final Group group = groups.get(position);
        holder.setDetails(group);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}
