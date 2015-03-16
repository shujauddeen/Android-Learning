package com.kmobile.gallery.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kmobile.gallery.KMNavDrawerItem;
import com.kmobile.gallery.R;

import java.util.ArrayList;

public class KMNavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<KMNavDrawerItem> navDrawerItems;

	public KMNavDrawerListAdapter(Context context,
                                  ArrayList<KMNavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}

		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.titleImg);

		txtTitle.setText(navDrawerItems.get(position).getTitle());
        switch (navDrawerItems.get(position).getTitle()){
            case "Recently Added":
                imageView.setImageResource(R.drawable.recently);
                break;
            case "Apartment":
                imageView.setImageResource(R.drawable.apartment);
                break;
            case "Bungalow":
                imageView.setImageResource(R.drawable.bungalow);
                break;
            case "Cottage":
                imageView.setImageResource(R.drawable.cottage);
                break;
            case "Home":
                imageView.setImageResource(R.drawable.home);
                break;
            case "Malls":
                imageView.setImageResource(R.drawable.mall);
                break;
            case "Office":
                imageView.setImageResource(R.drawable.office);
                break;
            default:
                imageView.setImageResource(R.drawable.recently);
                break;
        }
//        imageView.setImageResource(R.drawable.ic_action_download);
		return convertView;
	}

}
