package com.xk.chinesechess.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.xk.chinesechess.utils.Constant;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class DownloadFile {
	
	
	
	
	public final static String downloadPath = Environment.DIRECTORY_DOWNLOADS+System.getProperty("file.separator")+"ChineseChess";
	
	public String strFileName = "";
	
	public File file;
	
	private Handler handler;
	
	public DownloadFile(Handler handler) {
		this.handler=handler;
	}
	
	/**
	 * �����ļ���֧�ֶϵ�����
	 * @param strurl
	 */
	public void download(final String strurl){
		new Thread(new Runnable(){//�����߳������ļ���������ui�߳̿���

			@Override
			public void run() {
				File directory = Environment.getExternalStoragePublicDirectory(downloadPath);
				if(!directory.exists()){
					directory.mkdir();
				}
				
				HttpURLConnection conn = null;
				InputStream is = null;
				RandomAccessFile raf = null;
				try{
					URL url = new URL(strurl);
					conn = (HttpURLConnection) url.openConnection();
					String cdis=conn.getHeaderField("Content-Disposition");
					String fName=cdis.substring(cdis.indexOf("=")+1, cdis.length());
					if(null==fName){
						fName="cc.apk";
					}
					if(fName.indexOf("/")>0){
						fName=fName.substring(fName.indexOf("/")+1, fName.length());
					}
					file=new File(directory,fName);
					if(file.exists()){
						file.delete();
					}
					file.createNewFile();
					strFileName = fName;
					long count=0;
					is = conn.getInputStream();
					raf=new RandomAccessFile(file, "rw");
					//byte[] buf = new byte[256];
					byte[] buf = new byte[10240];
					conn.connect();//��������Դ����
					int size = 0;
					long total=conn.getContentLength();//��ȡ��������
					if(total<=0){
						Thread.sleep(1500);
						total=conn.getContentLength();
					}
					int persent=0;
					int time=0;
					if (conn.getResponseCode() >= 400) {
						handler.sendEmptyMessage(Constant.DOWNLOAD_TIMEOUT);
					} else {
						while((size=is.read(buf, 0, buf.length))>0){
							count+=size;
							time++;
							//��ٷֱ�
							persent=(int) (Math.ceil(((double)count/total)*100));
							raf.write(buf, 0, size);
							if(time%9==0||persent>=100){//ȡ�Ŵθ�webview��һ����Ϣ�������Ѿ�������ϣ���һ��
								time=0;
								Message msg=new Message();
								msg.what=Constant.UPDATA_DOWNLOAD;
								msg.arg1=persent;
								handler.sendMessage(msg);
							}
						}
					}
					//�ر����ӣ��ͷ���Դ
					conn.disconnect();
					raf.close();
					is.close();
					//����webview�������
					Message msg=new Message();
					msg.what=Constant.DOWNLOAD_FINISH;
					msg.obj=file;
					handler.sendMessage(msg);
				}catch(Exception e){//���س���
					handler.sendEmptyMessage(Constant.DOWNLOAD_ERROR);
				}finally{
					if(null!=conn)conn.disconnect();
					if(null!=raf){
						try {
							raf.close();
						} catch (IOException e) {}
					}
					if(null!=is)
						try {
							is.close();
						} catch (IOException e) {}
				}
			}
			
		}).start();
		
	}


}