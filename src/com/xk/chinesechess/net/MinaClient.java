package com.xk.chinesechess.net;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.xk.chinesechess.AndroidSender;
import com.xk.chinesechess.activity.ServerListActivity;
import com.xk.chinesechess.constant.MyApplication;
import com.xk.chinesechess.message.MessageListener;
import com.xk.chinesechess.message.PackageInfo;
import com.xk.chinesechess.utils.Constant;
import com.xk.chinesechess.utils.JSONUtil;


public class MinaClient {
	private static int checkCount=0;
	private static List<Map<String,String>>hosts =new ArrayList<Map<String,String>>();
	private IoSession session;
	private MessageListener listener;
	private ConnectionListener cListener;
	private IoConnector connector;
	private MinaClient(){}
	
	public boolean init(String host,int port){
		if(null==connector){
			connector = new NioSocketConnector();
			//设置链接超时时间
			connector.setConnectTimeoutMillis(5000);
			connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
			connector.setHandler(new MessageHandler());
			try{
				ConnectFuture future = connector.connect(new InetSocketAddress(host,port));
				future.awaitUninterruptibly();// 等待连接创建完成
				session = future.getSession();//获得session
				System.out.println("created connection!");
				return true;
			}catch (Exception e){
				close(true);
			}
		}
		return false;
	}
	
	public void setListener(MessageListener listener) {
		this.listener = listener;
	}
	
	public void setcListener(ConnectionListener cListener) {
		this.cListener = cListener;
	}
	
	public boolean writeMessage(final String msg){
		if(null==session){
			return false;
		}
		WriteFuture future=session.write(msg);
		return future.isWritten();
	}
	
	public static boolean searchHost(TestHostCallBack callback){
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
			testHost(first+i,callback);
		}
		return true;
	}
	
	private static void testHost(final String host,final TestHostCallBack callback){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				MinaClient client=new MinaClient();
				if(client.init(host, 5492)){
					Map<String,String> temp=new HashMap<String, String>();
					temp.put("name",host );
					temp.put("ip", host);
					hosts.add(temp);
				}
				client.close(false);
				checkCount++;
				if(checkCount>=255){
					callback.testEnd(hosts);
				}
			}
			
		}).start();
	}
	
	public void close(boolean notify){
		if(null!=listener&&notify){
			PackageInfo info=new PackageInfo(MyApplication.me.getCid(), "disconnect", "server", Constant.MSG_DISCONNECT, Constant.APP, 0);
			listener.getMessage(info);
			System.out.println("send close msg!");
		}
		if(null!=connector){
			connector.dispose();
		}
		session=null;
		connector=null;
	}
	
	public static MinaClient getInstance(){
		return MinaFactory.INSTANCE;
	}
	
	private class MessageHandler extends IoHandlerAdapter  {

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			session.closeNow();
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			PackageInfo info=JSONUtil.toBean(message.toString(),PackageInfo.class);
			if("auth".equals(info.getType())){
				String msg = info.getMsg();
				if("true".equals(msg)) {
					cListener.connected(info.getFrom());
				}else {
					cListener.connected(null);
				}
				
				return;
			}
			listener.getMessage(info);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			close(true);
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			System.out.println("conection conecting!");
		}
		
	}
	
	private static class MinaFactory{
		public static final MinaClient INSTANCE=new MinaClient();
	}
	
	
}
