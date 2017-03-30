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
public class XMask {
	private Context context;
	private Dialog ad;
	private TextView messageView;
	public XMask(Context context) {
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
		window.setContentView(R.layout.acivity_app_mask);
		messageView=(TextView)window.findViewById(R.id.mask_info);
	}
	
	public void setMessage(int resId) {
		messageView.setText(resId);
	}
 
	public void setMessage(String message)
	{
		messageView.setText(message);
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
}
