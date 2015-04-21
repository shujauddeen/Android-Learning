package com.kmobile.gallery;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kmobile.gallery.app.KMAppController;
import com.kmobile.gallery.helper.KMNavDrawerListAdapter;
import com.kmobile.gallery.model.KMCategory;

import java.util.ArrayList;
import java.util.List;

public class KMMainActivity extends ActionBarActivity {
	private static final String TAG = KMMainActivity.class.getSimpleName();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// Navigation drawer title
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private List<KMCategory> albumsList;
	private ArrayList<KMNavDrawerItem> navDrawerItems;
	private KMNavDrawerListAdapter adapter;
    private ActionBar actionBar;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<KMNavDrawerItem>();

		// Getting the albums from shared preferences
		albumsList = KMAppController.getInstance().getPrefManger().getCategories();

		// Insert "Recently Added" in navigation drawer first position
		KMCategory recentAlbum = new KMCategory(null,
				getString(R.string.nav_drawer_recently_added));

		albumsList.add(0, recentAlbum);

		// Loop through albums in add them to navigation drawer adapter
		for (KMCategory a : albumsList) {
			navDrawerItems.add(new KMNavDrawerItem(a.getId(), a.getTitle()));
		}

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// Setting the nav drawer list adapter
		adapter = new KMNavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// Enabling action bar app icon and behaving it as toggle button
        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
                actionBar.setTitle(mTitle);
                invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(mDrawerTitle);
                invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

	}

    /* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

	/**
	 * Navigation drawer menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}


	/**
	 * On menu item selected
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
			return super.onOptionsItemSelected(item);
	}


	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
        String albumTitle = albumsList.get(position).getTitle();
		switch (position) {
		case 0:
			// Recently added item selected
			// don't pass album id to home fragment
			fragment = KMGridFragment.newInstance(null,albumTitle);
			break;

		default:
			// selected wallpaper category
			// send album id to home fragment to list all the wallpapers
			String albumId = albumsList.get(position).getId();
			fragment = KMGridFragment.newInstance(albumId,albumTitle);
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(albumsList.get(position).getTitle());
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e(TAG, "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title + " Designs";
        actionBar.setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


}
