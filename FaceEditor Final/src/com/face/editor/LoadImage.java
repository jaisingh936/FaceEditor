package com.face.editor;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class LoadImage {
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		Log.d("test", "calculateInSampleSize reqquired size " + reqWidth + " " + reqHeight);
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = Math.min(heightRatio, widthRatio);
			// inSampleSize = heightRatio < widthRatio ? heightRatio :
			// widthRatio;
			Log.d("test", "calculateInSampleSize reqquired size " + reqWidth + " " + reqHeight + " insample " + inSampleSize);
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Context context, Uri uri, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 BitmapFactory.decodeStream(is, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		Log.d("test", "samplesize  " + options.inSampleSize + " options width " + options.outWidth);
		// Decode editBitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		InputStream inputs = null;
		try {
			inputs = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return BitmapFactory.decodeStream(inputs, null, options);
	}

}
