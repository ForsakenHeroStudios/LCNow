package com.example.stephen.todaylc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class HomeMenuActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case 1:
                        Intent addEventIntent = new Intent(getApplicationContext(),RequestAddEventActivity.class);
                        startActivity(addEventIntent);
                        break;
                }
            }
        });
    }
}
