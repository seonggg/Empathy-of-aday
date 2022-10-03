package com.project2022.emotiondiary.applock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.project2022.emotiondiary.LoginActivity;
import com.project2022.emotiondiary.MyRoom;
import com.project2022.emotiondiary.R;
import com.project2022.emotiondiary.SettingActivity;
import com.project2022.emotiondiary.applock.core.AppLock;
import com.project2022.emotiondiary.applock.core.AppLockActivity;
import com.project2022.emotiondiary.applock.core.BaseActivity;
import com.project2022.emotiondiary.applock.core.LockManager;

public class HomePage extends BaseActivity implements OnClickListener {
	//public static final String TAG = "HomePage";

	private Button btOnOff;
	private Button btChange;
	String path;
	public static Activity homePage;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_home);

		btOnOff = (Button) findViewById(R.id.bt_on_off);
		btOnOff.setOnClickListener(this);

		btChange = (Button) findViewById(R.id.bt_change);
		btChange.setText(R.string.change_passcode);
		btChange.setOnClickListener(this);

		updateUI();

		Intent pathIntent = getIntent();
		path = pathIntent.getStringExtra("path");

		homePage = HomePage.this;
		String path = ((SettingActivity)SettingActivity.context_main).path;
		// path가 start이고 잠금 설정 안돼있는 경우에만 액티비티 종료
		if(path.equals("start")&&!LockManager.getInstance().getAppLock().isPasscodeSet()){
			Intent intent = new Intent(HomePage.this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.equals(btOnOff)) {
			int type = LockManager.getInstance().getAppLock().isPasscodeSet() ? AppLock.DISABLE_PASSLOCK
					: AppLock.ENABLE_PASSLOCK;
			Intent intent = new Intent(this, AppLockActivity.class);
			intent.putExtra(AppLock.TYPE, type);
			intent.putExtra("path",path);
			startActivityForResult(intent, type);
		} else if (view.equals(btChange)) {
			Intent intent = new Intent(this, AppLockActivity.class);
			intent.putExtra(AppLock.TYPE, AppLock.CHANGE_PASSWORD);
			intent.putExtra(AppLock.MESSAGE,
					getString(R.string.enter_old_passcode));
			startActivityForResult(intent, AppLock.CHANGE_PASSWORD);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case AppLock.DISABLE_PASSLOCK:
			break;
		case AppLock.ENABLE_PASSLOCK:
		case AppLock.CHANGE_PASSWORD:
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, getString(R.string.setup_passcode),
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		updateUI();
	}

	private void updateUI() {
		if (LockManager.getInstance().getAppLock().isPasscodeSet()) {
			btOnOff.setText(R.string.disable_passcode);
			btChange.setEnabled(true);
		}
		else {
			btOnOff.setText(R.string.enable_passcode);
			btChange.setEnabled(false);
		}
	}

}
