package se.gizm0.rickitick.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public final class ShowProgress {
		
		//Below is used to create unique thread names
		private static int mId;
		private int mMyId;
		private final static String THREAD_NAME = "Progress_";
		
		//The actual progress dialog
		private ProgressDialog mProgress;


		public ShowProgress(final Runnable runnable, final Context context, final Handler callback,
				final String title, final String message) {
			 mMyId = mId++;
			
			//Runnable to start the progress dialog in
			final Runnable startProgress = new Runnable() {
				public void run() {
					mProgress = ProgressDialog.show(context, title, message);
				}
			};
			
			//Handler to start the progress bar after a while
			final Handler startProgressHandler = new Handler();
			startProgressHandler.postDelayed(startProgress, 500);
			
			//Handler for removing the progress bar
			final Handler stopProgressHandler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {
	            	startProgressHandler.removeCallbacks(startProgress);
	            	if (mProgress != null) {
	            		mProgress.dismiss();
	            	}
	            }
			};
			
			//Make the actual work in another thread
			new Thread(new Runnable() {
				public void run() {
					runnable.run();
					stopProgressHandler.sendEmptyMessage(0);
					
					if (callback != null) {
						callback.sendEmptyMessage(0);
					}
				}
			}, THREAD_NAME + mMyId).start();
		}

}
