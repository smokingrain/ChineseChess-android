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
		// ��Ӧ��ÿ�δ򿪶�ִ��һ�γ�ʼ������Ҫ���ڳ�ʼ��appid֮�����Ϣ
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
//			mask.setMessage("��ʼ���汾��");
//		}else{
//			Intent intent=new Intent(this,ServerListActivity.class);
//			this.startActivity(intent);
//			finish();
//		}
		
	}

	// ����ؼ��ϴʾ䣬���������ҳ�����޷���ȥԭsdk
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