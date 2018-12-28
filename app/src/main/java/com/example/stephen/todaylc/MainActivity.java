package com.example.stephen.todaylc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // A list of cards with relevant event info
    private RecyclerView eventRecyclerView, searchRecyclerView, groupRecyclerView;
    // a list of all event groups
    private ListView groupListView;
    public static EventAdapter eventAdapter;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> allGroups, groupsToShow;
    private static ArrayList<Event> eventArrayList;
    public static ArrayList<Event> manualSubs;
    private static ArrayList<Event> subList;
    private static ArrayList<Event> eventArrayListToShow;
    // calendar to select events occurring on a certain day that the user would like to be shown
    private CalendarView calendarView;
    // button that sends the user from the request to add event page to an email service of their choice
    private Button requestButton;
    // various text fields
    private EditText nameEdit, emailEdit, organizationEdit, editDescription, editDate, editTime, editLocation, titleEdit, groupEdit;
    private static EditText searchEdit;
    // date format to find which events should be displayed when a certain date is selected
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    // selected day
    private String day;
    private static LinearLayout searchLayout, linearLayoutMain, addEventLayout, groupViewLayout;
    // the email to request to add an event is sent to this address
    private final String MAIL_TO = "sbaker@lclark.edu"; // TODO: change this to Jason's email
    private static AlarmManager alarmManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    LinearLayoutManager linearLayoutManager;

    private static SQLiteDatabase db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return setSelectedView(item);
        }
    };
    private static Toolbar toolbar;

    // for more good stuff with eventRecyclerView: https://medium.com/@droidbyme/android-recyclerview-fca74609725e


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = this.openOrCreateDatabase("Subscriptions", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS subs (groups VARCHAR, filters VARCHAR, id INTEGER PRIMARY KEY)");
        Cursor groupsCursor = db.rawQuery("SELECT * FROM subs", null);
        int groupsIndex = groupsCursor.getColumnIndex("groups");
        int filtersIndex = groupsCursor.getColumnIndex("filters");
        int idIndex = groupsCursor.getColumnIndex("id");
        groupsCursor.moveToFirst();
        while (!groupsCursor.isAfterLast()) {
            Log.i("group", "" + groupsCursor.getString(groupsIndex));
            Log.i("filter", "" + groupsCursor.getString(filtersIndex));
            groupsCursor.moveToNext();
        }
        groupsCursor.close();

        db.execSQL("CREATE TABLE IF NOT EXISTS manuals (events INTEGER, id INTEGER PRIMARY KEY)");
        Cursor manualCursor = db.rawQuery("SELECT * FROM manuals", null);
        int eventIndex = manualCursor.getColumnIndex("events");
        int idIndex2 = manualCursor.getColumnIndex("id");
        manualCursor.moveToFirst();
        while (!manualCursor.isAfterLast()) {
            Log.i("manualdb",""+manualCursor.getInt(eventIndex));
            manualCursor.moveToNext();
        }
        manualCursor.close();

        linearLayoutManager = new LinearLayoutManager(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //lots of good drawable stuff here: https://github.com/google/material-design-icons
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_18dp);


        alarmManager = (AlarmManager) App.getApplication().getSystemService(Context.ALARM_SERVICE);

        subList = new ArrayList<>();

        manualSubs = (manualSubs == null) ? new ArrayList<Event>() : manualSubs;

        day = SDF.format(new Date());

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideSoftKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.drawer_today:
                        setTitle("Today at LC");
                        calendarView.setVisibility(View.INVISIBLE);
                        searchLayout.setVisibility(View.VISIBLE);
                        groupViewLayout.setVisibility(View.INVISIBLE);
                        hideSoftKeyboard(MainActivity.this);
                        createEventsToShow();
                        searchEdit.setVisibility(View.VISIBLE);
                        break;
                    case R.id.drawer_group:
                        setTitle("Find a group");
                        calendarView.setVisibility(View.INVISIBLE);
                        searchLayout.setVisibility(View.INVISIBLE);
                        groupViewLayout.setVisibility(View.VISIBLE);
                        hideSoftKeyboard(MainActivity.this);
                        break;
                    case R.id.drawer_subs:
                        setTitle("Current Subscribed Events");
                        calendarView.setVisibility(View.INVISIBLE);
                        searchLayout.setVisibility(View.VISIBLE);
                        groupViewLayout.setVisibility(View.INVISIBLE);
                        hideSoftKeyboard(MainActivity.this);
                        showSubbedEvents();
                        searchEdit.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });

        // main  event display initialization
        eventArrayList = new ArrayList<>();
        eventArrayListToShow = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventArrayListToShow);

        createListData();

        // calendar view initialization
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                day = SDF.format(new Date(year - 1900, month, dayOfMonth));
                createEventsToShow();
                calendarView.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "" + dayOfMonth, Toast.LENGTH_SHORT).show();
            }
        });

        // search view initialization
        searchLayout = findViewById(R.id.searchLayout);
        searchEdit = findViewById(R.id.searchEdit);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setAdapter(eventAdapter);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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

        //group view initialization
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
//        groupListView = findViewById(R.id.groupListView);
        groupRecyclerView = findViewById(R.id.groupRecyclerView);
        allGroups = new ArrayList<>();
        ArrayList<String> groupTitles = SplashActivity.groups;
        Collections.sort(groupTitles);
        for (String s : groupTitles) {
            allGroups.add(new Group(s, false));
        }
        groupsToShow = new ArrayList<>(allGroups);
        groupAdapter = new GroupAdapter(groupsToShow, this);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupRecyclerView.setAdapter(groupAdapter);
//        groupListView.setAdapter(groupAdapter);
//        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                hideSoftKeyboard(MainActivity.this);
//                showSelectedGroup(groupsToShow.get(position).getGroupName().toLowerCase());
//                groupViewLayout.setVisibility(View.INVISIBLE);
//                searchLayout.setVisibility(View.VISIBLE);
//            }
//        });
        addEventsToSubList();
        for (Event e : subList) {
            createEventNotification(e);
        }

//        for (int i = 0; i < linearLayoutManager.findLastVisibleItemPosition(); i++) {
//            Log.i("testy",""+i);
//            RecyclerView.ViewHolder v = eventRecyclerView.findViewHolderForAdapterPosition(i);
//            v.itemView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
//            int height = ((EventHolder)v).getCard().getMeasuredHeight();
//            ((EventHolder)v).setHeight(height);
//            eventAdapter.notifyDataSetChanged();
//        }



//        groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                String groupSelected = groupsToShow.get(position);
//                Cursor cursor = db.rawQuery("SELECT * FROM subs WHERE groups= '"+groupSelected+"'",null);
//                if (cursor.getCount() == 0) {
//                    db.execSQL("INSERT INTO subs (groups) VALUES ('" + groupSelected + "')");
//                    addEventsToSubList();
//                    createEventNotifications();
//                }
//                cursor.close();
//                Log.i("saved to database", groupSelected);
//                return true;
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                for (EventHolder holder : eventAdapter.getViewHolders()) {
                    holder.close();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void manualAddSub(Event e) {
        if (manualSubs.contains(e)) {
            return;
        }
        manualSubs.add(e);
        if (!subList.contains(e)) {
            subList.add(e);
        }
        db.execSQL("INSERT INTO manuals (events) VALUES ('"+e.hashCode()+"')");
        Log.i("manualdb","added "+e.getTitle());
        createEventNotification(e);
    }

    public static void manualCancelSub(Event e) {
        manualSubs.remove(e);
        subList.remove(e);
        db.execSQL("DELETE FROM manuals WHERE events = "+e.hashCode());
        cancelEventNotification(e);
        if (toolbar.getTitle().equals("Current Subscribed Events")) {
            eventArrayListToShow.remove(e);
        }
        eventAdapter.notifyDataSetChanged();
    }

    public static ArrayList<Event> getSubList() {
        return subList;
    }


    public void changeSubState(View v) {
        int counter = 0;
        ImageView star = (ImageView) v;
        String groupSelected = ((TextView) ((ViewGroup) star.getParent()).getChildAt(0)).getText().toString();
        if (star.getTag().toString().equals("off")) {
            Cursor cursor = db.rawQuery("SELECT * FROM subs WHERE groups= '" + groupSelected + "'", null);
            if (cursor.getCount() == 0) {
                db.execSQL("INSERT INTO subs (groups) VALUES ('" + groupSelected + "')");
                for (Group g : allGroups) {
                    if (g.getGroupName().equals(groupSelected)) {
                        g.setSub(true);
                        groupAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                for (Event e : eventArrayList) {
                    if (e.getGroup().equals(groupSelected) && !subList.contains(e)) {
                        subList.add(e);
                        createEventNotification(e);
                    }
                }

            }
            cursor.close();
            Log.i("saved", groupSelected);
        } else {
            db.execSQL("DELETE FROM subs WHERE groups= '" + groupSelected + "'");
            for (final int[] i = {0}; i[0] < subList.size(); i[0]++) {
                final Event e = subList.get(i[0]);
                if (e.getGroup().equals(groupSelected)) {
                    if (!manualSubs.contains(e)) {
                        cancelEventNotification(subList.remove(i[0]));
                        i[0]--;
                    } else {
                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Reminder Conflict")
                                .setMessage("You have manually created a notification in the group which you are unsubscribing from. Would you like to keep the reminder for "+e.getTitle()+"?")
                                .setPositiveButton("Yes", null)
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        subList.remove(e);
                                        cancelEventNotification(e);
                                        manualSubs.remove(e);
                                    }
                                })
                                .show();
                    }
                }
            }

            for (Group g : allGroups) {
                if (g.isSub())
                    counter++;
                if (g.getGroupName().equals(groupSelected)) {
                    g.setSub(false);
                    groupAdapter.notifyDataSetChanged();
                    break;
                }
            }
            Log.i("removed", groupSelected);
        }
    }

    private void addEventsToSubList() {
        ArrayList<String> groupsSubbed = new ArrayList<>();
        Cursor groupsCursor = db.rawQuery("SELECT * FROM subs", null);
        int groupsIndex = groupsCursor.getColumnIndex("groups");
        int filtersIndex = groupsCursor.getColumnIndex("filters");
        groupsCursor.moveToFirst();
        while (!groupsCursor.isAfterLast()) {
            String group = groupsCursor.getString(groupsIndex);
            if (group != null) {
                groupsSubbed.add(group);
                for (Group g : allGroups) {
                    if (g.getGroupName().equals(group)) {
                        g.setSub(true);
                        groupAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            groupsCursor.moveToNext();
        }
        for (String g : groupsSubbed) {
            for (Event e : SplashActivity.result) {
                if (e.getGroup().equals(g))
                    subList.add(e);
            }
        }
        Log.i("subbedEvents", "num: " + subList.size());
        groupsCursor.close();
        Cursor manualCursor = db.rawQuery("SELECT * FROM manuals",null);
        int eventIndex = manualCursor.getColumnIndex("events");
        manualCursor.moveToFirst();
        ArrayList<Integer> eventHashes = new ArrayList<>();
        while (!manualCursor.isAfterLast()) {
            eventHashes.add(manualCursor.getInt(eventIndex));
            manualCursor.moveToNext();
        }
        manualCursor.close();
        Log.i("manualdb","num manuals: "+eventHashes.size());
        for (Event e : eventArrayList) {
            if (eventHashes.isEmpty()) {
                break;
            }
            if (eventHashes.contains(e.hashCode())) {
                subList.add(e);
                manualSubs.add(e);
                eventHashes.remove((Integer)e.hashCode());
            }
        }
    }

    public static void createEventNotification(Event event) {
        int requestID = event.hashCode();
        Calendar calendar = Calendar.getInstance();
        String time = event.getTime();

        // The following determines if a specific start time was specified for the event,
        // and prefers that over the time JSON object, which don't always match.
        // We need to determine it's am or pm as well, since the calendar.set()
        // method wants 24 hour format.
        String start = event.getStartEnd();
        int hour = -1;
        if (start.equals("") || start.indexOf('m') <= 0) {
            start = time.substring(11, 13);
            hour += Integer.parseInt(start);
        } else {
            if (start.charAt(start.indexOf('m') - 1) == 'p') {
                hour += 12;
            }
            start = start.substring(0, start.indexOf(':'));
            hour += Integer.parseInt(start);

        }
        // subtract 1 from month because Calendar months are 0-11
        calendar.set(Integer.parseInt(time.substring(0, 4)), Integer.parseInt(time.substring(5, 7)) - 1, Integer.parseInt(time.substring(8, 10)), hour, Integer.parseInt(time.substring(14, 16)), Integer.parseInt(time.substring(17, 19)));
        Intent intent = new Intent(App.getContext(), EventNotificationReceiver.class);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("location", event.getLocation());
        // might want to look into effect of different flags
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getContext(), requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    private static void cancelEventNotification(Event event) {
        int requestID = event.hashCode();
        Intent intent = new Intent(App.getContext(), EventNotificationReceiver.class);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("location", event.getLocation());
        // might want to look into effect of different flags
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getContext(), requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Switches the current view shown to the correct view
     *
     * @param item menu item selected
     */
    private boolean setSelectedView(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_today:
                setTitle("Today at LC");
                linearLayoutMain.setVisibility(View.INVISIBLE);
                calendarView.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.VISIBLE);
                groupViewLayout.setVisibility(View.INVISIBLE);
                hideSoftKeyboard(MainActivity.this);
                createEventsToShow();
                searchEdit.setVisibility(View.VISIBLE);
                return true;
            case R.id.navigation_thismonth:
                setTitle("Month view");
                linearLayoutMain.setVisibility(View.INVISIBLE);
                calendarView.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.INVISIBLE);
                groupViewLayout.setVisibility(View.INVISIBLE);
                hideSoftKeyboard(MainActivity.this);
                return true;
            case R.id.navigation_groups:
                setTitle("Find a group");
                linearLayoutMain.setVisibility(View.INVISIBLE);
                calendarView.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.INVISIBLE);
                groupViewLayout.setVisibility(View.VISIBLE);
                hideSoftKeyboard(MainActivity.this);
                return true;
        }
        return false;
    }

    /**
     * Populates event array
     */
    private void createListData() {
        eventArrayList.addAll(SplashActivity.result);
        createEventsToShow();
    }

    /**
     * Populate event array with only events which contain the key
     *
     * @param key search key entered by the user
     */
    private void createEventsToShow(String key) {
        eventArrayListToShow.clear();
        for (Event e : eventArrayList) {
            if ((e.getTitle().toLowerCase().contains(key) || e.getDescription().toLowerCase().contains(key))) {
                e.setFirstOfDay(false);
                eventArrayListToShow.add(e);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }

    /**
     * Populates event array with events that occur on the specified day.
     * Defaults to today, can be changed by selecting a different day on the calendar
     */
    private void createEventsToShow() {
        eventArrayListToShow.clear();
        String currentDay = "";
        for (Event e : eventArrayList) {
            String eventDay = e.getTime().substring(0, 10);
            if (eventDay.equals(currentDay)) {
                e.setFirstOfDay(false);
            } else {
                e.setFirstOfDay(true);
                currentDay = e.getTime().substring(0, 10);
            }
            eventArrayListToShow.add(e);
        }
        eventAdapter.notifyDataSetChanged();
    }

    /**
     * Populates group array with only those groups which contain key
     *
     * @param key the search key entered by the user
     */
    private void createGroupsToShow(String key) {
        groupsToShow.clear();
        for (Group g : allGroups) {
            if (g.getGroupName().toLowerCase().contains(key)) {
                groupsToShow.add(g);
            }
        }
        groupAdapter.notifyDataSetChanged();

    }

    /**
     * hides the keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

    }

    /**
     * populates event array which fall under the selected group
     *
     * @param group selected group
     */
    public static void showSelectedGroup(String group) {
        eventArrayListToShow.clear();
        for (Event e : eventArrayList) {
            if (e.getGroup().toLowerCase().contains(group)) {
                e.setFirstOfDay(false);
                eventArrayListToShow.add(e);
            }
        }
        eventAdapter.notifyDataSetChanged();
        groupViewLayout.setVisibility(View.INVISIBLE);
        searchLayout.setVisibility(View.VISIBLE);
        searchEdit.setVisibility(View.GONE);
        hideSoftKeyboard((Activity) (searchLayout.getContext()));
    }

    public void showSubbedEvents() {
        eventArrayListToShow.clear();
        String currentDay = "";
        for (Event e : subList) {
            e.setFirstOfDay(false);
            String eventDay = e.getTime().substring(0, 10);
            if (eventDay.equals(currentDay)) {
                e.setFirstOfDay(false);
            } else {
                e.setFirstOfDay(true);
                currentDay = e.getTime().substring(0, 10);
            }
            eventArrayListToShow.add(e);
        }
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            hideSoftKeyboard(this);
            return true;
        }
        return super.onKeyUp(keyCode, keyEvent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (eventAdapter != null) {
            eventAdapter.saveStates(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (eventAdapter != null) {
            eventAdapter.restoreStates(savedInstanceState);
        }
    }

}
