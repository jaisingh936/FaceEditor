package com.face.editor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;

import com.face.editor.ShareShot.EnumSocial;

public class Sharing {

	public Context context;
	private boolean saved;
	private String dir = "photoblur";
	private ShareShot shareShot;

	public Sharing(Context context, ShareShot shareShot) {
		this.context = context;
		this.shareShot = shareShot;
	}

	public boolean share(byte[] bytes, String filename, String distance, String mode, String best, ShareShot.EnumSocial socials) {
		filename += "" + System.currentTimeMillis();

		Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		image = getScreenShot(context, image, distance, mode, best);

		String dir = context.getString(R.string.app_name);
		boolean saved = false;
		if (!saved)
			saved = saveBitmapToSDCard(image, dir, filename);
		if (!saved) {
			logs("Could not save to temporary location...\nPlease insert SD card");
			// return false;
		}

		File file = null;

		// Intent share = new Intent(Intent.ACTION_SEND);
		//
		// if (saved) {
		// share.setType("image/*");
		//
		final String local_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dir + "/";
		file = new File(local_path, filename + ".png");
		// logs("share", "Sharing file " + file.exists() +
		// file.getAbsolutePath());
		// share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		// } else {
		// share.setType("text/plain");
		// }
		//
		// share.putExtra(android.content.Intent.EXTRA_SUBJECT, dir);
		// share.putExtra(android.content.Intent.EXTRA_TEXT,
		// "Hey! I just reached " + distance + "m in " + mode +
		// " mode and my best score in this mode is " + best + "m.   \n" +
		// "http://play.google.com/store/apps/details?id=" +
		// this.context.getPackageName());
		// context.startActivity(Intent.createChooser(share, "Choose"));

		if (socials == EnumSocial.MESSENGER) {
			if (!this.shareShot.isAppAvailable(socials)) {
				socials = EnumSocial.FACEBOOK;
			}
		}

		this.shareShot.shareToApp(dir, "Hey! I just reached " + distance + "m in " + mode + " mode and my best score in this mode is " + best + "m.   \n" + "http://play.google.com/store/apps/details?id=" + this.context.getPackageName(), file, socials);

		return true;
	}

	public String saveToInternalSorage(byte[] bytes, String dir, String filename) {
		Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		return saveToInternalSorage(image, dir, filename);
	}

	private String saveToInternalSorage(Bitmap bitmapImage, String dir, String filename) {
		ContextWrapper cw = new ContextWrapper(context);
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir(dir, Context.MODE_PRIVATE);
		// Create imageDir
		File mypath = new File(directory, filename + ".png");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to
			// the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logs("save directory.getAbsolutePath() " + directory.getAbsolutePath());

		return directory.getAbsolutePath();
	}

	public byte[] loadImageFromInternal(String directory, String filename) {
		return bitmapToByteArray(loadImageFromStorage(directory, filename));
	}

	private Bitmap loadImageFromStorage(String directory, String filename) {
		ContextWrapper cw = new ContextWrapper(context);

		File dir = cw.getDir(directory, Context.MODE_PRIVATE);
		File mypath = new File(dir, filename + ".png");
		try {
			Bitmap b = BitmapFactory.decodeStream(new FileInputStream(mypath));
			logs("Bitmap " + b + " w " + b.getWidth());
			return b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] bitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	// public Texture bitmapToTexture(Bitmap bitmap) {
	// byte[] byteArray = bitmapToByteArray(bitmap);
	// Pixmap pixmap = new Pixmap(byteArray, 0, byteArray.length);
	// Texture texture = new Texture(pixmap);
	// return texture;
	// }

	boolean saveBitmapToSDCard(Bitmap bitmap, String dir, String filename) {
		// final String path = Environment.getExternalStorageDirectory()
		// .getAbsolutePath() + "/"+getString(R.string.app_name)+"/";
		String local_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dir + "/";
		logs("check", "local path " + local_path);
		if (Environment.getExternalStorageDirectory().getAbsolutePath() != null) {

			File directory = new File(local_path);
			boolean exists = directory.exists();
			logs("share", "folder exists " + exists);
			if (!exists) {
				boolean created = directory.mkdirs();
				logs("share", "folder created " + created);
			}

			boolean success = false;
			try {
				File file = new File(local_path + filename + ".png");
				file.createNewFile();

				logs("share", "File Created " + file.exists());
				FileOutputStream out = new FileOutputStream(file);

				success = bitmap.compress(CompressFormat.PNG, 100, out);
				out.flush();
				out.close();

				return success;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			logs("Please insert SD Card");
			return false;
		}
	}

	private void shareFromInternal(Context context) {
		ContextWrapper cw = new ContextWrapper(context);

		File directory = cw.getDir(dir, Context.MODE_WORLD_WRITEABLE);
		// Create imageDir
		logs("check", "" + directory);

		File mypath = new File(directory, "abcd" + ".png");
		logs("check", "" + mypath);

		if (!saved)
			saved = true;
		if (!saved) {
			logs("Could not save to temporary location...\nPlease insert SD card");
			return;
		}
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");

		share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mypath));
		share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Swapped Face");
		share.putExtra(android.content.Intent.EXTRA_TEXT, "");
		context.startActivity(Intent.createChooser(share, "Choose"));
	}

	public final int FLIP_VERTICAL = 1;
	public final int FLIP_HORIZONTAL = 2;

	public Bitmap flip(Bitmap src, int type) {
		// create new matrix for transformation
		Matrix matrix = new Matrix();
		// if vertical
		if (type == FLIP_VERTICAL) {
			// y = y * -1
			matrix.preScale(1.0f, -1.0f);
		}
		// if horizonal
		else if (type == FLIP_HORIZONTAL) {
			// x = x * -1
			matrix.preScale(-1.0f, 1.0f);
			// unknown type
		} else {
			return null;
		}

		// return transformed image
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	}

	Bitmap getScreenShot(Context context, Bitmap image, String distance, String mode, String best) {

		Bitmap bm = flip(Bitmap.createScaledBitmap(image, 512, 512, true), FLIP_VERTICAL);
		bm.copy(Config.ARGB_8888, true);
		logs("game", "bm size " + bm.getWidth() + " , " + bm.getHeight());
		Canvas canvas = new Canvas(bm);
		Paint paint = new Paint();

		canvas.save();
		Bitmap playstore = BitmapFactory.decodeResource(context.getResources(), R.drawable.back);

		float proportion = bm.getWidth() / (playstore.getWidth() * 1.0f);
		logs("game", "bm proportion " + proportion);

		// float px = bm.getWidth() - playstore.getWidth();
		// float py = bm.getHeight() - playstore.getHeight();
		canvas.scale(proportion, proportion);
		canvas.drawBitmap(playstore, 0, 0, paint);

		playstore = null;
		System.gc();

		canvas.restore();

		float scale = context.getResources().getDisplayMetrics().density;

		paint.setColor(Color.WHITE);

		int brushsize = (int) (30 * bm.getWidth() / 512.0f);
		// paint.setTextSize((int) (14 * scale));
		paint.setTextSize(brushsize);

		logs("game", "bm brush " + scale + " size " + paint.getTextSize());
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "arial.ttf");
		// paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		paint.setTypeface(tf);
		paint.setStrokeCap(Paint.Cap.ROUND);

		distance = distance + "m";

		Paint scorePaint = new Paint(paint);
		scorePaint.setTextSize(paint.getTextSize() * 1.6f);
		// scorePaint.setAntiAlias(true);
		Rect bounds = new Rect();
		scorePaint.getTextBounds(distance + "m", 0, distance.length(), bounds);
		logs("game", "text bound  " + bounds.width() + " , " + bounds.height());
		int x = (int) (bm.getWidth() * 0.5f + (bm.getWidth() * 0.5f - bounds.width()) * 0.5f);
		int y = (int) (1.5f * (bounds.height()));

		// Draw Mode
		String modeText = "in " + mode;
		Rect modebounds = new Rect();
		paint.getTextBounds(modeText, 0, modeText.length(), modebounds);
		logs("game", "mode bound  " + modebounds.width() + " , " + modebounds.height());
		int modex = (int) (bm.getWidth() * 0.5f + (bm.getWidth() * 0.5f - modebounds.width()) * 0.5f);
		int modeY = (int) (y + bounds.height() * 0.9f);

		// Draw best
		String bestText = "(BEST :" + best + "m)";
		Rect bestbounds = new Rect();
		paint.getTextBounds(bestText, 0, bestText.length(), bestbounds);
		logs("best bound  " + bestbounds.width() + " , " + bestbounds.height());
		int bestX = (int) (bm.getWidth() * 0.5f + (bm.getWidth() * 0.5f - bestbounds.width()) * 0.5f);
		int bestY = (int) (modeY + modebounds.height() * 1.5f);

		canvas.save();
		canvas.rotate(-15, bm.getWidth() * 0.75f, bm.getHeight() * 0.25f);

		canvas.drawText(distance, x, y, scorePaint);
		Paint stkPaint = new Paint(scorePaint);
		stkPaint.setStyle(Style.STROKE);
		// stkPaint.setAntiAlias(true);
		stkPaint.setStrokeWidth(4);
		stkPaint.setColor(Color.BLACK);
		canvas.drawText(distance, x, y, stkPaint);

		canvas.drawText(modeText, modex, modeY, paint);
		canvas.drawText(bestText, bestX, bestY, paint);

		Paint stkPaint2 = new Paint(paint);
		// stkPaint2.setAntiAlias(true);

		stkPaint2.setStyle(Style.STROKE);
		stkPaint2.setStrokeWidth(scale);
		stkPaint2.setColor(Color.BLACK);
		canvas.drawText(modeText, modex, modeY, stkPaint2);
		canvas.drawText(bestText, bestX, bestY, stkPaint2);

		canvas.restore();

		return bm;
	}

	private void logs(String msg) {
		logs("GB", msg);
	}

	private void logs(String tag, String msg) {
		// Log.e(tag, msg);
	}

}
