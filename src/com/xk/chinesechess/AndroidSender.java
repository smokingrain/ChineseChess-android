package com.xk.chinesechess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.xk.chinesechess.activity.ServerListActivity;
import com.xk.chinesechess.constant.MyApplication;
import com.xk.chinesechess.font.FreePaint;
import com.xk.chinesechess.message.MessageSender;
import com.xk.chinesechess.message.WindowCallback;
import com.xk.chinesechess.utils.Constant;

public class AndroidSender implements MessageSender {
	private Handler handler;
	private Context context;
	private Paint paint = null;
	public AndroidSender(Handler handler){
		this.handler=handler;
	}
	
	

	@Override
	public void showInfo(String info) {
		Message msg=new Message();
		msg.what=Constant.SHOW_MESSAGE;
		msg.obj=info;
		handler.sendMessage(msg);
		
	}

	@Override
	public boolean isAndroid() {
		return true;
	}


	@Override
	public String getMyIp(){  
	    WifiManager wm=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
	    //检查Wifi状态    
	    if(!wm.isWifiEnabled()) {
	    	return null;
	    } 
	    WifiInfo wi=wm.getConnectionInfo();  
	    if(null==wi){
	    	return null;
	    }
	    //获取32位整型IP地址    
	    Integer ipAdd=wi.getIpAddress();  
	    if(null==ipAdd){
	    	return null;
	    }
	    //把整型地址转换成“*.*.*.*”地址    
	    String ip=intToIp(ipAdd);  
	    return ip;  
	}  
	private String intToIp(int i) {  
	    return (i & 0xFF ) + "." +  
	    ((i >> 8 ) & 0xFF) + "." +  
	    ((i >> 16 ) & 0xFF) + "." +  
	    ( i >> 24 & 0xFF) ;  
	}


	public Context getContext() {
		return context;
	}


	public void setContext(Context context) {
		this.context = context;
	}



	@Override
	public void writeMessage(String info) {
		MyApplication.nc.writeMessage(info);
	}
	
	@Override
	public void close(){
		MyApplication.nc.close(true);
		MyApplication.serverip = null;
	}

	public static boolean isServiceWork(Context mContext, String serviceName) {  
	    boolean isWork = false;  
	    ActivityManager myAM = (ActivityManager) mContext  
	            .getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
	    if (myList.size() <= 0) {  
	        return false;  
	    }  
	    for (int i = 0; i < myList.size(); i++) {  
	        String mName = myList.get(i).service.getClassName().toString();  
	        if (mName.equals(serviceName)) {  
	            isWork = true;  
	            break;  
	        }  
	    }  
	    return isWork;  
	}



	@Override
	public void openWindow(String title, String message, WindowCallback callback) {
		Message msg=new Message();
		Bundle bundle=new Bundle();
		bundle.putString("title", title);
		bundle.putString("message", message);
		msg.setData(bundle);
		msg.obj=callback;
		msg.what=Constant.SHOW_WINDOW;
		handler.sendMessage(msg);
	}

	private int getColor(Color color) {
		return ((int) (255 * color.a) << 24) | ((int) (255 * color.r) << 16)
				| ((int) (255 * color.g) << 8) | ((int) (255 * color.b));
	}

	@Override
	public Pixmap getFontPixmap(String txt, FreePaint vpaint) {
		if (paint == null) {
			paint = new Paint();
			paint.setAntiAlias(true);
		}
		paint.setTextSize(vpaint.getTextSize());
		FontMetrics fm = paint.getFontMetrics();
		int w = (int) paint.measureText(txt);
		int h = (int) (fm.descent - fm.ascent);
		if (w == 0) {
			w = h = vpaint.getTextSize();
		}
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		// 如果是描边类型
		if (vpaint.getStrokeColor() != null) {
			// 绘制外层
			paint.setColor(getColor(vpaint.getStrokeColor()));
			paint.setStrokeWidth(vpaint.getStrokeWidth()); // 描边宽度
			paint.setStyle(Style.FILL_AND_STROKE); // 描边种类
			paint.setFakeBoldText(true); // 外层text采用粗体
			canvas.drawText(txt, 0, -fm.ascent, paint);
			paint.setFakeBoldText(false);
		} else {
			paint.setUnderlineText(vpaint.getUnderlineText());
			paint.setStrikeThruText(vpaint.getStrikeThruText());
			paint.setFakeBoldText(vpaint.getFakeBoldText());
		}
		// 绘制内层
		paint.setStrokeWidth(0);
		paint.setColor(getColor(vpaint.getColor()));
		canvas.drawText(txt, 0, -fm.ascent, paint);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, buffer);
		byte[] encodedData = buffer.toByteArray();
		Pixmap pixmap = new Pixmap(encodedData, 0, encodedData.length);
		try {
			buffer.close();
		} catch (IOException e) {
		}
		bitmap.recycle();
		bitmap = null;
		canvas = null;
		return pixmap;
	}

	 public boolean isWifiConnected(Context context)
	    {
	        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        if(wifiNetworkInfo.isConnected())
	        {
	            return true ;
	        }
	     
	        return false ;
	    }



	@Override
	public void saveData(String name, String value) {
		SharedPreferences sp = context.getSharedPreferences(ServerListActivity.SHARED_PREF_NAME,  Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(name, value);
		editor.commit();
	}
	
	@Override
	public void saveData(String name, Integer value) {
		SharedPreferences sp = context.getSharedPreferences(ServerListActivity.SHARED_PREF_NAME,  Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(name, value);
		editor.commit();
	}



	@Override
	public void openMask(String message) {
		Message msg=new Message();
		Bundle bundle=new Bundle();
		bundle.putString("message", message);
		msg.setData(bundle);
		msg.what=Constant.SHOW_MUSK;
		handler.sendMessage(msg);
	}



	@Override
	public void closeMask() {
		Message msg=new Message();
		msg.what=Constant.CLOSE_MUSK;
		handler.sendMessage(msg);
	}


}
