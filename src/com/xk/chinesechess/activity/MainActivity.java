package com.xk.chinesechess.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.xk.chinesechess.AndroidSender;
import com.xk.chinesechess.ChineseChess;
import com.xk.chinesechess.constant.MyApplication;
import com.xk.chinesechess.message.Client;
import com.xk.chinesechess.message.WindowCallback;
import com.xk.chinesechess.ui.XDialog;
import com.xk.chinesechess.ui.XMask;
import com.xk.chinesechess.utils.Constant;
import com.xk.chinesechess.utils.JSONUtil;

public class MainActivity extends AndroidApplication {
	private ADHandler handler;
	private AndroidSender sender;
	private XMask xMask;
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		boolean isMyPlace = intent.getBooleanExtra("isMyPlace", true);
		boolean isLocal = intent.getBooleanExtra("isLocal", true);
		String roomid = intent.getStringExtra("roomid");
		String enamyStr = intent.getStringExtra("enamy");
		handler = new ADHandler(this);
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = true;
		sender = new AndroidSender(handler);
		sender.setContext(this);
		ChineseChess cc = ChineseChess.getInstance(sender);
		cc.isLocal = isLocal;
		cc.isMyPlace = isMyPlace;
		cc.roomid = roomid;
		Constant.me = MyApplication.me;
		Map<String, Object> enm = JSONUtil.fromJson(enamyStr);
		Constant.enamy = new Client();
		Constant.enamy.setCname((String)enm.get("name"));
		Constant.enamy.setCid((String)enm.get("id"));
		Constant.enamy.setRoomid((String) enm.get("roomid"));
		cc.setListener(MyApplication.ml);
		initialize(cc, cfg);
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
			WindowCallback callback=new WindowCallback() {
				@Override
				public void callback() {
					finish();
				}
			};
			sender.openWindow("提示", "确定要退出吗？", callback);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
    private static class ADHandler extends Handler{
		private MainActivity activity;
		ADHandler(MainActivity activity){
			this.activity=activity;
		}
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle=msg.getData();
			String message=bundle.getString("message");
			switch(msg.what){
			case Constant.SHOW_MESSAGE:
				Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();;
				break;
			case Constant.CLOSE_MUSK:
				if(activity.xMask!=null){
					activity.xMask.dismiss();
				}
				break;
			case Constant.SHOW_MUSK:
				
				if(activity.xMask!=null){
					activity.xMask.dismiss();
				}
				activity.xMask=new XMask(activity);
				activity.xMask.setMessage(message);
				break;
			case Constant.SHOW_WINDOW:
				final WindowCallback callback=(WindowCallback) msg.obj;
				String title=bundle.getString("title");
				final XDialog dialog=new XDialog(activity);
				dialog.setTitle(title);
				dialog.setMessage(message);
				dialog.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						callback.callback();
						dialog.dismiss();
					}
				});
				dialog.setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
			default:break;
			}
		}
		
	}
	
}