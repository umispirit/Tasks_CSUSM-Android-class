package com.example.shom853.tasks;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private static final Level LOGGING_LEVEL = Level.OFF;
	private static final String PREF_ACCOUNT_NAME = "accountName";
	static final String TAG = "TasksSample";
	static final String TASKLIST_ID = "taskListId";
	static final String TASKLIST_INDEX = "taskListIndex";
	static final String ACCOUNT_EMAIL = "accountEmail";
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	static final int REQUEST_AUTHORIZATION = 1;
	static final int REQUEST_ACCOUNT_PICKER = 2;
	static final int REQUEST_TASK_ADD = 3;
	static final int REQUEST_TASK_EDIT = 4;
	final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	GoogleAccountCredential credential;
	List<Task> tasksList;
	List<TaskList> lists;
	String listID;
	int listIndex;
	TaskAdapter adapter;
	TasksAdapter listAdapter;
	com.google.api.services.tasks.Tasks service;
	int numAsyncTasks;
	private ListView listView;
	private ListView navigationList;
	private RelativeLayout navigationHeader;
	private String accountEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable logging
		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
		// view and menu
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.list);
		// Google Accounts
		credential =
				GoogleAccountCredential.usingOAuth2(this, Collections.singleton(TasksScopes.TASKS));
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		// Tasks client
		service =
				new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential)
						.setApplicationName("Google-TasksAndroidSample/1.0").build();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Setup FAB
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// start AddActivity
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				int requestCode = REQUEST_TASK_ADD;
				startActivityForResult(intent, requestCode);

			}
		});

		// Setup Navigation Drawer
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();
		navigationList = (ListView) findViewById(R.id.navigation_drawer_list);

		// get saved data
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		int defaultListIndex = getResources().getInteger(R.integer.saved_listIndex_default);
		String defaultListID = getResources().getString(R.string.saved_listID_default);
		String defaultAccountEmail = getResources().getString(R.string.saved_accountEmail_default);
		listIndex = sharedPref.getInt(getString(R.string.saved_listIndex), defaultListIndex);
		listID = sharedPref.getString(getString(R.string.saved_listID), defaultListID);
		accountEmail = sharedPref.getString(getString(R.string.saved_accountEmail), defaultAccountEmail);

//		if(savedInstanceState != null){
//			listID = savedInstanceState.getString(TASKLIST_ID);
//			accountEmail = savedInstanceState.getString(ACCOUNT_EMAIL);
//			listIndex = savedInstanceState.getInt(TASKLIST_INDEX);
//		}
//		else{
//			listID = "@default";
//			accountEmail = "android.studio@android.com";
//			listIndex = 0;
//		}

		// get account email if saved
		if (credential.getSelectedAccountName() != null) {
			accountEmail = credential.getSelectedAccountName();
		}

		// setup navigation drawer
		navigationHeader = (RelativeLayout) findViewById(R.id.navigation_drawer_header_include);
		navigationHeader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkGooglePlayServicesAvailable()) {
					chooseAccount();
				}
				// close the drawer
				DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
				drawer.closeDrawer(GravityCompat.START);
			}
		});
		TextView email = (TextView) findViewById(R.id.accountEmail);
		email.setText(accountEmail);
	}

//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		savedInstanceState.putString(TASKLIST_ID, listID);
//		savedInstanceState.putString(ACCOUNT_EMAIL, accountEmail);
//		savedInstanceState.putInt(TASKLIST_INDEX, listIndex);
//	}

	void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog =
						GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, MainActivity.this,
								REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}

	void refreshView() {
		adapter = new TaskAdapter(this, tasksList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
	}

	void refreshNavigationList() {
		listAdapter = new TasksAdapter(this, lists);
		navigationList.setAdapter(listAdapter);
		navigationList.setOnItemClickListener(listClickListener);
		if(lists != null && lists.size() > 0) {
			// load tasks into tasklist
			AsyncLoadTasks.run(this, listID);
		}
		getSupportActionBar().setTitle(lists.get(listIndex).getTitle());

	}

	@Override
	protected void onPause(){
		super.onPause();

		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(getString(R.string.saved_listIndex), listIndex);
		editor.putString(getString(R.string.saved_listID), listID);
		editor.putString(getString(R.string.saved_accountEmail), accountEmail);
		editor.commit();
	}


	@Override
	protected void onResume() {
		super.onResume();

		if (checkGooglePlayServicesAvailable()) {
			haveGooglePlayServices();
		}
	}


	/** Check that Google Play services APK is installed and up to date. */
	private boolean checkGooglePlayServicesAvailable() {
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}

	private void haveGooglePlayServices() {
		// check if there is already an account selected
		if (credential.getSelectedAccountName() == null) {
			// ask user to choose account
			chooseAccount();
		} else {
			// load tasklists from Google
			AsyncLoadTaskLists.run(this);
		}
	}

	private void chooseAccount() {
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_GOOGLE_PLAY_SERVICES:
				if (resultCode == Activity.RESULT_OK) {
					haveGooglePlayServices();
				} else {
					checkGooglePlayServicesAvailable();
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK) {
					AsyncLoadTasks.run(this, listID);
				} else {
					chooseAccount();
				}
				break;
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
					String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						// set account email
						accountEmail = accountName;
						TextView email = (TextView) findViewById(R.id.accountEmail);
						email.setText(accountEmail);

						if(accountName.equals(accountEmail)) {
							// reset listID and listIndex to default when account changes
							listID = "@default";
							listIndex = 0;
						}
						credential.setSelectedAccountName(accountName);
						SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(PREF_ACCOUNT_NAME, accountName);
						editor.commit();
						AsyncLoadTasks.run(this, listID);
					}
				}
				break;
			case REQUEST_TASK_ADD:
				if(resultCode == Activity.RESULT_OK){
					String title = data.getStringExtra(AddActivity.ADD_TITLE);
					String completed = data.getStringExtra(AddActivity.ADD_COMPLETED);
					String notes = data.getStringExtra(AddActivity.ADD_NOTES);
					DateTime date = (DateTime) data.getSerializableExtra(AddActivity.ADD_DATE);

					Task task = new Task();
					task.setTitle(title);
					task.setNotes(notes);
					task.setStatus(completed);
					if(date != null) {
						task.setDue(date);
					}
					AsyncInsertTask.run(this, task);
				}
				break;
			case REQUEST_TASK_EDIT:
				if(resultCode == Activity.RESULT_OK){
					int index = (Integer) data.getExtras().get(EditActivity.EDIT_INDEX);
					String title = data.getStringExtra(EditActivity.EDIT_TITLE);
					String completed = data.getStringExtra(EditActivity.EDIT_COMPLETED);
					String notes = data.getStringExtra(EditActivity.EDIT_NOTES);
					DateTime date = (DateTime) data.getSerializableExtra(EditActivity.EDIT_DATE);

					Task task = tasksList.get(index);
					task.setTitle(title);
					task.setNotes(notes);
					task.setStatus(completed);
					if(completed.equals("needsAction")){
						task.setCompleted(null);
					}
					if(date != null){
						task.setDue(date);
					}
					AsyncUpdateTask.run(this, task);
				}
				else if(resultCode == EditActivity.RESULT_DELETE){
					int index = (Integer) data.getExtras().get(EditActivity.EDIT_INDEX);
					Task task = tasksList.get(index);
					AsyncDeleteTask.run(this, task.getId());
				}
				break;
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// start EditActivity
			Intent intent = new Intent(MainActivity.this, EditActivity.class);
			int requestCode = REQUEST_TASK_EDIT;
			Task task = tasksList.get(position);
			intent.putExtra(EditActivity.EDIT_INDEX,(int) id);
			intent.putExtra(EditActivity.EDIT_TITLE, task.getTitle());
			intent.putExtra(EditActivity.EDIT_COMPLETED, task.getStatus());
			intent.putExtra(EditActivity.EDIT_NOTES, task.getNotes());
			intent.putExtra(EditActivity.EDIT_DATE, task.getDue());
			startActivityForResult(intent, requestCode);
		}
	};

	AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// set list id to new list
			TaskList list = lists.get(position);
			listIndex = position;
			listID = list.getId();
			// set toolbar title
			getSupportActionBar().setTitle(lists.get(listIndex).getTitle());
			// load list task's
			if (checkGooglePlayServicesAvailable()) {
				haveGooglePlayServices();
			}
			// close the drawer
			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
			drawer.closeDrawer(GravityCompat.START);

		}
	};
}
