package com.face.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.PlusOneButton;

public class ResultScreen extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	boolean saved;
	String local_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	String file_name;
	ShareShot shareshot;
	PlusOneButton plusOneButton;
	// private String APPURL =
	// "https://play.google.com/store/apps/details?id=com.face.editor";

	private static String APPURL = "https://play.google.com/store/apps/details?id=com.scoompa.facechanger";
//	GoogleApiClient mGoogleApiClient;

//	@Override
//	protected void onStart() {
//		super.onStart();
//		Log.i("log", "onstart");
//		// if (!mResolvingError) { // more about this later
//		mGoogleApiClient.connect();
//		// }
//	}
//
//	@Override
//	protected void onStop() {
//		mGoogleApiClient.disconnect();
//		super.onStop();
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_screen);
		// APPURL = APPURL + getPackageName();
		((ImageButton) findViewById(R.id.back_button)).setOnClickListener(clickListener);
		// ((ImageButton)
		// findViewById(R.id.google_plus)).setOnClickListener(clickListener);
		plusOneButton = ((PlusOneButton) findViewById(R.id.plus_one_button));
		// plusOneButton.setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.rate_app)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.save_image)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.more_app)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.watsapp)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.facebook)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.instagram)).setOnClickListener(clickListener);
		((ImageButton) findViewById(R.id.more)).setOnClickListener(clickListener);

		ImageView resultImage = (ImageView) findViewById(R.id.result_image);
		// resultImage.setImageBitmap(FirstActivity.fetchedImage);
		local_path = local_path + getResources().getString(R.string.app_name) + "/";

		int w = getWindowManager().getDefaultDisplay().getWidth();
		int h = getWindowManager().getDefaultDisplay().getHeight();
		this.file_name = System.currentTimeMillis() + ".png";
		float sf = 1.0f;
		Log.i("result ", "width " + w + " height " + h);
		if (FirstActivity.fetchedImage.getWidth() > FirstActivity.fetchedImage.getHeight()) {
			sf = w / FirstActivity.fetchedImage.getWidth();
		} else {
			sf = (h * 0.7f) / FirstActivity.fetchedImage.getHeight();
		}
		resultImage.setImageBitmap(Bitmap.createScaledBitmap(FirstActivity.fetchedImage, (int) (FirstActivity.fetchedImage.getWidth() * sf), (int) (FirstActivity.fetchedImage.getHeight() * sf), true));

		shareshot = new ShareShot(ResultScreen.this);
		Sharing sharing = new Sharing(ResultScreen.this, shareshot);

//		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_button:
				finish();
				break;

			case R.id.rate_app:
				showRateOption();

				break;
			case R.id.save_image:
				save();
				break;
			case R.id.more_app:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://search?q=pub:Axhunter"));
				startActivity(intent);

				break;
			case R.id.facebook:
				if ((shareshot.isAppAvailable(ShareShot.EnumSocial.FACEBOOK))) {
					save();
					if (saved)
						shareshot.shareToApp("Image", "From" + getResources().getString(R.string.app_name), new File(local_path + file_name), ShareShot.EnumSocial.FACEBOOK);

				} else {
					Toast.makeText(ResultScreen.this, "This App is not Available", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.instagram:
				if ((shareshot.isAppAvailable(ShareShot.EnumSocial.INSTAGRAM))) {
					save();
					if (saved)
						shareshot.shareToApp("Image", "From" + getResources().getString(R.string.app_name), new File(local_path + file_name), ShareShot.EnumSocial.INSTAGRAM);

				} else {
					Toast.makeText(ResultScreen.this, "This App is not Available", Toast.LENGTH_LONG).show();
				}
				break;
			// case R.id.google_plus:
			// if ((shareshot.isAppAvailable(ShareShot.EnumSocial.GOOGLE_PLUS)))
			// {
			// save();
			// if (saved)
			// shareshot.shareToApp("Image", "From" +
			// getResources().getString(R.string.app_name), new File(local_path
			// + file_name), ShareShot.EnumSocial.GOOGLE_PLUS);
			// } else {
			// Toast.makeText(ResultScreen.this, "This App is not Available",
			// Toast.LENGTH_LONG).show();
			// }
			// break;
			case R.id.watsapp:
				if ((shareshot.isAppAvailable(ShareShot.EnumSocial.WHATS_APP))) {
					save();
					if (saved)
						shareshot.shareToApp("Image", "From" + getResources().getString(R.string.app_name), new File(local_path + file_name), ShareShot.EnumSocial.WHATS_APP);
				} else {
					Toast.makeText(ResultScreen.this, "This App is not Available", Toast.LENGTH_LONG).show();
				}
				// Log.i("share", "file path" + file_name.getAbsolutePath() +
				// " is null " + (file_name != null));
				break;
			case R.id.more:
				share();
				break;
			default:
				break;
			}

		}
	};

	void save() {
		if (!saved)
			saved = saveBitmapToSDCard(FirstActivity.fetchedImage);
		if (saved) {
			// sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
			// Uri.parse("file://"
			// + Environment.getExternalStorageDirectory())));
			scanNewImageAdded();
			Toast.makeText(ResultScreen.this, "Image Saved to SD Card in " + getResources().getString(R.string.app_name), Toast.LENGTH_LONG).show();
			// Log.d("check","image saved");
		} else {
			// Log.d("check","image not saved");
		}

	}

	boolean saveBitmapToSDCard(Bitmap bitmap) {
		// final String path = Environment.getExternalStorageDirectory()
		// .getAbsolutePath() + "/"+getString(R.string.app_name)+"/";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			File directory = new File(local_path);
			boolean exists = directory.exists();
			if (!exists)
				directory.mkdirs();

			boolean success = false;
			try {
				FileOutputStream out = new FileOutputStream(local_path + file_name);
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
			Toast.makeText(this, "Please insert SD Card", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	void share() {
		if (!saved)
			saved = saveBitmapToSDCard(FirstActivity.fetchedImage);

		if (!saved) {
			Toast.makeText(ResultScreen.this, "Could not save to temporary location...\nPlease insert SD card", Toast.LENGTH_SHORT).show();
			return;
		}
		scanNewImageAdded();
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");

		share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(local_path, file_name)));

		// share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Swapped Face");
		share.putExtra(android.content.Intent.EXTRA_TEXT, "");

		startActivity(Intent.createChooser(share, "Choose"));

	}

	private void scanNewImageAdded() {
		// Log.i("check", "adding image to media store...");
		MediaScannerConnectionClient mClient = new MediaScannerConnectionClient() {

			MediaScannerConnection con;
			{
				con = new MediaScannerConnection(getApplicationContext(), this);
				con.connect();
				// Log.i("check", "Connection to MediaStore created...");
			}

			@Override
			public void onMediaScannerConnected() {
				con.scanFile(ResultScreen.this.local_path + ResultScreen.this.file_name, "image/jpg");
				// Log.i("check",
				// "mImageSavePath scanning started..."+FatifyResult.this.local_path+FatifyResult.this.file_name);
			}

			@Override
			public void onScanCompleted(String path, Uri uri) {
				con.disconnect();
				// Log.i("check", "scanning completed...");
			}
		};

	}

	boolean isRated() {
		SharedPreferences pref = getSharedPreferences("rate", MODE_WORLD_WRITEABLE);
		return pref.getBoolean("rate", false);
	}

	void rated(boolean rate) {
		SharedPreferences pref = getSharedPreferences("rate", MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("rate", rate);
		editor.commit();
	}

	void showRateOption() {
		Log.d("check", "ShowRateOption");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("Rate This App");
		builder.setMessage("If you like the application please rate us 5 Star in the market").setCancelable(false).setNegativeButton("Rate 5 Star", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Uri marketUri = Uri.parse(String.format("market://details?id=%s", getPackageName()));
				Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
				startActivity(marketIntent);
				dialog.cancel();
				ResultScreen.this.finish();
				rated(true);
			}
		}).setNeutralButton("Need Work", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Toast.makeText(ResultScreen.this, "Thanks for your Feedback", Toast.LENGTH_SHORT).show();

				ResultScreen.this.finish();
				rated(true);

			}
		})

		.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				// showAd();

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		plusOneButton.initialize(APPURL, 1);

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Log.i("result ", "onfinish of resultscreen");
		Intent data = new Intent();
		data.putExtra(FaceSwapScreen.FINISH_SWAP_SCREEN, true);
		setResult(RESULT_OK, data);

		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("result ", "ondestrou of resultscreen");

		super.onDestroy();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.i("log", "connected falid");
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.i("log", "connected");
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		Log.i("log", "connected suspend");
	}
}
