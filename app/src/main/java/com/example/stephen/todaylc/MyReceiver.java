package com.example.stephen.todaylc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MyNewIntentService.class);
        intent1.putExtra("title",intent.getStringExtra("title"));
        intent1.putExtra("time",intent.getStringExtra("time"));
        context.startService(intent1);
    }
}
