package com.xk.chinesechess.net;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;


public class Loginer {
	public HttpClient httpClient = HttpClientHelper.getHttpClient();
	
	public Loginer(String name){
		
	}
	
	public void close(){
	}
	
	public String readJsonfromURL(String url,Map<String,String> params) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if(null!=params){
			Set<String> keys=params.keySet();
			for(String key:keys){
				formparams.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		StringBuilder result;
		try {
			UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(formparams, "GB2312");  
			
			//新建Http  post请求  
			HttpPost httppost = new HttpPost(url);  
			httppost.setEntity(entity1);  
  
			//处理请求，得到响应  
			HttpResponse response = httpClient.execute(httppost);  
     
			//打印返回的结果  
			HttpEntity entity = response.getEntity();  
			  
			result = new StringBuilder();  
			if (entity != null) {  
			    InputStream instream = entity.getContent();  
			    BufferedReader br = new BufferedReader(new InputStreamReader(instream,"GB2312"));  
			    String temp = "";  
			    while ((temp = br.readLine()) != null) {  
			        result.append(temp);  
			    }  
			    br.close();
			}
		} catch (Exception e) {
			Log.e("Loginer", "readJsonfromURL failed"+e.getMessage());
			return "";
		} 
        Log.i("Loginer","readJsonfromURL:"+result);
		return result.toString();
	}
	public String readJsonfromURL2(String url,Map<String,String> params) throws ClientProtocolException, IOException{
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if(null!=params){
			Set<String> keys=params.keySet();
			for(String key:keys){
				formparams.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(formparams, "UTF-8");  
		
		//新建Http  post请求  
		HttpPost httppost = new HttpPost(url);  
		httppost.setEntity(entity1);  
		
		//处理请求，得到响应  
		HttpResponse response = httpClient.execute(httppost);  
		
		//打印返回的结果  
		HttpEntity entity = response.getEntity();  
		
		StringBuilder result = new StringBuilder();  
		if (entity != null) {  
			InputStream instream = entity.getContent();  
			BufferedReader br = new BufferedReader(new InputStreamReader(instream,"UTF-8"));  
			String temp = "";  
			while ((temp = br.readLine()) != null) {  
				result.append(temp);  
			}  
			br.close();
		}  
		Log.i("Loginer","readJsonfromURL:"+result);
		return result.toString();
	}
	
}
