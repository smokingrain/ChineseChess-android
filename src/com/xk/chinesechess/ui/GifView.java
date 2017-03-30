package com.xk.chinesechess.ui;

import com.xk.chinesechess.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

public class GifView extends View {
	private long mMovieStart;
	private Movie mMovie;
	public GifView(Context context, AttributeSet attrs) {
		super(context,attrs);
		//以文件流（InputStream）读取进gif图片资源
		mMovie=Movie.decodeStream(getResources().openRawResource(R.drawable.maskgif));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		long now = android.os.SystemClock.uptimeMillis();   
        
        if (mMovieStart == 0) { // first time   
            mMovieStart = now;   
        }   
        if (mMovie != null) {   
              
            int dur = mMovie.duration();   
            if (dur == 0) {   
                dur = 1000;   
            }   
            int relTime = (int) ((now-mMovieStart) % dur);                  
            mMovie.setTime(relTime);   
            mMovie.draw(canvas, 0, 0);   
            invalidate();   
        }   
	}
}
