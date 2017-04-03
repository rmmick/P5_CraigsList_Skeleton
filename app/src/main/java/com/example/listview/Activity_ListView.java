package com.example.listview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.List;

import static com.example.listview.ConnectivityCheck.isNetworkReachableAlertUserIfNot;
import static com.example.listview.JSONHelper.parseAll;

public class Activity_ListView extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    public String myURL = "http://www.tetonsoftware.com/bikes/";
    private String listURL = myURL + "bikes.json";

    List<BikeData> bikes;

    DownloadTask myTask;
    DownloadImageTask imageTask;
    Spinner spinner;

    private RecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sort by:");

        //TODO Fix so that RecyclerView works
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new RecyclerAdapter(bikes);
        mRecyclerView.setAdapter(mAdapter);

        //TODO Code for listview that is so far unused
        //my_listview = (ListView)findViewById(R.id.lv);
        //myAdapter = new CustomAdapter(this);
        //setListAdapter(myAdapter);
        //listView = getListView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.show();

        setupSimpleSpinner();
        isNetworkReachableAlertUserIfNot(this);

        //TODO set the listview onclick listener
        setupListViewOnClickListener();

        //Initial JSON data gathered
        doTask();

        //Listener for the URL Preference Change
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("PREF_LIST")) {
                    myURL = pref.getString("PREF_LIST", "Nothing Found");
                    listURL = myURL + "bikes.json";
                    bikes.clear();
                    spinner.setAdapter(null);
                    setupSimpleSpinner();

                    Toast.makeText(Activity_ListView.this, "Now Connected to " + listURL, Toast.LENGTH_SHORT).show();

                    if (isNetworkReachableAlertUserIfNot(Activity_ListView.this)) {
                        doTask();
                    }
                }

            }
        };

        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    private void setupListViewOnClickListener() {
        //TODO you want to call my_listviews
        //TODO setOnItemClickListener with a new instance of android.widget.AdapterView.OnItemClickListener()

    }

    /**
     * Takes the string of bikes, parses it using JSONHelper
     * Sets the adapter with this list using a custom row layout and an instance of the CustomAdapter
     * binds the adapter to the Listview using setAdapter
     *
     * @param JSONString complete string of all bikes
     */
    protected void bindData(String JSONString) {

        bikes = parseAll(JSONString);

        Toast.makeText(this, "Bike data succesfully retrieved!", Toast.LENGTH_SHORT).show();
    }

    /**
     * create a data adapter to fill above spinner with choices(Company,Location and Price),
     * bind it to the spinner
     * Also create a OnItemSelectedListener for this spinner so
     * when a user clicks the spinner the list of bikes is resorted according to selection
     * dontforget to bind the listener to the spinner with setOnItemSelectedListener!
     */

    private void setupSimpleSpinner() {

        spinner = (Spinner) findViewById(R.id.spinner);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settings = new Intent(this, activityPreference.class);
                startActivity(settings);
                return true;
            default:
                error();
                break;
        }
        return true;
    }

    public void doTask() {
        myTask = new DownloadTask(this);
        myTask.execute(listURL);
    }

    public void error() {
        spinner.setEnabled(false);
        Toast.makeText(this, "An Error has Occurred", Toast.LENGTH_SHORT).show();
    }
}
