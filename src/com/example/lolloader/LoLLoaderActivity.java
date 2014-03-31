package com.example.lolloader;

/* This app was created by Dalton Schmidt as a Coding Challenge for CrowdTorch's recruiting
 * process.  This app loads and displays "lolcat" images from Flickr on each press.
 */

/* List of important information to use Flickr's REST API  
    Flickr key: 8cc252462b263ba4f14a6e6d53ae62b2
	Flickr secret:  e756a5e59cbc247a
	Flickr image request format: http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
	Flickr sample request:  http://farm3.staticflickr.com/2840/13468770644_63315b363b.jpg
	Flickr sample search request:  http://api.flickr.com/services/rest/?&method=flickr.photos.search&api_key=8cc252462b263ba4f14a6e6d53ae62b2&tags=lolcat
*/

import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/** MainActivity:  Activity used to display a LoLCat image from Flickr
 *  To get an image it sends a url to FlickrImageLoader which uses
 *  an Asynctask to fetch and display the image.
 */
public class LoLLoaderActivity extends Activity {

    private int totalPictures = 1;
	private Vibrator vibrator; //Used for haptic feedback
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lolloader_layout);
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		//Makes the app fullscreen The style in manifest.xml is also a fullscreen style
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Restore saved instance from orientation change or app reset
		if (savedInstanceState != null)
        {
			 //Retrieve last image shown and it's picture number
             Bitmap lastPic = savedInstanceState.getParcelable("bitmap");

             //If exists, load the last image that was displayed before StateChange
             if(lastPic != null){
                 ImageView img = (ImageView)findViewById(R.id.image);
            	 img.setImageBitmap(lastPic);
            	 Log.d("savedInstanceState", "loaded Bitmap in onCreate SavedInstanceState");
             }
             else{
            	 Log.d("savedInstanceState", "Failed to load Bitmap in onCreate SavedInstanceState");
             }

             //Enable presses for next image
             setImageOnClickListener();
             return;
        }

		//Fresh start, setup clicks and load an image
		setImageOnClickListener();
	    executeFlickrImageLoader();
        Log.d("MainActivity", "End OnCreate");
	}

	/** onSaveInstanceState: Stores the current image being displayed along with
	 *  it's number so it can be restored later (upon orientation change or reset)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);

	    //Get the current Bitmap in the ImageView
	    ImageView img = (ImageView)findViewById(R.id.image);
	    Bitmap bitmap=((BitmapDrawable)img.getDrawable()).getBitmap();

	    //Put in the current image being shown
	    if(bitmap != null){
	      outState.putParcelable("bitmap", bitmap);
	    }

	    super.onSaveInstanceState(outState);
	    Log.d("onSaveInstanceState", "Put in bitmap of last image");
	}


	/** executeFlickrImageLoader:  This creates and executes an instance of 
	 *  FlickrImageLoader, which performs an AsyncTask to format a URL for an
	 *  image to download and then downloads and displays it when finished
	 */
	public void executeFlickrImageLoader(){
		FlickrImageLoader img = new FlickrImageLoader(this);
		img.execute("foo"); //random generation of images so the parameters do not matter
	}
	
	/** setImageOnClickListener:  initializes a listener that will load another image
	 *  upon pressing it using the FlickrImageLoader class.
	 */
	public void setImageOnClickListener(){
		ImageView image = (ImageView)findViewById(R.id.image);
	    image.setOnClickListener(new View.OnClickListener(){
	        @Override
	        public void onClick(View v){
	        	vibrator.vibrate(50);
	        	executeFlickrImageLoader();
	        }
	    });
	}
	
	/** setTotalPics:  Sets the total number of Pictures for the query.  Needed for randomization */
	public void setTotalPics(int numPics){
		totalPictures = numPics;
	}
	
	/** getTotalPics:  Returns the integer of the total pictures available for the query */
	public int getTotalPics(){
		return totalPictures;
	}
	
	/** getRandomPictureNumber:  Makes a random number [1,totalPictures] to grab a random image */
	public int getRandomPictureNumber(){
		int randomNum = (int)(totalPictures * Math.random()) + 1;
		Log.d("RandomNum", Integer.toString(randomNum));
		return randomNum;		
	}
		
	/** onCreateOptionsMenu:  inflates the menu */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** onOptionsItemSelected: Loads the menu items */ 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	 
		switch (item.getItemId()) {
			case R.id.download_image:
				Log.d("Menu", "Download Image Selected");
				downloadCurrentImage();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);	 
		}
	}
	
	/** downloadCurrentImage:  Downloads the current image being displayed to user storage */
	private void downloadCurrentImage(){
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/saved_images");    
		myDir.mkdirs();
		String fileName = "Image-"+ (int)(Math.random() * 10000) +".jpg";
		File file = new File (myDir, fileName);
		while (file.exists()){ 
			fileName = "Image-"+ (int)(Math.random() * 10000) +".jpg";
			file = new File (myDir, fileName);
		}
		try {
		       FileOutputStream out = new FileOutputStream(file);

		       //Get the current Bitmap in the ImageView
			   ImageView img = (ImageView)findViewById(R.id.image);
			   Bitmap bitmap=((BitmapDrawable)img.getDrawable()).getBitmap();
			   
		       bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		       out.flush();
		       out.close();
		       
		       Log.d("Download image", "Downloaded image as " + fileName + " at location " + 
			    		  root + "saved_images");
		       Toast.makeText(this, "Downloaded image as " + fileName + " at location " + 
		    		  root + "saved_images" , Toast.LENGTH_LONG).show();

		} catch (Exception e) {
		       e.printStackTrace();
		}
	}
	/*End of MainActivity.java*/
	
	
	
	/** FlickrImageLoader:  Inner class Used to take a Flickr url and perform 
	 *  an AsyncTask to fetch information about an image, turn that information 
	 *  into a URL, download that image from URL, then display it.
	 */
	private class FlickrImageLoader extends AsyncTask<String, Integer, Bitmap> {

		private ProgressDialog loadingDialog;
		private ImageView img;
		private Activity mContext;
		private String imgURL = "";
		
		/** Constructor creates an instance linking the activity that initiated it */
		public FlickrImageLoader(Activity context){		
			mContext = context;
			img = (ImageView)findViewById(R.id.image);
			Log.d("IMGLoader", "IMGLoader constructed");
		}
		
		/** doInBackground: sets up downloading the image from Flickr by 
		 *  constructing a URL from JSON info returned by the API request
		 *  and then downloads the image from Flickr.
		 */
	    @Override
	    protected Bitmap doInBackground(String... urls) {
	    	Log.d("IMGLoader", "Do in background Begin.");        
	 
	        //Constructs the URL where the .jpg is located.
	        try {
	        	String JSONString = getJSONString();
	        	makeURLFromJSONString(JSONString);
	        	}catch (JSONException e1) {e1.printStackTrace();}
	        
	        //Takes the URL, downloads the .jpg, and turns it into a bitmap
			try {
				URL url = new URL(imgURL);
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
		    	Log.d("IMGLoader", "ERROR:  doInBackground() returned NULL!!!");	
				return null;
			}
		
	    }
	    
	    /**onPreExecute:  Displays a ProgressDialog to be displayed while loading the picture*/
	    @Override
	    protected void onPreExecute(){
	    	loadingDialog = new ProgressDialog(mContext);
	        loadingDialog.setMessage("Loading LoLCat");
	        loadingDialog.show();
	    }
	    
	    /**onPostExecute sets the fetched image to be displayed on the MainActivity */
	    @Override
	    protected void onPostExecute(Bitmap result){
	    	Log.d("IMGLoader", "OnPostExecute SetBitmap");
	        super.onPostExecute(result);

	        //Set the ImageView to the resulting Bitmap
	        img.setImageBitmap(result);
	        
	        //Dismiss loading dialog
	        loadingDialog.dismiss();
	    }
	    
	    /** getJSONString: requests the JSON data for the URL of the picture.  It uses a 
	     * URLConnection and an InputStream to get a string that can be turned into a 
	     * JSONArray and then JSONObject
	     * 
	     * @throws JSONException
	     */
	    String getJSONString() throws JSONException{
	        Log.d("IMGLoader", "begin getJSONString");
	        
	        String JSONString = "";
			StringBuffer JSONStringBuffer = new StringBuffer();
			
			//urlString is the request that we send flickr.  pictureNum increments on each call so there are
			//no duplicates.
			String urlString = "http://api.flickr.com/services/rest/" +
					 "?&method=flickr.photos.search" +
					 "&api_key=8cc252462b263ba4f14a6e6d53ae62b2" +
					 "&tags=lolcat&per_page=1&format=json&sort=relevance" +
					 "&page=" + getRandomPictureNumber();
			
			//Turn urlString into a URL instance
	    	URL url;
			try {url = new URL(urlString);} 
			catch (MalformedURLException e) {e.printStackTrace();return JSONString;}

			//Make a connection from the url
			URLConnection con;
	    	try {con = url.openConnection();}
			catch (IOException e) {e.printStackTrace();return "";}

	    	//Setup InputStream 
	    	InputStream inStream;
			try {inStream = con.getInputStream();}
	    	catch (IOException e) {	e.printStackTrace(); return "";	}
			
			//Iterate through the InputStream and put the contents into a String
			int currentChar;
	        try {
				while((currentChar = inStream.read()) != -1)
				{
				   JSONStringBuffer.append((char)currentChar);
				}
			} catch (IOException e) {return "";}

	        /*Removes irrelevant data from the String so it can be formatted for JSON.
	          The FlickrAPI request returns "jsonFlickrApi(" at the beginning and a closing
	          paren ')'at the end, which is invalid JSON syntax */
	        JSONString = JSONStringBuffer.substring(14, JSONStringBuffer.length() - 1);
	        
	        Log.d("IMGLoader", "end getJSONString");
	        return JSONString;
	    }
	    
	    /** makeURLFromJSONString Takes a string parsed from a HTTP request served as JSON and formats a 
	     * URL string that contains the location of the picture The URL contains a structure like this:
	     * http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
	     * @param JSONString a String returned from the HTTP request.
	     * @throws JSONException
	     */
	    void makeURLFromJSONString(String JSONString) throws JSONException{
	        Log.d("JSON INFO", "JSON String from Server = " + JSONString);

	        //Create JSON objects. data is all of the information. photoData is only info about the photo
	        JSONObject data = new JSONObject(JSONString);
	        JSONObject photoData = data.getJSONObject("photos").getJSONArray("photo").getJSONObject(0);
	        
	        //Extract information needed for the URL
	        String totalPics = data.getJSONObject("photos").getString("total");
	        String farmid = photoData.getString("farm");
	        String server = photoData.getString("server");
	        String id = photoData.getString("id");
	        String secret = photoData.getString("secret");
	        
	        //Log the data
	        Log.d("JSON INFO", "totalPics = " + totalPics + 
	        				   "farmid = " + farmid +
	        				   "server = " + server +
	        				   "id = " + id + 
	        				   "secret = " + secret );


	        //This sets the total number of pictures for random generation of the
	        //Next call for a photo.
	        setTotalPics(Integer.parseInt(totalPics));
	        
	    	//Make the URL
	        imgURL = "http://farm" + farmid + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
	        Log.d("URL INFO", "Image URL = " + imgURL);
	    }
		
	}
	/*End of FlickrImageLoader*/
	
	
}
