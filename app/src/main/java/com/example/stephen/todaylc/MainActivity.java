package com.example.stephen.todaylc;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.example.stephen.todaylc.EventAdapter.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    private static Context context;
    private RecyclerView eventRecyclerView, searchRecyclerView;
    private ConstraintLayout container;
    private ListView groupListView;
    private EventAdapter eventAdapter;
    private ArrayAdapter<String> groupAdapter;
    private ArrayList<String> allGroups, groupsToShow;
    private ArrayList<Event> eventArrayList, eventArrayListToShow;
    private CalendarView calendarView;
    private BottomNavigationView bottomNavigationView;
    private Button requestButton;
    private EditText nameEdit, emailEdit, organizationEdit, editDescription, editDate, editTime, editLocation, titleEdit, searchEdit, groupEdit;
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private String dae/*searchType*/;
    private LinearLayout searchLayout, linearLayoutMain, addEventLayout, groupViewLayout;
//    private final String SEARCH_TYPE_GENERAL = "general";
//    private final String SEARCH_TYPE_GROUP = "group";
    private final String MAIL_TO = "sbaker@lclark.edu"; // change this to Jason's email


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.navigation_search:
//                    setTitle("Search");
//                    linearLayoutMain.setVisibility(View.INVISIBLE);
//                    calendarView.setVisibility(View.INVISIBLE);
//                    addEventLayout.setVisibility(View.INVISIBLE);
//                    searchLayout.setVisibility(View.VISIBLE);
//                    hideSoftKeyboard(MainActivity.this);
//                    return true;
                case R.id.navigation_today:
                    setTitle("Today at LC");
                    linearLayoutMain.setVisibility(View.INVISIBLE);
                    calendarView.setVisibility(View.INVISIBLE);
                    addEventLayout.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.VISIBLE);
                    groupViewLayout.setVisibility(View.INVISIBLE);
                    hideSoftKeyboard(MainActivity.this);
//                    searchType = SEARCH_TYPE_GENERAL;
//                    searchEdit.setHint("Find activities...");
                    return true;
                case R.id.navigation_thismonth:
                    setTitle("Month view");
                    linearLayoutMain.setVisibility(View.INVISIBLE);
                    calendarView.setVisibility(View.VISIBLE);
                    addEventLayout.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.INVISIBLE);
                    groupViewLayout.setVisibility(View.INVISIBLE);
                    hideSoftKeyboard(MainActivity.this);
//                    searchType = SEARCH_TYPE_GENERAL;
//                    searchEdit.setHint("Find activities...");
                    return true;
                case R.id.navigation_add_event:
                    setTitle("Request to post an event");
                    linearLayoutMain.setVisibility(View.INVISIBLE);
                    calendarView.setVisibility(View.INVISIBLE);
                    addEventLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.INVISIBLE);
                    groupViewLayout.setVisibility(View.INVISIBLE);
                    hideSoftKeyboard(MainActivity.this);
//                    searchType = SEARCH_TYPE_GENERAL;
                    return true;
                case R.id.navigation_groups:
                    setTitle("Find a group");
                    linearLayoutMain.setVisibility(View.INVISIBLE);
                    calendarView.setVisibility(View.INVISIBLE);
                    addEventLayout.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.INVISIBLE);
                    groupViewLayout.setVisibility(View.VISIBLE);
                    hideSoftKeyboard(MainActivity.this);
//                    searchType = SEARCH_TYPE_GROUP;
//                    searchEdit.setHint("Search for a group...");
                    return true;
            }
            return false;
        }
    };

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public void request(View v) {
        String emailBody = "Hi Jason,\n\n" +
                "I'm with "+organizationEdit.getText()+
                " and we would like to add our event, "+titleEdit.getText()+
                " to the LC events calendar." +
                " It will take place at "+editLocation.getText()+
                " on "+editDate.getText()+" at "+editTime.getText() + ". "+
                "The description is below, and the image is attached.\n\n" +
                "Thank you for your time,\n" +nameEdit.getText()+ "\n\n" +
                editDescription.getText()+"\n\n" +
                "Sent with the LC Now App";
        String[] CC = {""};
        hideSoftKeyboard(MainActivity.this);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIL_TO});
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Event posting request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        try {
            startActivity(Intent.createChooser(emailIntent,"Send email"));
            finish();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "No email client installed.", Toast.LENGTH_LONG).show();
        }

    }
    // for more good stuff with eventRecyclerView: https://medium.com/@droidbyme/android-recyclerview-fca74609725e
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();

//        searchType = SEARCH_TYPE_GENERAL;

        container = findViewById(R.id.container);
        // not sure if this actually does anything
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(MainActivity.this);
            }
        });

        dae = SDF.format(new Date());

        linearLayoutMain = findViewById(R.id.linearLayoutMain);

        editDate = findViewById(R.id.editTextDate);
        editDescription = findViewById(R.id.editTextDescription);
        editLocation = findViewById(R.id.editTextLocation);
        editTime = findViewById(R.id.editTextTime);
        emailEdit = findViewById(R.id.editTextEmail);
        organizationEdit = findViewById(R.id.editTextOrganization);
        nameEdit = findViewById(R.id.editTextName);
        titleEdit = findViewById(R.id.editTextEventTitle);

        addEventLayout = findViewById(R.id.addEventLayout);
        addEventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(MainActivity.this);
            }
        });
        bottomNavigationView = findViewById(R.id.navigation);
        // lets see if this works
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            removeTextLabel(bottomNavigationView,bottomNavigationView.getMenu().getItem(i).getItemId());
        }
        bottomNavigationView.setItemIconTintList(null);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
        eventRecyclerView = (RecyclerView) findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventArrayList = new ArrayList<>();
        eventArrayListToShow = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventArrayListToShow);
        eventRecyclerView.setAdapter(eventAdapter);
        createListData();
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                dae = SDF.format(new Date(year-1900,month,dayOfMonth));
                createEventsToShow();
                calendarView.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.VISIBLE);
//                int datePosition = 0;
//                for (int i = 0; i < eventArrayList.size(); i++) {
//                    String date = eventArrayList.get(i).getTime();
//                    String day = (dayOfMonth<10) ? "0"+dayOfMonth : ""+dayOfMonth;
//                    String monthStr = (month<10) ? "0"+month : ""+month;
//                    Log.i("daytag",date.substring(8,10)+", "+day);
//                    if (/*date.substring(0, 4).equals("" + year) && date.substring(5, 7).equals(monthStr) && */date.substring(8, 10).equals(day)) {
//                        datePosition = i;
//                        break;
//                    }
//                }
//                eventRecyclerView.smoothScrollToPosition(datePosition);
                Toast.makeText(MainActivity.this, "" + dayOfMonth, Toast.LENGTH_SHORT).show();
            }
        });

        searchLayout = findViewById(R.id.searchLayout);
        searchEdit = findViewById(R.id.searchEdit);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(eventAdapter);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                createEventsToShow(s.toString().toLowerCase());
            }
        });
        searchRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(MainActivity.this);
            }
        });

        groupViewLayout = findViewById(R.id.groupLayout);
        groupViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(MainActivity.this);
            }
        });
        groupEdit = findViewById(R.id.groupSearchEditText);
        groupEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                createGroupsToShow(s.toString().toLowerCase());
            }
        });
        groupListView = findViewById(R.id.groupListView);
        allGroups = new ArrayList<>(SplashActivity.groups);
        Collections.sort(allGroups);
        groupsToShow = new ArrayList<>(allGroups);
        groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupsToShow);
        groupListView.setAdapter(groupAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboard(MainActivity.this);
                showSelectedGroup(groupsToShow.get(position).toLowerCase());
                groupViewLayout.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.VISIBLE);
            }
        });


    }

    private void removeTextLabel(@NonNull BottomNavigationView bottomNavigationView, @IdRes int menuItemId) {
        View view = bottomNavigationView.findViewById(menuItemId);
        if (view == null) return;
        if (view instanceof MenuView.ItemView) {
            ViewGroup viewGroup = (ViewGroup) view;
            int padding = 0;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    padding = v.getHeight();
                    viewGroup.removeViewAt(i);
                }
            }
            viewGroup.setPadding(view.getPaddingLeft(), (viewGroup.getPaddingTop() + padding) / 2, view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    private void createListData() {
        for(Event e : SplashActivity.result) {
            eventArrayList.add(e);
        }
        createEventsToShow();
    }

    private void createEventsToShow(String key) {
        eventArrayListToShow.clear();
        for (Event e : eventArrayList) {
            if (/*searchType.equals(SEARCH_TYPE_GENERAL) && */(e.getTitle().toLowerCase().contains(key) || e.getDescription().toLowerCase().contains(key))) {
                eventArrayListToShow.add(e);
            } /*else if (searchType.equals(SEARCH_TYPE_GROUP) && e.getGroup().toLowerCase().contains(key)) {
                eventArrayListToShow.add(e);
            }*/
        }
        eventAdapter.notifyDataSetChanged();
    }

    private void createEventsToShow() {
        eventArrayListToShow.clear();
        for (Event e : eventArrayList) {
            Log.i("Date",e.getTime().substring(0,10)+", "+dae);
            if (e.getTime().substring(0,10).equals(dae)) {
                eventArrayListToShow.add(e);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }

    private void createGroupsToShow(String key) {
        groupsToShow.clear();
        for (String s : allGroups) {
            if (s.toLowerCase().contains(key)) {
                groupsToShow.add(s);
            }
        }
        groupAdapter.notifyDataSetChanged();

    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    private void showSelectedGroup(String group) {
        eventArrayListToShow.clear();
        for (Event e : eventArrayList) {
            if (e.getGroup().toLowerCase().contains(group)) {
                eventArrayListToShow.add(e);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && (searchRecyclerView.getVisibility()==View.VISIBLE || groupViewLayout.getVisibility()==View.VISIBLE)) {
            hideSoftKeyboard(this);
            return true;
        }
        return super.onKeyUp(keyCode, keyEvent);

    }


}
