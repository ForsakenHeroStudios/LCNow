package com.example.stephen.todaylc;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupHolder extends RecyclerView.ViewHolder {
    private TextView groupTextView;
    private ImageView subStar;
    private CardView cardView;

    public GroupHolder(View itemView) {
        super(itemView);
        groupTextView = itemView.findViewById(R.id.groupTextView);
        subStar = itemView.findViewById(R.id.groupSubStar);
        cardView = itemView.findViewById(R.id.groupCard);

    }

    public void setDetails(final Group group) {
        groupTextView.setText(group.getGroupName());
        if (group.isSub()) {
            subStar.setImageResource(android.R.drawable.star_big_on);
            subStar.setTag("on");
        } else {
            subStar.setImageResource(android.R.drawable.star_big_off);
            subStar.setTag("off");
        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showSelectedGroup(group.getGroupName().toLowerCase());
            }
        });
    }
}
