package com.pcjz.lems.business.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;

public class NoMsgDialog extends Dialog {

	private TextView tvTitle, tvMsg, tvCancel, tvDraft, tvCommit;
	private RelativeLayout rlPositive, rlNeutral, rlNegative;
	private View vNeutralRight, vNeutralLeft;
	private String title, msg, positive, neutral, nagative;
	private OnMyPositiveListener onMyPositiveListener;
	private OnMyNeutralListener onMyNeutralListener;
	private OnMyNegativeListener onMyNegativeListener;
	private EditText etContent;
	private boolean isEditText;

	public NoMsgDialog(Context context, String title, String msg, String positive,
					   String neutral, String nagative,
					   OnMyPositiveListener onMyPositiveListener,
					   OnMyNeutralListener onMyNeutralListener,
					   OnMyNegativeListener onMyNegativeListener) {
		super(context, R.style.FullHeightDialog);
		this.title = title;
		this.msg = msg;
		this.positive = positive;
		this.neutral = neutral;
		this.nagative = nagative;
		this.onMyPositiveListener = onMyPositiveListener;
		this.onMyNeutralListener = onMyNeutralListener;
		this.onMyNegativeListener = onMyNegativeListener;

	}

	public NoMsgDialog(Context context, String title, String msg, String positive,
					   String nagative, OnMyPositiveListener onMyPositiveListener,
					   OnMyNegativeListener onMyNegativeListener) {
		super(context, R.style.FullHeightDialog);
		this.title = title;
		this.msg = msg;
		this.positive = positive;
		this.nagative = nagative;
		this.onMyPositiveListener = onMyPositiveListener;
		this.onMyNegativeListener = onMyNegativeListener;
	}

	public NoMsgDialog(Context context, String title, String msg, String positive,
					   String nagative, OnMyPositiveListener onMyPositiveListener,
					   OnMyNegativeListener onMyNegativeListener, boolean isEditText) {
		super(context, R.style.FullHeightDialog);
		this.title = title;
		this.msg = msg;
		this.positive = positive;
		this.nagative = nagative;
		this.onMyPositiveListener = onMyPositiveListener;
		this.onMyNegativeListener = onMyNegativeListener;
		this.isEditText = isEditText;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dialog_nomsg);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER;
		DisplayMetrics dm = new DisplayMetrics();
		window.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = (int) (screenWidth * 0.8);
		window.setAttributes(params);
		//window.setBackgroundDrawableResource(R.drawable.shape_dialog);
		rlPositive = (RelativeLayout) findViewById(R.id.rl_positive);
		rlNeutral = (RelativeLayout) findViewById(R.id.rl_neutral);
		rlNegative = (RelativeLayout) findViewById(R.id.rl_negative);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvMsg = (TextView) findViewById(R.id.tv_msg);
		tvCancel = (TextView) findViewById(R.id.tv_cancel);
		tvDraft = (TextView) findViewById(R.id.tv_draft);
		tvCommit = (TextView) findViewById(R.id.tv_commit);
		vNeutralRight = findViewById(R.id.v_neutral_right);
		vNeutralLeft = findViewById(R.id.v_neutral_left);
		etContent = (EditText) findViewById(R.id.et_content);
		init();
		setListener();
	}

	private void init() {
		tvTitle.setText(title);
		tvMsg.setText(msg);
		tvCancel.setText(nagative);
		if (StringUtils.isEmpty(neutral)) {
			rlNeutral.setVisibility(View.GONE);
			vNeutralLeft.setVisibility(View.GONE);
		} else {
			tvDraft.setText(neutral);
		}
		if (isEditText) {
			tvMsg.setVisibility(View.GONE);
			etContent.setVisibility(View.VISIBLE);
		} else {
			tvMsg.setVisibility(View.VISIBLE);
			etContent.setVisibility(View.GONE);
		}
		tvCommit.setText(positive);

	}

	private void setListener() {
		rlPositive.setOnClickListener(onClickListener);
		rlNeutral.setOnClickListener(onClickListener);
		rlNegative.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			NoMsgDialog.this.dismiss();
			switch (v.getId()) {
			case R.id.rl_positive:
				if (isEditText) {
					onMyPositiveListener
							.onClick(etContent.getText().toString());
				} else {
					onMyPositiveListener.onClick();
				}
				break;
			case R.id.rl_neutral:
				onMyNeutralListener.onClick();
				break;
			case R.id.rl_negative:
				onMyNegativeListener.onClick();
				break;
			default:
				break;
			}
		}
	};
}
