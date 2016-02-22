package com.whatswall.ui;

import com.whatswall.R;
import com.whatswall.service.WWService;
import com.whatswall.service.WWService.GetToBinder;
import com.whatswall.tools.Show;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class ReportActivity extends BaseActivity {

	private String contentObjectId = "";
	private WWService mService;
	private ServiceConnection mSc;
	private final String TAG = "ReportActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		setActionBarLayout(R.layout.actionbar_layout_room);

		contentObjectId = getIntent().getExtras().getString("report");

		ImageButton back = (ImageButton) findViewById(R.id.back);
		ImageButton favourites = (ImageButton) findViewById(R.id.favourites);
		TextView title = (TextView) findViewById(R.id.title);
		favourites.setVisibility(View.GONE);
		title.setText(R.string.action_report);
		final RadioGroup types = (RadioGroup) findViewById(R.id.report_types);
		final RadioButton sex = (RadioButton) findViewById(R.id.report_sex);
		final RadioButton baoli = (RadioButton) findViewById(R.id.report_baoli);
		final RadioButton ad = (RadioButton) findViewById(R.id.report_ad);
		final RadioButton other = (RadioButton) findViewById(R.id.report_other);
		Button report = (Button) findViewById(R.id.report_enter);
		final EditText editText = (EditText) findViewById(R.id.report_edit);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		types.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				Show.logInfo(getApplication(), TAG, "checkedId=" + checkedId);
				if(other.getId() == checkedId){
					editText.setVisibility(View.VISIBLE);
				}else{
					editText.setVisibility(View.INVISIBLE);
				}
			}
		});

		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String type = "";
				int typesId = types.getCheckedRadioButtonId();
				if (typesId != -1) {
					if (typesId == sex.getId()) {
						type = "ɫ��";
					} else if (typesId == baoli.getId()) {
						type = "����";
					} else if (typesId == ad.getId()) {
						type = "���";
					} else {
						type = "����";
					}
					Show.logInfo(getApplication(), TAG, "type=" + type
							+ " contentObjectId=" + contentObjectId);
					mService.saveReportContent(type, contentObjectId, editText.getText().toString());
					finish();
				} else {
					Show.showToast(getApplication(), "��ѡ����Ҫ�ٱ������!");
				}
			}
		});

		mSc = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {

				mService = ((GetToBinder) service).getGetToService();
			}
		};
	}

	@Override
	protected void onStart() {

		super.onStart();
		Intent intent = new Intent(ReportActivity.this, WWService.class);
		this.getApplicationContext().bindService(intent, mSc,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		super.onStop();
		this.getApplicationContext().unbindService(mSc);
	}
}
