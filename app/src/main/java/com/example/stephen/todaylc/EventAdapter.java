package com.example.stephen.todaylc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

    private ArrayList<Event> events;
    private Context context;
    public static final String CHANNEL_ID = "1";

    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row,parent,false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventHolder holder, final int position) {
        final Event event = events.get(position);
        holder.setDetails(event);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int requestID = (int) System.currentTimeMillis();
                Toast.makeText(context,event.getTitle()+" at "+event.getTime()+" reminder set!",Toast.LENGTH_LONG).show();
                Calendar calendar = Calendar.getInstance();
                String time = event.getTime();

                // The following determines if a specific start time was specified for the event,
                // and prefers that over the time JSON object, which don't always match.
                // We need to determine it's am or pm as well, since the calendar.set()
                // method wants 24 hour format.
                String start = event.getStartEnd();
                int hour = -1;
                if (start.equals("") || start.indexOf('m')<=0) {
                    start = time.substring(11, 13);
                    hour += Integer.parseInt(start);
                } else {
                    if (start.charAt(start.indexOf('m') - 1) == 'p') {
                        hour+=12;
                    }
                    start = start.substring(0, start.indexOf(':'));
                    hour += Integer.parseInt(start);

                }
                // subtract 1 from month because Calendar months are 0-11
                calendar.set(Integer.parseInt(time.substring(0,4)),Integer.parseInt(time.substring(5, 7))-1,Integer.parseInt(time.substring(8, 10)),hour,Integer.parseInt(time.substring(14, 16)),Integer.parseInt(time.substring(17, 19)));
                Intent intent = new Intent(App.getContext(),EventNotificationReceiver.class);
                intent.putExtra("title",event.getTitle());
                intent.putExtra("location",event.getLocation());
                // might want to look into effect of different flags
                PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getContext(),requestID, intent,0);
                AlarmManager alarmManager = (AlarmManager)App.getApplication().getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pendingIntent);
//                alarmManager.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+500, pendingIntent);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
