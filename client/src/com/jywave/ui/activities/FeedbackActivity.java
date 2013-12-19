package com.jywave.ui.activities;

import com.jywave.AppMain;
import com.jywave.R;
import com.jywave.player.Player;
import com.jywave.provider.CommonProvider;
import com.jywave.provider.UserProvider;
import com.jywave.util.CommonUtil;

import android.app.Activity;
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
	
	private static final String TAG = "FeedbackActivity";
	private AppMain app = AppMain.getInstance();
	private Player player = Player.getInstance();
	
	public static enum msgResult {NETWORK_ERROR, SENT, SENT_FAILED};

	private Context thisContext;
	
	private ImageButton btnBack;
	private ImageButton btnPlaying;
	private LinearLayout btnSend;
	private TextView txtBtnSendLabel;
	private EditText txtNickname;
	private EditText txtContent;
	
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
				intent.putExtra("epIndex", player.playingIndexOfEpList);
				intent.setClass(v.getContext(), PlayerActivity.class);
				startActivity(intent);
			}
		});
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(validate())
				{
					send();
				}
				
			}
		});		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	};
	
	private boolean validate()
	{
		txtContent.setError(null);
		String content = txtContent.getText().toString().trim();
		
		if(content.equals(""))
		{
			txtContent.setError(Html.fromHtml("<font color='black'>请写下您的建议后再发送哦</font>"));
			txtContent.requestFocus();
			return false;
		}
		
		return true;
	}
	
	private void send()
	{
		txtBtnSendLabel.setText("发送中...");
		btnSend.setClickable(false);
		SendFeedbackTask task = new SendFeedbackTask();
		task.execute(new Void[]{});
	}
	
	private void back()
	{
		finish();
	}
	
	class SendFeedbackTask extends AsyncTask<Void, Void, Void>
	{
		private msgResult resultCode;

		@Override
		protected Void doInBackground(Void... params) {
			
			if(CommonUtil.checkNetState(thisContext))
			{
				if(app.userId == 0)
				{
					UserProvider userProvider = new UserProvider(thisContext);
					userProvider.activeUser();
				}
				
				CommonProvider commonProvider = new CommonProvider(thisContext);
				if(commonProvider.feedback(txtNickname.getText().toString(), txtContent.getText().toString()))
				{
					resultCode = msgResult.SENT;
				}
				else
				{
					resultCode = msgResult.SENT_FAILED;
				}
			}
			else
			{
				resultCode = msgResult.NETWORK_ERROR;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			if(resultCode == msgResult.NETWORK_ERROR)
			{
				Toast.makeText(thisContext, getString(R.string.no_network_connection_toast), Toast.LENGTH_LONG).show();
				btnSend.setClickable(true);
			}
			else if(resultCode == msgResult.SENT)
			{
				Toast.makeText(thisContext, getString(R.string.sent_successfully), Toast.LENGTH_LONG).show();
				back();
			}
			else if(resultCode == msgResult.SENT_FAILED)
			{
				Toast.makeText(thisContext, getString(R.string.sent_failed), Toast.LENGTH_LONG).show();
				btnSend.setClickable(true);
			}
		}
		
	}

}
