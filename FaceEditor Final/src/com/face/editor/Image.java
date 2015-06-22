package com.face.editor;

//import android.app.Activity;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.MotionEvent;

public class Image {

	Context context;
	Bitmap image;
	Canvas imageCanvas;
	Bitmap originalImage, boundry;

	private float angle = 0.0f, dx = 0.0f, dy = 0.0f;
	float centerX, centerY, scalefactor = 1.0f, prevScaleFactor = 1.0f;
	int width, height;
	float prevX = 0.0f, prevY = 0.0f, canvasWidth, canvasHeight;
	Canvas canvas;

	int currentAction;
	Paint paint = new Paint();
	Paint blackpaint = new Paint();
	boolean isExtract, isManual = true, clear = true;
	float imageLeft, imageRight, imageTop, imageBottom;
	List<Face> faceList;
	Face touchedFace;

	// FaceCropView view;
	Bitmap fitXY, scaleOnly, rotateOnly, rotateNScale, scaleX, scaleY, trash;
	Bitmap cfitXY, cscaleOnly, crotateOnly, crotateNScale, cscaleX, cscaleY, ctrash;

	public Image(Bitmap bitmap, float centerX, float centerY, Context context) {
		this.image = bitmap.copy(Config.ARGB_8888, true);
		imageCanvas = new Canvas(image);
		originalImage = bitmap.copy(Config.ARGB_8888, true);
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();

		this.context = context;
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
		paint.setColor(Color.TRANSPARENT);

		blackpaint.setColor(Color.BLACK);
		blackpaint.setStyle(Style.STROKE.FILL_AND_STROKE);

		fitXY = BitmapFactory.decodeResource(context.getResources(), R.drawable.fit_xy);
		scaleOnly = BitmapFactory.decodeResource(context.getResources(), R.drawable.scale);
		rotateOnly = BitmapFactory.decodeResource(context.getResources(), R.drawable.rotate_only);
		rotateNScale = BitmapFactory.decodeResource(context.getResources(), R.drawable.sale_n_rotate);
		scaleX = BitmapFactory.decodeResource(context.getResources(), R.drawable.scalex);
		scaleY = BitmapFactory.decodeResource(context.getResources(), R.drawable.scaley);
		trash = BitmapFactory.decodeResource(context.getResources(), R.drawable.trash);

		cfitXY = BitmapFactory.decodeResource(context.getResources(), R.drawable.cfit_xy);
		cscaleOnly = BitmapFactory.decodeResource(context.getResources(), R.drawable.cscale);
		crotateOnly = BitmapFactory.decodeResource(context.getResources(), R.drawable.crotate_only);
		crotateNScale = BitmapFactory.decodeResource(context.getResources(), R.drawable.csale_n_rotate);
		cscaleX = BitmapFactory.decodeResource(context.getResources(), R.drawable.cscalex);
		cscaleY = BitmapFactory.decodeResource(context.getResources(), R.drawable.cscaley);
		ctrash = BitmapFactory.decodeResource(context.getResources(), R.drawable.ctrash);

	}

	protected void setCurrentAction(int currentAction) {
		this.currentAction = currentAction;
	}

	void setFaceList(List<Face> faceList) {
		this.faceList = faceList;
	}

	void setTouchedFace(Face touchedFace) {
		this.touchedFace = touchedFace;

	}

	void setBoundry(Bitmap boundry) {
		this.boundry = boundry;
	}

	Canvas getCanvas() {
		// this.image = image.copy(Config.ARGB_8888, true);
		canvas = new Canvas(image);
		// canvas.drawColor(color);
		// this.scalefactor=(((EditorScreen)context).editorView.image.scalefactor);
		// canvas.save();
		//
		// canvas.scale((((EditorScreen)context).editorView.image.scalefactor),
		// (((EditorScreen)context).editorView.image.scalefactor), centerX,
		// centerY);
		// canvas.restore();
		System.gc();
		return canvas;

	}

	void onDraw(Canvas canvas) {
		Log.i("can", "canvas ondraw can height" + canvas.getHeight() + " sc ht");

		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();
		canvas.save();
		// canvas.rotate(angle, centerX, centerY);
		canvas.scale(scalefactor, scalefactor, centerX, centerY);
		//
		// canvas.drawRect(0, 0, width, height, paint);
		// canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		//
		float x = centerX - (image.getWidth() * 0.5f);
		float y = centerY - (image.getHeight() * 0.5f);
		imageCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		imageCanvas.drawBitmap(originalImage, 0, 0, null);
		canvas.drawBitmap(image, x, y, null);
		canvas.restore();

		// transCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		// transCanvas.drawBitmap(image.originalImage, 0, 0, null);
		if (faceList != null) {
			for (int i = 0; i < faceList.size(); i++) {
				if (faceList.get(i).isSelected) {
					touchedFace = faceList.get(i);
					Log.i("touchedNumber", "touchedNumber onimagedraw " + i + " " + touchedFace.faceBitmap);
					break;
				}

			}
		}

		for (int i = 0; i < faceList.size(); i++) {
			Log.i("tag", "canvas ondraw of facebitmap " + this + " i" + i);
			if (touchedFace != faceList.get(i)){
				faceList.get(i).onDraw(canvas);
				Log.i("touchedNumber", "on other draw "+faceList.get(i).faceBitmap);
			}
		}
		Log.i("FaceEditor", "image touchd face" + touchedFace);
		if (touchedFace != null)
			touchedFace.onDraw(canvas);
		// if (touchedFace != null) {
		// // int left = (int) (touchedFace.getCoordx() - ((boundry.getWidth() *
		// 0.5f) * (touchedFace.scaleFactorX)));
		// // int top = (int) (touchedFace.getCoordy() - ((boundry.getHeight() *
		// 0.5f) * touchedFace.scaleFactorY));
		// // imageCanvas.drawCircle(left + ((boundry.getWidth()) *
		// (touchedFace.scaleFactorX)), top + ((boundry.getHeight()) *
		// (touchedFace.scaleFactorY)), 20, blackpaint);
		// touchedFace.onDraw(canvas);
		// }
		// image.setImage(transBitmap);

	}

	// void onDraw(Canvas canvas, Image previmage) {
	// Log.i("can", "canvas ondraw of image " + this + " canvas " + canvas);
	//
	// canvasWidth = canvas.getWidth();
	// canvas.save();
	// // canvas.rotate(angle, centerX, centerY);
	// // canvas.scale(scalefactor, scalefactor, centerX, centerY);
	//
	// // canvas.drawRect(0, 0, width, height, paint);
	// // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
	//
	// float x = previmage.centerX - (image.getWidth() * 0.5f);
	// float y = previmage.centerY - (image.getHeight() * 0.5f);
	//
	// if (image != null) {
	// canvas.drawBitmap(image, x, y, null);
	// }
	//
	// // if (pathList != null && pathList.size() > 0) {
	// // for (int i = 0; i < pathList.size(); i++) {
	// //
	// // pathList.get(i).onDraw(canvas);
	// // }
	// // }
	// // if (isExtract && drawPathSelection != null) {
	// // drawPathSelection.onDraw(canvas);
	// // }
	// Log.d("image", "(" + centerX + "," + centerY + ") (" + x + " , " + y +
	// ")" + "(" + image.getWidth() + "," + image.getHeight() + ") " + this);
	//
	// canvas.restore();
	//
	// }

	void setPreXY(float x, float y) {
		this.prevX = x;
		this.prevY = y;
	}

	public boolean isTouch(float x, float y) {

		// double c = Math.cos(-angle * Math.PI / 180);
		// double s = Math.sin(-angle * Math.PI / 180);

		// perform a normal check if the new point is inside the
		// bounds of the UNrotated rectangle
		double leftX = centerX - image.getWidth() * scalefactor / 2;
		double rightX = centerX + image.getWidth() * scalefactor / 2;
		double topY = centerY - image.getHeight() * scalefactor / 2;
		double bottomY = centerY + image.getHeight() * scalefactor / 2;

		// double rotatedX = centerX + c * (x - centerX) - s * (y - centerY);
		// double rotatedY = centerY + s * (x - centerX) + c * (y - centerY);

		if (leftX <= x && x <= rightX && topY <= y && y <= bottomY
		// && y <= canvasWidth
		) {
			this.prevX = x;
			this.prevY = y;
			return true;

		}

		return false;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;

	}

	public void positionX(MotionEvent event) {
		float x = event.getX();
		dx = prevX - x;
		centerX -= dx;
		centerX = (centerX > 0 && centerX < canvasWidth) ? centerX : (centerX < 0 ? 0 : canvasWidth);
		Log.i("FaceEditor", "Image centerx " + centerX);
		prevX = x;

	}

	public float getDisplacementX() {
		return dx;
	}

	public void setDisplacementX(float dx) {
		this.dx = dx;
	}

	public float getDisplacementY() {
		return dy;
	}

	public void setDisplacementY(float dy) {
		this.dy = dy;
	}

	public void positionY(MotionEvent event) {
		float y = event.getY();
		dy = prevY - y;
		centerY -= dy;
		centerY = (centerY > 0 && centerY < canvasHeight) ? centerY : (centerY < 0 ? 0 : canvasHeight);
		prevY = y;

	}

	public void setAngle(float angle) {
		if (angle > 20 || angle < -20)
			return;
		this.angle += angle;
	}

	public float getScaleFactor() {
		return scalefactor;
	}

	public void setScaleFactor(float scalefactor) {
		Log.i("tag", "sf for image " + image + " " + scalefactor);

		this.scalefactor = scalefactor;

	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	// public void setWidth(float scalefactor) {
	// this.width = width * scalefactor;
	// }
	//
	// public float getWidth() {
	// return width;
	// }
	//
	// public void setHeight(float scalefactor) {
	// this.height = height * scalefactor;
	// }
	//
	// public float getHeight() {
	// return height;
	// }

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public float getAngle() {
		return angle;
	}

	Bitmap getFinalImage() {
		Bitmap bit = Bitmap.createBitmap(originalImage.getWidth(), originalImage.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bit);
		canvas.drawBitmap(originalImage, 0, 0, null);
		for (int i = 0; i < faceList.size(); i++) {
			faceList.get(i).setSelected(false);
			faceList.get(i).onBitmapDraw(canvas);
		}
		return bit;
	}

	protected float getImageLeft() {
		return (centerX - image.getWidth() * 0.5f * scalefactor);
	}

	protected float getImageRight() {
		return (centerX + image.getWidth() * 0.5f * scalefactor);
	}

	protected float getImageTop() {
		return (centerY - image.getHeight() * 0.5f * scalefactor);
	}

	protected float getImageBottom() {
		return (centerY + image.getHeight() * 0.5f * scalefactor);
	}

}
