package com.xk.chinesechess.activity.adapter;

import java.util.ArrayList;



import java.util.HashMap;
import java.util.List;


import java.util.Map;

import com.xk.chinesechess.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServerListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	
	
	
	public ServerListAdapter(Context context,List<Map<String,String>> list){
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Map<String,String> sip = list.get(position);
		view = LayoutInflater.from(context).inflate(R.layout.server_item_view, parent, false);
		TextView v1=(TextView) view.findViewById(R.id.sid);
		TextView v2=(TextView) view.findViewById(R.id.sip);
		v1.setText(position+1+"");
		v2.setText(sip.get("name"));
		ViewTag tag=new ViewTag();
		tag.sid=position+1;
		tag.sip=sip.get("ip");
		view.setTag(tag);
		return view;
	}
	
	public void reset(List<Map<String,String>> data){
		Map<String,String> temp=new HashMap<String,String>();
		temp.put("name", "公共服务器");
		temp.put("ip", "10.60.15.162");
		data.add(temp);
		list=data;
	}
}
