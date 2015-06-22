package com.face.editor;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

public class ShareShot {

	Context context;

	public enum EnumSocial {
		FACEBOOK, TWITTER, GMAIL, GOOGLE_PLUS, WHATS_APP, MESSENGER, INSTAGRAM;
	};

	public ShareShot(Context context) {
		this.context = context;
	}

	public boolean isAppAvailable(EnumSocial enumLocal) {
		try {
			String pkgName = null;
			switch (enumLocal) {
			case FACEBOOK:
				pkgName = "com.facebook.katana";
				break;

			case MESSENGER:
				pkgName = "com.facebook.orca";
				break;

			case TWITTER:
				pkgName = "com.twitter.android";
				break;

			case GMAIL:
				pkgName = "com.google.android.gm";
				break;

			case GOOGLE_PLUS:
				pkgName = "com.google.android.apps.plus";
				break;

			case WHATS_APP:
				pkgName = "com.whatsapp";
				break;
			case INSTAGRAM:
				pkgName = "com.instagram.android";
				break;
			}
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkgName, 0);
			if (info != null) {
				return true;
			}
			return false;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public void shareToApp(String subject, String message, File filePath, EnumSocial enumLocal) {

		if (isAppAvailable(enumLocal)) {
			try {
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				switch (enumLocal) {
				case FACEBOOK:
					sharingIntent.setPackage("com.facebook.katana");
					break;
				case MESSENGER:
					sharingIntent.setPackage("com.facebook.orca");
					break;
				case TWITTER:
					sharingIntent.setPackage("com.twitter.android");
					break;
				case GMAIL:
					sharingIntent.setPackage("com.google.android.gm");
					break;
				case GOOGLE_PLUS:
					sharingIntent.setPackage("com.google.android.apps.plus");
					break;
				case WHATS_APP:
					sharingIntent.setPackage("com.whatsapp");
					break;
				case INSTAGRAM:
					sharingIntent.setPackage("com.instagram.android");
					break;
				}

				// if (enumLocal !=
				// com.cg.book.glowdraw.ShareShot.EnumSocial.WHATS_APP) {
				// sharingIntent.setType("text/plain");
				// sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				// sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
				// context.startActivity(Intent.createChooser(sharingIntent,
				// "Share Text"));
				// } else {
				// if (filePath != null) {
				// Log.i("share", "for image");
				// sharingIntent.setType("image/*");
				// sharingIntent.putExtra(Intent.EXTRA_STREAM,
				// Uri.fromFile(filePath));
				// } else {
				// Log.i("share", "for text");
				// sharingIntent.setType("text/plain");
				// sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				// sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
				// }
				//
				// context.startActivity(Intent.createChooser(sharingIntent,
				// filePath != null ? "Share image" : "Share Text"));
				// }

				// if (enumLocal == EnumSocial.WHATS_APP) {
				// sharingIntent.setType("text/plain");
				// sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				// sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
				// context.startActivity(Intent.createChooser(sharingIntent,
				// "Share Text"));
				// } else {
				if (filePath != null) {
					sharingIntent.setType("image/*");
					sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
				} else {
					sharingIntent.setType("text/plain");
				}

				sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
				context.startActivity(Intent.createChooser(sharingIntent, filePath != null ? "Share image" : "Share Text"));
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean openFBFanPage(String fbPageID) {
		if (isAppAvailable(EnumSocial.FACEBOOK)) {
			try {
				Intent ifb = new Intent(Intent.ACTION_VIEW);
				String uri = "fb://page/" + fbPageID; // your FB fanpage ID is
														// here
				Uri.parse(uri);
				ifb.setData(Uri.parse(uri));
				context.startActivity(ifb);

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

}
