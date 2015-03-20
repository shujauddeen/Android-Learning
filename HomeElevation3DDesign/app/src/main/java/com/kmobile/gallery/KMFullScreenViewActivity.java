package com.kmobile.gallery;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kmobile.gallery.app.KMAppController;
import com.kmobile.gallery.helper.KMActivitySwipeDetector;
import com.kmobile.gallery.helper.KMPhotoUtils;
import com.kmobile.gallery.helper.KMSwipeInterface;
import com.kmobile.gallery.model.KMWallpaper;
import com.kmobile.gallery.util.KMUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class KMFullScreenViewActivity extends ActionBarActivity implements OnClickListener, KMSwipeInterface {
	private static final String TAG = KMFullScreenViewActivity.class
			.getSimpleName();
	public static final String TAG_SEL_IMAGE = "selectedImage";
    private static final int REQ_CODE_WP_IMAGE = 1;
    private KMWallpaper selectedPhoto;
	private ImageView fullImageView;
    private HorizontalScrollView llImgLayout;
    private LinearLayout llSetWallpaper, llDownloadWallpaper;
	private KMUtils utils;
	private ProgressBar pbLoader;
    private KMActivitySwipeDetector swipe;
    private List<KMWallpaper> photolist;
    private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";
	// Picasa JSON response node keys
	private static final String TAG_ENTRY = "entry",
			TAG_MEDIA_GROUP = "media$group",
			TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
			TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_image);

		fullImageView = (ImageView) findViewById(R.id.imgFullscreen);
        llSetWallpaper = (LinearLayout) findViewById(R.id.llSetWallpaper);
        llDownloadWallpaper = (LinearLayout) findViewById(R.id.llDownloadWallpaper);
        llImgLayout = (HorizontalScrollView) findViewById(R.id.imgLayout);
		pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
//        btnClose = (Button) findViewById(R.id.btnClose);
        swipe = new KMActivitySwipeDetector(this,KMFullScreenViewActivity.this);
        llImgLayout.setOnTouchListener(swipe);



        // layout click listeners
        llSetWallpaper.setOnClickListener(this);
        llDownloadWallpaper.setOnClickListener(this);

        // setting layout buttons alpha/opacity
        llSetWallpaper.getBackground().setAlpha(70);
        llDownloadWallpaper.getBackground().setAlpha(70);

        photolist = KMPhotoUtils.getIntance().get();
		utils = new KMUtils(getApplicationContext());

		Intent i = getIntent();
		selectedPhoto = (KMWallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);
        String title = i.getStringExtra("title");
        // hide the action bar in fullscreen mode
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title + " Design");

        // check for selected photo null
		if (selectedPhoto != null) {

			// fetch photo full resolution image by making another json request
			fetchFullResolutionImage();

		} else {
			Toast.makeText(getApplicationContext(),
                    getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Fetching image fullresolution json
	 * */
	private void fetchFullResolutionImage() {
		String url = selectedPhoto.getPhotoJson();
        llSetWallpaper.setVisibility(View.GONE);
        llDownloadWallpaper.setVisibility(View.GONE);

		// show loader before making request
		pbLoader.setVisibility(View.VISIBLE);
		// volley's json obj request
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							// Parsing the json response
							JSONObject entry = response
									.getJSONObject(TAG_ENTRY);

							JSONArray mediacontentArry = entry.getJSONObject(
									TAG_MEDIA_GROUP).getJSONArray(
									TAG_MEDIA_CONTENT);

							JSONObject mediaObj = (JSONObject) mediacontentArry
									.get(0);

							String fullResolutionUrl = mediaObj
									.getString(TAG_IMG_URL);
							// image full resolution widht and height
							final int width = mediaObj.getInt(TAG_IMG_WIDTH);
							final int height = mediaObj.getInt(TAG_IMG_HEIGHT);

							ImageLoader imageLoader = KMAppController
									.getInstance().getImageLoader();

							imageLoader.get(fullResolutionUrl,
									new ImageListener() {

										@Override
										public void onErrorResponse(
												VolleyError arg0) {
											Toast.makeText(
                                                    getApplicationContext(),
                                                    getString(R.string.msg_wall_fetch_error),
                                                    Toast.LENGTH_LONG).show();
										}

										@Override
										public void onResponse(
												ImageContainer response,
												boolean arg1) {
											if (response.getBitmap() != null) {
												// load bitmap into imageview
												fullImageView
														.setImageBitmap(response
																.getBitmap());
												adjustImageAspect(width, height);

												// hide loader and show set &
												// download buttons
												pbLoader.setVisibility(View.GONE);
                                                llSetWallpaper
                                                        .setVisibility(View.VISIBLE);
                                                llDownloadWallpaper
                                                        .setVisibility(View.VISIBLE);
											}
										}
									});

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
						Log.e(TAG, "Error: " + error.getMessage());
						// unable to fetch wallpapers
						// either google username is wrong or
						// devices doesn't have internet connection
						Toast.makeText(getApplicationContext(),
                                getString(R.string.msg_wall_fetch_error),
                                Toast.LENGTH_LONG).show();

					}
				});

		// Remove the url from cache
		KMAppController.getInstance().getRequestQueue().getCache().remove(url);

		// Disable the cache for this url, so that it always fetches updated
		// json
		jsonObjReq.setShouldCache(false);

		// Adding request to request queue
		KMAppController.getInstance().addToRequestQueue(jsonObjReq);
	}

	/**
	 * Adjusting the image aspect ration to scroll horizontally, Image height
	 * will be screen height, width will be calculated respected to height
	 * */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void adjustImageAspect(int bWidth, int bHeight) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams sParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams dParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		if (bWidth == 0 || bHeight == 0)
			return;
        int sWidth = 0;

		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
            sWidth = size.x;
		} else {
			Display display = getWindowManager().getDefaultDisplay();
            sWidth = display.getWidth();
		}

        int new_height = (int) Math.floor((double) bHeight * (double) sWidth
                / (double) bWidth);
        int rMargin = (sWidth/100)*17;
		params.width = sWidth;
		params.height = new_height;

        sParams.topMargin = new_height + 100;
        sParams.leftMargin = 20;
        dParams.topMargin = new_height + 100;
        dParams.leftMargin = sWidth-rMargin;
        llSetWallpaper.setLayoutParams(sParams);
        llDownloadWallpaper.setLayoutParams(dParams);

		fullImageView.setLayoutParams(params);

	}

    public void performCrop(String path){
        try{
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(getUri(path), "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("return-data", false);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
            startActivityForResult(cropIntent, REQ_CODE_WP_IMAGE);
        }catch (ActivityNotFoundException e){
            String errorMessage = "Your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * View click listener
     * */
    @Override
    public void onClick(View v) {
        if(fullImageView != null && fullImageView.getDrawable() != null)
        {
            Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                    .getBitmap();
            String uri = null;
            switch (v.getId()) {
                // button Download Wallpaper tapped
                case R.id.llDownloadWallpaper:
                    uri = utils.saveImageToSDCard(bitmap,true);
                    break;
                // button Set As Wallpaper tapped
                case R.id.llSetWallpaper:
                    uri = utils.saveImageToSDCard(bitmap,true);
                    performCrop(uri);
                    break;
                default:
                    break;
            }
        }
    }

    private Uri getUri(String f) {
        File file = new File(f);
        return Uri.fromFile(file);
//        return Uri.fromFile(getTempFile());
    }

    private File getTempFile(){
        File file = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
        try {
            file.createNewFile();
        } catch (IOException e) {}
        return file;
    }

    private Uri getTempUri() {
      return Uri.fromFile(getTempFile());
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQ_CODE_WP_IMAGE:
                File tempFile = getTempFile();
                String filePath= Environment.getExternalStorageDirectory()
                        +"/"+TEMP_PHOTO_FILE;
                Bitmap selectedImage =  BitmapFactory.decodeFile(filePath);
                utils.setAsWallpaper(selectedImage);
                if (tempFile.exists()) tempFile.delete();

        }
    }

    @Override
    public void onLeftToRight(View v) {
        switch (v.getId()){
            case R.id.imgLayout:
//                float maxScrollX = llImgLayout.getChildAt(0).getMeasuredWidth() - llImgLayout.getMeasuredWidth();
//                if(llImgLayout.getScrollX() == 0){
                    // On selecting the grid image, we launch fullscreen activity
                    Intent i = getIntent();
                    int pos = i.getIntExtra("pos",0);
                    pos--;
                    if(pos >= 0){
                        KMWallpaper photo = photolist.get(pos);
                        i.putExtra(KMFullScreenViewActivity.TAG_SEL_IMAGE, photo);
                        i.putExtra("pos",pos);
                        finish();
                        overridePendingTransition(R.anim.left_in,R.anim.right_out);
                        startActivity(i);
                    }
//                }
                break;
        }
    }

    @Override
    public void onRightToLeft(View v) {
        switch (v.getId()){
            case R.id.imgLayout:
//                float maxScrollX = llImgLayout.getChildAt(0).getMeasuredWidth() - llImgLayout.getMeasuredWidth();
//                if(llImgLayout.getScrollX() == maxScrollX){
                    Intent i = getIntent();
                    int pos = i.getIntExtra("pos",0);
                    pos++;
                    if(pos < photolist.size()){
                        KMWallpaper photo = photolist.get(pos);
                        i.putExtra(KMFullScreenViewActivity.TAG_SEL_IMAGE, photo);
                        i.putExtra("pos",pos);
                        finish();
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                        startActivity(i);
                    }
//                }
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * On menu item selected
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_share:
                if(fullImageView != null && fullImageView.getDrawable() != null){
                    String uri = null;
                    Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                            .getBitmap();
                    uri = utils.saveImageToSDCard(bitmap,false);
                    shareImage(uri);
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method to share any image.
    private void shareImage(String path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");
        Uri uri = getUri(path);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image!"));
    }
}