package com.komli.adcatalog;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ListActivity {


   String[] itemname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONObject json = getJSONfromRaw();
        try{
            JSONArray results = json.getJSONArray("results");
            itemname = new String[results.length()];
            ProductStore obj = ProductStore.getInstance();
            for(int i=0;i < results.length();i++){
                JSONObject e = results.getJSONObject(i);
                itemname[i]=i+"";
                obj.set(String.valueOf(i),e);
            }
        }catch (Exception e){
            Log.e("log_tag", "Error parsing data "+e.toString());
        }

        CustomListApdapter adapter = new CustomListApdapter(this,itemname);
        setListAdapter(adapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onListItemClick(ListView lv, View view, int position, long imgid) {
        Map<String, String> d = new HashMap<String,String>();
        String pos = String.valueOf(position);
        Intent i = new Intent(getApplicationContext(), DetailListItem.class);
        d = ProductStore.getInstance().get(pos);
        i.putExtra("position",pos);
        startActivity(i);
    }

    public JSONObject getJSONfromRaw(){
        String result = "";
        JSONObject jArray = null;
        InputStream is = getResources().openRawResource(R.raw.product_catalog);
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }catch (Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
        }
        try{
           jArray = new JSONObject(result);
        }catch (Exception e){

        }
        return jArray;
    };


//    new AsyncTask<String, Void, Void>() {
//        @Override
//        protected Void doInBackground(String... params) {
//            try{
//                URL url = new URL(params[0]);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(10000 /* milliseconds */);
//                conn.setConnectTimeout(15000 /* milliseconds */);
//                conn.setRequestMethod("GET");
//                conn.setDoInput(true);
//                // Starts the query
//                conn.connect();
//                InputStream stream = conn.getInputStream();
//
//                String data = convertStreamToString(stream);
//                Log.i("MainActivity",data);
//                stream.close();
//            }catch (Exception e){
//
//            }
//            return null;
//        }
//    }.execute("http://20.20.20.62:9000/public/javascripts/product_catalog.json");
////        ends here
//
//    private String convertStreamToString(java.io.InputStream is) {
//        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
//        return s.hasNext() ? s.next() : "";
//    }

}
