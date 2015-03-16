package com.komli.adcatalog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CustomListApdapter extends ArrayAdapter<String> {

    private final Activity context;

    public CustomListApdapter(Activity context, String[] itemname) {
        super(context, R.layout.productlist, itemname);
        // TODO Auto-generated constructor stub
        this.context=context;
    }

    public View getView(final int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.productlist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.description);
        TextView pri = (TextView) rowView.findViewById(R.id.price);
        TextView av = (TextView) rowView.findViewById(R.id.availability);

        //get the image using url
        String pos = String.valueOf(position);
        final Map<String, String> d = ProductStore.getInstance().get(pos);

        int tMaxLen = (d.get("title").length() < 20)?d.get("title").length():20;
        int dMaxLen = (d.get("description").length() < 30)?d.get("description").length():30;

        txtTitle.setText(d.get("title").substring(0,tMaxLen));
        extratxt.setText(d.get("description").substring(0,dMaxLen));
        pri.setText("Price: " + d.get("price"));
        av.setText("Available: " +d.get("availability"));

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
        return rowView;
    };


}
