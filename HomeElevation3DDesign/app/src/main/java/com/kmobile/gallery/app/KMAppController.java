package com.kmobile.gallery.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.kmobile.gallery.util.KMLruBitmapCache;
import com.kmobile.gallery.util.KMPrefManager;

public class KMAppController extends Application {

	public static final String TAG = KMAppController.class.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	KMLruBitmapCache mKMLruBitmapCache;

	private static KMAppController mInstance;
	private KMPrefManager pref;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		pref = new KMPrefManager(this);
	}

	public static synchronized KMAppController getInstance() {
		return mInstance;
	}

	public KMPrefManager getPrefManger() {
		if (pref == null) {
			pref = new KMPrefManager(this);
		}

		return pref;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			getLruBitmapCache();
			mImageLoader = new ImageLoader(this.mRequestQueue, mKMLruBitmapCache);
		}

		return this.mImageLoader;
	}

	public KMLruBitmapCache getLruBitmapCache() {
		if (mKMLruBitmapCache == null)
			mKMLruBitmapCache = new KMLruBitmapCache();
		return this.mKMLruBitmapCache;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}
