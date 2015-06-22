package com.face.editor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class SocialImages {
	public static final String PHOTOWALLET_PACKAGE = "com.photowallet.socialnetworks";

	public static void pickImage(final Activity activity, int requestCode) {

		if (isPackageInstalled(PHOTOWALLET_PACKAGE, activity)) {

			Intent socialIntent = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);
			socialIntent.setPackage(PHOTOWALLET_PACKAGE);
			((Activity) activity).startActivityForResult(socialIntent, requestCode);

		} else {

			final Dialog dialog = new Dialog(activity);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.social_image_dialog);
			ImageButton crossButton = (ImageButton) dialog.findViewById(R.id.cancel_dialog);
			RelativeLayout relative = (RelativeLayout) dialog.findViewById(R.id.relative_dialog);
			crossButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.photowallet.socialnetworks"));
					activity.startActivity(intent);
					dialog.cancel();
				}
			});
			dialog.show();
		}
	}

	private static boolean isPackageInstalled(String packagename, Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
}
