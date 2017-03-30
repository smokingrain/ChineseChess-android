package com.xk.chinesechess.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.xk.chinesechess.R;
import com.xk.chinesechess.message.Rooms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RoomListAdapter extends BaseAdapter {
	private Context context;
	private List<Rooms> list = new ArrayList<Rooms>();
	
	
	
	public RoomListAdapter(Context context,List<Rooms> list){
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
		Rooms room = list.get(position);
		view = LayoutInflater.from(context).inflate(R.layout.room_item_view, parent, false);
		TextView name=(TextView) view.findViewById(R.id.cname);
		TextView creator=(TextView) view.findViewById(R.id.creator);
		TextView cstatus=(TextView) view.findViewById(R.id.cstatus);
		TextView ctime=(TextView) view.findViewById(R.id.ctime);
		name.setText(room.getName());
		creator.setText(room.getCreater().getCname());
		cstatus.setText(null==room.getClient()?"等待中":"正在游戏");
		ctime.setText(room.getCreateTime());
		ViewTag tag=new ViewTag();
		tag.sid=position+1;
		tag.sip=room.getId();
		view.setTag(tag);
		return view;
	}

	public void reset(List<Rooms> data){
		list=data;
	}
}
