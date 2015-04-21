package com.kmobile.gallery.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kmobile.gallery.R;
import com.kmobile.gallery.app.KMAppController;
import com.kmobile.gallery.model.KMWallpaper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class KMEditImage extends ActionBarActivity {

    private SeekBar hueBar, satBar, valBar;
    private TextView hueText, satText, valText;
    private Bitmap bitmapMaster;
    private ImageView imageResult;
    private final String TAG_SEL_IMAGE = "selectedImage";
    private static final String TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url";
    private String photo_id = null;
    private int rnd = 0;
    private static final String TAG = KMEditImage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmedit_image);

        Random r = new Random();
        rnd = r.nextInt(65 - 15) + 15;

        Intent i = getIntent();
        KMWallpaper selectedPhoto = (KMWallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);
        photo_id = i.getStringExtra("photo_id");

        fetchFullResolutionImage(selectedPhoto);

        hueText = (TextView) findViewById(R.id.texthue);
        satText = (TextView) findViewById(R.id.textsat);
        valText = (TextView) findViewById(R.id.textval);
        hueBar = (SeekBar) findViewById(R.id.huebar);
        satBar = (SeekBar) findViewById(R.id.satbar);
        valBar = (SeekBar) findViewById(R.id.valbar);
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        satBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valBar.setOnSeekBarChangeListener(seekBarChangeListener);
        final KMUtils utils = new KMUtils(getApplicationContext());
        imageResult = (ImageView) findViewById(R.id.result);

//        loadBitmapHSV();

        Button btnResetHSV = (Button)findViewById(R.id.resethsv);
        Button btnSetHSV = (Button) findViewById(R.id.sethsv);
        btnResetHSV.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // reset SeekBars
                hueBar.setProgress(256);
                satBar.setProgress(256);
                valBar.setProgress(256);

                loadBitmapHSV(0);
            }});

        btnSetHSV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Bitmap bitmap = ((BitmapDrawable) imageResult.getDrawable())
                        .getBitmap();
                utils.saveImageToSDCard(bitmap, true);
            }});
    }

    private void fetchFullResolutionImage(KMWallpaper selectedPhoto) {
        String url = selectedPhoto.getPhotoJson();
        // volley's json obj request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
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
                    ImageLoader imageLoader = KMAppController
                            .getInstance().getImageLoader();
                    imageLoader.get(fullResolutionUrl,
                            new ImageLoader.ImageListener() {

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
                                        ImageLoader.ImageContainer response,
                                        boolean arg1) {
                                    if (response.getBitmap() != null) {
                                        // load bitmap into imageview
                                        imageResult.setImageBitmap(response.getBitmap());
                                        bitmapMaster = response.getBitmap();
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

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {
            KMEditImage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadBitmapHSV(seekBar.getId());
                }
            });
//            loadBitmapHSV(seekBar.getId());
        }

    };

    private void loadBitmapHSV(final int id) {
        if (bitmapMaster != null) {

            int progressHue = hueBar.getProgress() - 256;
            int progressSat = satBar.getProgress() - 256;
            int progressVal = valBar.getProgress() - 256;

			/*
			 * Hue (0 .. 360) Saturation (0...1) Value (0...1)
			 */

            final float hue = (float) progressHue * 360 / 256;
            final float sat = (float) progressSat / 256;
            final float val = (float) progressVal / 256;

            hueText.setText("Hue: " + String.valueOf(hue));
            satText.setText("Saturation: " + String.valueOf(sat));
            valText.setText("Value: " + String.valueOf(val));

            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        int w = bitmapMaster.getWidth();
                        int h = bitmapMaster.getHeight();
                        int[] mapSrcColor = new int[w * h];
                        int[] mapDestColor = new int[w * h];
                        float[] pixelHSV = new float[3];
                        bitmapMaster.getPixels(mapSrcColor, 0, w, 0, 0, w, h);
                        int index = 0;
                        for (int y = 0; y < h; ++y) {
                            for (int x = 0; x < w; ++x) {

                                // Convert from Color to HSV
                                Color.colorToHSV(mapSrcColor[index], pixelHSV);

                                // Adjust HSV
                                if(id == R.id.huebar){
                                    pixelHSV[0] = pixelHSV[0] + hue;
                                    if (pixelHSV[0] < 0.0f) {
                                        pixelHSV[0] = 0.0f;
                                    } else if (pixelHSV[0] > 360.0f) {
                                        pixelHSV[0] = 360.0f;
                                    }
                                }

                                if(id == R.id.satbar){
                                    pixelHSV[1] = pixelHSV[1] + sat;
                                    if (pixelHSV[1] < 0.0f) {
                                        pixelHSV[1] = 0.0f;
                                    } else if (pixelHSV[1] > 1.0f) {
                                        pixelHSV[1] = 1.0f;
                                    }
                                }

                                if(id == R.id.valbar){
                                    pixelHSV[2] = pixelHSV[2] + val;
                                    if (pixelHSV[2] < 0.0f) {
                                        pixelHSV[2] = 0.0f;
                                    } else if (pixelHSV[2] > 1.0f) {
                                        pixelHSV[2] = 1.0f;
                                    }
                                }

                                // Convert back from HSV to Color
                                mapDestColor[index] = Color.HSVToColor(pixelHSV);
                                index++;
                            }
                        }

                        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bmp) {
                    if (bmp != null)
                        imageResult.setImageBitmap(bmp);
                }
            }.execute();


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

}
