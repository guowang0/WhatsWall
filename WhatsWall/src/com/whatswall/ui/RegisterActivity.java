package com.whatswall.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.whatswall.R;
import com.whatswall.tools.Show;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {

	// �Ƿ��Ѿ���������֤��
	private boolean isSendCode = false;

	private final static String TAG = "RegisterActivity";

	private Button send;
	private Button register;
	private TextView contract;
	private Timer timer;
	private RegisterHandler handler;

	private ProgressDialog pd = null;

	public static final int REGISTER_SMS = 200;
	public static final int LOGIN_SMS = 201;

	private int state = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setActionBarLayout(R.layout.actionbar_layout_register);

		state = getIntent().getExtras().getInt("register");

		handler = new RegisterHandler();

		final EditText phone = (EditText) findViewById(R.id.register_phone);
		final EditText code = (EditText) findViewById(R.id.register_code);
		register = (Button) findViewById(R.id.finish);
		send = (Button) findViewById(R.id.register_send);
		contract = (TextView) findViewById(R.id.register_contract);
		TextView title = (TextView) findViewById(R.id.title);
		Button cancle = (Button) findViewById(R.id.back);

		title.setText(R.string.textview_register_title);

		if (state == LOGIN_SMS)
			loginBySMS();

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sendCode(phone.getText().toString());

			}
		});

		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pd = ProgressDialog.show(RegisterActivity.this, null, "���Ժ�..",
						true);
				if (phone.getText().toString().isEmpty()) {
					Toast.makeText(getApplication(), "�������ֻ���!",
							Toast.LENGTH_SHORT).show();
					pd.dismiss();
				} else if (code.getText().toString().isEmpty()) {
					Toast.makeText(getApplication(), "��������֤��!",
							Toast.LENGTH_SHORT).show();
					pd.dismiss();
				} else {
					AVUser.signUpOrLoginByMobilePhoneInBackground(phone
							.getText().toString(), code.getText().toString(),
							new LogInCallback<AVUser>() {

								@Override
								public void done(AVUser user, AVException e) {
									pd.dismiss();
									if (null == e) {
										Log.i(TAG, "�û�"
												+ phone.getText().toString()
												+ "ע����¼�ɹ�!");
										// startActivity(new Intent().setClass(
										// getApplication(),
										// UserInfoActivity.class));
										// finishLoginActivity();
										finish();
									} else {
										Show.disposeError(getApplication(),
												TAG, e);
									}

								}
							});
				}

			}
		});
	}

	/**
	 * ������֤��
	 */
	private void sendCode(final String phone) {
		if (!isSendCode) {
			isSendCode = true;
			// ��ʱ����ʼ
			timer = new Timer(true);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {

					Message message = handler.obtainMessage();
					message.obj = "code";
					handler.sendMessage(message);
				}
			}, 0, 1000);
			AVOSCloud.requestSMSCodeInBackgroud(phone,
					new RequestMobileCodeCallback() {

						@Override
						public void done(AVException e) {
							if (null == e) {

								Log.i(TAG, phone + "  ������֤��ɹ�!");

							} else {
								send.setClickable(true);
								send.setText(R.string.action_sendcode);
								isSendCode = false;
								timer.cancel();
								Show.disposeError(getApplication(), TAG, e);
							}
						}
					});
		}
	}

	@SuppressLint("HandlerLeak")
	class RegisterHandler extends Handler {

		// ������֮�����·���
		int sendtimer = 60;

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			if (msg.obj.toString().equals("code")) {

				send.setClickable(false);
				send.setText("���·���(" + (sendtimer--) + ")");
				if (sendtimer < 0) {
					send.setClickable(true);
					send.setText(R.string.action_sendcode);
					isSendCode = false;
					sendtimer = 60;
					timer.cancel();
				}
			}
		}

	}

	private void finishLoginActivity() {

		Intent intent = new Intent();
		intent.putExtra("data", "finish");
		intent.setAction(LoginActivity.FLAG);
		sendBroadcast(intent);
	}

	private void loginBySMS() {
		contract.setVisibility(View.INVISIBLE);
		register.setText(R.string.action_login);
	}
}
