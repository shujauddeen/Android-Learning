package com.komli.adcatalog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AppEventsConstants;
import com.facebook.AppEventsLogger;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class DetailListItem extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list_item);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        TextView txtDesc = (TextView) findViewById(R.id.description);
        TextView txtPrice = (TextView) findViewById(R.id.price);
        TextView txtAvail = (TextView) findViewById(R.id.availability);
        TextView txtBrand = (TextView) findViewById(R.id.brand);
        TextView txtCondition = (TextView) findViewById(R.id.condition);
        final ImageView imageView = (ImageView) findViewById(R.id.productIcon);

        final Intent i = getIntent();
        final String pos = i.getStringExtra("position");
        final Map<String, String> d = ProductStore.getInstance().get(pos);

        String title = d.get("title");
        String description = d.get("description");
        String price = d.get("price");
        String availability = d.get("availability");
        String id = d.get("id");

        txtTitle.setText("Title : " + title);
        txtDesc.setText("Description : " + description);
        txtPrice.setText("Price : " + price);
        txtAvail.setText("Availability : " + availability);
        txtBrand.setText("Brand : " + d.get("brand"));
        txtCondition.setText("Condition : " + d.get("condition"));

        Double prc = Double.valueOf(price);

        //fb event log
        AppEventsLogger logger = AppEventsLogger.newLogger(getApplicationContext());
        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, description);
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,prc.intValue(),parameters);

        //load and set the image
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    InputStream in = new URL(d.get("image_link")).openStream();
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    return bmp;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                if (bmp != null)
                    imageView.setImageBitmap(bmp);
            }
        }.execute();


        final Button button = (Button) findViewById(R.id.add_to_cart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                // Perform action on click
                Intent in = new Intent(getApplicationContext(), AddToCart.class);
                in.putExtra("position",pos);
//                in.putExtra("description",i.getStringExtra("description"));
//                in.putExtra("image_link",i.getStringExtra("image_link"));
//                in.putExtra("link",i.getStringExtra("link"));
//                in.putExtra("title",i.getStringExtra("title"));
//                in.putExtra("price",i.getStringExtra("price"));


                startActivity(in);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_list_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
