package com.kmobile.gallery.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.kmobile.gallery.app.KMAppConst;
import com.kmobile.gallery.model.KMCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KMPrefManager {
	private static final String TAG = KMPrefManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "AwesomeWallpapers";

	// Google's username
	private static final String KEY_GOOGLE_USERNAME = "google_username";

	// No of grid columns
	private static final String KEY_NO_OF_COLUMNS = "no_of_columns";

	// Gallery directory name
	private static final String KEY_GALLERY_NAME = "gallery_name";

	// gallery albums key
	private static final String KEY_ALBUMS = "albums";

	public KMPrefManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

	}

	/**
	 * Storing google username
	 * */
	public void setGoogleUsername(String googleUsername) {
		editor = pref.edit();

		editor.putString(KEY_GOOGLE_USERNAME, googleUsername);

		// commit changes
		editor.commit();
	}

	public String getGoogleUserName() {
		return pref.getString(KEY_GOOGLE_USERNAME, KMAppConst.PICASA_USER);
	}

	/**
	 * store number of grid columns
	 * */
	public void setNoOfGridColumns(int columns) {
		editor = pref.edit();

		editor.putInt(KEY_NO_OF_COLUMNS, columns);

		// commit changes
		editor.commit();
	}

	public int getNoOfGridColumns() {
		return pref.getInt(KEY_NO_OF_COLUMNS, KMAppConst.NUM_OF_COLUMNS);
	}

	/**
	 * storing gallery name
	 * */
	public void setGalleryName(String galleryName) {
		editor = pref.edit();

		editor.putString(KEY_GALLERY_NAME, galleryName);

		// commit changes
		editor.commit();
	}

	public String getGalleryName() {
		return pref.getString(KEY_GALLERY_NAME, KMAppConst.SDCARD_DIR_NAME);
	}

	/**
	 * Storing albums in shared preferences
	 * */
	public void storeCategories(List<KMCategory> albums) {
		editor = pref.edit();
		Gson gson = new Gson();

		Log.d(TAG, "Albums: " + gson.toJson(albums));

		editor.putString(KEY_ALBUMS, gson.toJson(albums));

		// save changes
		editor.commit();
	}

	/**
	 * Fetching albums from shared preferences. Albums will be sorted before
	 * returning in alphabetical order
	 * */
	public List<KMCategory> getCategories() {
		List<KMCategory> albums = new ArrayList<KMCategory>();

		if (pref.contains(KEY_ALBUMS)) {
			String json = pref.getString(KEY_ALBUMS, null);
			Gson gson = new Gson();
			KMCategory[] albumArry = gson.fromJson(json, KMCategory[].class);

			albums = Arrays.asList(albumArry);
			albums = new ArrayList<KMCategory>(albums);
		} else
			return null;

		List<KMCategory> allAlbums = albums;

		// Sort the albums in alphabetical order
		Collections.sort(allAlbums, new Comparator<KMCategory>() {
            public int compare(KMCategory a1, KMCategory a2) {
                return a1.getTitle().compareToIgnoreCase(a2.getTitle());
            }
        });

		return allAlbums;

	}

	/**
	 * Comparing albums titles for sorting
	 * */
	public class CustomComparator implements Comparator<KMCategory> {
		@Override
		public int compare(KMCategory c1, KMCategory c2) {
			return c1.getTitle().compareTo(c2.getTitle());
		}
	}
}