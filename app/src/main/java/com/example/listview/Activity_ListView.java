package com.example.listview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.example.listview.ConnectivityCheck.isNetworkReachableAlertUserIfNot;
import static com.example.listview.JSONHelper.parseAll;

public class Activity_ListView extends AppCompatActivity {


	ListView my_listview;
	SharedPreferences pref;
	SharedPreferences.OnSharedPreferenceChangeListener listener;

	private String myURL = "http://www.tetonsoftware.com/bikes/";
	private String listURL = myURL + "bikes.json";

	List<BikeData> bikes;

	DownloadTask myTask;
	DownloadImageTask imageTask;
	private CustomAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Change title to indicate sort by
		setTitle("Sort by:");

		//listview that you will operate on
		my_listview = (ListView)findViewById(R.id.lv);

		myAdapter = new CustomAdapter(this);
		setListAdapter(myAdapter);

		listView = getListView();

		//toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		setupSimpleSpinner();

		//set the listview onclick listener
		setupListViewOnClickListener();

		isNetworkReachableAlertUserIfNot(this);

		//TODO call a thread to get the JSON list of bikes

		doTask();

		//TODO when it returns it should process this data with bindData
	}

	private void setupListViewOnClickListener() {
		//TODO you want to call my_listviews setOnItemClickListener with a new instance of android.widget.AdapterView.OnItemClickListener() {
	}

	/**
	 * Takes the string of bikes, parses it using JSONHelper
	 * Sets the adapter with this list using a custom row layout and an instance of the CustomAdapter
	 * binds the adapter to the Listview using setAdapter
	 *
	 * @param JSONString  complete string of all bikes
	 */
	protected void bindData(String JSONString) {
		bikes = parseAll(JSONString);

		TextView tv = (TextView) findViewById(R.id.textView);
		String s = "";

		for(int i = 0; i < bikes.size(); i++){
			//TODO implement builder constructor and check bike data
		}

		Toast.makeText(this, "Bike data succesfully retrieved!", Toast.LENGTH_SHORT).show();

	}

	Spinner spinner;
	/**
	 * create a data adapter to fill above spinner with choices(Company,Location and Price),
	 * bind it to the spinner
	 * Also create a OnItemSelectedListener for this spinner so
	 * when a user clicks the spinner the list of bikes is resorted according to selection
	 * dontforget to bind the listener to the spinner with setOnItemSelectedListener!
	 */
	private void setupSimpleSpinner() {

		spinner = (Spinner) findViewById(R.id.spinner);
		pref = PreferenceManager.getDefaultSharedPreferences(this);

		listener = new SharedPreferences.OnSharedPreferenceChangeListener(){
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if(key.equals("PREF_LIST")){
					myURL = pref.getString("PREF_LIST", "Nothing Found");
					bikes.clear();
					spinner.setAdapter(null);

					if(doNetworkCheck()){
						doTask();
					}
				}

			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
				break;
		}
		return true;
	}

	public boolean doNetworkCheck() {
		String res = isNetworkReachableAlertUserIfNot(this)?"Connected":"No Network Connection";
		return res.equals("Connected");
	}

	public void doTask(){
		myTask = new DownloadTask(this);
		myTask.execute(listURL);
	}

	public void error() {
		spinner.setEnabled(false);
		Toast.makeText(this, "An Error has Occurred", Toast.LENGTH_SHORT).show();
	}
}
