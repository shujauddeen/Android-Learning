package com.komli.adcatalog;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsConstants;
import com.facebook.AppEventsLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class AddToCart extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        final TextView txtTitle = (TextView) findViewById(R.id.title);
        final Intent i = getIntent();

        final String pos = i.getStringExtra("position");
        Map<String, String> d = new HashMap<String,String>();
        d = ProductStore.getInstance().get(pos);


        final String title = d.get("title");
        final String price = d.get("price");
        final String description = d.get("description");
        final String id = d.get("id");
        txtTitle.setText("Successfully added product : " + title);

        AppEventsLogger logger = AppEventsLogger.newLogger(getApplicationContext());
        final Double prc = Double.valueOf(price);
        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, description);
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id);

        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART,prc.intValue(),parameters);

        final Button button = (Button) findViewById(R.id.remove_from_cart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast.makeText(getApplicationContext(),
                        title + " Removed from Cart",
                        Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

        final Button btn = (Button) findViewById(R.id.buy);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast.makeText(getApplicationContext(),
                        "Thank you, Your order has been placed successfully.",
                        Toast.LENGTH_LONG).show();
                btn.setEnabled(false);
                button.setEnabled(false);
                AppEventsLogger logger = AppEventsLogger.newLogger(getApplicationContext());
                Bundle parameters = new Bundle();
                parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
                parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, description);
                parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id);
                logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED,prc.intValue(),parameters);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        finish();
                        Intent homepage = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(homepage);
                    }
                }, 2500);



            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_to_cart, menu);
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
}
