package com.face.editor;

import java.util.Currency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;

public class Face {
	// int left, right, top, bottom;
	boolean isSelected;
	float scaleFactorX = 1.0f, scaleFactorY = 1.0f, prevX, prevY, dx, dy;
	int canvasWidth, canvasHeight;
	Paint whitePaint;
	Bitmap boundry, faceBitmap;
	float coordx, coordy, eyeDistance;
	Image image;
	float angle = 0.0f, prevAngle = 0.0f;
	// FaceCropView view;
	Context context;
	// float prevScaleX = 0.0f, prevScaleY = 0.0f;
	double lineEqA, lineEqB, lineEqC;
	public static int FACE_CROP_VIEW = 1;
	public static int FACE_SWAP_VIEW = 2;
	int faceViewtype = 1;
	boolean isFitXy = true;
	boolean cropBitmap;

	public Face(Context context, float coordx, float coordy, float eyeDistance, Image image, int faceView) {
		// TODO Auto-generated constructor stub
		this.coordx = coordx;
		this.coordy = coordy;
		this.eyeDistance = eyeDistance;

		whitePaint = new Paint();
		whitePaint.setStrokeWidth(5);
		whitePaint.setStyle(Style.STROKE);

		this.image = image;
		this.context = context;
		faceViewtype = faceView;
	}

	void setBoundry(Bitmap boundry) {
		this.boundry = boundry;
		scaleFactorX = (eyeDistance * 2) / boundry.getWidth();
		scaleFactorY = (eyeDistance * 2) / boundry.getWidth();

		// view = ((FaceCropScreen) context).facecropView;
	}

	void setFaceBitmap(Bitmap faceBitmap) {
		this.faceBitmap = faceBitmap;
	}

	protected Bitmap getFaceBitmap() {
		return faceBitmap;
	}

	public void onBitmapDraw(Canvas canvas) {
		canvas.save();
		canvas.rotate(angle, coordx, coordy);
		canvas.scale(scaleFactorX, scaleFactorY, coordx, coordy);
		float left = coordx - faceBitmap.getWidth() * 0.5f;
		float top = coordy - faceBitmap.getHeight() * 0.5f;
		canvas.drawBitmap(faceBitmap, left, top, null);
		canvas.restore();
	}

	public void onDraw(Canvas canvas) {
		Log.i("ondraw", "facebitmap " + faceBitmap);
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();

		float left = (image.getImageLeft()) + (coordx * image.scalefactor - boundry.getWidth() * 0.5f);
		float top = (image.getImageTop()) + (coordy * image.scalefactor - boundry.getHeight() * 0.5f);

		if (faceBitmap != null) {
			canvas.save();
			canvas.rotate(angle, (image.getImageLeft()) + (coordx * image.scalefactor), (image.getImageTop()) + (coordy * image.scalefactor));
			canvas.scale(scaleFactorX * image.scalefactor, scaleFactorY * image.scalefactor, (image.getImageLeft()) + (coordx * image.scalefactor), (image.getImageTop()) + (coordy * image.scalefactor));
			canvas.drawBitmap(faceBitmap, left, top, null);
			canvas.restore();
		}
		if (isSelected && !cropBitmap) {

			canvas.save();
			canvas.rotate(angle, (image.getImageLeft()) + (coordx * image.scalefactor), (image.getImageTop()) + (coordy * image.scalefactor));
			canvas.scale(scaleFactorX * image.scalefactor, scaleFactorY * image.scalefactor, (image.getImageLeft()) + (coordx * image.scalefactor), (image.getImageTop()) + (coordy * image.scalefactor));
			canvas.drawBitmap(boundry, left, top, null);
			canvas.restore();
			left = image.getImageLeft() + (coordx - boundry.getWidth() * 0.5f * scaleFactorX) * image.scalefactor;
			top = image.getImageTop() + (coordy - boundry.getHeight() * 0.5f * scaleFactorY) * image.scalefactor;
			canvas.save();
			canvas.rotate(angle, (image.getImageLeft()) + (coordx * image.scalefactor), (image.getImageTop()) + (coordy * image.scalefactor));
			canvas.drawRect(left, top, (left + boundry.getWidth() * scaleFactorX * image.scalefactor), (top + boundry.getHeight() * scaleFactorY * image.scalefactor), whitePaint);
			if (faceViewtype == FACE_SWAP_VIEW) {
				if (image.currentAction != FaceCropView.IS_TRASH)
					canvas.drawBitmap(image.trash, left - image.trash.getWidth() * 0.5f, top - image.trash.getHeight() * 0.5f, null);
				else
					canvas.drawBitmap(image.ctrash, left - image.ctrash.getWidth() * 0.5f, top - image.ctrash.getHeight() * 0.5f, null);
			}
			if (image.currentAction != FaceCropView.IS_ROTATE_N_SCALE)
				canvas.drawBitmap(image.rotateNScale, left + ((boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.crotateNScale.getWidth() * 0.5f), top - image.crotateNScale.getHeight() * 0.5f, null);

			else
				canvas.drawBitmap(image.crotateNScale, left + ((boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.crotateNScale.getWidth() * 0.5f), top - image.crotateNScale.getHeight() * 0.5f, null);

			if (!isFitXy) {
				canvas.drawBitmap(image.fitXY, left + ((boundry.getWidth() * scaleFactorX * image.getScaleFactor() * 0.5f) - image.fitXY.getWidth() * 0.5f), top - image.fitXY.getHeight() * 0.5f, null);
				canvas.drawBitmap(image.fitXY, left - image.fitXY.getWidth() * 0.5f, top + ((boundry.getHeight() * scaleFactorY * 0.5f * image.getScaleFactor()) - image.fitXY.getHeight() * 0.5f), null);

			}
			Log.i("Face", "currentAction in face " + image.currentAction);
			if (image.currentAction != FaceCropView.IS_SCALE_X)
				canvas.drawBitmap(image.scaleX, left + ((boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.cscaleX.getWidth() * 0.5f), top + (boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor()) - image.cscaleX.getHeight() * 0.5f, null);

			else
				canvas.drawBitmap(image.cscaleX, left + ((boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.cscaleX.getWidth() * 0.5f), top + (boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor()) - image.cscaleX.getHeight() * 0.5f, null);

			if (image.currentAction != FaceCropView.IS_ROTATE_ONLY)
				canvas.drawBitmap(image.rotateOnly, left - image.crotateOnly.getWidth() * 0.5f, top + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.crotateOnly.getHeight() * 0.5f, null);
			else
				canvas.drawBitmap(image.crotateOnly, left - image.crotateOnly.getWidth() * 0.5f, top + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.crotateOnly.getHeight() * 0.5f, null);

			if (image.currentAction != FaceCropView.IS_SCALE_Y)
				canvas.drawBitmap(image.scaleY, left + (boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor()) - image.cscaleY.getWidth() * 0.5f, top + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.cscaleY.getHeight() * 0.5f, null);
			else
				canvas.drawBitmap(image.cscaleY, left + (boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor()) - image.cscaleY.getWidth() * 0.5f, top + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.cscaleY.getHeight() * 0.5f, null);

			if (image.currentAction != FaceCropView.IS_SCALE_ONLY)
				canvas.drawBitmap(image.scaleOnly, left + (boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.cscaleOnly.getWidth() * 0.5f, top + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.cscaleOnly.getHeight() * 0.5f, null);

			else
				canvas.drawBitmap(image.cscaleOnly, left + (boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.cscaleOnly.getWidth() * 0.5f, top + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.cscaleOnly.getHeight() * 0.5f, null);
			canvas.restore();
			// canvas.drawCircle((image.getImageLeft()) + coordx,
			// image.getImageTop() + coordy, 5, whitePaint);
		}

	}

	protected boolean isSelected() {
		return isSelected;
	}

	protected void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	boolean isFaceTouched(MotionEvent event) {
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);
		Log.i("tag", "tested for facetouch");
		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		if (rotatedX >= x && rotatedX <= (x + (boundry.getWidth() * scaleFactorX * image.getScaleFactor())) && rotatedY >= y && rotatedY <= (y + boundry.getHeight() * scaleFactorX * image.getScaleFactor())) {
			Log.i("tag", "facetouch");
			prevX = event.getX();
			prevY = event.getY();
			return true;
		}
		return false;
	}

	boolean isRotate90Touched(MotionEvent event) {

		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		float left = (x - image.ctrash.getWidth() * 0.5f), top = y - image.ctrash.getHeight() * 0.5f;
		// Log.i("tag", "left " + left + " top " + top + " right " + (left +
		// (view.rotate90.getWidth())) + " btm " + (top +
		// view.rotate90.getHeight()) + " rotatedx " + rotatedX + " rotatedy " +
		// rotatedY);
		if (rotatedX >= left && rotatedX <= (left + (image.ctrash.getWidth())) && rotatedY >= top && rotatedY <= (top + image.ctrash.getHeight())) {
			Log.i("tag", "isRotate90Touched");
			// setAngleForRotation90();
			return true;
		}
		return false;
	}

	boolean isRotateAndScaleTouched(MotionEvent event) {
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());
		float left = (x + ((boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.crotateNScale.getWidth() * 0.5f)), top = y - image.crotateNScale.getHeight() * 0.5f;

		if (rotatedX > left && rotatedX < (left + (image.crotateNScale.getWidth())) && rotatedY > top && rotatedY < (top + image.crotateNScale.getHeight())) {
			Log.i("tag", "angle isRotateAndScaleTouched");

			prevAngle = getNewAngle(event.getX(), event.getY());

			return true;
		}
		return false;
	}

	boolean isScaleXTouched(MotionEvent event) {
		Log.i("xtouch", "angle at xtouch is " + angle);
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());
		float left = (x + (boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.cscaleX.getWidth() * 0.5f), top = y + (boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor()) - image.cscaleX.getHeight() * 0.5f;

		if (rotatedX > left && rotatedX < (left + (image.cscaleX.getWidth())) && rotatedY > top && rotatedY < (top + image.cscaleX.getHeight())) {
			Log.i("tag", "isScaleXTouched ");

			float prev = 0.0f;

			if (angle == 0.0f || angle == 180) {
				Log.i("xtouch", "angle is 0.0");
				prev = angle;
				angle = getNewAngle(event.getX(), event.getY() - boundry.getHeight() * 0.5f * scaleFactorY);

			} else
				prev = angle;
			c = Math.cos(angle * Math.PI / 180);
			s = Math.sin(angle * Math.PI / 180);
			float dy = ((coordy * image.getScaleFactor()) + image.getImageTop()) - (boundry.getHeight() * 0.5f * scaleFactorY);
			x1 = (coordx * image.getScaleFactor()) + image.getImageLeft();
			y1 = (coordy * image.getScaleFactor()) + image.getImageTop();
			double x2 = ((coordx * image.getScaleFactor()) + image.getImageLeft() - (s * (dy - (coordy * image.getScaleFactor()) + image.getImageTop())));
			double y2 = ((coordy * image.getScaleFactor()) + image.getImageTop() + c * (dy - (coordy * image.getScaleFactor()) + image.getImageTop()));

			double m = (y2 - y1) / (x2 - x1);
			lineEqA = m;
			lineEqB = -1;
			lineEqC = (y1 - x1 * m);

			angle = prev;
			return true;
		}
		return false;
	}

	boolean isScaleYTouched(MotionEvent event) {
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = (int) ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		float left = (x + (boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor()) - image.cscaleY.getWidth() * 0.5f), top = y + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.cscaleY.getHeight() * 0.5f;

		if (rotatedX > left && rotatedX < (left + (image.cscaleY.getWidth())) && rotatedY > top && rotatedY < (top + image.cscaleY.getHeight())) {
			Log.i("tag", "isScaleYTouched");
			float prev = 0.0f;
			if (angle == 90f || angle == 270) {
				Log.i("xtouch", "angle is 0.0");
				prev = angle;
				angle = getNewAngle(event.getX(), event.getY() - boundry.getHeight() * 0.5f * scaleFactorY);

			} else
				prev = angle;
			c = Math.cos(angle * Math.PI / 180);
			s = Math.sin(angle * Math.PI / 180);
			float dx = ((coordx * image.getScaleFactor()) + image.getImageLeft()) - (boundry.getWidth() * 0.5f * scaleFactorY);
			x1 = (coordx * image.getScaleFactor()) + image.getImageLeft();
			y1 = (coordy * image.getScaleFactor()) + image.getImageTop();
			double x2 = (((coordx * image.getScaleFactor()) + image.getImageLeft()) + c * (dx - ((coordx * image.getScaleFactor()) + image.getImageLeft())));
			double y2 = (((coordy * image.getScaleFactor()) + image.getImageTop()) + s * (dx - ((coordx * image.getScaleFactor()) + image.getImageLeft())));

			double m = (y2 - y1) / (x2 - x1);
			lineEqA = m;
			lineEqB = -1;
			lineEqC = (y1 - x1 * m);

			angle = prev;
			return true;
		}
		return false;
	}

	boolean isScaleOnlyTouched(MotionEvent event) {
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		float left = (x + (boundry.getWidth() * scaleFactorX * image.getScaleFactor()) - image.cscaleOnly.getWidth() * 0.5f), top = y + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.cscaleOnly.getHeight() * 0.5f;

		if (rotatedX > left && rotatedX < (left + (image.cscaleY.getWidth())) && rotatedY > top && rotatedY < (top + image.cscaleY.getHeight())) {
			Log.i("tag", "isScaleOnlyTouched");

			return true;
		}
		return false;
	}

	boolean isRotateOnlyTouched(MotionEvent event) {
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		float left = (x - image.crotateOnly.getWidth() * 0.5f), top = y + (boundry.getHeight() * scaleFactorY * image.getScaleFactor()) - image.crotateOnly.getHeight() * 0.5f;

		if (rotatedX > left && rotatedX < (left + (image.crotateOnly.getWidth())) && rotatedY > top && rotatedY < (top + image.crotateOnly.getHeight())) {
			Log.i("tag", " isRotateOnlyTouched");
			prevAngle = getNewAngle(event.getX(), event.getY());
			return true;
		}
		return false;
	}

	boolean isFitXTouched(MotionEvent event) {
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		float left = (x - image.fitXY.getWidth() * 0.5f), top = y + ((boundry.getHeight() * scaleFactorY * image.getScaleFactor() * 0.5f) - image.fitXY.getHeight() * 0.5f);

		if (rotatedX > left && rotatedX < (left + (image.rotateOnly.getWidth())) && rotatedY > top && rotatedY < (top + image.rotateOnly.getHeight())) {
			Log.i("tag", " isFitXTouched");
			// if (scaleFactorX < 0) {
			// scaleFactorX = -scaleFactorX * ((boundry.getHeight() *
			// scaleFactorY) / (boundry.getWidth() * scaleFactorX)) *
			// image.getScaleFactor();
			// } else {
			scaleFactorX = scaleFactorY;
			// }

			return true;
		}
		return false;
	}

	boolean isFitYTouched(MotionEvent event) {
		double c = Math.cos(-angle * Math.PI / 180);
		double s = Math.sin(-angle * Math.PI / 180);

		float x1 = event.getX() - image.getImageLeft();
		float y1 = event.getY() - (image.getImageTop());
		double rotatedX = (coordx * image.getScaleFactor()) + c * (x1 - (coordx * image.getScaleFactor())) - s * (y1 - (coordy * image.getScaleFactor()));
		double rotatedY = (coordy * image.getScaleFactor()) + s * (x1 - (coordx * image.getScaleFactor())) + c * (y1 - (coordy * image.getScaleFactor()));
		float x = ((coordx * image.getScaleFactor()) - boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());
		float y = ((coordy * image.getScaleFactor()) - boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		float left = (x + ((boundry.getWidth() * image.getScaleFactor() * scaleFactorX * 0.5f) - image.fitXY.getWidth() * 0.5f)), top = y - image.fitXY.getHeight() * 0.5f;

		if (rotatedX > left && rotatedX < (left + (image.rotateOnly.getWidth())) && rotatedY > top && rotatedY < (top + image.rotateOnly.getHeight())) {
			Log.i("tag", " isFitYTouched");
			// if (scaleFactorX < 0) {
			// scaleFactorY = -scaleFactorY * ((boundry.getWidth() *
			// scaleFactorX) / (boundry.getHeight() * scaleFactorY));
			// } else {
			scaleFactorY = scaleFactorX;
			// }
			return true;
		}
		return false;
	}

	public void positionXY(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		dy = prevY - y;
		dx = prevX - x;
		coordx -= dx / image.getScaleFactor();
		coordy -= dy / image.getScaleFactor();

		// coordx = ((coordx*image.getScaleFactor()) > image.getImageLeft() &&
		// (coordx*image.getScaleFactor() < image.getImageRight()) ? coordx :
		// (coordx < image.getImageLeft()
		// ? image.getImageLeft() : image.getImageRight()));
		// coordy = (coordy > 0 && coordy < canvasHeight) ? coordy : (coordy < 0
		// ? 0 : canvasHeight);

		// Log.i("FaceEditor", "Image centerx " + centerX);
		prevX = x;
		prevY = y;

	}

	double getHypotaneous() {

		float sideX = (boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor());

		float sideY = (boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor());

		return Math.sqrt(Math.pow(sideX, 2) + Math.pow(sideY, 2));

	}

	double getNewHypotaneous(float x, float y) {
		float sideX = x - (coordx * image.getScaleFactor() + image.getImageLeft());
		float sideY = y - (coordy * image.getScaleFactor() + image.getImageTop());

		return Math.sqrt(Math.pow(sideX, 2) + Math.pow(sideY, 2));
	}

	protected void setScaleFactorX(float sf) {
		this.scaleFactorX = sf;

	}

	protected void setScaleFactorY(float sf) {
		this.scaleFactorY = sf;
	}

	protected void setAngle(MotionEvent event) {
		Log.i("tag", "setangle true");
		double newAngle = getNewAngle(event.getX(), event.getY());
		double da = newAngle - prevAngle;
		this.angle = (float) (this.angle + da);
		Log.i("layerimage", "newangle" + newAngle + " prevangle " + prevAngle + " angle " + angle);
		this.prevAngle = (float) newAngle;
	}

	float getNewAngle(float x, float y) {

		double dx = x - (coordx * image.getScaleFactor() + image.getImageLeft());
		double dy = y - (coordy * image.getScaleFactor() + image.getImageTop());

		double angle = Math.atan2(dy, dx);
		return (float) Math.toDegrees(angle);
	}

	float getScaledX(MotionEvent event) {

		double d = ((Math.abs((lineEqA * event.getX()) + ((lineEqB * event.getY()) + lineEqC))) / ((Math.sqrt(Math.pow(lineEqA, 2) + Math.pow(lineEqB, 2)))) * 1f);
		float dx = (float) ((d) / (boundry.getWidth() * 0.5f * scaleFactorX * image.getScaleFactor()));
		Log.i("value ", "d " + d + " disx " + dx);
		// prevScaleX = event.getX();
		// prevScaleY = event.getY();
		return dx;
	}

	float getScaledY(MotionEvent event) {

		double d = ((Math.abs((lineEqA * event.getX()) + ((lineEqB * event.getY()) + lineEqC))) / ((Math.sqrt(Math.pow(lineEqA, 2) + Math.pow(lineEqB, 2)))) * 1f);
		float dy = (float) ((d) / (boundry.getHeight() * 0.5f * scaleFactorY * image.getScaleFactor()));
		Log.i("value ", "d " + d + " disx " + dy);
		// prevScaleX = event.getX();
		// prevScaleY = event.getY();
		return dy;

		// Log.i("layerimage", "scaley " + scaleFactorY);
	}

	protected float getCoordx() {
		return coordx;
	}

	public float getCoordy() {
		return coordy;
	}

	public void setFitXy(boolean isFitXY) {
		this.isFitXy = isFitXY;
	}
}
