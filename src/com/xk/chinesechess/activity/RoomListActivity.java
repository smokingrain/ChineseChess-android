package com.xk.chinesechess.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.xk.chinesechess.R;
import com.xk.chinesechess.activity.adapter.RoomListAdapter;
import com.xk.chinesechess.activity.adapter.ViewTag;
import com.xk.chinesechess.constant.MyApplication;
import com.xk.chinesechess.message.MessageCallBack;
import com.xk.chinesechess.message.PackageInfo;
import com.xk.chinesechess.message.Rooms;
import com.xk.chinesechess.ui.XDialog;
import com.xk.chinesechess.ui.XMask;
import com.xk.chinesechess.utils.Constant;
import com.xk.chinesechess.utils.JSONUtil;
import com.xk.server.utils.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class RoomListActivity extends Activity implements OnItemClickListener,MessageCallBack{
	private ListView rooms;
	private RoomsHandler handler;
	private XMask xmask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rooms);
		rooms=(ListView) findViewById(R.id.rooms);
		RoomListAdapter adapter = new RoomListAdapter(this, new ArrayList<Rooms>());
		rooms.setAdapter(adapter);
		rooms.setOnItemClickListener(this);
		handler=new RoomsHandler(this);
		MyApplication.ml.registListener(this);
		refreshRooms(null);
	}
	
	public void createRoom(View view){
		LinearLayout layout=new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		final EditText text=new EditText(this){

			@Override
			public void draw(Canvas canvas) {
				int width=this.getWidth()-1;
				int height=this.getHeight()-1;
				float[]pts=new float[]{1,1,1,height,1,height,width,height,width,height,width,1,width,1,1,1};
				Paint paint = new Paint();
				paint.setStrokeWidth(2);
				paint.setStyle(Paint.Style.STROKE);
				if(StringUtil.isBlank(this.getText().toString())){
					paint.setColor(Color.RED);
				}else{
					paint.setColor(Color.GREEN);
				}
				
				canvas.drawLines(pts, paint);
				super.draw(canvas);
			}
			
		};
		text.setHint("请输入房间名(长度1-10)");
		text.setSingleLine(true);
		text.setBackgroundColor(R.drawable.bg_edittext);
		LayoutParams params2=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1);
		layout.addView(text,params2);
		final XDialog dialog=new XDialog(this);
		dialog.setTitle("创建房间");
		dialog.setEditor(layout);
		dialog.setCancelable(true);
		dialog.setPositiveButton("确定", new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				String name=text.getText().toString();
				if(StringUtil.isBlank(name)){
					return;
				}
				if(name.length()>10){
					return;
				}
//				Rooms room=new Rooms();
//				room.setCreater(MyApplication.me);
//				room.setName(name);
				
				Map<String, Object> room = new HashMap<String, Object>();
				room.put("name", name);
				room.put("type", 1);
				PackageInfo info = new PackageInfo("server", JSONUtil.toJosn(room), MyApplication.me.getCid(), Constant.MSG_CROOM,Constant.APP, 0);
				System.out.println("CROOM:"+JSONUtil.toJosn(info));
				MyApplication.nc.writeMessage(JSONUtil.toJosn(info));
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("取消", new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}
	
	public void refreshRooms(View view){
		PackageInfo info=new PackageInfo("server", "", MyApplication.me.getCid(), Constant.MSG_ROOMS, Constant.APP, 0);
		MyApplication.nc.writeMessage(JSONUtil.toJosn(info));
	}

	public void goBack(View view){
		MyApplication.nc.close(false);
		MyApplication.serverip=null;
		this.finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.ml.unregistListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View view, int arg2, long arg3) {
		xmask=new XMask(this);
		xmask.setMessage("连接服务器...");
		final ViewTag tag=(ViewTag) view.getTag();
		PackageInfo info=new PackageInfo(tag.sip, "", MyApplication.me.getCid(), Constant.MSG_JOIN, Constant.APP, 0);
		System.out.println(JSONUtil.toJosn(info));
		MyApplication.nc.writeMessage(JSONUtil.toJosn(info));
	}

	@Override
	public boolean callBack(PackageInfo info) {
		Message msg=new Message();
		msg.what=Constant.GET_MESSAGE;
		msg.obj=info;
		handler.sendMessage(msg);
		return false;
	}
	
	public void handleMessage(PackageInfo pack){
		if(Constant.MSG_ROOMS.equals(pack.getType())){
			String msg=pack.getMsg();
			JavaType type = JSONUtil.getCollectionType(List.class, Map.class);
			List<Map<String, Object>> rooms = JSONUtil.toBean(msg, type);
			List<Rooms> rms = new ArrayList<Rooms>();
			for(Map<String, Object> map : rooms) {
				Rooms room = new Rooms();
				room.setId((String) map.get("id"));
				room.setName((String) map.get("name"));
				room.setCreator((String) map.get("creator"));
				room.setCreateTime((String) map.get("createTime"));
				room.setMembers((List<String>) map.get("members"));
				room.setType((Integer) map.get("type"));
				rms.add(room);
			}
			initRooms(rms);
		}else if(Constant.MSG_CROOM.equals(pack.getType())){
			String msg = pack.getMsg();
			if(null == msg) {
				Toast.makeText(getApplicationContext(), "创建房间失败", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent=new Intent(this,MainActivity.class);
			intent.putExtra("isMyPlace", true);
			intent.putExtra("isLocal", false);
			intent.putExtra("roomid", msg);
			MyApplication.me.setRoomid(msg);
			startActivity(intent);
		}else if(Constant.MSG_JOIN.equals(pack.getType())){
			if(!MyApplication.me.getCid().equals(pack.getFrom())) {
				return;	
			}
			xmask.dismiss();
			if(null == pack.getMsg()){
				Toast.makeText(getApplicationContext(), "加入房间失败", Toast.LENGTH_SHORT).show();
				return;
			}
			Map<String, Object> room = JSONUtil.fromJson(pack.getMsg());
			Intent intent=new Intent(this,MainActivity.class);
			intent.putExtra("isMyPlace", false);
			intent.putExtra("isLocal", false);
			intent.putExtra("roomid", (String)room.get("id"));
			intent.putExtra("enamy", JSONUtil.toJosn(room.get("creator")));
			startActivity(intent);
		}else if(Constant.MSG_DISCONNECT.equals(pack.getType())){
			Toast.makeText(getApplicationContext(), "服务器连接中断", Toast.LENGTH_SHORT).show();
			MyApplication.serverip=null;
			this.finish();
		}
	}
	
	private void initRooms(List<Rooms> list) {
		RoomListAdapter adapter=(RoomListAdapter) rooms.getAdapter();
		adapter.reset(list);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
			MyApplication.nc.close(false);
			MyApplication.serverip=null;
		}
		return super.onKeyDown(keyCode, event);
	}

	private static class RoomsHandler extends Handler{
		private RoomListActivity activity;
		RoomsHandler(RoomListActivity activity){
			this.activity=activity;
		}
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constant.GET_MESSAGE:
				PackageInfo pack=(PackageInfo) msg.obj;
				System.out.println("GETMSG:"+JSONUtil.toJosn(pack));
				activity.handleMessage(pack);
				break;
			case Constant.REFRESH_DATA:
			default:break;
			}
		}
		
	}
	
}
