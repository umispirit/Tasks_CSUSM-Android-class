package com.example.shom853.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.tasks.model.Task;

import java.util.List;

/**
 * Created by Sandy on 5/15/2016.
 */
public class TaskAdapter extends ArrayAdapter<Task> {

	public TaskAdapter(Context context, List<Task> tasksList){
		super(context, 0, tasksList);
	}

	public View getView(int position, View convertView, ViewGroup parent){
		// get data item from ArrayList for position
		final Task one_task = getItem(position);

		// inflate layout for list item if it doesn't exist
		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
		}

		// Lookup views for data population
		TextView title = (TextView) convertView.findViewById(R.id.task_title);
		TextView note = (TextView) convertView.findViewById(R.id.task_note);
		CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.completed);

		// Populate data
		title.setText(one_task.getTitle());
		note.setText(one_task.getNotes());
		String status = one_task.getStatus();
		if(status != null && status.equals("completed")){
			checkbox.setChecked(true);
		}
		else {
			checkbox.setChecked(false);
		}

		checkbox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: find out why changing status of any completed task will get a Error 400: Bad Request

				// set setStatus to inverse
				String status = one_task.getStatus();
				if(status != null && status.equals("completed")){
					one_task.setStatus("needsAction");
					one_task.setCompleted(null);
				}
				else {
					one_task.setStatus("completed");
				}
				AsyncUpdateTask.run((MainActivity) getContext(), one_task);
				// update listView
				TaskAdapter.this.notifyDataSetChanged();
			}
		});

		return convertView;
	}

}

