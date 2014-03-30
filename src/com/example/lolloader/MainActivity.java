package com.example.lolloader;

/* List of important information to use Flickr's REST API  
    Flickr key: 8cc252462b263ba4f14a6e6d53ae62b2
	Flickr secret:  e756a5e59cbc247a
	Flickr image request format: http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
	Flickr sample request:  http://farm3.staticflickr.com/2840/13468770644_63315b363b.jpg
	Flickr sample search request:  http://api.flickr.com/services/rest/?&method=flickr.photos.search&api_key=8cc252462b263ba4f14a6e6d53ae62b2&tags=lolcat
*/

import java.util.Queue;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

/** MainActivity:  Activity used to display a LoLCat image from Flickr
 *  To get an image it sends a url to FlickrImageLoader which uses
 *  an Asynctask to fetch and display the image.
 */
public class MainActivity extends Activity {

	private int pictureNumber = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//TODO:  make these calls actually mean something
		View mDecorView = getWindow().getDecorView();
		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 
										 View.SYSTEM_UI_FLAG_IMMERSIVE |
										 View.SYSTEM_UI_FLAG_FULLSCREEN);
		
		setImageOnClickListener();
	    executeFlickrImageLoader();
        Log.d("MainActivity", "End OnCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** getNextPictureNumber: returns an int of the next picture to be displayed.
	 *  pictureNumber is an integer that gets incremented every call to avoid duplicates
	 */
	public int getNextPictureNumber(){
		return pictureNumber++;
	}

	/**executeFlickrImageLoader:  This creates and executes an instance of 
	 *  executeFlickrImageLoader, which performs an AsyncTask to format a
	 *  URL for an image to download and then downloads and displays it when finished
	 */
	public void executeFlickrImageLoader(){
		FlickrImageLoader img = new FlickrImageLoader(this, getNextPictureNumber());
		img.execute("foo");
	}
	
	/** setImageOnClickListener:  initializes a listener that will load another image
	 *  upon pressing it using the FlickrImageLoader class.
	 */
	public void setImageOnClickListener(){

		ImageView image = (ImageView)findViewById(R.id.image);
	    image.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v){
	        	executeFlickrImageLoader();
	        }
	    });
	}
	
	
}
