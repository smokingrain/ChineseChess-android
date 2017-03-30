package com.xk.chinesechess.ui;



import com.xk.chinesechess.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 自定义对话框类
 * @author xiaokui
 *
 */
public class XDialog {
	private Context context;
	private Dialog ad;
	private TextView titleView;
	private TextView messageView;
	private LinearLayout contentview;
	private Button pbtn;
	private Button nbtn;
	private Object data;//对话框额外数据
	public XDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;//本质上是一个AlertDialog
//		ad=new android.app.AlertDialog.Builder(context).create();
		ad=new Dialog(context);
		ad.setCancelable(false);//不允许返回键取消弹窗
		ad.setCanceledOnTouchOutside(false);//点击空白处不允许取消弹窗
		ad.show();
		
		//使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setBackgroundDrawable(new ColorDrawable(0));
		window.setContentView(R.layout.acivity_app_dialog);
		titleView=(TextView)window.findViewById(R.id.message_title);
		messageView=(TextView)window.findViewById(R.id.message_info);
		contentview=(LinearLayout) window.findViewById(R.id.dialogcontent);
		pbtn=(Button) window.findViewById(R.id.exitBtn1);
		nbtn=(Button) window.findViewById(R.id.exitBtn2);
	}
	
	public void setTitle(int resId)
	{
		titleView.setText(resId);
	}
	public void setTitle(String title) {
		titleView.setText(title);
	}
	public void setMessage(int resId) {
		messageView.setText(resId);
	}
 
	public void setMessage(String message)
	{
		messageView.setText(message);
	}
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,View.OnClickListener listener)
	{
		pbtn.setVisibility(View.VISIBLE);
		pbtn.setText(text);
		if(null!=listener){
			pbtn.setOnClickListener(listener);
		}
	}
 
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text,View.OnClickListener listener)
	{
		nbtn.setVisibility(View.VISIBLE);
		nbtn.setText(text);
		if(null!=listener){
			nbtn.setOnClickListener(listener);
		}
 
	}
	
	//原生dialog代理方法
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
	/**
	 * 设置是否可被返回键关闭（默认false）
	 * @param flag
	 */
	public void setCancelable(boolean flag) {
		ad.setCancelable(flag);
	}
	
	/**
	 * 设置是否点击区域外部关闭
	 * @param cancel
	 */
	public void setCanceledOnTouchOutside(boolean cancel) {
		ad.setCanceledOnTouchOutside(cancel);
	}
	
	/**
	 * 设置取消监听
	 * @param listener
	 */
	public void setOnCancelListener(OnCancelListener listener) {
		ad.setOnCancelListener(listener);
	}
	
	/**
	 * 设置消失监听
	 * @param listener
	 */
	public void setOnDismissListener(OnDismissListener listener) {
		ad.setOnDismissListener(listener);
	}
	/**
	 * 设置中间区域为指定视图
	 * @param view
	 */
	public void setEditor(View view){
		messageView.setVisibility(View.GONE);
		LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		params.leftMargin=20;
		params.rightMargin=20;
		params.gravity=16;
		contentview.addView(view,params);
	}
}
