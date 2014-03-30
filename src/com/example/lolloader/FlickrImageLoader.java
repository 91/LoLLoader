package com.example.lolloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


/**FlickrImageLoader:  Used to take a Flickr url and perform an Async task   
 * to fetch the image and then display it.
 */
public class FlickrImageLoader extends AsyncTask<String, Integer, Bitmap> {

	ImageView img = null;
	Activity mContext;
	
	/** Constructor creates an instance linking the activity that initiated it */
	public FlickrImageLoader(Activity myContext){
		//Grabs ImageView and Activity from myContext
		img = (ImageView)myContext.findViewById(R.id.image);
		mContext = myContext;
		
		Log.d("IMGLoader", "IMGLoader constructed");
	}
	
	/**doInBackground requests the url of the image from Flickr */
    @Override
    protected Bitmap doInBackground(String... urls) {
    	Log.d("IMGLoader", "Do in background Begin.");
  
        //Sample, hardcoded URL for starting off
        String urlString = "http://farm3.staticflickr.com/2840/13468770644_63315b363b.jpg";        
  
        //Request the photo from Flickr
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
	    	Log.d("IMGLoader", "doInBackground Ended Successfully");	
			return myBitmap;
		} 
		catch (IOException e) {
			e.printStackTrace();
	    	Log.d("IMGLoader", "ERROR:  doInBackground returned NULL!!!");	
			return null;
		}
	
    }
    
    /**onPostExecute sets the fetched image to be diplayed on the MainActivity */
    @Override
    protected void onPostExecute(Bitmap result){
    	Log.d("IMGLoader", "OnPostExecute");
    	img.setImageBitmap(result);
    }
	
}