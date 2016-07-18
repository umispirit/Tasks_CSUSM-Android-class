package com.example.shom853.tasks;

import com.google.api.services.tasks.model.Task;

import java.io.IOException;

/**
 * Created by Sandy on 5/16/2016.
 */
class AsyncInsertTask extends CommonAsyncTask{

	Task task;
	String listID;

	AsyncInsertTask(MainActivity tasksSample, Task task) {
		super(tasksSample);
		this.task = task;
		listID = tasksSample.listID;
	}

	@Override
	protected void doInBackground() throws IOException {
		client.tasks().insert(listID, task).execute();
	}

	static void run(MainActivity tasksSample, Task task) {
		new AsyncInsertTask(tasksSample, task).execute();
	}
}
