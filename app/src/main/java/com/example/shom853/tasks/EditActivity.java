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

public class EditActivity extends AppCompatActivity {

	static final String EDIT_TITLE = "EDIT_TITLE";
	static final String EDIT_COMPLETED = "EDIT_COMPLETED";
	static final String EDIT_NOTES = "EDIT_NOTES";
	static final String EDIT_DATE = "EDIT_DATE";
	static final String EDIT_INDEX = "EDIT_INDEX";
	static final int RESULT_DELETE = 100;
	private int taskIndex;
	DateTime dueDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// get Task information from extras
		taskIndex = (Integer) getIntent().getExtras().get(EditActivity.EDIT_INDEX);
		String title = getIntent().getStringExtra(EditActivity.EDIT_TITLE);
		String completed = getIntent().getStringExtra(EditActivity.EDIT_COMPLETED);
		String notes = getIntent().getStringExtra(EditActivity.EDIT_NOTES);
		DateTime date = (DateTime) getIntent().getSerializableExtra(EditActivity.EDIT_DATE);

		// get view objects
		EditText editTitle = (EditText) findViewById(R.id.editTitle);
		EditText editDate = (EditText) findViewById(R.id.editDate);
		EditText editNotes = (EditText) findViewById(R.id.editNotes);
		CheckBox checkbox = (CheckBox) findViewById(R.id.editCompleted);

		// Populate data
		editTitle.setText(title);
		editNotes.setText(notes);
		if(date != null) {
			editDate.setText(date.toString());
		}
		if(completed != null && completed.equals("completed")){
			checkbox.setChecked(true);
		}
		else {
			checkbox.setChecked(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id) {
			case R.id.action_edit_ok:

				// get layout objects
				EditText title = (EditText) findViewById(R.id.editTitle);
				CheckBox completed = (CheckBox) findViewById(R.id.editCompleted);
				EditText date = (EditText) findViewById(R.id.editDate);
				EditText notes = (EditText) findViewById(R.id.editNotes);

				Intent intentOK = new Intent();
				intentOK.putExtra(EDIT_INDEX, taskIndex);
				intentOK.putExtra(EDIT_TITLE ,title.getText().toString());
				intentOK.putExtra(EDIT_NOTES ,notes.getText().toString());
				// TODO: after date picker is implemented, replace line below to read in DateTime dueDate
				if(date != null && !date.getText().toString().equals("")) {
					DateTime datetime = new DateTime(System.currentTimeMillis() + 3600000);
					intentOK.putExtra(EDIT_DATE, datetime);
				}
				if(completed.isChecked()){
					intentOK.putExtra(EDIT_COMPLETED ,"completed");
				}
				else{
					intentOK.putExtra(EDIT_COMPLETED, "needsAction");
				}

				setResult(RESULT_OK, intentOK);
				finish();

				return true;
			case R.id.action_edit_delete:
				Intent intentDelete = new Intent();
				intentDelete.putExtra(EDIT_INDEX, taskIndex);
				setResult(RESULT_DELETE, intentDelete);
				finish();

		}

		return super.onOptionsItemSelected(item);
	}

	// TODO: add onClick Listener for date edit view to start a date picker
	// get DateTime from datepicker and save in dueDate
}
