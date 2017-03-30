package com.xk.chinesechess.activity;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xk.chinesechess.AndroidSender;
import com.xk.chinesechess.MessageService;
import com.xk.chinesechess.R;
import com.xk.chinesechess.activity.adapter.ServerListAdapter;
import com.xk.chinesechess.activity.adapter.ViewTag;
import com.xk.chinesechess.constant.MyApplication;
import com.xk.chinesechess.message.Client;
import com.xk.chinesechess.net.ConnectionListener;
import com.xk.chinesechess.net.DownloadFile;
import com.xk.chinesechess.net.Loginer;
import com.xk.chinesechess.net.MinaClient;
import com.xk.chinesechess.net.TestHostCallBack;
import com.xk.chinesechess.ui.XDialog;
import com.xk.chinesechess.ui.XMask;
import com.xk.chinesechess.utils.Constant;
import com.xk.server.utils.StringUtil;

import a.b.c.DynamicSdkManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.Toast;

public class ServerListActivity extends Activity implements OnItemClickListener,TestHostCallBack{
	public static final String SHARED_PREF_NAME="data.data";
	private boolean searching=false;
	private RadioButton serviceBtn;
	private ListView lview;
	private ServerHandler handler;
	private XMask xmask;
	private XMask downloadMask;
	private XDialog update;
	private boolean oldVersion=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servers);
		lview=(ListView) findViewById(R.id.servers);
		ServerListAdapter adapter=new ServerListAdapter(this,new ArrayList<Map<String,String>>());
		lview.setAdapter(adapter);
		lview.setOnItemClickListener(this);
		handler=new ServerHandler(this);
		serviceBtn=(RadioButton) findViewById(R.id.service_btn);
		checkUpdate();
		refreshList(new ArrayList<Map<String,String>>());
		AndroidSender sender=new AndroidSender(null);
		if(!sender.isWifiConnected(this)){
			serviceBtn.setVisibility(View.GONE);
			Toast.makeText(this, "δ����wifi�����������Ͻ�ͼ����е�����Ϸ", Toast.LENGTH_SHORT).show();
			return;
		}
		if(AndroidSender.isServiceWork(this, "com.me.Gobang.MessageService")){
			serviceBtn.setText(R.string.stopserver);
		}else{
			serviceBtn.setText(R.string.startserver);
		}
		
	}
	
	private void checkUpdate(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<String,String>params=new HashMap<String, String>();
				params.put("v", getAppVersionName(ServerListActivity.this));
				Loginer loginer=new Loginer("");
				String result=loginer.readJsonfromURL("http://120.25.90.35:8080/SpringMVC_01/ccUpdate.html", params);
				if("true".equals(result)){
					oldVersion=true;
					handler.sendEmptyMessage(Constant.SHOULD_UPDATE);
				}else if("false".equals(result)){
					oldVersion=false;
				}
			}
		}).start();;
	}
	public String getAppVersionName(Context context) {  
	    String versionName = "";  
	    try {  
	        // ---get the package info---  
	        PackageManager pm = context.getPackageManager();  
	        android.content.pm.PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);  
	    }  
	    return versionName;  
	}
	
	private void handleUpdate(){
		update=new XDialog(this);
		update.setMessage("��⵽�°汾���Ƿ�������");
		update.setPositiveButton("����", new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				update.dismiss();
				update=null;
				startDownload();
			}
		});
		update.setNegativeButton("����", new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				update.dismiss();
				update=null;
			}
		});
		update.setTitle("�汾����");
		
	}

	private void updateDownload(int arg1) {
		if(null!=downloadMask){
			downloadMask.setMessage(arg1+"%");
		}
	}
	private void closeDownload(){
		if(null!=downloadMask){
			downloadMask.dismiss();
			downloadMask=null;
		}
		
	}
	//��APK�������
	private void openFile(File file) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
		"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	protected void startDownload() {
		AndroidSender sender=new AndroidSender(null);
		if(sender.isWifiConnected(getApplicationContext())){
			download();
		}else{
			final XDialog dialog=new XDialog(this);
			dialog.setTitle("��ʾ");
			dialog.setMessage("δ����wifi,ʹ���������أ�");
			dialog.setPositiveButton("ȷ��", new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					download();
				}
			});
			dialog.setNegativeButton("ȡ��", new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});
		}
		
	}
	
	private void download(){
		downloadMask=new XMask(this);
		downloadMask.setMessage("��������");
		String url="http://120.25.90.35:8080/SpringMVC_01/ccdownload.html";
		DownloadFile df=new DownloadFile(handler);
		df.download(url);
	}

	@Override
	protected void onStart() {
		MyApplication.me=new Client();
		SharedPreferences sp = this.getSharedPreferences(SHARED_PREF_NAME,  Context.MODE_PRIVATE);
		MyApplication.me.setCname(sp.getString("PlayerName", ""));
		MyApplication.me.setComputer(sp.getInt("Computer", 0));
		MyApplication.me.setCwin(sp.getInt("cwin", 0));
		MyApplication.me.setDuizhan(sp.getInt("duizhan", 0));
		MyApplication.me.setDwin(sp.getInt("dwin", 0));
		super.onStart();
	}


	/**
	 * ��������
	 * @param view
	 */
	public void startServer(View view){
		if(AndroidSender.isServiceWork(this, "com.xk.chinesechess.MessageService")){
			Intent service = new Intent(this,MessageService.class);
			this.stopService(service);
			serviceBtn.setText(R.string.startserver);
		}else{
			Intent service = new Intent(this,MessageService.class);
			this.startService(service);
			serviceBtn.setText(R.string.stopserver);
		}
		
	}
	/**
	 * ����
	 * @param view
	 */
	public void localGame(View view){
		if(StringUtil.isBlank(MyApplication.me.getCname())){
			createName(null);
			return;
		}
		Intent intent=new Intent(this,MainActivity.class);
		intent.putExtra("isMyPlace", true);
		intent.putExtra("isLocal", true);
		intent.putExtra("roomid", "");
		startActivity(intent);
	}
	/**
	 * ˢ��
	 * @param view
	 */
	public void refreshServer(View view){
		if(searching){
			return;
		}
		if(MinaClient.searchHost(this)){
			System.out.println("show add!");
			searching=true;
			xmask=new XMask(this);
			xmask.setMessage("����������...");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date limit=new Date();
			try {
				limit = sdf.parse("2016-01-15 17:31:22");
			} catch (ParseException e) {
				System.out.println("format fail");
			}
			if(new Date().getTime()>limit.getTime()&&DynamicSdkManager.getInstance(this).isDexLoadCompleted()){
				DynamicSdkManager.getInstance(this).showSpot(this);
			}
		}else{
			refreshList(new ArrayList<Map<String,String>>());
			Toast.makeText(this, "����ʧ�ܣ���ȷ��wifi�Ѿ��򿪲����ӵ���", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void refreshList(List<Map<String,String>> data){
		DynamicSdkManager.getInstance(this).disMiss(this);
		if(null!=xmask){
			xmask.dismiss();
			xmask=null;
		}
		ServerListAdapter adapter=(ServerListAdapter) lview.getAdapter();
		adapter.reset(data);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void testEnd(List<Map<String,String>> data) {
		Message msg=new Message();
		msg.what=Constant.REFRESH_DATA;
		msg.obj=data;
		handler.sendMessage(msg);
		
	}
	
	public void connectServer(Long id){
		if(null!=xmask){
			xmask.dismiss();
			xmask=null;
		}
		if(null==id||id<0){
			Toast.makeText(this, "����������ʧ�ܣ���ˢ������", Toast.LENGTH_SHORT).show();
		}else{
			MyApplication.me.setCid(id);
			Intent intent= new Intent(this,RoomListActivity.class);
			startActivity(intent);
		}
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View view, int arg2, long arg3) {
		if(oldVersion){
//			final XDialog dialog=new XDialog(this);
//			dialog.setTitle("��ʾ");
//			dialog.setMessage("���汾ʧ�ܣ���ȷ�����������Ѿ��򿪣�");
//			dialog.setPositiveButton("ȷ��", new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					dialog.dismiss();
//				}
//			});
//			return;
		}
		if(StringUtil.isBlank(MyApplication.me.getCname())){
			createName(null);
			return;
		}
		xmask=new XMask(this);
		xmask.setMessage("���ӷ�����...");
		final ViewTag tag=(ViewTag) view.getTag();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				MyApplication.nc=MinaClient.getInstance();
				MyApplication.nc.setListener(MyApplication.ml);
				MyApplication.nc.setcListener(new ConnectionListener() {
					
					@Override
					public void connected(Long uid) {
						if(null!=uid){
							MyApplication.serverip=tag.sip;
						}
						Message msg=new Message();
						msg.obj=uid;
						msg.what=Constant.CONNECT_SERVER;
						handler.sendMessage(msg);
					}
				});
				MyApplication.nc.init(tag.sip, 5492);
				
			}
		}).start();
		
	}
	
	public void createName(View view){
		final SharedPreferences sp = this.getSharedPreferences(SHARED_PREF_NAME,  Context.MODE_PRIVATE);
		String playerName=sp.getString("PlayerName", "");
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
		text.setHint("�����������(����1-10)");
		text.setSingleLine(true);
		text.setText(playerName);
		text.setBackgroundColor(R.drawable.bg_edittext);
		LayoutParams params2=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1);
		layout.addView(text,params2);
		final XDialog dialog=new XDialog(this);
		dialog.setTitle("�����������");
		dialog.setEditor(layout);
		dialog.setCancelable(true);
		dialog.setPositiveButton("ȷ��", new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				String name=text.getText().toString();
				if(StringUtil.isBlank(name)){
					return;
				}
				if(name.length()>10){
					return;
				}
				MyApplication.me.setCname(name);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("PlayerName", name);
				editor.commit();
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("ȡ��", new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}
	
	// ����ؼ��ϴʾ䣬���������ҳ�����޷���ȥԭsdk
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 10045) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
    	DynamicSdkManager.getInstance(this).disMiss(this);
    	super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
	    // ��������ʽ�����Դ
	 	DynamicSdkManager.getInstance(this).onDestroy(this);
	    DynamicSdkManager.getInstance(this).onAppDestroy();
	    super.onDestroy();
	}
	
	private static class ServerHandler extends Handler{
		private ServerListActivity activity;
		ServerHandler(ServerListActivity activity){
			this.activity=activity;
		}
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constant.CLOSE_XMASK:
				if(activity.searching){
					activity.searching=false;
					Toast.makeText(activity, "���ӳ�ʱ", Toast.LENGTH_SHORT).show();
					activity.refreshList(new ArrayList<Map<String,String>>());
				}
				break;
			case Constant.REFRESH_DATA:
				if(activity.searching){
					activity.searching=false;
					List<Map<String,String>>data=(List<Map<String,String>>) msg.obj;
					if(data.size()==0){
						Toast.makeText(activity, "��ǰ������δ��������,������������ť��������.", Toast.LENGTH_SHORT).show();
					}
					activity.refreshList(data);
				}
				break;
			case Constant.CONNECT_SERVER:
				Long id=(Long) msg.obj;
				activity.connectServer(id);
				break;
			case Constant.SHOULD_UPDATE:
				activity.handleUpdate();
				break;
			case Constant.UPDATA_DOWNLOAD:
				activity.updateDownload(msg.arg1);
				break;
			case Constant.DOWNLOAD_FINISH:
				File file= (File) msg.obj;
				activity.closeDownload();
				activity.openFile(file);
				break;
			case Constant.DOWNLOAD_TIMEOUT:
				Toast.makeText(activity, "���ӳ�ʱ", Toast.LENGTH_SHORT).show();
				activity.closeDownload();
				break;
			case Constant.DOWNLOAD_ERROR:
				Toast.makeText(activity, "����ʧ��", Toast.LENGTH_SHORT).show();
				activity.closeDownload();
				break;
			default:break;
			}
		}
		
	}

}
