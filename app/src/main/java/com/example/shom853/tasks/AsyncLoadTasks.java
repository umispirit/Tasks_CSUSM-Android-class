package com.example.shom853.tasks;

import com.google.api.services.tasks.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandy on 5/15/2016.
 */
class AsyncLoadTasks extends CommonAsyncTask {

	String listID;

	AsyncLoadTasks(MainActivity tasksSample, String listID) {
		super(tasksSample);
		this.listID = listID;
	}

	@Override
	protected void doInBackground() throws IOException {
		List<Task> results = new ArrayList<Task>();
		List<Task> tasks = client.tasks().list(listID).execute().getItems();
		if(tasks != null) {
			for (Task task : tasks) {
				results.add(task);
			}
		}
		activity.tasksList = results;
	}

	static void run(MainActivity tasksSample, String listID) {
		new AsyncLoadTasks(tasksSample, listID).execute();
	}
}