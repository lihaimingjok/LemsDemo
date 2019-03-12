package com.pcjz.lems.business.base;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.TDevice;

public class BaseListAdapter extends MyBaseAdapter {
	
	public static final int STATE_EMPTY_ITEM = 0;
	public static final int STATE_LOAD_MORE = 1;
	public static final int STATE_NO_MORE = 2;
	public static final int STATE_NO_DATA = 3;
	public static final int STATE_LESS_ONE_PAGE = 4;
	public static final int STATE_NETWORK_ERROR = 5;
	protected int state = STATE_LESS_ONE_PAGE;
	
	protected int _loadmoreText;
	protected int _loadFinishText;
	
	private View loadMore;
	private LinearLayout llLoadMore;
	private TextView tvLoadMore;
	private ProgressBar pbLoadMore;
	private OnNetErrorListener onNetErrorListener;

	public void setState(int state) {
		this.state = state;
		notifyDataSetChanged();
	}

	public int getState() {
		return this.state;
	}

	public BaseListAdapter() {
		_loadmoreText = R.string.loading_more;
		_loadFinishText = R.string.loading_no_more;
	}

	@Override
	public int getCount() {
		switch (getState()) {
		case STATE_EMPTY_ITEM:
			return getDataSize() + 1;
		case STATE_NETWORK_ERROR:
		case STATE_LOAD_MORE:
			return getDataSize() + 1;
		case STATE_NO_DATA:
			return 0;
		case STATE_NO_MORE:
			return getDataSize() + 1;
		case STATE_LESS_ONE_PAGE:
			return getDataSize();
		default:
			break;
		}
		return getDataSize();
	}

	public void setLoadmoreText(int loadmoreText) {
		_loadmoreText = loadmoreText;
	}

	public void setLoadFinishText(int loadFinishText) {
		_loadFinishText = loadFinishText;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		if (position == getCount() - 1) {// 最后一行
			if (getState() == STATE_LOAD_MORE || getState() == STATE_NO_MORE
					|| state == STATE_EMPTY_ITEM
					|| getState() == STATE_NETWORK_ERROR) {
				initLoadMore(parent.getContext());
				switch (getState()) {
				case STATE_LOAD_MORE:
					llLoadMore.setVisibility(View.VISIBLE);
					tvLoadMore.setVisibility(View.VISIBLE);
					pbLoadMore.setVisibility(View.VISIBLE);
					tvLoadMore.setText(_loadmoreText);
					llLoadMore.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							return true;
						}
					});
					break;
				case STATE_NO_MORE:
					if (getData().size() > 0) {
						llLoadMore.setVisibility(View.VISIBLE);
						tvLoadMore.setVisibility(View.VISIBLE);
						pbLoadMore.setVisibility(View.GONE);
						tvLoadMore.setText(_loadFinishText);
						llLoadMore.setOnTouchListener(new OnTouchListener() {
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								return true;
							}
						});
					}
					break;
				case STATE_EMPTY_ITEM:
					llLoadMore.setVisibility(View.GONE);
					break;
				case STATE_NETWORK_ERROR:
					llLoadMore.setVisibility(View.VISIBLE);
					tvLoadMore.setVisibility(View.VISIBLE);
					pbLoadMore.setVisibility(View.GONE);
					if (TDevice.hasInternet()) {
						tvLoadMore.setText(R.string.load_error_click_to_refresh);
					} else {
						tvLoadMore.setText(R.string.network_error_click_to_refresh);
					}
					llLoadMore.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if(event.getAction() == MotionEvent.ACTION_UP){
								if (onNetErrorListener != null) {
									onNetErrorListener.onNetError();
									pbLoadMore.setVisibility(View.VISIBLE);
									tvLoadMore.setText(_loadmoreText);
								}
							}
							return true;
						}
					});
					break;
				}
				return loadMore;
			}
		}
		return getRealView(position, convertView, parent);
	}

	public interface OnNetErrorListener {
		void onNetError();
	}

	public void setOnNetErrorListener(OnNetErrorListener onNetErrorListener) {
		this.onNetErrorListener = onNetErrorListener;
	}
	
	private void initLoadMore(Context context){
		if(loadMore == null){
			loadMore = getLayoutInflater(context).inflate(R.layout.layout_load_more, null);
			llLoadMore = (LinearLayout) loadMore.findViewById(R.id.ll_load_more);
			pbLoadMore = (ProgressBar) loadMore.findViewById(R.id.pb_load_more);
			tvLoadMore = (TextView) loadMore.findViewById(R.id.tv_load_more);
			/*llLoadMore.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});*/
		}
	}
	
}
