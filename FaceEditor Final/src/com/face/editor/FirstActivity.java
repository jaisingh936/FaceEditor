package com.face.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FirstActivity extends Activity {
	
	
	public static final int FETCH_FROM_GALLERY = 1;
	public static final int FETCH_FROM_CAMERA = 2;
	public static final int FETCH_FROM_SOCIAL = 3;
	int width, height;
	Uri imageUri;
	public static Bitmap fetchedImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstactivity_main);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
		((ImageButton) findViewById(R.id.camera)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.gallery)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.social)).setOnClickListener(clickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.camera:
				pickImageFromCamera();

				break;
			case R.id.gallery:
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, FETCH_FROM_GALLERY);

				break;
			case R.id.social:
				SocialImages.pickImage((Activity) (FirstActivity.this), FETCH_FROM_SOCIAL);

				break;

			default:
				break;
			}

		}
	};

	// protected void onPause() {
	// cropFrame.setVisibility(View.VISIBLE);
	// };
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
		imageUri = Uri.fromFile(photo);
		photo.delete();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

		startActivityForResult(intent, FETCH_FROM_CAMERA);
	}

	private File createTemporaryFile(String part, String ext) throws Exception {
		File tempDir = Environment.getExternalStorageDirectory();
		tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		return File.createTempFile(part, ext, tempDir);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FETCH_FROM_GALLERY:

			if (data != null) {
				fetchedImage = getPhoto(data.getData());
				// Log.d("test", "image from gallery w" +
				// MainApplication.fetchedImage.getWidth() + " h " +
				// MainApplication.fetchedImage.getHeight() + " width " + width
				// + " height " + height);

			}
			break;
		case FETCH_FROM_CAMERA: // camera
			if (resultCode == RESULT_OK) {

				// if (mImageUri == null) {
				// if (data != null) {
				// imageUri = data.getData();
				// }
				// if (imageUri == null) {
				// return;
				// }
				// }

				fetchedImage = getPhoto(imageUri);

			}

			break;
		case FETCH_FROM_SOCIAL:
			if (resultCode == RESULT_OK && data != null) {
				Log.d("test", "image from gallery" + data.getData());
				Uri selectedImage = data.getData();
				if (selectedImage == null)
					return;

				fetchedImage = getPhoto(selectedImage);

			}
			break;
		default:
			break;
		}
		startActivity(new Intent(FirstActivity.this, FaceCropScreen.class));
	};

	Bitmap getPhoto(Uri selectedImage) {

		Bitmap phoBitmap = null;

		InputStream inputStream = null;
		// int width = (int) (this.width - (.35f * this.width * 1.0f));
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

		//
		// Log.d("test", "File image without load " + imageWidth + " , "
		// + imageHeight);
		InputStream is = null;
		try {
			is = getContentResolver().openInputStream(selectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		float scale = 1.0f;

		if (imageWidth < imageHeight) {
			if (imageHeight > width * 1.0f) {

				scale = width * 1.0f / (imageHeight * 1.0f);
				Log.d("test", " if width " + width + " imageHeight " + imageHeight + " scale " + scale);
			}

		} else {
			if (imageWidth > width * 1.0f) {
				scale = width * 1.0f / (imageWidth * 1.0f);
				Log.d("test", " else width " + width + " imageWidth " + ((int) (imageWidth * scale)) + " scale " + scale);
			}

		}

		phoBitmap = LoadImage.decodeSampledBitmapFromResource(this, selectedImage, (int) (imageWidth * scale), (int) (imageHeight * scale));
		Log.d("test", " return width " + phoBitmap.getWidth() + " height " + phoBitmap.getHeight());
		return phoBitmap;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FirstActivity.fetchedImage.recycle();
		System.gc();
	}

}
