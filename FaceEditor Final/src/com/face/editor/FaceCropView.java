package com.face.editor;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class FaceCropView extends View implements OnTouchListener {

	Image image;
	ScaleGestureDetector detector;
	float scaleFactor = 1.0f;
	float coordX1[], coordY1[], eye_distance[];
	int no_of_face_detected = 0;
	int width, height;
	Bitmap boundry, boundryMask;
	List<Face> faceList;
	Context context;
	Bitmap transBitmap;
	Canvas transCanvas;
	Face touchFace;
	int currentAction = 10;
	public static final int IS_SCALE_ONLY = 1;
	public static final int IS_ROTATE_ONLY = 2;
	public static final int IS_ROTATE_N_SCALE = 3;
	public static final int IS_SCALE_X = 4;
	public static final int IS_SCALE_Y = 5;
	public static final int IS_TRASH = 6;
	public static final int IS_FITX = 7;
	public static final int IS_FITY = 8;

	public FaceCropView(Context context, Image image, int width, int height) {
		super(context);
		this.context = context;
		this.image = image;

		detector = new ScaleGestureDetector(context, new ScaleListener());
		setOnTouchListener(this);
		this.width = width;
		this.height = height;
		boundry = BitmapFactory.decodeResource(getResources(), R.drawable.face_boundry);
		boundryMask = BitmapFactory.decodeResource(getResources(), R.drawable.face_mask);
		faceList = new ArrayList<Face>();
		setImagedetect(image.image);

		transBitmap = Bitmap.createBitmap(image.image.getWidth(), image.image.getHeight(), Config.ARGB_8888);
		transCanvas = new Canvas(transBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		// drawImageWithMarker(transCanvas);
		if (image != null)
			image.onDraw(canvas);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getPointerCount() == 1) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (touchFace != null) {
					// touchFace.setBoundryColor(Color.parseColor("#ff553b"));
					if (touchFace.isRotate90Touched(event)) {
						currentAction = IS_TRASH;
						image.setCurrentAction(currentAction);
						invalidate();
						return true;
					}
					if (touchFace.isRotateAndScaleTouched(event)) {
						currentAction = IS_ROTATE_N_SCALE;
						image.setCurrentAction(currentAction);
						// touchedImage.scaleFactorY =
						// touchedImage.scaleFactorX;
						invalidate();
						return true;
					}
					if (touchFace.isRotateOnlyTouched(event)) {
						currentAction = IS_ROTATE_ONLY;
						image.setCurrentAction(currentAction);
						invalidate();
						return true;
					}
					if (touchFace.isScaleOnlyTouched(event)) {
						currentAction = IS_SCALE_ONLY;
						image.setCurrentAction(currentAction);
						// touchedImage.scaleFactorY =
						// touchedImage.scaleFactorX;
						invalidate();
						return true;
					}
					if (touchFace.isScaleXTouched(event)) {
						currentAction = IS_SCALE_X;
						image.setCurrentAction(currentAction);
						touchFace.setFitXy(false);
						invalidate();
						return true;
					}
					if (touchFace.isScaleYTouched(event)) {
						currentAction = IS_SCALE_Y;
						image.setCurrentAction(currentAction);
						touchFace.setFitXy(false);
						invalidate();
						return true;
					}
					if (touchFace.isFitXTouched(event)) {
						touchFace.setFitXy(true);
						currentAction = IS_FITX;
						image.setCurrentAction(currentAction);
						invalidate();
						return false;
					}
					if (touchFace.isFitYTouched(event)) {
						touchFace.setFitXy(true);
						currentAction = IS_FITY;
						image.setCurrentAction(currentAction);
						invalidate();
						return false;
					}
					if (touchFace.isFaceTouched(event)) {
						currentAction = 9;
						image.setCurrentAction(currentAction);
						invalidate();
						return true;
					}
					if (image.isTouch(event.getX(), event.getY())) {
						currentAction = 10;
						image.setCurrentAction(currentAction);
						return true;
					}
					// }
					//
					// for (int i = 0; i < layerImageList.size(); i++) {
					// if (layerImageList.get(i).isImageTouch(event)) {
					// Log.i("tag", "touchedimage setting false inside loop");
					// if (touchedImage != null) {
					// touchedImage.setTouched(false);
					// touchedImage = null;
					// }
					// touchedImage = layerImageList.get(i);
					// touchedImage.setTouched(true);
					//
					// touchedImage.setBoundryColor(Color.parseColor("#ff553b"));
					// ((EditorScreen) context).setButtonsVisibility(true,
					// true);
					//
					// bgImage.setTouched(false);
					// invalidate();
					//
					// return true;
					// }
					// }
					// if (touchedImage != null) {
					// Log.i("tag", "touchedimage setting false inside if");
					// touchedImage.setTouched(false);
					// touchedImage = null;
					// invalidate();
					// }
					//
					// if (bgImage.isImageTouched(event)) {
					//
					// imageTouched = true;
					// bgImage.setTouched(true);
					// bgImage.setBoundryColor(Color.parseColor("#ff553b"));
					// ((EditorScreen) context).setButtonsVisibility(true,
					// false);
					// return true;
					// } else {
					// Log.i("tag",
					// "touchedimage setting false inside else part");
					// imageTouched = false;
					// bgImage.setTouched(false);
					// if (touchedImage != null) {
					// touchedImage.setTouched(false);
					// touchedImage = null;
					//
					// invalidate();
					// }
					image.setCurrentAction(currentAction);
				}

				return false;
			case MotionEvent.ACTION_MOVE:
				Log.i("faceeditor", "onmonve");
				switch (currentAction) {
				case 9:
					touchFace.positionXY(event);
					break;

				case 10:
					image.positionX(event);
					image.positionY(event);
					break;
				case IS_SCALE_ONLY:

					float sf = (float) ((touchFace.getNewHypotaneous(event.getX(), event.getY())) / touchFace.getHypotaneous());
					touchFace.setScaleFactorX(touchFace.scaleFactorX * sf);
					touchFace.setScaleFactorY(touchFace.scaleFactorY * sf);

					break;
				case IS_ROTATE_N_SCALE:
					touchFace.setAngle(event);

					float sf1 = (float) ((touchFace.getNewHypotaneous(event.getX(), event.getY())) / touchFace.getHypotaneous());
					touchFace.setScaleFactorX(touchFace.scaleFactorX * sf1);
					touchFace.setScaleFactorY(touchFace.scaleFactorY * sf1);
					break;

				case IS_ROTATE_ONLY:
					Log.i("tag", "isrotateonly moving");
					touchFace.setAngle(event);

					break;
				case IS_SCALE_X:
					if ((touchFace.scaleFactorX * touchFace.getScaledX(event)) > 0.13f) {

						touchFace.setScaleFactorX(touchFace.scaleFactorX * touchFace.getScaledX(event));
						Log.i("faceeditor", "setScaleFactorX if scalex  moving");
					} else {
						Log.i("editview", "scalex  moving sf " + touchFace.scaleFactorX + " angle " + touchFace.angle);
					}

					// touchFace.setFitXy(false);
					break;

				case IS_SCALE_Y:
					if ((touchFace.scaleFactorY * touchFace.getScaledY(event)) > 0.13f)
						touchFace.setScaleFactorY(touchFace.scaleFactorY * touchFace.getScaledY(event));
					// touchFace.setFitXy(false);
					break;
				default:
					break;
				}

				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				currentAction = 10;
				image.setCurrentAction(currentAction);
				invalidate();
				break;

			default:
				break;
			}

		} else {
			detector.onTouchEvent(event);
		}
		return false;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();
			image.setScaleFactor(scaleFactor);
			// drawImageWithMarker(transCanvas);
			invalidate();
			return true;
		}
	}

	void drawImageWithMarker(Canvas canvas) {
		transCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		transCanvas.drawBitmap(image.originalImage, 0, 0, null);
		if (faceList != null) {
			for (int i = 0; i < faceList.size(); i++) {
				if (faceList.get(i).isSelected) {
					touchFace = faceList.get(i);
					Log.i("FaceEditor", "image touchd face posi" + i);
					break;
				}

			}
		}
		if (touchFace != null) {
			touchFace.onDraw(transCanvas);
		}
		image.setImage(transBitmap);
	}

	public void setImagedetect(Bitmap bitmap) {

		no_of_face_detected = 0;

		// float mScale = transImage.getScaleFactor();
		float mScale = 1.0f;
		Log.i("tag", " editBitmap null " + bitmap);
		Bitmap bmpFD = Effects.toGrayscale(bitmap);
		Log.i("size", "bit w " + bitmap.getWidth() + "bit h " + bitmap.getHeight() + " bmpfd w " + bmpFD.getWidth() + " bmpfd h " + bmpFD.getHeight());
		FaceDetector.Face[] detectedFaces = new FaceDetector.Face[10];
		FaceDetector faceDetector = new FaceDetector(bmpFD.getWidth(), bmpFD.getHeight(), 10);
		no_of_face_detected = faceDetector.findFaces(bmpFD, detectedFaces);
		coordX1 = new float[no_of_face_detected];
		coordY1 = new float[no_of_face_detected];
		eye_distance = new float[no_of_face_detected];
		// faceScale=new float[no_of_face_detected];
		// dA=new float[no_of_face_detected];
		System.gc();
		Log.d("FaceSwap", "No. of facedetected " + no_of_face_detected);
		if (no_of_face_detected != 0) {
			for (int i = 0; i < no_of_face_detected; i++) {
				FaceDetector.Face face = detectedFaces[i];
				PointF midPoint = new PointF();
				face.getMidPoint(midPoint);

				eye_distance[i] = face.eyesDistance() / mScale;
				// Log.d("FaceSwap", "eye distance " + eye_distance);
				// Log.d("FaceSwap", "face confidence  " + face.confidence());

				coordX1[i] = midPoint.x / mScale;
				coordY1[i] = midPoint.y / mScale;
				// float sf = (eye_distance[i] * 2) / boundry.getWidth();
				// float left = image.getImageLeft() + coordX1[i] -
				// boundry.getWidth() * 0.5f * sf;
				// float top = image.getImageTop() + coordY1[i] -
				// boundry.getHeight() * 0.5f * sf;
				Face newface = new Face(context, coordX1[i], coordY1[i], eye_distance[i], image, Face.FACE_CROP_VIEW);
				newface.setBoundry(boundry);

				if (faceList.size() == 0) {
					newface.setSelected(true);
					touchFace = newface;
					Log.d("faceeditor", "image face setselected true");
				}
				faceList.add(newface);
				// Log.d("FaceSwap", "cordx " + coordX1 + " cordy" + coordY1 +
				// " scale" + mScale);

			}

		}
		if (no_of_face_detected == 0) {
			coordX1 = new float[1];
			coordY1 = new float[1];
			eye_distance = new float[1];
			eye_distance[0] = (float) (image.image.getWidth() * 0.33f);

			coordX1[0] = (float) (image.image.getWidth() * 0.5f);
			coordY1[0] = (float) (image.image.getHeight() * 0.5f);

			Face newface = new Face(context, coordX1[0], coordY1[0], eye_distance[0], image, Face.FACE_CROP_VIEW);
			newface.setBoundry(boundry);
			if (faceList.size() == 0) {
				newface.setSelected(true);
				touchFace = newface;
			}
			faceList.add(newface);
			Log.i("tag", " eyedistance " + eye_distance[0] + " coordx1 " + coordX1[0] + " coordy " + coordY1[0] + " w " + width);
		}

		invalidate();
	}

	void setTouchedFaceToNext() {
		if (touchFace != null)
			touchFace.setSelected(false);
		if (faceList.size() < 2) {
			AlertDialog.Builder dia = new AlertDialog.Builder(context);
			dia.setMessage("No more Face found.Adjust Marker yourself");
			dia.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			dia.show();

		}
		for (int i = 0; i < faceList.size(); i++) {
			if (touchFace == faceList.get(i)) {
				Log.i("FaceEditor", "facecropview touchedview " + i);
				if (i + 1 < faceList.size())
					touchFace = faceList.get(i + 1);
				else {
					touchFace = faceList.get(0);
				}
				touchFace.setSelected(true);
				break;
			}
		}
		invalidate();
	}

	// Bitmap getCroppedBitmap() throws Exception {
	// int left = (int) (touchFace.getCoordx() - ((boundry.getWidth() * 0.5f) *
	// (touchFace.scaleFactorX)));
	// int top = (int) (touchFace.getCoordy() - ((boundry.getHeight() * 0.5f) *
	// touchFace.scaleFactorY));
	// int width = (int) ((boundry.getWidth() * touchFace.scaleFactorX));
	// int height = (int) ((boundry.getHeight() * touchFace.scaleFactorY));
	// Log.i("faceeditor", "getCroppedBitmap left " + left + " top " + top +
	// " width " + width + " height " + height);
	//
	// float leftnwidth = (touchFace.getCoordx() * image.getScaleFactor() -
	// ((boundry.getWidth() * 0.5f) * (touchFace.scaleFactorX) *
	// image.getScaleFactor())) + ((boundry.getWidth() * touchFace.scaleFactorX
	// * image.getScaleFactor()));
	// float topnheight = (touchFace.getCoordy() * image.getScaleFactor() -
	// ((boundry.getHeight() * 0.5f) * touchFace.scaleFactorY) *
	// image.getScaleFactor()) + ((boundry.getHeight() * touchFace.scaleFactorY)
	// * image.getScaleFactor());
	// if ((leftnwidth) > (image.image.getWidth() * image.getScaleFactor())) {
	// Log.i("FaceCropView", "getCroppedBitmap (left + width) >");
	// width = (int) (image.image.getWidth() - left);
	// }
	// if ((topnheight) > (image.image.getHeight() * image.getScaleFactor())) {
	// height = (int) (image.image.getHeight() - top);
	// Log.i("FaceCropView", "getCroppedBitmap (top + height) >");
	// }
	// if (left < 0) {
	// Log.i("FaceCropView", "getCroppedBitmap left < 0");
	// width = width + left;
	// left = 0;
	// }
	//
	// if (top < 0) {
	// Log.i("FaceCropView", "getCroppedBitmap top < 0");
	// height = height + top;
	// top = 0;
	// }
	//
	// // Matrix m = new Matrix();
	// // m.preRotate(touchFace.angle, touchFace.getCoordx(),
	// touchFace.getCoordy());
	// // Log.i("tag", "origina width " + image.originalImage.getWidth() +
	// " height " + image.originalImage.getHeight());
	// // Bitmap bit = Bitmap.createBitmap(image.originalImage, 0, 0,
	// image.originalImage.getWidth(), image.originalImage.getHeight(), m,
	// true);
	// // Log.i("tag", "bit width " + bit.getWidth() + " height " +
	// bit.getHeight());
	// Bitmap cropped = Bitmap.createBitmap(image.originalImage, left, top,
	// width, height);
	//
	// Bitmap transBitmap = Bitmap.createBitmap((int) (boundry.getWidth() *
	// touchFace.scaleFactorX), (int) (boundry.getHeight() *
	// touchFace.scaleFactorY), Config.ARGB_8888);
	// Canvas transCanvas = new Canvas(transBitmap);
	// Paint paint = new Paint();
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	// paint.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL));
	// transCanvas.drawBitmap(Bitmap.createScaledBitmap(boundryMask,
	// transBitmap.getWidth(), transBitmap.getHeight(), true), 0, 0, null);
	// left = 0;
	// top = 0;
	// // if ((image.getImageLeft() + touchFace.getCoordx() *
	// // image.getScaleFactor()) < image.centerX) {
	// // left = transBitmap.getWidth() - cropped.getWidth();
	// // } else {
	// // left = 0;
	// // }
	// // if ((image.getImageTop() + touchFace.getCoordy() *
	// // image.getScaleFactor()) < image.centerY) {
	// // Log.i("FaceCropView", "less " + "face cy " + touchFace.getCoordy() +
	// // " image cy " + image.centerY);
	// // top = transBitmap.getHeight() - cropped.getHeight();
	// // } else {
	// // Log.i("FaceCropView", "greater " + "face cy " + touchFace.getCoordy()
	// // + " image cy " + image.centerY);
	// // top = 0;
	// // }
	// transCanvas.drawBitmap(cropped, left, top, paint);
	// return bit;
	// }
	//
	Bitmap getCroppedBitmap(ImageView loading) throws Exception {

		return getBitmapFromView(loading);

	}

	public Bitmap getBitmapFromView(ImageView loading) {
		touchFace.cropBitmap = true;
		invalidate();
		Bitmap returnBit = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnBit);
		Drawable bgDrawable = this.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.WHITE);
		draw(canvas);
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		paint.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL));

		Bitmap transBit = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas transCanvas = new Canvas(transBit);

		transCanvas.save();
		transCanvas.rotate(touchFace.angle, (image.getImageLeft() + touchFace.getCoordx() * image.getScaleFactor()), (image.getImageTop() + touchFace.getCoordy() * image.getScaleFactor()));
		transCanvas.scale(touchFace.scaleFactorX * image.getScaleFactor(), touchFace.scaleFactorY * image.getScaleFactor(), (image.getImageLeft() + touchFace.getCoordx() * image.getScaleFactor()), (image.getImageTop() + touchFace.getCoordy() * image.getScaleFactor()));
		transCanvas.drawBitmap(boundryMask, (image.getImageLeft() + touchFace.getCoordx() * image.getScaleFactor()) - touchFace.boundry.getWidth() * 0.5f, (image.getImageTop() + touchFace.getCoordy() * image.getScaleFactor()) - touchFace.boundry.getHeight() * 0.5f, null);
		// canvas.drawBitmap(boundryMask, touchFace.boundry.getWidth() * 0.5f,
		// touchFace.boundry.getHeight() * 0.5f, paint);
		transCanvas.restore();

		transCanvas.drawBitmap(returnBit, 0, 0, paint);
		touchFace.cropBitmap = false;
		return gettrimBitmap(transBit, loading);
	}

	public Bitmap trimBitmap(Bitmap transBitmap) {
		Log.i("tag ", "saveBitmap start ");
		int left = 0, right = 0, top = 0, bottom = 0;
		int width = transBitmap.getWidth();
		int height = transBitmap.getHeight();
		Log.i("size", "before transBitmap size is width " + transBitmap.getWidth() + " height " + transBitmap.getHeight());
		// top
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (transBitmap.getPixel(j, i) != Color.TRANSPARENT) {
					top = i;
					break;
				}

			}
			if (top != 0)
				break;
		}
		// bottom
		for (int i = height - 1; i >= 0; i--) {
			for (int j = 0; j < width; j++) {
				if (transBitmap.getPixel(j, i) != Color.TRANSPARENT) {
					bottom = i;
					break;
				}

			}
			if (bottom != 0)
				break;
		}
		// left
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (transBitmap.getPixel(i, j) != Color.TRANSPARENT) {
					left = i;
					break;
				}

			}
			if (left != 0)
				break;
		}
		// right
		for (int i = width - 1; i >= 0; i--) {
			for (int j = 0; j < height; j++) {
				if (transBitmap.getPixel(i, j) != Color.TRANSPARENT) {
					right = i;
					break;
				}
			}
			if (right != 0)
				break;
		}
		if ((right - left) > 0)
			return (Bitmap.createBitmap(transBitmap, left, top, right - left, bottom - top, null, false));
		else
			return null;
	}

	Bitmap gettrimBitmap(Bitmap bitmap, final ImageView loading) {
		Bitmap returnBitmap = null;
		final AsyncTask<Bitmap, Void, Bitmap> trimAsync = new AsyncTask<Bitmap, Void, Bitmap>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				loading.setVisibility(View.VISIBLE);
				loading.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotateanim));

			}

			@Override
			protected Bitmap doInBackground(Bitmap... voids) {
				// TODO Auto-generated method stub

				return trimBitmap(voids[0]);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				loading.setVisibility(View.GONE);
				loading.clearAnimation();
				((FaceCropScreen) context).setBitmapInCroppedImages(result);
				// returnBitmap = result;

			}
		}.execute(bitmap);
		return returnBitmap;
	}
}