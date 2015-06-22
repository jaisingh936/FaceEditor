package com.face.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FaceSwapView extends View implements OnTouchListener {

	int width, height;
	Image image;
	Context context;
	ScaleGestureDetector detector;
	Face touchFace;
	int currentAction = 10;
	Bitmap transBitmap;
	Canvas transCanvas;
	public static final int IS_SCALE_ONLY = 1;
	public static final int IS_ROTATE_ONLY = 2;
	public static final int IS_ROTATE_N_SCALE = 3;
	public static final int IS_SCALE_X = 4;
	public static final int IS_SCALE_Y = 5;
	public static final int IS_TRASH = 6;
	public static final int IS_FITX = 7;
	public static final int IS_FITY = 8;
	float scaleFactor = 1.0f;

	List<Face> faceList;
	float coordX1[], coordY1[], eye_distance[];
	int no_of_face_detected = 0, touchedNumber;

	public FaceSwapView(Context context, int width, int height) {
		super(context);

		this.context = context;

		detector = new ScaleGestureDetector(context, new ScaleListener());
		setOnTouchListener(this);
		this.width = width;
		this.height = height;

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
						deleteCurrentItem();
						invalidate();
						return true;
					}
					if (touchFace.isRotateAndScaleTouched(event)) {
						currentAction = IS_ROTATE_N_SCALE;
						image.setCurrentAction(currentAction);
						// touchedImage.scaleFactorY =
						// touchedImage.scaleFactorX;
						return true;
					}
					if (touchFace.isRotateOnlyTouched(event)) {
						currentAction = IS_ROTATE_ONLY;
						image.setCurrentAction(currentAction);
						return true;
					}
					if (touchFace.isScaleOnlyTouched(event)) {
						currentAction = IS_SCALE_ONLY;
						image.setCurrentAction(currentAction);
						// touchedImage.scaleFactorY =
						// touchedImage.scaleFactorX;
						return true;
					}
					if (touchFace.isScaleXTouched(event)) {
						currentAction = IS_SCALE_X;
						image.setCurrentAction(currentAction);
						touchFace.setFitXy(false);
						return true;
					}
					if (touchFace.isScaleYTouched(event)) {
						currentAction = IS_SCALE_Y;
						image.setCurrentAction(currentAction);
						touchFace.setFitXy(false);
						return true;
					}
					if (touchFace.isFitXTouched(event)) {
						currentAction = IS_FITX;
						image.setCurrentAction(currentAction);
						touchFace.setFitXy(true);
						invalidate();
						return true;
					}
					if (touchFace.isFitYTouched(event)) {
						currentAction = IS_FITY;
						image.setCurrentAction(currentAction);
						touchFace.setFitXy(true);
						invalidate();
						return true;
					}
					// if (touchFace.isFaceTouched(event)) {
					// currentAction = 9;
					// invalidate();
					// return true;
					// }
					// if (image.isTouch(event.getX(), event.getY())) {
					// currentAction = 10;
					// return true;
					// }
					// }
				}

				if (touchFace != null && touchFace.isFaceTouched(event)) {
					currentAction = 9;
					image.setCurrentAction(currentAction);
					touchFace.setSelected(true);
					((FaceSwapScreen) context).enableEdit(true);
					invalidate();

					return true;
				}
				for (int i = faceList.size() - 1; i >= 0; i--) {
					if (faceList.get(i).isFaceTouched(event)) {

						if (touchFace != null) {
							touchFace.setSelected(false);
							;
							touchFace = null;
						}

						touchFace = faceList.get(i);
						touchedNumber = i;
						Log.i("touchedNumber", "touchedNumber ontouc " + touchedNumber);
						((FaceSwapScreen) context).enableEdit(true);
						touchFace.setSelected(true);

						// touchFace.setBoundryColor(Color.parseColor("#ff553b"));
						// ((EditorScreen)
						// context).setButtonsVisibility(true, true);

						// bgImage.setTouched(false);
						currentAction = 9;
						image.setCurrentAction(currentAction);
						invalidate();

						return true;
					}
				}
				if (touchFace != null) {
					Log.i("tag", "touchedimage setting false inside if");
					touchFace.setSelected(false);
					((FaceSwapScreen) context).enableEdit(true);
					touchFace = null;
					invalidate();
				}

				if (image.isTouch(event.getX(), event.getY())) {

					currentAction = 10;
					((FaceSwapScreen) context).enableEdit(false);
					image.setCurrentAction(currentAction);
					// imageTouched = true;
					// bgImage.setTouched(true);
					// bgImage.setBoundryColor(Color.parseColor("#ff553b"));
					// ((EditorScreen) context).setButtonsVisibility(true,
					// false);
					return true;
				}

				// else {
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
				// }

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
				// Log.i("ontouch", "touchedface selected" +
				// touchFace.isSelected);
				break;

			default:
				break;
			}

		} else {
			detector.onTouchEvent(event);
		}
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		// drawImageWithMarker(transCanvas);
		if (image != null)
			image.onDraw(canvas);

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

	void setImage(Bitmap bitmap) {
		this.image = new Image(bitmap, width * 0.5f, height * 0.5f, context);
		faceList = new ArrayList<Face>();
		image.setFaceList(faceList);
		image.setTouchedFace(touchFace);
		image.setCurrentAction(currentAction);
		// transBitmap = (Bitmap.createBitmap(image.image.getWidth(),
		// image.image.getHeight(), Config.ARGB_8888)).copy(Config.ARGB_8888,
		// true);
		// transCanvas = new Canvas(transBitmap);
		setImagedetect(bitmap);
		int j = 0;
		for (int i = 0; i < coordX1.length; i++) {
			Log.i("faceswapview", "setImage i " + i + " j " + j);
			Face face = new Face(context, coordX1[i], coordY1[i], eye_distance[i], image, Face.FACE_SWAP_VIEW);
			if (((FaceSwapScreen) context).croopedArray.size() > j) {
				face.setBoundry(((FaceSwapScreen) context).croopedArray.get(j));
				face.setFaceBitmap(((FaceSwapScreen) context).croopedArray.get(j));
				if (touchFace != null)
					touchFace.setSelected(false);
				face.setSelected(true);
				touchFace = face;
				faceList.add(face);
				if (j < i) {
					j++;
				} else {
					j = 0;
				}
			}
		}
		invalidate();

	}

	void setImageOnBg(Bitmap croppedBitmap) {
		Random rn = new Random();
		int randomNum = rn.nextInt(coordX1.length);

		Face face = new Face(context, coordX1[randomNum], coordY1[randomNum], eye_distance[randomNum], image, Face.FACE_SWAP_VIEW);
		face.setBoundry(croppedBitmap);
		face.setFaceBitmap(croppedBitmap);
		if (touchFace != null)
			touchFace.setSelected(false);
		face.setSelected(true);

		touchFace = face;
		faceList.add(face);
		invalidate();
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

			}

		}
		if (no_of_face_detected == 0) {
			coordX1 = new float[1];
			coordY1 = new float[1];
			eye_distance = new float[1];
			eye_distance[0] = (float) (bitmap.getWidth() * 0.33f);

			coordX1[0] = (float) (bitmap.getWidth() * 0.5f);
			coordY1[0] = (float) (bitmap.getHeight() * 0.5f);

		}

		invalidate();
	}

	void flipFaceImage() {

		if (touchFace == null || !touchFace.isSelected) {
			Toast.makeText(getContext(), "First Select the face", Toast.LENGTH_SHORT).show();

		} else {
			Matrix m = new Matrix();
			m.preScale(-1, 1);
			Bitmap flip = Bitmap.createBitmap(touchFace.getFaceBitmap(), 0, 0, touchFace.getFaceBitmap().getWidth(), touchFace.getFaceBitmap().getHeight(), m, true);
			touchFace.setFaceBitmap(flip);
			touchFace.boundry = flip;
		}
		invalidate();
	}

	Bitmap getFinalImage() {
		return image.getFinalImage();
	}

	void deleteCurrentItem() {
		AlertDialog.Builder dia = new AlertDialog.Builder(context);
		dia.setMessage("Do you want to delete this face?");
		dia.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// touchFace.setSelected(false);
				faceList.remove(touchFace);
				touchFace = null;
				image.setTouchedFace(null);
				dialog.dismiss();
				invalidate();
			}
		});
		dia.show();

	}

	public void refreshCroppedImages(Bitmap image) {
		// Toast.makeText(EditorScreen.this, "tag is " + tag,
		// Toast.LENGTH_LONG).show();

		Log.i("tag", "touchedNumber " + touchedNumber);
//		if (touchFace != null) {
//			touchFace.setSelected(false);
//			;
//			touchFace = null;
//		}
		// touchFace.setFaceBitmap(image);
		Log.i("touchedNumber", "touchedNumber onrefresh " + touchedNumber + " " + image);
		faceList.get(touchedNumber).setSelected(true);
		faceList.get(touchedNumber).setFaceBitmap(image);
		faceList.get(touchedNumber).setBoundry(image);
		this.image.setTouchedFace(faceList.get(touchedNumber));

		this.image.setFaceList(faceList);

		// ((ImageView) croppedImages.getChildAt(tag)).setImageBitmap(image);
		// for (int i = 0; i < croppedImages.getChildCount(); i++) {
		//
		// ((ImageView)
		// croppedImages.getChildAt(i)).setImageBitmap(croopedArray.get(i));
		//
		// ((ImageView) croppedImages.getChildAt(i)).invalidate();
		// }

		invalidate();
		// touchFace = faceList.get(touchedNumber);
		// invalidate();
	}
}
