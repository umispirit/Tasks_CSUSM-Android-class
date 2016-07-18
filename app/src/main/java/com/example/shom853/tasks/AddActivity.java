package com.example.shom853.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.api.client.util.DateTime;

public class AddActivity extends AppCompatActivity {

	static final String ADD_TITLE = "ADD_TITLE";
	static final String ADD_COMPLETED = "ADD_COMPLETED";
	static final String ADD_NOTES = "ADD_NOTES";
	static final String ADD_DATE = "ADD_DATE";
	DateTime dueDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_add_ok) {
			// get layout objects
			EditText title = (EditText) findViewById(R.id.editTitle);
			CheckBox completed = (CheckBox) findViewById(R.id.editCompleted);
			EditText date = (EditText) findViewById(R.id.editDate);
			EditText notes = (EditText) findViewById(R.id.addNotes);

			Intent intent = new Intent();
			intent.putExtra(ADD_TITLE ,title.getText().toString());
			intent.putExtra(ADD_NOTES ,notes.getText().toString());
			// TODO: after date picker is implemented, replace line below to read in DateTime dueDate
			if(date != null && !date.getText().toString().equals("")){
				DateTime datetime = new DateTime(System.currentTimeMillis() + 3600000);
				intent.putExtra(ADD_DATE, datetime);
			}
			if(completed.isChecked()){
				intent.putExtra(ADD_COMPLETED ,"completed");
			}
			else{
				intent.putExtra(ADD_COMPLETED, "needsAction");
			}

			setResult(RESULT_OK, intent);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	// TODO: add onClick Listener for date edit view to start a date picker
		// get DateTime from datepicker and save in dueDate
}
