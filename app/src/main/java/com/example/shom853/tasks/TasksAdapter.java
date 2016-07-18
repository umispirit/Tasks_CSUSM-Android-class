package com.example.shom853.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.util.List;

/**
 * Created by Sandy on 5/17/2016.
 */
public class TasksAdapter extends ArrayAdapter<TaskList> {

	public TasksAdapter(Context context, List<TaskList> taskLists) {
		super(context, 0, taskLists);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		// get data item from ArrayList for position
		final TaskList one_tasklist = getItem(position);

		// inflate layout for list item if it doesn't exist
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.tasklist_item, parent, false);
		}
		TextView title = (TextView) convertView.findViewById(R.id.listTitle);
		title.setText(one_tasklist.getTitle());

		return convertView;
	}
}