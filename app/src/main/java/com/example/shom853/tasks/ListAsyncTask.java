package com.example.shom853.tasks;

import android.os.AsyncTask;
import android.view.View;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.io.IOException;

/**
 * Created by Sandy on 5/17/2016.
 */
abstract class ListAsyncTask extends AsyncTask<Void, Void, Boolean> {

	final MainActivity activity;
	final com.google.api.services.tasks.Tasks client;
	private final View progressBar;

	ListAsyncTask(MainActivity activity) {
		this.activity = activity;
		client = activity.service;
		progressBar = activity.findViewById(R.id.title_refresh_progress);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		activity.numAsyncTasks++;
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected final Boolean doInBackground(Void... ignored) {
		try {
			doInBackground();
			return true;
		} catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
			activity.showGooglePlayServicesAvailabilityErrorDialog(
					availabilityException.getConnectionStatusCode());
		} catch (UserRecoverableAuthIOException userRecoverableException) {
			activity.startActivityForResult(
					userRecoverableException.getIntent(), MainActivity.REQUEST_AUTHORIZATION);
		} catch (IOException e) {
			Utils.logAndShow(activity, MainActivity.TAG, e);
		}
		return false;
	}

	@Override
	protected final void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		if (0 == --activity.numAsyncTasks) {
			progressBar.setVisibility(View.GONE);
		}
		if (success) {
			activity.refreshNavigationList();
		}
	}

	abstract protected void doInBackground() throws IOException;
}
