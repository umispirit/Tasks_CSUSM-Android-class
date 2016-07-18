package com.example.shom853.tasks;

import com.google.api.services.tasks.model.TaskList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandy on 5/17/2016.
 */
class AsyncLoadTaskLists extends ListAsyncTask {
	AsyncLoadTaskLists(MainActivity tasksSample) {
		super(tasksSample);
	}

	@Override
	protected void doInBackground() throws IOException {
		List<TaskList> results = new ArrayList<>();
		List<TaskList> taskLists = client.tasklists().list().execute().getItems();
		if(taskLists != null) {
			for (TaskList taskList : taskLists) {
				results.add(taskList);
			}
		}
		activity.lists = results;
	}

	static void run(MainActivity tasksSample) {
		new AsyncLoadTaskLists(tasksSample).execute();
	}
}
