package com.example.shom853.tasks;

import com.google.api.services.tasks.model.Task;

import java.io.IOException;

/**
 * Created by Sandy on 5/15/2016.
 */
class AsyncUpdateTask extends CommonAsyncTask{

	Task task;
	String listID;

	AsyncUpdateTask(MainActivity tasksSample, Task task) {
		super(tasksSample);
		this.task = task;
		listID = tasksSample.listID;
	}

	@Override
	protected void doInBackground() throws IOException {
		client.tasks().update(listID, task.getId(), task).execute();
	}

	static void run(MainActivity tasksSample, Task task) {
		new AsyncUpdateTask(tasksSample, task).execute();
	}
}
