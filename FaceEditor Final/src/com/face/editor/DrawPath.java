package com.face.editor;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

public class DrawPath {
	Context context;
	Path path;
	Paint paint = new Paint();

	public DrawPath(Context context, Path path, int seekWidth) {
		// TODO Auto-generated constructor stub
		this.path = path;
		paint.setAntiAlias(true);
		paint.setDither(true);

		paint.setStrokeWidth(seekWidth);
		paint.setStyle(Paint.Style.STROKE);

		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setMaskFilter(new BlurMaskFilter(seekWidth / 2, Blur.NORMAL));
		// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
		paint.setColor(Color.BLACK);
	}

	public void onDraw(Canvas canvas) {

		canvas.drawPath(path, paint);
		Log.i("ondraw", "drawpath ondraw");
	}

}
