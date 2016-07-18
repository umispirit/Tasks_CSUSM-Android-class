package com.example.shom853.tasks;

import java.io.IOException;

/**
 * Created by Sandy on 5/16/2016.
 */
class AsyncDeleteTask extends CommonAsyncTask{
	String taskID;
	String listID;

	AsyncDeleteTask(MainActivity tasksSample, String taskID) {
		super(tasksSample);
		this.taskID = taskID;
		listID = tasksSample.listID;
	}

	@Override
	protected void doInBackground() throws IOException {
		client.tasks().delete(listID, taskID).execute();
	}

	static void run(MainActivity tasksSample, String taskID) {
		new AsyncDeleteTask(tasksSample, taskID).execute();
	}
}
