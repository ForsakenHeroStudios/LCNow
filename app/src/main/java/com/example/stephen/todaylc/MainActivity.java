package com.example.stephen.todaylc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // A list of cards with relevant event info
    private RecyclerView eventRecyclerView, searchRecyclerView;
    // a list of all event groups
    private ListView groupListView;
    private EventAdapter eventAdapter;
    private ArrayAdapter<String> groupAdapter;
    private ArrayList<String> allGroups, groupsToShow;
    private ArrayList<Event> eventArrayList, eventArrayListToShow, subList;
    // calendar to select events occurring on a certain day that the user would like to be shown
    private CalendarView calendarView;
    // current form of navigation between views
    private BottomNavigationView bottomNavigationView;
    // button that sends the user from the request to add event page to an email service of their choice
    private Button requestButton;
    // various text fields
    private EditText nameEdit, emailEdit, organizationEdit, editDescription, editDate, editTime, editLocation, titleEdit, searchEdit, groupEdit;
    // date format to find which events should be displayed when a certain date is selected
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    // selected day
    private String day;
    private LinearLayout searchLayout, linearLayoutMain, addEventLayout, groupViewLayout;
    // the email to request to add an event is sent to this address
    private final String MAIL_TO = "sbaker@lclark.edu"; // TODO: change this to Jason's email

    private SQLiteDatabase db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return setSelectedView(item);
        }
    };

    // for more good stuff with eventRecyclerView: https://medium.com/@droidbyme/android-recyclerview-fca74609725e


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = this.openOrCreateDatabase("Subscriptions",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS subs (groups VARCHAR, filters VARCHAR, id INTEGER PRIMARY KEY)");
        Cursor c = db.rawQuery("SELECT * FROM subs",null);
        int groupsIndex = c.getColumnIndex("groups");
        int filtersIndex = c.getColumnIndex("filters");
        int idIndex = c.getColumnIndex("id");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Log.i("group", ""+c.getString(groupsIndex));
            Log.i("filter", ""+c.getString(filtersIndex));
            c.moveToNext();
        }
        c.close();

        subList = new ArrayList<>();
        addEventsToSubList();
        createEventNotifications();


        day = SDF.format(new Date());

        linearLayoutMain = findViewById(R.id.linearLayoutMain);

        // add event initialization
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

        //initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attempt to make bottom navigation appear neater
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            removeTextLabel(bottomNavigationView, bottomNavigationView.getMenu().getItem(i).getItemId());
        }
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }

        // gives the icons colors
        bottomNavigationView.setItemIconTintList(null);

        // main  event display initialization
        eventRecyclerView = (RecyclerView) findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventArrayList = new ArrayList<>();
        eventArrayListToShow = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventArrayListToShow);
        eventRecyclerView.setAdapter(eventAdapter);

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
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        //TODO: lots of things to get subs to work here. Next step is enable subscription to certain filter results
        groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String groupSelected = groupsToShow.get(position);
                Cursor cursor = db.rawQuery("SELECT * FROM subs WHERE groups= '"+groupSelected+"'",null);
                if (cursor.getCount() == 0) {
                    db.execSQL("INSERT INTO subs (groups) VALUES ('" + groupSelected + "')");
                    addEventsToSubList();
                    createEventNotifications();
                }
                cursor.close();
                Log.i("saved to database", groupSelected);
                return true;
            }
        });
    }

    private void addEventsToSubList() {
        ArrayList<String> groupsSubbed = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM subs",null);
        int groupsIndex = c.getColumnIndex("groups");
        int filtersIndex = c.getColumnIndex("filters");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String group = c.getString(groupsIndex);
            if (group!=null) {
                groupsSubbed.add(group);
            }
            c.moveToNext();
        }
        for (String g : groupsSubbed) {
            for (Event e : SplashActivity.result) {
                if (e.getGroup().equals(g))
                    subList.add(e);
            }
        }
        Log.i("subbedEvents","num: "+ subList.size());
        c.close();
    }

    //TODO: Will calling this to create the same notification multiple times be a problem?
    private void createEventNotifications() {
        for (int i = 0; i<subList.size(); i++) {
            Event event = subList.get(i);
            int requestID = event.hashCode();
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
        }
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
                addEventLayout.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.VISIBLE);
                groupViewLayout.setVisibility(View.INVISIBLE);
                hideSoftKeyboard(MainActivity.this);
                return true;
            case R.id.navigation_thismonth:
                setTitle("Month view");
                linearLayoutMain.setVisibility(View.INVISIBLE);
                calendarView.setVisibility(View.VISIBLE);
                addEventLayout.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.INVISIBLE);
                groupViewLayout.setVisibility(View.INVISIBLE);
                hideSoftKeyboard(MainActivity.this);
                return true;
            case R.id.navigation_add_event:
                setTitle("Request to post an event");
                linearLayoutMain.setVisibility(View.INVISIBLE);
                calendarView.setVisibility(View.INVISIBLE);
                addEventLayout.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.INVISIBLE);
                groupViewLayout.setVisibility(View.INVISIBLE);
                hideSoftKeyboard(MainActivity.this);
                return true;
            case R.id.navigation_groups:
                setTitle("Find a group");
                linearLayoutMain.setVisibility(View.INVISIBLE);
                calendarView.setVisibility(View.INVISIBLE);
                addEventLayout.setVisibility(View.INVISIBLE);
                searchLayout.setVisibility(View.INVISIBLE);
                groupViewLayout.setVisibility(View.VISIBLE);
                hideSoftKeyboard(MainActivity.this);
                return true;
        }
        return false;
    }

    /**
     * Called when the request button is pressed. Sends formatted user inputs to third party email
     * service of the user's choice.
     * @param v the view which called this method
     */
    public void request(View v) {
        String emailBody = "Hi Jason,\n\n" +
                "I'm with " + organizationEdit.getText() +
                " and we would like to add our event, " + titleEdit.getText() +
                " to the LC events calendar." +
                " It will take place at " + editLocation.getText() +
                " on " + editDate.getText() + " at " + editTime.getText() + ". " +
                "The description is below, and the image is attached.\n\n" +
                "Thank you for your time,\n" + nameEdit.getText() + "\n\n" +
                editDescription.getText() + "\n\n" +
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
            startActivity(Intent.createChooser(emailIntent, "Send email"));
            finish();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "No email client installed.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Further attempts to improve bottom navigation appearance
     */
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

    /**
     * Populates event array
     */
    private void createListData() {
        for (Event e : SplashActivity.result) {
            eventArrayList.add(e);
        }
        createEventsToShow();
    }

    /**
     * Populate event array with only events which contain the key
     * @param key search key entered by the user
     */
    private void createEventsToShow(String key) {
        eventArrayListToShow.clear();
        for (Event e : eventArrayList) {
            if ((e.getTitle().toLowerCase().contains(key) || e.getDescription().toLowerCase().contains(key))) {
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
        for (Event e : eventArrayList) {
            Log.i("Date", e.getTime().substring(0, 10) + ", " + day);
            if (e.getTime().substring(0, 10).equals(day)) {
                eventArrayListToShow.add(e);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }

    /**
     * Populates group array with only those groups which contain key
     * @param key the search key entered by the user
     */
    private void createGroupsToShow(String key) {
        groupsToShow.clear();
        for (String s : allGroups) {
            if (s.toLowerCase().contains(key)) {
                groupsToShow.add(s);
            }
        }
        groupAdapter.notifyDataSetChanged();

    }

    /**
     * hides the keyboard
     */
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    /**
     * populates event array which fall under the selected group
     * @param group selected group
     */
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
        if (keyCode == KeyEvent.KEYCODE_ENTER && addEventLayout.getVisibility() == View.INVISIBLE) {
            hideSoftKeyboard(this);
            return true;
        }
        return super.onKeyUp(keyCode, keyEvent);
    }


}
