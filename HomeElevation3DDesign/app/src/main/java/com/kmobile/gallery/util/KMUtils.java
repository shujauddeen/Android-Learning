package com.kmobile.gallery.util;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.kmobile.gallery.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class KMUtils {
	private String TAG = KMUtils.class.getSimpleName();
	private Context _context;
	private KMPrefManager pref;
	// constructor
	public KMUtils(Context context) {
		this._context = context;
		pref = new KMPrefManager(_context);
	}

	/*
	 * getting screen width
	 */
	@SuppressWarnings("deprecation")
	public int getScreenWidth() {
		int columnWidth;
		WindowManager wm = (WindowManager) _context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) {
			// Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		columnWidth = point.x;
		return columnWidth;
	}

	public String saveImageToSDCard(Bitmap bitmap,boolean showToast) {
		File myDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				pref.getGalleryName());

		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Wallpaper-" + n + ".jpg";
		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			if(showToast){
                Toast.makeText(
                        _context,
                        _context.getString(R.string.toast_saved),
                        Toast.LENGTH_SHORT).show();
            }
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(_context,
                    _context.getString(R.string.toast_saved_failed),
                    Toast.LENGTH_SHORT).show();
		}
        return file.getAbsolutePath();
	}

	public void setAsWallpaper(Bitmap bitmap) {
		try {
			WallpaperManager wm = WallpaperManager.getInstance(_context);
			wm.setBitmap(bitmap);
			Toast.makeText(_context,
                    _context.getString(R.string.toast_wallpaper_set),
                    Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(_context,
                    _context.getString(R.string.toast_wallpaper_set_failed),
                    Toast.LENGTH_SHORT).show();
		}
	}
}