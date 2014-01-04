package com.jywave.ui.activities;

import com.jywave.AppMain;
import com.jywave.Player;
import com.jywave.R;
import com.jywave.provider.CommonProvider;
import com.jywave.provider.UserProvider;
import com.jywave.util.CommonUtil;

import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "FeedbackActivity";
	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();

	public static enum msgResult {
		NETWORK_ERROR, SENT, SENT_FAILED
	};

	private Context thisContext;

	private ImageButton btnBack;
	private ImageButton btnPlaying;
	private LinearLayout btnSend;
	private TextView txtBtnSendLabel;
	private EditText txtNickname;
	private EditText txtContent;

	private NotificationManager notificationMgr;
	private Notification.Builder notificationBuilder;

	private static final int NOTIFICATION_ID_SENDING = 1;
	private static final int NOTIFICATION_ID_SENT_SUCCESSFULLY = 2;
	private static final int NOTIFICATION_ID_SENT_FAILED = 3;
	private static final int NOTIFICATION_ID_NETWORK_ERROR = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		thisContext = this;

		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnPlaying = (ImageButton) findViewById(R.id.btnPlaying);
		btnSend = (LinearLayout) findViewById(R.id.btnSend);
		txtBtnSendLabel = (TextView) findViewById(R.id.txtBtnSendLabel);
		txtNickname = (EditText) findViewById(R.id.txtNickname);
		txtContent = (EditText) findViewById(R.id.txtContent);

		notificationMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationBuilder = new Builder(thisContext);

		if (!player.isPlaying) {
			btnPlaying.setVisibility(View.GONE);
		}

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		btnPlaying.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("epIndex", player.playingIndex);
				intent.setClass(v.getContext(), PlayerActivity.class);
				startActivity(intent);
			}
		});

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validate()) {
					send();
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	};

	private boolean validate() {
		txtContent.setError(null);
		String content = txtContent.getText().toString().trim();

		if (content.equals("")) {
			txtContent.setError(Html
					.fromHtml("<font color='black'>请写下您的建议后再发送哦</font>"));
			txtContent.requestFocus();
			return false;
		}

		return true;
	}

	private void send() {
		notificationBuilder.setSmallIcon(R.drawable.ico_paper_plane);
		notificationBuilder.setTicker(getString(R.string.sending));
		notificationBuilder.setWhen(System.currentTimeMillis());
		Notification n = notificationBuilder.getNotification();
		notificationMgr.notify(NOTIFICATION_ID_SENDING, n);
		btnSend.setClickable(false);
		SendFeedbackTask task = new SendFeedbackTask();
		task.execute(new Void[] {});
	}

	private void back() {
		notificationMgr.cancelAll();
		finish();
	}

	class SendFeedbackTask extends AsyncTask<Void, Void, Void> {
		private msgResult resultCode;

		@Override
		protected Void doInBackground(Void... params) {

			if (CommonUtil.checkNetState(thisContext)) {
				if (app.userId == 0) {
					UserProvider userProvider = new UserProvider(thisContext);
					userProvider.activeUser();
				}

				CommonProvider commonProvider = new CommonProvider(thisContext);
				if (commonProvider.feedback(txtNickname.getText().toString(),
						txtContent.getText().toString())) {
					resultCode = msgResult.SENT;
				} else {
					resultCode = msgResult.SENT_FAILED;
				}
			} else {
				resultCode = msgResult.NETWORK_ERROR;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			notificationMgr.cancel(NOTIFICATION_ID_SENDING);
			if (resultCode == msgResult.NETWORK_ERROR) {
				notificationBuilder.setSmallIcon(R.drawable.ico_network);
				notificationBuilder.setTicker(getString(R.string.network_conn_error_try_again_later));
				notificationBuilder.setWhen(System.currentTimeMillis());
				Notification n = notificationBuilder.getNotification();
				notificationMgr.notify(NOTIFICATION_ID_NETWORK_ERROR, n);
				btnSend.setClickable(true);
				notificationMgr.cancelAll();
			} else if (resultCode == msgResult.SENT) {
				notificationBuilder.setSmallIcon(R.drawable.ico_checkmark);
				notificationBuilder.setTicker(getString(R.string.sent_successfully));
				notificationBuilder.setWhen(System.currentTimeMillis());
				Notification n = notificationBuilder.getNotification();
				notificationMgr.notify(NOTIFICATION_ID_SENT_SUCCESSFULLY, n);
				back();
			} else if (resultCode == msgResult.SENT_FAILED) {
				notificationBuilder.setSmallIcon(R.drawable.ico_delete);
				notificationBuilder.setTicker(getString(R.string.sent_failed));
				notificationBuilder.setWhen(System.currentTimeMillis());
				Notification n = notificationBuilder.getNotification();
				notificationMgr.notify(NOTIFICATION_ID_SENT_FAILED, n);
				btnSend.setClickable(true);
				notificationMgr.cancelAll();
			}
			
		}

	}

}
