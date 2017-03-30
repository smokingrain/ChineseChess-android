package com.xk.chinesechess;

import com.xk.server.ServerLauncher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MessageService extends Service {
	private ServerLauncher sl;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("服务启动");
		sl=new ServerLauncher();
		sl.lunch();
	}
	@Override
	public void onDestroy() {
		System.out.println("服务停止");
		sl.stop();
		super.onDestroy();
	}
	
	
}
