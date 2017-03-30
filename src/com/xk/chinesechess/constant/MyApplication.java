package com.xk.chinesechess.constant;

import com.xk.chinesechess.message.Client;
import com.xk.chinesechess.message.MessageListener;
import com.xk.chinesechess.net.MinaClient;

import a.b.c.DynamicSdkManager;
import android.app.Application;

public class MyApplication extends Application {
	public static MinaClient nc;
	public static MessageListener ml=new MessageListener();
	public static Client me;
	public static String serverip;
	@Override
	public void onCreate() {
		DynamicSdkManager.getInstance(this).loadInDate("2015-11-02");
		try {
            DynamicSdkManager.onCreate(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
		super.onCreate();
	}
	
}
