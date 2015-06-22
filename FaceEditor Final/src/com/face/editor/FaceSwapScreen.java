package com.face.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class FaceSwapScreen extends Activity {

	int width, height, selectedPicNumber;
	FaceSwapView editorView;
	static final int PICK_FROM_GALLERY = 1;
	static final int PICK_FROM_CAMERA = 2;
	static final int START_RESULT_SCREEN = 3;
	static final String FINISH_SWAP_SCREEN = "finish_swap_screen";
	Uri mImageUri;
	List<Bitmap> croopedArray;
	LinearLayout croppedImages;
	ImageView imgview;
	RelativeLayout editView;

	String[] options = { "Paste", "Edit" };
	StickerView currentTouch;
	FrameLayout stickerFrame;
	RelativeLayout eraserRelative, offsetRelative;
	ImageButton undo, redo, manual, eraser;
	LinearLayout linearOrientation, optionLinear;
	RelativeLayout applyCancel;
	SeekBar ereaserBar, cursorOffset, colorSimilarity;
	float brushWidth;
	Button editButton;
	Animation slideDownAnim, slideUpAnim;
	boolean finishActivity;
	ImageButton editImageButton;

	// List<Bitmap> selectedFaces;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.faceswap_layout);

		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();

		slideDownAnim = AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.top_in);
		slideUpAnim = AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.top_up);

		stickerFrame = (FrameLayout) findViewById(R.id.stic_editor_frame);
		editButton = (Button) findViewById(R.id.edit_option);
		editButton.setVisibility(View.GONE);
		editButton.setOnClickListener(onClickListener);
		celebImageSelect();
		croopedArray = new ArrayList<Bitmap>();
		// selectedFaces = new ArrayList<Bitmap>();
		if (FaceCropScreen.croopedArray != null) {
			for (int i = 0; i < FaceCropScreen.croopedArray.size(); i++) {
				croopedArray.add(FaceCropScreen.croopedArray.get(i));

			}
		}
		croppedImages = (LinearLayout) findViewById(R.id.cropped_images);

		Log.i("size", "screen width " + width + " heig " + height);
		editorView = new FaceSwapView(FaceSwapScreen.this, width, width);

		// editorView.setImage(mainImage);

		editView = (RelativeLayout) findViewById(R.id.edit_view);

		editView.addView(editorView);
		imgview = null;

		for (int i = 0; i < croopedArray.size(); i++) {
			imgview = new ImageView(FaceSwapScreen.this);
			imgview.setLayoutParams(new LayoutParams((int) (height * 0.18f), (int) (height * 0.18f)));

			final Bitmap currentCroppedBitmap = croopedArray.get(i);
			imgview.setImageBitmap(currentCroppedBitmap);
			imgview.setTag(croppedImages.getChildCount());
			croppedImages.addView(imgview);

			imgview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					// TODO Auto-generated method stub
					AlertDialog builder = new AlertDialog.Builder(FaceSwapScreen.this).setTitle("Options").setItems(options, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case 1:
								showStickerScreen(v, imgview);
								dialog.dismiss();
								break;

							case 0:
								if (editorView.image != null) {
									editorView.setImageOnBg(croopedArray.get((Integer) (v.getTag())));
									// }

									setSelectedPicNumber(Integer.parseInt("" + v.getTag()));
								} else {
									Toast.makeText(FaceSwapScreen.this, "first Select background image", Toast.LENGTH_LONG).show();
								}
								dialog.dismiss();

								break;
							default:
								break;
							}
						}
					}).show();

				}
			});

		}

		ImageButton saveImage = (ImageButton) findViewById(R.id.done);
		saveImage.setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.flip_image)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.image_change)).setOnClickListener(clickListener);
		editImageButton = (ImageButton) findViewById(R.id.edit);
		(editImageButton).setOnClickListener(clickListener);
		editImageButton.setEnabled(false);
		editImageButton.setImageResource(R.drawable.dedit);

		final SlidingDrawer slider = (SlidingDrawer) findViewById(R.id.slider);
		// final LinearLayout linear1 = (LinearLayout)
		// findViewById(R.id.linear1);
		slider.getLayoutParams().width = height / 5;

		eraser = (ImageButton) findViewById(R.id.eraser);
		eraser.setOnClickListener(onClickListener);
		ImageButton light = (ImageButton) findViewById(R.id.light);
		light.setOnClickListener(onClickListener);
		ImageButton splash = (ImageButton) findViewById(R.id.splash);
		splash.setOnClickListener(onClickListener);
		ImageButton orientation = (ImageButton) findViewById(R.id.orientation);
		orientation.setOnClickListener(onClickListener);
		ImageButton rotateAnti = (ImageButton) findViewById(R.id.rotate_anticlock);
		rotateAnti.setOnClickListener(onClickListener);
		ImageButton rotateClock = (ImageButton) findViewById(R.id.rotate_clock);
		rotateClock.setOnClickListener(onClickListener);
		ImageButton flipX = (ImageButton) findViewById(R.id.flip_x);
		flipX.setOnClickListener(onClickListener);
		ImageButton flipY = (ImageButton) findViewById(R.id.flip_y);
		flipY.setOnClickListener(onClickListener);
		ImageButton saveSticker = (ImageButton) findViewById(R.id.save_sticker);
		saveSticker.setOnClickListener(onClickListener);
		ImageButton undo = (ImageButton) findViewById(R.id.undo);
		undo.setOnClickListener(onClickListener);
		ImageButton redo = (ImageButton) findViewById(R.id.redo);
		redo.setOnClickListener(onClickListener);
		// Button manual = (Button) findViewById(R.id.manual);
		// manual.setOnClickListener(onClickListener);
		Button apply = (Button) findViewById(R.id.apply);
		apply.setOnClickListener(onClickListener);
		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(onClickListener);

		optionLinear = (LinearLayout) findViewById(R.id.option_linear);
		applyCancel = (RelativeLayout) findViewById(R.id.apply_cancel);

		ereaserBar = (SeekBar) findViewById(R.id.ereaser_bar);
		ereaserBar.setOnSeekBarChangeListener(ereaserChangeListener);
		cursorOffset = (SeekBar) findViewById(R.id.cursor_offset);
		cursorOffset.setOnSeekBarChangeListener(offsetChangeListener);
		cursorOffset.setMax(300);
		ereaserBar.setThumbOffset(-1);
		cursorOffset.setThumbOffset(-1);

		colorSimilarity = (SeekBar) findViewById(R.id.color_similarity);
		colorSimilarity.setOnSeekBarChangeListener(colorChangeListener);
		colorSimilarity.setMax(2000);

		eraserRelative = (RelativeLayout) findViewById(R.id.eraser_bar_relative);
		offsetRelative = (RelativeLayout) findViewById(R.id.cursor_offset_relative);

		linearOrientation = (LinearLayout) findViewById(R.id.linearScrollLayout_orientation);

	}

	protected void celebImageSelect() {

		final Dialog dia = new Dialog(FaceSwapScreen.this);
		dia.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dia.setContentView(R.layout.celeb_imgselect_dialog);
		dia.setCanceledOnTouchOutside(false);

		ImageButton gallery = (ImageButton) dia.findViewById(R.id.gallery);
		ImageButton camera = (ImageButton) dia.findViewById(R.id.camera);

		// final CelebrityImageChooser ciChooser = new
		// CelebrityImageChooser(this);

		gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// celebImgView.setFirstCall(true);
				// // celebImgView.setFaceScale(1.0f);
				// CelebImageView.imagePro = null;
				// ciChooser.actionChooser(1);
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, PICK_FROM_GALLERY);

				dia.dismiss();

			}
		});
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// celebImgView.setFaceTouch(false);
				// celebImgView.setFirstCall(true);
				// celebImgView.setFaceScale(1.0f);
				// ciChooser.actionChooser(2);
				pickImageFromCamera();
				dia.dismiss();

			}
		});

		dia.show();

	}

	void pickImageFromCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File photo = null;
		try {

			photo = this.createTemporaryFile("picture", ".jpg");

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("test", "Can't create file to take picture!");
			return;
		}
		mImageUri = Uri.fromFile(photo);
		photo.delete();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	// protected List<Bitmap> getCroppedFaces() {
	// return croopedArray;
	// }

	private File createTemporaryFile(String part, String ext) throws Exception {
		File tempDir = Environment.getExternalStorageDirectory();
		tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		return File.createTempFile(part, ext, tempDir);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch (requestCode) {
		case PICK_FROM_CAMERA: // camera

			if (resultCode == RESULT_OK) {
				Log.d("faceswap", "imageReturnedIntent is " + imageReturnedIntent + " restcode " + requestCode + " resultcode " + requestCode);
				// if (mImageUri == null) {

				// mImageUri = imageReturnedIntent.getData();
				if (mImageUri == null) {
					return;
				}
				if (editorView.image != null) {
					editorView.image.image.recycle();

				}
				Bitmap image = getPhoto(mImageUri);
				// editView.requestLayout();
				editorView.setImage(image);
				Log.d("faceswap", "imagechooser camera image url" + mImageUri);

				// }s

			} else {
				Toast.makeText(FaceSwapScreen.this, "Image not Received,try again", Toast.LENGTH_LONG).show();
			}

			break;
		case PICK_FROM_GALLERY: // gallery

			if (resultCode == RESULT_OK && imageReturnedIntent != null) {
				Log.d("test", "image from gallery");
				Uri selectedImage = imageReturnedIntent.getData();
				if (selectedImage == null)
					return;

				Log.d("test", "image from gallery url " + selectedImage);
				if (editorView.image != null) {
					editorView.image.image.recycle();

				}
				Bitmap image = getPhoto(selectedImage);
				// editView.requestLayout();
				editorView.setImage(image);

			} else {
				Toast.makeText(FaceSwapScreen.this, "Image not Received,try again", Toast.LENGTH_LONG).show();
			}
			break;
		case START_RESULT_SCREEN:
			Log.i("result ", "vlaue" + imageReturnedIntent.getBooleanExtra(FINISH_SWAP_SCREEN, true));
			if (resultCode == RESULT_OK && imageReturnedIntent != null) {
				if (imageReturnedIntent.getBooleanExtra(FINISH_SWAP_SCREEN, true)) {
					finishActivity = true;
					finish();
				} else {
					finishActivity = false;
				}
			}
			break;

		}
		System.gc();
	}

	protected int getSelectedPicNumber() {
		return selectedPicNumber;
	}

	protected void setSelectedPicNumber(int selectedPicNumber) {
		this.selectedPicNumber = selectedPicNumber;
	}

	Bitmap getPhoto(Uri selectedImage) {

		Bitmap phoBitmap = null;

		InputStream inputStream = null;
		try {
			inputStream = getContentResolver().openInputStream(selectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
		int imageWidth = bitmapOptions.outWidth;
		int imageHeight = bitmapOptions.outHeight;

		int angle = (int) rotationForImage(this, selectedImage);

		Log.d("test", "File image without load " + imageWidth + " , " + imageHeight);
		InputStream is = null;
		try {
			is = getContentResolver().openInputStream(selectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		float scale = 1;
		// if (angle == 0) {
		//
		// if (imageWidth < imageHeight) {
		// if (imageHeight > width) {
		// scale = width / (imageHeight * 1.0f);
		// }
		//
		// } else {
		// if (imageWidth > width) {
		// scale = width / (imageWidth * 1.0f);
		// }
		//
		// }
		// } else {
		// if (imageWidth > imageHeight) {
		// if (imageHeight > width) {
		// scale = width / (imageHeight * 1.0f);
		// }
		// } else {
		// if (imageWidth > width) {
		// scale = width / (imageWidth * 1.0f);
		// }
		// }
		// }

		if (imageWidth < imageHeight) {
			if (imageHeight > width) {

				scale = width / (imageHeight * 1.0f);
				Log.d("test", " if width " + width + " imageHeight " + imageHeight + " scale " + scale);
			}

		} else {
			if (imageWidth > width) {
				scale = width / (imageWidth * 1.0f);
				Log.d("test", " else width " + width + " imageWidth " + imageWidth + " scale " + scale);
			}

		}

		Log.d("test", "scale factor " + scale);

		phoBitmap = LoadImage.decodeSampledBitmapFromResource(this, selectedImage, (int) (imageWidth * scale), (int) (imageHeight * scale));
		if (angle != 0) {
			phoBitmap = rotate(phoBitmap, angle);
		}

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		Log.d("test", "File image after " + phoBitmap.getWidth() + " , " + phoBitmap.getHeight());
		return phoBitmap;
	}

	public Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();

			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				ex.printStackTrace();
			}
		}
		return b;
	}

	public float rotationForImage(Context context, Uri uri) {
		if (uri.getScheme().equals("content")) {
			String[] projection = { Images.ImageColumns.ORIENTATION };
			Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
			if (c.moveToFirst()) {
				return c.getInt(0);
			}
		} else if (uri.getScheme().equals("file")) {
			try {
				ExifInterface exif = new ExifInterface(uri.getPath());
				int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
				return rotation;
			} catch (IOException e) {
				Log.e("Image", "Error checking exif", e);
			}
		}
		return 0f;
	}

	private float exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.done:
				if (editorView.image != null && editorView.getFinalImage() != null) {
					// editorView.setFaceTouch(false);
					FirstActivity.fetchedImage = editorView.getFinalImage();
					Intent intent = new Intent(FaceSwapScreen.this, ResultScreen.class);

					startActivityForResult(intent, START_RESULT_SCREEN);
					// saveFinalImage(finalPic);

				} else {
					Toast.makeText(FaceSwapScreen.this, "no Background Image  found", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.image_change:
				celebImageSelect();
				break;
			case R.id.flip_image:
				editorView.flipFaceImage();
				break;
			case R.id.edit:

				showEditScreen();
				break;

			default:
				break;
			}
		}
	};
	void showEditScreen() {
		stickerFrame.setVisibility(View.VISIBLE);
		stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.slide_in_up));
		RelativeLayout seditor_view = (RelativeLayout) findViewById(R.id.stic_editor_view);
		StickerView stickerview = new StickerView(FaceSwapScreen.this, editorView.faceList.get(editorView.touchedNumber).getFaceBitmap(),editorView.touchedNumber, seditor_view.getWidth(), seditor_view.getHeight(), 3);
		currentTouch = stickerview;
		seditor_view.addView(stickerview);
	}
	void enableEdit(boolean enable) {
		if (enable) {
			editImageButton.setEnabled(true);
			editImageButton.setImageResource(R.drawable.edit);
		} else {
			editImageButton.setEnabled(false);
			editImageButton.setImageResource(R.drawable.dedit);
		}
	}

	public void saveFinalImage(final Bitmap bmp) {
		final Dialog customDia = new Dialog(FaceSwapScreen.this);
		customDia.requestWindowFeature(Window.FEATURE_NO_TITLE);

		customDia.setContentView(R.layout.custom_dialog);

		Button yes = (Button) customDia.findViewById(R.id.yes);
		Button no = (Button) customDia.findViewById(R.id.no);
		TextView tv = (TextView) customDia.findViewById(R.id.alertText);
		tv.setText("save this picture ");
		ImageView CusDiaImageV = (ImageView) customDia.findViewById(R.id.customImage);
		CusDiaImageV.setImageBitmap(bmp);

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Intent intent = new Intent(FaceSwapScreen.this,
				// ResultScreen.class);
				// startActivity(intent);
				customDia.dismiss();
				finish();

			}
		});

		no.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				customDia.dismiss();
			}

		});

		customDia.show();

		// TODO Auto-generated catch block

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			// case R.id.edit_option:
			//
			// int position = 0;
			// for (int i = 0; i < editorView.faceList.size(); i++) {
			// if (editorView.touchFace == editorView.faceList.get(i)) {
			// position = i;
			// break;
			// }
			// }
			// Log.i("onclick", "positjion " + position);
			// showStickerScreen(position);
			// break;
			case R.id.eraser:
				// currentTouch.setEreaser(((ToggleButton) v).isChecked());
				eraserRelative.setVisibility(View.VISIBLE);
				offsetRelative.setVisibility(View.VISIBLE);
				break;
			case R.id.light:
				currentTouch.imageEffects();
				eraserRelative.setVisibility(View.GONE);
				offsetRelative.setVisibility(View.GONE);
				break;
			case R.id.splash:
				eraserRelative.setVisibility(View.GONE);
				offsetRelative.setVisibility(View.GONE);
				currentTouch.setSplash();
				break;
			case R.id.orientation:
				eraserRelative.setVisibility(View.GONE);
				offsetRelative.setVisibility(View.GONE);
				currentTouch.backupBeforeOrientation();
				linearOrientation.setVisibility(View.VISIBLE);
				linearOrientation.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.push_left_in));
				// optionLinear.startAnimation(AnimationUtils.loadAnimation(EditorScreen.this,
				// R.anim.top_in));
				// optionLinear.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				optionLinear.setVisibility(View.GONE);
				// }
				// }, 500);

				applyCancel.setVisibility(View.VISIBLE);
				applyCancel.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.push_left_in));

				break;

			case R.id.apply:
				applyChanges(true);

				break;
			case R.id.cancel:
				applyChanges(false);

				break;

			case R.id.rotate_anticlock:
				Log.i("tag", "anticliock");
				currentTouch.rotateImageAntiClockWise();
				break;
			case R.id.rotate_clock:
				Log.i("tag", "cliock");
				currentTouch.rotateImageClockWise();
				break;
			case R.id.flip_x:
				if (currentTouch.angle == 0 || currentTouch.angle == 180)
					currentTouch.setMirrorY();
				else {
					currentTouch.setMirrorX();
				}

				break;
			case R.id.flip_y:
				if (currentTouch.angle == 0 || currentTouch.angle == 180)
					currentTouch.setMirrorX();
				else
					currentTouch.setMirrorY();

				break;
			case R.id.save_sticker:
				saveStickerSure();
				eraserRelative.setVisibility(View.GONE);
				offsetRelative.setVisibility(View.GONE);

				break;
			case R.id.undo:
				currentTouch.undoLastPath();
				eraserRelative.setVisibility(View.GONE);
				offsetRelative.setVisibility(View.GONE);
				break;
			case R.id.redo:
				currentTouch.redoLastPath();
				eraserRelative.setVisibility(View.GONE);
				offsetRelative.setVisibility(View.GONE);

				break;

			default:
				break;
			}
		}
	};

	OnSeekBarChangeListener ereaserChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			// seekBar.setVisibility(View.GONE);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			if (currentTouch != null)
				currentTouch.setSeekWidth(progress);

		}
	};

	OnSeekBarChangeListener colorChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			// seekBar.setVisibility(View.GONE);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			if (currentTouch != null)
				currentTouch.setColorSmilarity(progress);

		}
	};

	OnSeekBarChangeListener seekChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			// setBrushVisibility(false);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			brushWidth = progress;

		}
	};

	OnSeekBarChangeListener offsetChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			if (currentTouch != null)
				currentTouch.setOffsetY(progress);

		}
	};

	public void saveStickerSure() {

		AlertDialog buider = new AlertDialog.Builder(FaceSwapScreen.this).setTitle("Save").setMessage("Do You want to save changes ??").setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				currentTouch.saveSticker();
				stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.slide_out_down));
				stickerFrame.postDelayed(new Runnable() {

					@Override
					public void run() {
						stickerFrame.setVisibility(View.GONE);
					}
				}, 500);
				dialog.dismiss();
			}
		}).setNegativeButton("cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		}).show();
	}

	void applyChanges(boolean apply) {

		if (apply) {
			currentTouch.saveAfterOrientation(true);
			currentTouch.orientation = false;
			showVisibility();
		} else {
			currentTouch.saveAfterOrientation(false);
			currentTouch.removeOrientation();
			currentTouch.orientation = false;
			showVisibility();
		}

	}

	void showVisibility() {

		linearOrientation.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.push_left_out));
		applyCancel.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.push_left_out));
		linearOrientation.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				linearOrientation.setVisibility(View.GONE);
				applyCancel.setVisibility(View.GONE);
			}
		}, 500);

		optionLinear.setVisibility(View.VISIBLE);
		// optionLinear.startAnimation(AnimationUtils.loadAnimation(EditorScreen.this,
		// R.anim.top_up));

	}

	// public void deleteCroppedItem(ImageView imageView) {
	// for (int i = 0; i < croppedImages.getChildCount(); i++) {
	// if ((Integer.parseInt((String) (croppedImages.getChildAt(i).getTag()))) >
	// (Integer.parseInt((String) (imageView.getTag())))) {
	// croppedImages.getChildAt(i).setTag(((Integer.parseInt((String)
	// (croppedImages.getChildAt(i).getTag()))) - 1) + "");
	// }
	// }
	// croppedImages.removeViewAt((Integer.parseInt((String)
	// (imageView.getTag()))));
	// croopedArray.remove((Integer.parseInt((String) (imageView.getTag()))));
	// croppedImages.invalidate();
	// }

	public void showStickerScreen(View v, ImageView imgview) {

		stickerFrame.setVisibility(View.VISIBLE);
		stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.slide_in_up));
		RelativeLayout seditor_view = (RelativeLayout) findViewById(R.id.stic_editor_view);
		StickerView stickerview = new StickerView(FaceSwapScreen.this, croopedArray.get((Integer) (v.getTag())), (Integer) imgview.getTag(), seditor_view.getWidth(), seditor_view.getHeight(), Face.FACE_SWAP_VIEW);
		currentTouch = stickerview;
		seditor_view.addView(stickerview);
	}

	public void refreshCroppedImages(Bitmap image, int tag) {
		// Toast.makeText(EditorScreen.this, "tag is " + tag,
		// Toast.LENGTH_LONG).show();
		Log.i("tag", "tag of current image is " + tag);
		croopedArray.set(tag, image);
		((ImageView) croppedImages.getChildAt(tag)).setImageBitmap(image);
		for (int i = 0; i < croppedImages.getChildCount(); i++) {
			((ImageView) croppedImages.getChildAt(i)).setImageBitmap(croopedArray.get(i));
			((ImageView) croppedImages.getChildAt(i)).invalidate();
		}
		croppedImages.invalidate();
	}

	public void onBackPressed() {
		if (stickerFrame.getVisibility() == View.VISIBLE) {

			// currentTouch.orientation=false;
			if (linearOrientation.getVisibility() != View.VISIBLE) {

				new AlertDialog.Builder(FaceSwapScreen.this).setTitle("Save Image").setMessage("Do you want to apply changes ? ").setPositiveButton("Apply", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						currentTouch.saveSticker();
						stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.slide_out_down));
						stickerFrame.postDelayed(new Runnable() {

							@Override
							public void run() {
								stickerFrame.setVisibility(View.GONE);
							}
						}, 500);
						dialog.dismiss();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceSwapScreen.this, R.anim.slide_out_down));
						stickerFrame.postDelayed(new Runnable() {

							@Override
							public void run() {
								stickerFrame.setVisibility(View.GONE);
							}
						}, 500);
						dialog.dismiss();
					}
				}).show();

			} else {
				new AlertDialog.Builder(FaceSwapScreen.this).setTitle("Save Image").setMessage("Do you want to apply changes ? ").setPositiveButton("Apply", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						applyChanges(true);
						
						dialog.dismiss();
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						applyChanges(false);
						dialog.dismiss();
					}
				}).show();
			}

		} else
			super.onBackPressed();

	}

	@Override
	protected void onDestroy() {

		if (editorView.image != null && editorView.image.image != null) {
			editorView.image.image.recycle();
			editorView.image.originalImage.recycle();
		}

		for (int i = 0; i < 10; i++) {
			System.gc();
		}

		super.onDestroy();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent data = new Intent();
		data.putExtra(FaceCropScreen.FINISH_CROP_SCREEN, finishActivity);
		setResult(RESULT_OK, data);
		super.finish();
	}
}
