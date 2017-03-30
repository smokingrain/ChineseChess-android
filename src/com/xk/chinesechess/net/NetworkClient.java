package com.xk.chinesechess.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.xk.chinesechess.AndroidSender;
import com.xk.chinesechess.activity.ServerListActivity;
import com.xk.chinesechess.message.MessageListener;
import com.xk.chinesechess.message.PackageInfo;
import com.xk.chinesechess.utils.JSONUtil;


public class NetworkClient {
	private int checkCount=0;//扫描ip数量
	private List<Map<String,String>> hosts=new CopyOnWriteArrayList<Map<String,String>>();
	private Socket sk;
	private DataInputStream din;
	private DataOutputStream dou;
	private String id;
	private boolean connected=false;
	private String host;
	private MessageListener listener;
	private boolean isListening=false;
	private static NetworkClient instance=new NetworkClient();
	
	public static NetworkClient getInstance(String id){
		if(null==instance){
			instance=new NetworkClient(id);
		}
		if(null==instance.id){
			instance.id=id;
		}
		return instance;
	}
	public static NetworkClient getInstance(){
		if(null==instance){
			instance=new NetworkClient();
		}
		return instance;
	}
	
	private NetworkClient(){
		
	}
	
	private NetworkClient(String id){
		this.id=id;
	}
	
	public String connect(String addr,int port) {
		if(null==sk){
			try {
				sk=new Socket(addr,port);
				din=new DataInputStream(sk.getInputStream());
				dou=new DataOutputStream(sk.getOutputStream());
				if(null==id){
					dou.writeUTF("+");
					String inf=din.readUTF();
					PackageInfo info=JSONUtil.toBean(inf, PackageInfo.class);
					id=info.getMsg();
				}else{
					dou.writeUTF(id);
				}
				connected=true;
				readMessage();
			} catch (UnknownHostException e) {
				return id;
			} catch (IOException e) {
				return id;
			}
		}
		return id;
		
	}
	
	private synchronized void counting(){
		checkCount++;
	}
	
	private synchronized void setHost(String host){
		if(null==host){
			System.out.println(host);
			this.host=host;
		}
	}
	
	public boolean searchHost(TestHostCallBack callback){
		hosts.clear();
		checkCount=0;
		ServerListActivity activity=(ServerListActivity) callback;
		AndroidSender sender=new AndroidSender(null);
		sender.setContext(activity);
		if(!sender.isWifiConnected(activity)){
			return false;
		}
		String myIp=sender.getMyIp();
		if(null==myIp){
			return false;
		}
		String[] spl=myIp.split("[.]");
		String first=spl[0]+"."+spl[1]+"."+spl[2]+".";
		for(int i=1;i<=255;i++){
			if(!connected){
				testHost(first+i,callback);
			}else{
				break;
			}
		}
		return true;
	}
	
	private void testHost(final String host,final TestHostCallBack callback){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Socket sk=new Socket();
					SocketAddress sa=new InetSocketAddress(host,5491);
					sk.connect(sa, 5000);
					DataOutputStream dou=new DataOutputStream(sk.getOutputStream());
					dou.writeUTF("-");
					dou.close();
					sk.close();
					Map<String,String> temp=new HashMap<String, String>();
					temp.put("name",host );
					temp.put("ip", host);
					hosts.add(temp);
				} catch (Exception e) {
				}
				counting();
				if(checkCount>=255){
					callback.testEnd(hosts);
				}
			}
		}).start();
	}
	
	
	public void writeMessage(String info){
		try {
			if(null!=dou){
				dou.writeUTF(info);
				dou.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readMessage(){
		if(isListening){
			return;
		}
		isListening=true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while(isListening){
						if(din!=null){
							String info=din.readUTF();
							if(!isListening){
								break;
							}
							if(null!=listener){
								listener.getMessage(JSONUtil.toBean(info,PackageInfo.class));
							}
						}
					}
					isListening=false;
					if(null!=din){
						din.close();
					}
				} catch (IOException e) {
					NetworkClient.this.destory();
					System.out.println("接收消息结束！");
				}
			}
		}).start();
	}
	
	public void destory(){
		id=null;
		connected=false;
		isListening=false;
		instance=null;
		try {
			if(null!=din){
				din.close();
				din=null;
			}
			if(null!=dou){
				dou.close();
				dou=null;
			}
			if(null!=sk){
				sk.close();
				sk=null;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MessageListener getListener() {
		return listener;
	}

	public void setListener(MessageListener listener) {
		this.listener = listener;
	}
}	
