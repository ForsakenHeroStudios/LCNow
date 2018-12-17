package com.example.stephen.todaylc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
//                Toast.makeText(holder.itemView.getContext(), "Long clicked position "+position, Toast.LENGTH_SHORT).show();
//                NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                Intent notificationIntent = new Intent(context,MainActivity.class);
//                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                PendingIntent pendingIntent = PendingIntent.getActivity(context,requestID,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//                Intent notifyIntent = new Intent(context,MyReceiver.class);
                Toast.makeText(context,events.get(position).getTitle()+" at "+events.get(position).getTime()+" reminder set!",Toast.LENGTH_LONG).show();
//                notifyIntent.putExtra("title",events.get(position).getTitle());
//                notifyIntent.putExtra("time",events.get(position).getTime());
//                PendingIntent pendingIntent = PendingIntent.getBroadcast
//                        (context, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                String time = events.get(position).getTime();
                // will prolly give an error if time is 0:00:00
                calendar.set(Integer.parseInt(time.substring(0,4)),Integer.parseInt(time.substring(5, 7))-1,Integer.parseInt(time.substring(8, 10)),Integer.parseInt(time.substring(11, 13))-1,Integer.parseInt(time.substring(14, 16)),Integer.parseInt(time.substring(17, 19)));
                Intent intent = new Intent(App.getContext(),MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getContext(),requestID, intent,0);
                AlarmManager alarmManager = (AlarmManager)App.getApplication().getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pendingIntent);

//                alarmManager.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+2000, pendingIntent);
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext(), CHANNEL_ID)
//                        .setCategory(Notification.CATEGORY_EVENT)
//                        .setContentTitle("Test title")
//                        .setContentText("Test text")
//                        .setSmallIcon(R.drawable.lcnowicon)
//                        .setAutoCancel(true)
//                        .setVisibility(Notification.VISIBILITY_PUBLIC)
////                        .addAction(android.R.drawable.ic_menu_view, "Test menu")
//                        .setPriority(NotificationCompat.PRIORITY_MAX)
//                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                        .setDefaults(Notification.DEFAULT_ALL);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    builder.setChannelId(CHANNEL_ID)
//                            .setColorized(true);
//                }
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());
//                notificationManager.notify(event.hashCode(),builder.build());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
