package com.face.editor;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
//import android.graphics.PorterDuff.Mode;
//import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class StickerView extends View implements OnTouchListener {

	Bitmap image, originalBitmap, permTransImage, tempTransImage, beforeOrientation;
	Context context;
	int tag, width, height, removeX, removeY;
	// boolean isEreaser;
	List<DrawPath> pathList;
	List<DrawPath> undoList;
	List<Point> pointList;
	Path path;
	// Paint paint;
	private static final float TOUCH_TOLERANCE = 4;
	private float mX, mY, ny, px, py;
	int filterColor = Color.parseColor("#ABADCB");
	int seekWidth = 10;
	float angle = 0.0f;
	Paint paint = new Paint();
	Paint redFillPaint = new Paint();
	Paint redStrokePaint = new Paint();
	Paint transPaint = new Paint();
	int offsetY = 100, colorSmilarity;
	boolean showOffset, isManual, orientation;
	ScaleGestureDetector detector;
	float scaleFactor = 1.0f;
	Canvas permTransCanvas, tempTransCanvas, imageCanvas;
	float imageLeft, imageRight, imageTop, imageBottom;
	int screenType;

	public StickerView(Context context, Bitmap sticker, int tag, int width, int height, int screenType) {
		super(context);
		this.screenType = screenType;
		this.image = sticker;
		originalBitmap = image.copy(Config.ARGB_8888, true);

		this.context = context;
		this.tag = tag;
		this.width = width;
		this.height = height;
		paint = new Paint();

		setOnTouchListener(this);

		pathList = new ArrayList<DrawPath>();
		undoList = new ArrayList<DrawPath>();
		pointList = new ArrayList<Point>();
		invalidate();

		// transLayer = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		// transCanvas = new Canvas(transLayer);

		permTransImage = Bitmap.createBitmap(sticker.getWidth(), sticker.getHeight(), Config.ARGB_8888);
		permTransCanvas = new Canvas(permTransImage);
		tempTransImage = Bitmap.createBitmap(sticker.getWidth(), sticker.getHeight(), Config.ARGB_8888);
		tempTransCanvas = new Canvas(tempTransImage);
		imageCanvas = new Canvas(image);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
		// paint.setColor(Color.BLACK);

		redFillPaint.setColor(Color.RED);
		redStrokePaint.setColor(Color.RED);
		redStrokePaint.setStyle(Style.STROKE);

		transPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
		transPaint.setColor(Color.TRANSPARENT);

		detector = new ScaleGestureDetector(context, new ScaleListener());

		imageLeft = (width * 0.5f - image.getWidth() * 0.5f);
		imageTop = (height * 0.5f - image.getHeight() * 0.5f);
		imageRight = (width * 0.5f + image.getWidth() * 0.5f);
		imageBottom = (height * 0.5f + image.getHeight() * 0.5f);

	}

	protected int getColorSmilarity() {
		return colorSmilarity;
	}

	protected void setColorSmilarity(int colorSmilarity) {
		this.colorSmilarity = colorSmilarity;
	}

	protected int getSeekWidth() {
		return seekWidth;
	}

	protected void setSeekWidth(int seekWidth) {
		this.seekWidth = seekWidth;
		invalidate();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// Log.i("tag", "ondraw");
		canvas.drawColor(Color.RED);
		canvas.save();
		canvas.scale(scaleFactor, scaleFactor, width * 0.5f, height * 0.5f);
		canvas.rotate(angle, width * 0.5f, height * 0.5f);
		// canvas.drawBitmap(image, width * 0.5f - image.getWidth() * 0.5f,
		// height * 0.5f - image.getHeight() * 0.5f, null);
		tempTransCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		if (pathList != null && pathList.size() > 0) {
			// for (int i = 0; i < pathList.size(); i++) {
			Log.i("ondraw", "drawpath items " + scaleFactor);
			pathList.get(pathList.size() - 1).onDraw(tempTransCanvas);
			// }
		}
		tempTransCanvas.drawBitmap(permTransImage, 0, 0, null);
		tempTransCanvas.drawBitmap(image, 0, 0, paint);
		canvas.drawBitmap(tempTransImage, width * 0.5f - image.getWidth() * 0.5f, height * 0.5f - image.getHeight() * 0.5f, null);

		canvas.restore();

		if (!orientation) {
			canvas.drawCircle(mX, mY, 10, redFillPaint);
			canvas.drawCircle(mX, ny, (seekWidth * 0.5f), redStrokePaint);
		}

		// canvas.rotate(angle, width * 0.5f, height * 0.5f);
		// canvas.drawBitmap(image, width * 0.5f - image.getWidth() * 0.5f,
		// height * 0.5f - image.getHeight() * 0.5f, null);
		//
		// canvas.restore();

	}

	// void drawTransLayer() {
	// transCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
	// transCanvas.save();
	// transCanvas.scale(scaleFactor, scaleFactor, width * 0.5f, height * 0.5f);
	// transCanvas.rotate(angle, width * 0.5f, height * 0.5f);
	// transCanvas.drawRect(0, 0, width, height, paint);
	// transCanvas.drawBitmap(image, width * 0.5f - image.getWidth() * 0.5f,
	// height * 0.5f - image.getHeight() * 0.5f, null);
	//
	// // if (isManual) {
	// // for (int i = 0; i < pointList.size(); i++) {
	// // transCanvas.drawPoint(pointList.get(i).x, pointList.get(i).y,
	// // transPaint);
	// // }
	//
	// // }
	// transCanvas.restore();
	//
	// }

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getPointerCount() == 1) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				// if (isEreaser) {

				if (!orientation) {
					path = new Path();
					path.reset();
					pathList.add(new DrawPath(context, path, (int) (seekWidth / scaleFactor)));

					float drawX = width * 0.5f - image.getWidth() * 0.5f * scaleFactor;
					float drawY = height * 0.5f - image.getHeight() * 0.5f * scaleFactor;
					float x = (event.getX() - drawX) / scaleFactor;

					float y = (event.getY() - offsetY - drawY) / scaleFactor;
					path.moveTo(x, y);

					px = x;
					py = y;

					// if (!orientation && pathList != null && pathList.size() >
					// 0)
					// {
					// // pathList.get(pathList.size() - 1).onDraw(transCanvas);
					// if (pathList != null && pathList.size() > 0) {
					// // for (int i = 0; i < pathList.size(); i++) {
					//
					// pathList.get(pathList.size() - 1).onDraw(transCanvas);
					// // }
					// }
					// invalidate();
					// }
					// }
					// }

					mX = (event.getX());
					mY = (event.getY());
					ny = (event.getY() - offsetY);
					return true;
				}

				return false;
			case MotionEvent.ACTION_MOVE:

				// if (!isManual) {
				float drawx = width * 0.5f - image.getWidth() * 0.5f * scaleFactor;
				float drawy = height * 0.5f - image.getHeight() * 0.5f * scaleFactor;
				float x1 = (event.getX() - drawx) / scaleFactor;
				float y1 = (event.getY() - offsetY - drawy) / scaleFactor;
				float dx = Math.abs(x1 - px);
				float dy = Math.abs(y1 - py);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					path.quadTo(px, py, (x1 + px) / 2, (y1 + py) / 2);
					px = x1;
					py = y1;
					mX = (event.getX());
					mY = (event.getY());
					ny = (event.getY() - offsetY);
				}
				// }
				mX = (event.getX());
				mY = (event.getY());
				ny = (event.getY() - offsetY);

				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				if (pathList != null && pathList.size() > 0) {
					// for (int i = 0; i < pathList.size(); i++) {

					pathList.get(pathList.size() - 1).onDraw(permTransCanvas);
					// invalidate();
					// }
				}

				if (isManual && mX > (width * 0.5f - image.getWidth() * 0.5f)) {
					Log.i("tag", "touch for image x " + (mX) + " y " + ny);
					int currentPixel = image.getPixel((int) (mX - imageLeft), (int) (ny - imageTop));
					removeXY((int) mX, (int) ny);
					for (int i = (int) (mX - imageLeft); i > 0; i--) {
						for (int j = (int) (ny - imageTop); j > 0; j--) {
							// Color.colorToHSV(color, hsv)

							if (currentPixel >= image.getPixel(i, j) && !(currentPixel > image.getPixel(i, j) + colorSmilarity)) {
								removeXY((int) (i + imageLeft), (int) (j + imageTop));
							}
						}
					}

					for (int i = (int) (mX - imageLeft + 1); i < (image.getWidth()); i++) {
						for (int j = (int) (ny - imageTop); j > 0; j--) {

							if (currentPixel >= image.getPixel(i, j) && !(currentPixel > image.getPixel(i, j) + colorSmilarity)) {
								removeXY((int) (i + imageLeft), (int) (j + imageTop));
							}
						}
					}

					for (int i = (int) (mX - imageLeft); i > 0; i--) {
						for (int j = (int) (ny - imageTop) + 1; j < image.getHeight(); j++) {

							if (currentPixel >= image.getPixel(i, j) && !(currentPixel > image.getPixel(i, j) + colorSmilarity)) {
								removeXY((int) (i + imageLeft), (int) (j + imageTop));
							}
						}
					}

					for (int i = (int) (mX - imageLeft + 1); i < (image.getWidth()); i++) {
						for (int j = (int) (ny - imageTop) + 1; j < image.getHeight(); j++) {

							if (currentPixel >= image.getPixel(i, j) && !(currentPixel > image.getPixel(i, j) + colorSmilarity)) {
								removeXY((int) (i + imageLeft), (int) (j + imageTop));
							}
						}
					}

				} else {

				}
				break;
			default:
				break;
			}
		} else {

			detector.onTouchEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				return true;
			case MotionEvent.ACTION_MOVE:

				return true;

			default:
				break;
			}
		}
		return false;
	}

	void removeXY(int x, int y) {

		this.removeX = x;
		this.removeY = y;
		pointList.add(new Point(removeX, removeY));
		invalidate();
	}

	// protected boolean isEreaser() {
	// return isEreaser;
	// }
	//
	// protected void setEreaser(boolean isEreaser) {
	// this.isEreaser = isEreaser;
	// }

	void imageEffects() {

		AmbilWarnaDialog dialog1 = new AmbilWarnaDialog(context, filterColor, new OnAmbilWarnaListener() {
			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
			}

			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {

				setColor(color);
			}
		});

		dialog1.show();

	}

	void setColor(int color) {
		filterColor = color;
		lightShadeEffect();
		invalidate();
	}

	void lightShadeEffect() {

		AsyncTask<Bitmap, Void, Bitmap> task = new AsyncTask<Bitmap, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Bitmap... params) {
				Bitmap colorBitmap = params[0].copy(Bitmap.Config.ARGB_8888, true);
				Paint p = new Paint(filterColor);
				ColorFilter filter = new LightingColorFilter(filterColor, 1);
				p.setColorFilter(filter);
				Canvas canvas = new Canvas(colorBitmap);
				canvas.drawBitmap(colorBitmap, 0, 0, p);
				return colorBitmap;

			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub

				// waitingDialog = ProgressDialog.show(getContext(), "wait",
				// "Processing");
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				// waitingDialog.dismiss();
				// imageset.ImageBitmap(result);
				image = result;
				result = null;
				invalidate();

			}
		};
		task.execute(originalBitmap);

	}

	public void setSplash() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context).setPositiveButton("yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				image = convertColorIntoBlackAndWhiteImage(originalBitmap);
				invalidate();
				dialog.dismiss();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).setMessage("Do you want to convert it into black & white image ?").setTitle("Black&White");
		dialog.show();

	}

	public Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);

		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);

		Bitmap blackAndWhiteBitmap = orginalBitmap.copy(Bitmap.Config.ARGB_8888, true);

		Paint paint = new Paint();
		paint.setColorFilter(colorMatrixFilter);

		Canvas canvas = new Canvas(blackAndWhiteBitmap);
		canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

		return blackAndWhiteBitmap;
	}

	public void rotateImageClockWise() {
		// TODO Auto-generated method stub
		// float angle = getAngle();
		// angle += 90;
		// if (angle >= 360) {
		// angle = 0;
		// }
		// setAngle(angle);

		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap bitmap;
		bitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
		image.recycle();
		System.gc();
		this.image = bitmap;

		bitmap = Bitmap.createBitmap(tempTransImage, 0, 0, tempTransImage.getWidth(), tempTransImage.getHeight(), matrix, true);
		tempTransImage.recycle();
		System.gc();
		tempTransImage = bitmap;

		bitmap = Bitmap.createBitmap(permTransImage, 0, 0, permTransImage.getWidth(), permTransImage.getHeight(), matrix, true);
		permTransImage.recycle();
		System.gc();
		permTransImage = bitmap;
		tempTransCanvas = new Canvas(tempTransImage);
		permTransCanvas = new Canvas(permTransImage);

		((FaceSwapScreen) context).editorView.image.touchedFace.setBoundry(image);
		((FaceSwapScreen) context).editorView.image.touchedFace.setFaceBitmap(image);
		invalidate();
		// invalidate();

	}

	void setMirrorX() {
		Matrix m = new Matrix();
		m.preScale(1, -1);

		Bitmap bitmap;
		bitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);
		image.recycle();
		System.gc();
		this.image = bitmap;

		bitmap = Bitmap.createBitmap(tempTransImage, 0, 0, tempTransImage.getWidth(), tempTransImage.getHeight(), m, true);
		tempTransImage.recycle();
		System.gc();
		tempTransImage = bitmap;

		bitmap = Bitmap.createBitmap(permTransImage, 0, 0, permTransImage.getWidth(), permTransImage.getHeight(), m, true);
		permTransImage.recycle();
		System.gc();
		permTransImage = bitmap;

		tempTransCanvas = new Canvas(tempTransImage);
		permTransCanvas = new Canvas(permTransImage);
		((FaceSwapScreen) context).editorView.image.touchedFace.setBoundry(image);
		((FaceSwapScreen) context).editorView.image.touchedFace.setFaceBitmap(image);
		invalidate();

	}

	void setMirrorY() {
		Matrix m = new Matrix();
		m.preScale(-1, 1);

		Bitmap bitmap;
		bitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);
		image.recycle();
		System.gc();
		this.image = bitmap;

		bitmap = Bitmap.createBitmap(tempTransImage, 0, 0, tempTransImage.getWidth(), tempTransImage.getHeight(), m, true);
		tempTransImage.recycle();
		System.gc();
		tempTransImage = bitmap;

		bitmap = Bitmap.createBitmap(permTransImage, 0, 0, permTransImage.getWidth(), permTransImage.getHeight(), m, true);
		permTransImage.recycle();
		System.gc();
		permTransImage = bitmap;

		tempTransCanvas = new Canvas(tempTransImage);
		permTransCanvas = new Canvas(permTransImage);

		((FaceSwapScreen) context).editorView.image.touchedFace.setBoundry(image);
		((FaceSwapScreen) context).editorView.image.touchedFace.setFaceBitmap(image);
		invalidate();

	}

	public void setImageBitmap(Bitmap image) {
		this.image = image;
		// this.transLayer = transLayer;
		// imageWidth = image.getWidth();
		// imageHeight = image.getHeight();

	}

	public void rotateImageAntiClockWise() {
		// TODO Auto-generated method stub
		// float angle = getAngle();
		// angle -= 90;
		// if (angle <= -360) {
		// angle = 0;
		// }
		// setAngle(angle);
		Matrix matrix = new Matrix();
		matrix.postRotate(270);
		Bitmap bitmap;
		bitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
		image.recycle();
		System.gc();
		this.image = bitmap;

		bitmap = Bitmap.createBitmap(tempTransImage, 0, 0, tempTransImage.getWidth(), tempTransImage.getHeight(), matrix, true);
		tempTransImage.recycle();
		System.gc();
		tempTransImage = bitmap;

		bitmap = Bitmap.createBitmap(permTransImage, 0, 0, permTransImage.getWidth(), permTransImage.getHeight(), matrix, true);
		permTransImage.recycle();
		System.gc();
		permTransImage = bitmap;
		tempTransCanvas = new Canvas(tempTransImage);
		permTransCanvas = new Canvas(permTransImage);
		((FaceSwapScreen) context).editorView.image.touchedFace.setBoundry(image);
		((FaceSwapScreen) context).editorView.image.touchedFace.setFaceBitmap(image);
		invalidate();
	}

	protected float getAngle() {
		return angle;
	}

	protected void setAngle(float angle) {
		this.angle = angle;
	}

	public void removeOrientation() {
		this.angle = 0;

		invalidate();
	}

	public void saveSticker() {
		/*
		 * int left = (int) (width * 0.5f - image.getWidth() * 0.5f *
		 * scaleFactor); // if (left < 0) { // left = 0; // } int top = (int)
		 * (height * 0.5f - image.getHeight() * 0.5f * scaleFactor); // if (top
		 * < 0) { // top = 0; // } int iwidth = (int) (image.getWidth() *
		 * scaleFactor); // if ((iwidth) > this.width) { // iwidth = width; // }
		 * int iheight = (int) (image.getHeight() * scaleFactor); // if (iheight
		 * > this.height) { // iheight = height; // }
		 */
		Bitmap finalImage = Bitmap.createBitmap(tempTransImage.getWidth(), tempTransImage.getHeight(), Config.ARGB_8888);
		// scaleFactor = 1.0f;
		Canvas canvas = new Canvas(finalImage);
		// lkkjkj
		// canvas.drawBitmap(tempTransImage, 0, 0, null);
		// tempTransCanvas.drawBitmap(image, 0, 0, paint);
		canvas.drawBitmap(tempTransImage, 0, 0, null);
		image = finalImage;
		beforeOrientation = finalImage;
		switch (screenType) {
		case 1:
			((FaceCropScreen) context).refreshCroppedImages(image, tag);
			break;
		case 2:
			((FaceSwapScreen) context).refreshCroppedImages(image, tag);
			break;
		case 3:
			((FaceSwapScreen) context).editorView.refreshCroppedImages(image);
			break;
		default:
			break;
		}

	}

	public void saveAfterOrientation(boolean save) {
		int left, top, iheight, iwidth;

		if (save) {
			// if (angle == 90 || angle == 270) {
			// left = (int) (width * 0.5f - image.getHeight() * 0.5f *
			// scaleFactor);
			// // if (left < 0) {
			// // left = 0;
			// // }
			// top = (int) (height * 0.5f - image.getWidth() * 0.5f *
			// scaleFactor);
			// // if (top < 0) {
			// // top = 0;
			// // }
			// iwidth = (int) (image.getWidth() * scaleFactor);
			// // if ((iwidth+left) > this.width) {
			// // iwidth = width-left;
			// // }
			// iheight = (int) (image.getHeight() * scaleFactor);
			// // if (iheight+top > this.height) {
			// // Log.i("im", " transl h " + transLayer.getWidth() +
			// // " trranslayer heig " + transLayer.getHeight() + " left " +
			// // left + " top " + top + " iwidth " + iheight + " idheight " +
			// // iwidth);
			// // left = (left < 0) ? 0 : left;
			// // if (left == 0)
			// // iwidth = ((left + iwidth) > (image.getWidth()) ? (width) :
			// // iwidth);
			// // else
			// // iwidth = ((left + iwidth) > (image.getWidth()) ? (width) :
			// // width - left);
			// this.image = Bitmap.createBitmap(getBitmapFromView(this), left,
			// top, iheight, iwidth);
			// scaleFactor = 1.0f;

			// } else {
			// left = (int) (width * 0.5f - image.getWidth() * 0.5f *
			// scaleFactor);
			// // if (left < 0) {
			// // left = 0;
			// // }
			// top = (int) (height * 0.5f - image.getHeight() * 0.5f *
			// scaleFactor);
			// // if (top < 0) {
			// // top = 0;
			// // }
			// iwidth = (int) (image.getWidth() * scaleFactor);
			// // if ((iwidth+left) > this.width) {
			// // iwidth = width-left;
			// // }
			// iheight = (int) (image.getHeight() * scaleFactor);
			// // if (iheight+top > this.height) {
			// // iheight = height-top;
			// // }
			// this.image = Bitmap.createBitmap(getBitmapFromView(this), left,
			// top, iwidth, iheight);
			// scaleFactor = 1.0f;
			// }
			pathList.clear();
			this.beforeOrientation = this.image;
		} else {
			this.image = beforeOrientation;
		}

		// image = tempTransImage;
		image = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Config.ARGB_8888);
		tempTransCanvas = new Canvas(image);
		angle = 0;
		orientation = false;

	}

	protected int getOffsetY() {
		return offsetY;
	}

	protected void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
		// showOffset=true;
		// MotionEvent motionEvent =
		// MotionEvent.obtain(System.currentTimeMillis(),
		// System.currentTimeMillis(), MotionEvent.ACTION_DOWN, mX, mY, 1);
		// dispatchTouchEvent(new To)
		ny = mY - offsetY;
		invalidate();
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (!(scaleFactor * detector.getScaleFactor() * image.getWidth() > width) && !(scaleFactor * detector.getScaleFactor() * image.getHeight() > width)) {
				scaleFactor *= detector.getScaleFactor();
				// Log.i("scale", " detector  " + detector.getScaleFactor() +
				// " sf "
				// + scaleFactor);
				// if (!isFaceTouch) {
				//
				// // mainImage.setScaleFactor(scaleFactor);
				//
				// // invalidate();
				// } else {
				// // Log.i("see", " scatle true "+scaleFactor);
				// touchedFace.setFaceScale(scaleFactor);
				// drawCroppedImage();
				// }
				invalidate();
			}
			return true;
		}
	}

	public void undoLastPath() {
		if (pathList.size() > 0) {
			undoList.add(pathList.get(pathList.size() - 1));
			pathList.remove(pathList.get(pathList.size() - 1));
			permTransCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			tempTransCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			for (int i = 0; i < pathList.size(); i++) {
				pathList.get(i).onDraw(permTransCanvas);
			}
			invalidate();
		} else {
			Toast.makeText(context, "can't undo", Toast.LENGTH_LONG).show();
		}
	}

	public void redoLastPath() {
		if (undoList.size() > 0) {
			pathList.add(undoList.get(undoList.size() - 1));
			undoList.remove(undoList.get(undoList.size() - 1));
			permTransCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			tempTransCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			for (int i = 0; i < pathList.size(); i++) {
				pathList.get(i).onDraw(permTransCanvas);
			}
			invalidate();
		} else {
			Toast.makeText(context, "can't redo", Toast.LENGTH_LONG).show();
		}
		invalidate();
	}

	protected boolean isManual() {
		return isManual;
	}

	protected void setManual(boolean isManual) {
		this.isManual = isManual;
	}

	public void backupBeforeOrientation() {
		saveSticker();
		orientation = true;
		beforeOrientation = image;
		tempTransCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		permTransCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		pathList.clear();
		undoList.clear();
		invalidate();
		// image = Bitmap.createBitmap(transLayer, (int) (width * 0.5f -
		// image.getWidth() * 0.5f * scaleFactor), (int) (height * 0.5f -
		// image.getHeight() * 0.5f * scaleFactor), (int) (image.getWidth() *
		// scaleFactor), (int) (image.getHeight() * scaleFactor));
		// scaleFactor=1.0f;
	}

	public Bitmap getBitmapFromView(View view) {
		// Define a bitmap with the same size as the view

		// isCapture = true;
		invalidate();
		Bitmap returnBit = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		// Bind a canvas to it
		Canvas canvas = new Canvas(returnBit);
		// Get the view's background
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			// has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		else
			// does not have background drawable, then draw white background on
			// the canvas
			canvas.drawColor(Color.WHITE);
		// draw the view on the canvas
		view.draw(canvas);
		// drawingPointX = markerImage.getEye1x();

		// return the bitmap
		// isCapture = false;
		return returnBit;
	}

}
