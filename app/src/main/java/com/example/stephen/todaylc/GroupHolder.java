package com.example.stephen.todaylc;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupHolder extends RecyclerView.ViewHolder {
    private TextView groupTextView;
    private ImageView subStar;

    public GroupHolder(View itemView) {
        super(itemView);
        groupTextView = itemView.findViewById(R.id.groupTextView);
        subStar = itemView.findViewById(R.id.groupSubStar);
    }

    public void setDetails(Group group) {
        groupTextView.setText(group.getGroupName());
        if (group.isSub()) {
            subStar.setImageResource(android.R.drawable.star_big_on);
        } else {
            subStar.setImageResource(android.R.drawable.star_big_off);
        }
    }
}
