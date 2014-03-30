package com.example.lolloader;

/* List of important information to use Flickr's REST API  
    Flickr key: 8cc252462b263ba4f14a6e6d53ae62b2
	Flickr secret:  e756a5e59cbc247a
	Flickr image request format: http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
	Flickr sample request:  http://farm3.staticflickr.com/2840/13468770644_63315b363b.jpg
	Flickr sample search request:  http://api.flickr.com/services/rest/?&method=flickr.photos.search&api_key=8cc252462b263ba4f14a6e6d53ae62b2&tags=lolcat
*/

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

/** MainActivity:  Activity used to diplay a LoLCat image from Flickr
 *  To get an image it sends a url to FlickrImageLoader which uses
 *  an Asynctask to fetch and display the image.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//TODO:  make these calls actually mean something
		View mDecorView = getWindow().getDecorView();
		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 
										 View.SYSTEM_UI_FLAG_IMMERSIVE |
										 View.SYSTEM_UI_FLAG_FULLSCREEN);
		
		//Create an image request (right now image hard coded)
		FlickrImageLoader img = new FlickrImageLoader(this);
		img.execute("foo");
		
        Log.d("MainActivity", "End OnCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
