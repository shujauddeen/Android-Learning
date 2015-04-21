package com.kmobile.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kmobile.gallery.app.KMAppConst;
import com.kmobile.gallery.app.KMAppController;
import com.kmobile.gallery.model.KMCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KMSplashActivity extends ActionBarActivity {
	private static final String TAG = KMSplashActivity.class.getSimpleName();
	private static final String TAG_FEED = "feed", TAG_ENTRY = "entry",
			TAG_GPHOTO_ID = "gphoto$id", TAG_T = "$t",
			TAG_ALBUM_TITLE = "title";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
		setContentView(R.layout.activity_splash);

		// Picasa request to get list of albums
		String url = KMAppConst.URL_PICASA_ALBUMS
				.replace("_PICASA_USER_", KMAppController.getInstance()
						.getPrefManger().getGoogleUserName());
		// Preparing volley's json object request
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						List<KMCategory> albums = new ArrayList<KMCategory>();
						try {
							// Parsing the json response
							JSONArray entry = response.getJSONObject(TAG_FEED)
									.getJSONArray(TAG_ENTRY);

							// loop through albums nodes and add them to album
							// list
							for (int i = 0; i < entry.length(); i++) {
								JSONObject albumObj = (JSONObject) entry.get(i);
								// album id
								String albumId = albumObj.getJSONObject(
										TAG_GPHOTO_ID).getString(TAG_T);

								// album title
								String albumTitle = albumObj.getJSONObject(
										TAG_ALBUM_TITLE).getString(TAG_T);

								KMCategory album = new KMCategory();
								album.setId(albumId);
								album.setTitle(albumTitle);

								// add album to list
								albums.add(album);
							}

							// Store albums in shared pref
							KMAppController.getInstance().getPrefManger()
									.storeCategories(albums);

							// String the main activity
							Intent intent = new Intent(getApplicationContext(),
									KMMainActivity.class);
							startActivity(intent);
							// closing spalsh activity
							finish();

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),
                                    getString(R.string.msg_unknown_error),
                                    Toast.LENGTH_LONG).show();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Volley Error: " + error.getMessage());

						// show error toast
						Toast.makeText(getApplicationContext(),
                                getString(R.string.splash_error),
                                Toast.LENGTH_LONG).show();

						// Unable to fetch albums
						// check for existing Albums data in Shared Preferences
						if (KMAppController.getInstance().getPrefManger()
								.getCategories() != null && KMAppController.getInstance().getPrefManger()
								.getCategories().size() > 0) {
							// String the main activity
							Intent intent = new Intent(getApplicationContext(),
									KMMainActivity.class);
							startActivity(intent);
							// closing spalsh activity
							finish();
						} else {
							// Albums data not present in the shared preferences
							// Launch settings activity, so that user can modify
							// the settings

							Intent i = new Intent(KMSplashActivity.this,
									KMSettingsActivity.class);
							// clear all the activities
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(i);
						}

					}
				});

		// disable the cache for this request, so that it always fetches updated
		// json
		jsonObjReq.setShouldCache(false);

		// Making the request
		KMAppController.getInstance().addToRequestQueue(jsonObjReq);

	}

}
