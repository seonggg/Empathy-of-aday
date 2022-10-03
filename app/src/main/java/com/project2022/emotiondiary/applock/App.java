package com.project2022.emotiondiary.applock;

import android.app.Application;

import com.project2022.emotiondiary.applock.core.LockManager;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LockManager.getInstance().enableAppLock(this);
	}

}
