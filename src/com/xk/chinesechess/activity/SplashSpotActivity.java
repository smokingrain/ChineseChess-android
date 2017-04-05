package com.xk.chinesechess.activity;

import com.xk.chinesechess.ui.XMask;

import a.b.c.CommonManager;
import a.b.c.DynamicSdkManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashSpotActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		DynamicSdkManager.getInstance(this).setApplication(getApplication());
		// 在应用每次打开都执行一次初始化，主要用于初始化appid之类的信息
		CommonManager.getInstance(this).init("df7b4701690c1abe", "d1dcd59c9fe867f0", false);
		CommonManager.getInstance(this).setUserDataCollect(true);
		DynamicSdkManager.getInstance(this).initNormalAd();
		ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		Intent intent=new Intent(this,ServerListActivity.class);
		this.startActivity(intent);
		finish();
//		if(null!=cwjManager&&null!=cwjManager.getActiveNetworkInfo()&&cwjManager.getActiveNetworkInfo().isAvailable()&&DynamicSdkManager.getInstance(this).isDexLoadCompleted()){
//			DynamicSdkManager.getInstance(this).showSplash(this, ServerListActivity.class);
//			XMask mask=new XMask(this);
//			mask.setMessage("开始检测版本！");
//		}else{
//			Intent intent=new Intent(this,ServerListActivity.class);
//			this.startActivity(intent);
//			finish();
//		}
		
	}

	// 请务必加上词句，否则进入网页广告后无法进去原sdk
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 10045) {
			Intent intent = new Intent(SplashSpotActivity.this, ServerListActivity.class);
			startActivity(intent);
			finish();
		}
	}

	
}