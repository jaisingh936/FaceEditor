package com.face.editor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class FaceCropScreen extends Activity {

	FaceCropView facecropView;
	LinearLayout croppedImages;
	static List<Bitmap> croopedArray;
	int height;
	String[] options = { "Edit", "Delete" };
	StickerView currentTouch;
	FrameLayout stickerFrame;

	// Button picColor, fillColor, doneForPicColor, setBrushWidth, moveButton,
	// save;
	RelativeLayout eraserRelative, offsetRelative;
	ImageButton undo, redo, manual, eraser;
	LinearLayout linearOrientation, optionLinear;
	RelativeLayout applyCancel;
	SeekBar ereaserBar, cursorOffset, colorSimilarity;
	float brushWidth;
	SlidingDrawer drawer;
	ScrollView scrollview;
	ImageView loading;
	public static final String FINISH_CROP_SCREEN = "finish_crop_screen";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facecrop_layout);

		stickerFrame = (FrameLayout) findViewById(R.id.stic_editor_frame);

		loading = (ImageView) findViewById(R.id.loading);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		height = metrics.heightPixels;

		Log.i("can", "canvas ondraw  scrn ht " + height);

		RelativeLayout editorView = (RelativeLayout) findViewById(R.id.editor_view);
		croppedImages = (LinearLayout) findViewById(R.id.cropped_images);
		croopedArray = new ArrayList<Bitmap>();
		Bitmap bit = FirstActivity.fetchedImage.copy(Config.ARGB_8888, true);
		Image image = new Image(bit, width * 0.5f, height * 0.5f, FaceCropScreen.this);
		facecropView = new FaceCropView(FaceCropScreen.this, image, width, height);
		image.setFaceList(facecropView.faceList);
		image.setCurrentAction(facecropView.currentAction);
		// image.setTouchedFace(facecropView.touchFace);
		image.setBoundry(facecropView.boundry);
		// image.initialize();
		editorView.addView(facecropView);

		ImageButton next = (ImageButton) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				facecropView.setTouchedFaceToNext();
			}
		});
		ImageButton crop = (ImageButton) findViewById(R.id.crop);
		crop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					facecropView.getBitmapFromView(loading);
					scrollview.fullScroll(ScrollView.FOCUS_DOWN);
					if (!drawer.isOpened())
						drawer.animateOpen();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(FaceCropScreen.this, "Put marker on image", Toast.LENGTH_LONG).show();
				}

			}
		});
		((ImageButton) findViewById(R.id.done)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (croopedArray.size() < 1) {
					AlertDialog.Builder dia = new AlertDialog.Builder(FaceCropScreen.this);
					dia.setMessage("First Crop Face,for swapping");
					dia.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					dia.show();
				} else {
					startActivityForResult(new Intent(FaceCropScreen.this, FaceSwapScreen.class), 0);
				}
			}
		});
		drawer = (SlidingDrawer) findViewById(R.id.slider);
		scrollview = (ScrollView) findViewById(R.id.content);
		// final LinearLayout linear1 = (LinearLayout)
		// findViewById(R.id.linear1);
		drawer.getLayoutParams().width = height / 5;
		// final Button hideButton = (Button) findViewById(R.id.hide);
		// hideButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (spaceForStickers.getVisibility() == View.VISIBLE) {
		//
		// linear1.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this,
		// R.anim.slide_out_down));
		// //
		// hideButton.startAnimation(AnimationUtils.loadAnimation(EditorScreen.this,
		// // R.anim.slide_out_down));
		// spaceForStickers.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// spaceForStickers.setVisibility(View.GONE);
		// }
		// }, 500);
		// } else {
		// spaceForStickers.setVisibility(View.VISIBLE);
		// //
		// hideButton.startAnimation(AnimationUtils.loadAnimation(EditorScreen.this,
		// // R.anim.slide_in_up));
		// linear1.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this,
		// R.anim.slide_in_up));
		// }
		// }
		// });

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

		// final LinearLayout spaceForStickers = (LinearLayout)
		// findViewById(R.id.space_for_sticker);
		// final LinearLayout linear1 = (LinearLayout)
		// findViewById(R.id.linear1);
		// spaceForStickers.getLayoutParams().height = height / 5;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data.getBooleanExtra(FINISH_CROP_SCREEN, true)) {
				finish();
			} else {
				onResume();
			}
		}

	};

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
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
				linearOrientation.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.push_left_in));
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
				applyCancel.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.push_left_in));

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

	void setBitmapInCroppedImages(Bitmap bitmap) {
		Log.i("tag", "bitmap set called");
		final ImageView imgview = new ImageView(FaceCropScreen.this);

		imgview.setLayoutParams(new LayoutParams((int) (height * 0.18f), (int) (height * 0.18f)));

		imgview.setTag((croppedImages.getChildCount()) + "");
		imgview.setImageBitmap(bitmap);

		imgview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				AlertDialog builder = new AlertDialog.Builder(FaceCropScreen.this).setTitle("Options").setItems(options, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 1:
							deleteCroppedItem(imgview);
							dialog.dismiss();
							break;

						case 0:
							showStickerScreen(v, imgview);
							dialog.dismiss();
							break;
						default:
							break;
						}
					}
				}).show();

			}
		});

		croopedArray.add(bitmap);
		croppedImages.addView(imgview);
	}

	public void deleteCroppedItem(ImageView imageView) {
		for (int i = 0; i < croppedImages.getChildCount(); i++) {
			if ((Integer.parseInt((String) (croppedImages.getChildAt(i).getTag()))) > (Integer.parseInt((String) (imageView.getTag())))) {
				croppedImages.getChildAt(i).setTag(((Integer.parseInt((String) (croppedImages.getChildAt(i).getTag()))) - 1) + "");
			}
		}
		croppedImages.removeViewAt((Integer.parseInt((String) (imageView.getTag()))));
		croopedArray.remove((Integer.parseInt((String) (imageView.getTag()))));
		croppedImages.invalidate();
	}

	public void showStickerScreen(View v, ImageView imgview) {
		stickerFrame.setVisibility(View.VISIBLE);
		stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.slide_in_up));
		RelativeLayout seditor_view = (RelativeLayout) findViewById(R.id.stic_editor_view);
		StickerView stickerview = new StickerView(FaceCropScreen.this, croopedArray.get(Integer.parseInt((String) (v.getTag()))), Integer.parseInt((String) imgview.getTag()), seditor_view.getWidth(), seditor_view.getHeight(), Face.FACE_CROP_VIEW);
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

	void showVisibility() {

		linearOrientation.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.push_left_out));
		applyCancel.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.push_left_out));
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

	public void saveStickerSure() {

		AlertDialog buider = new AlertDialog.Builder(FaceCropScreen.this).setTitle("Save").setMessage("Do You want to save changes ??").setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				currentTouch.saveSticker();
				stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.slide_out_down));
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

	public void onBackPressed() {
		if (stickerFrame.getVisibility() == View.VISIBLE) {

			// currentTouch.orientation=false;
			if (linearOrientation.getVisibility() != View.VISIBLE) {

				new AlertDialog.Builder(FaceCropScreen.this).setTitle("Save Image").setMessage("Do you want to apply changes ? ").setPositiveButton("Apply", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						currentTouch.saveSticker();
						stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.slide_out_down));
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
						stickerFrame.startAnimation(AnimationUtils.loadAnimation(FaceCropScreen.this, R.anim.slide_out_down));
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
				new AlertDialog.Builder(FaceCropScreen.this).setTitle("Save Image").setMessage("Do you want to apply changes ? ").setPositiveButton("Apply", new DialogInterface.OnClickListener() {

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

		for (int i = 0; i < croopedArray.size(); i++) {
			croopedArray.get(i).recycle();

		}
		croopedArray.clear();
		facecropView.image.image.recycle();
		facecropView.image.originalImage.recycle();
		facecropView.image.boundry.recycle();
		for (int i = 0; i < 10; i++) {
			System.gc();
		}
		super.onDestroy();
	}

}
