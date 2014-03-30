package com.example.lolloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Queue;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


/**FlickrImageLoader:  Used to take a Flickr url and perform an AsyncTask   
 * to fetch the image and then display it.
 */
public class FlickrImageLoader extends AsyncTask<String, Integer, Bitmap> {

	private ProgressDialog loadingDialog;
	private ImageView img = null;
	private Activity mContext;
	private String imgURL = "";
	private int totalPics;
	private int pictureNum;
	
    private String total;
    private String farmid;
    private String server;
    private String id;
    private String secret;
	
	/** Constructor creates an instance linking the activity that initiated it */
	public FlickrImageLoader(Activity myContext, int pictureNumber){

		//Grabs ImageView and Activity from myContext
		img = (ImageView)myContext.findViewById(R.id.image);
		mContext = myContext;
		pictureNum = pictureNumber;
		
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
        
        //Dismisses loading dialog
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
        //Initialize 
        String JSONString = "";
		StringBuffer JSONStringBuffer = new StringBuffer();
		
		//urlString is the request that we send flickr.  pictureNum increments on each call so there are
		//no duplicates.
		String urlString = "http://api.flickr.com/services/rest/" +
				 "?&method=flickr.photos.search" +
				 "&api_key=8cc252462b263ba4f14a6e6d53ae62b2" +
				 "&tags=lolcat&per_page=1&format=json&sort=relevance" +
				 "&page=" + pictureNum;
		
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
        total = data.getJSONObject("photos").getString("total");
        farmid = photoData.getString("farm");
        server = photoData.getString("server");
        id = photoData.getString("id");
        secret = photoData.getString("secret");
        
        //Log the data
        Log.d("JSON INFO", "total = " + total);
        Log.d("JSON INFO", "farmid = " + farmid);     
        Log.d("JSON INFO", "server = " + server);
        Log.d("JSON INFO", "id = " + id);
        Log.d("JSON INFO", "secret = " + secret);


    	//Make the URL
        imgURL = "http://farm" + farmid + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
        Log.d("URL INFO", "Image URL = " + imgURL);
    }
	
}