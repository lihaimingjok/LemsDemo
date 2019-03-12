package com.pcjz.lems.business.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseGridAdapter extends BaseAdapter {

	protected ArrayList _data = new ArrayList();
	private LayoutInflater mInflater;

	protected LayoutInflater getLayoutInflater(Context context) {
		if (mInflater == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		return mInflater;
	}

	public void setData(ArrayList data) {
		if (_data != null) {
			_data.clear();
			_data = null;
		}
		_data = data;
		notifyDataSetChanged();
	}

	protected int getDataSize() {
		if (_data != null)
			return _data.size();
		return 0;
	}

	@Override
	public int getCount() {
		return getDataSize();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0)
			return null;
		if (_data.size() > position) {
			return _data.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ArrayList getData() {
		return _data == null ? (_data = new ArrayList()) : _data;
	}

	public void addData(List data) {
		if (_data == null) {
			_data = new ArrayList();
		}
		_data.addAll(data);
	}

	public void clear() {
		_data.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getRealView(position, convertView, parent);
	}

	protected View getRealView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
