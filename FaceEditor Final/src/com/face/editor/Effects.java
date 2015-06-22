package com.face.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

public class Effects {

	
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;

		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Log.d("check","toGrayscale before "+width);

		if(width%2!=0){
			width-=1;
		}
		Log.d("check","toGrayscale after "+width);
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	static Bitmap getMirrorImageY(Bitmap image) {
		float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
		Matrix matrixMirrorY = new Matrix();
		matrixMirrorY.setValues(mirrorY);

		Matrix matrix = new Matrix();
		matrix.postConcat(matrixMirrorY);

	    return Bitmap.createBitmap(image, 0, 0,image.getWidth(),
				image.getHeight(), matrix, true);
	}

}
