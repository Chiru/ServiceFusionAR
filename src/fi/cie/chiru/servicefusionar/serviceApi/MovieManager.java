package fi.cie.chiru.servicefusionar.serviceApi;


import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.text.Layout;

import org.json.JSONException;

import fi.cie.chiru.servicefusionar.R;
import gl.GLFactory;
import gl.ObjectPicker;
import gl.scenegraph.MeshComponent;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.DragEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import util.IO;
import util.Log;
import util.Vec;



import fi.cie.chiru.servicefusionar.serviceApi.FinnkinoXmlParser.Movie;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class MovieManager
{
	private static final String LOG_TAG = "MovieManager";
	private ServiceManager serviceManager;
	private String movieInfo[];
	private boolean movieInfoDownloaded;
	private int maxMovies = 5;

	private InfoBubble infobubble;
	private String selectedMovie;
	private OnDragListener listener;
	
	
	public MovieManager(ServiceManager serviceManager)
	{
		movieInfoDownloaded = false;
		
		String URL = "http://www.finnkino.fi/xml/Schedule/?area=1018";
		new DownloadXmlTask().execute(URL);
		
        this.serviceManager = serviceManager;
        movieInfo = new String[maxMovies];
	}
	
    public void showMovieInfo(String requesterName)
    {
        if(!movieInfoDownloaded)
    	    return;
    	
    	infobubble.visible();   
    }
    
    public void seatSelection()
    {
    	
    }
    
    public void login()
    {
    	createAuditoriumScreen();
    	createLogInScreen();
    }
    
    public void payment()
    {
    	
    }
    
    private void fillMovieInfo(List<Movie> movielist)
    {
    	if (!movieInfoDownloaded || movielist == null)
    		return;
    	
    	int longestTitle = getLongestMovieTitlteLen(movielist);

    	for(int i=0; i<maxMovies; i++)
    	{	
    	
    	    String movieInfoString = new String();
    		String time = new String();
    		String movieTitle = new String();
    		String auditorium = new String();
    		
    		time = movielist.get(i).time.split("[T]")[1];
    		movieTitle = movielist.get(i).title;
    		auditorium = movielist.get(i).auditorium;
    		
    		movieInfoString = time + " " + movieTitle + fillWhiteSpace(longestTitle - movieTitle.length()) + "    " + auditorium;

    		Log.d(LOG_TAG, movieInfoString);
    		movieInfo[i] = movieInfoString;
    	}
    	
    	infobubble = new InfoBubble(serviceManager);
    	    	
 		if(infobubble.setInfoBubbleApplication("MusicInfobubble"))
 		    infobubble.populateItems(movieInfo);
    }
    
    private String fillWhiteSpace(int number)
    {
    	String whitespace = new String();
    	
    	for(int j=0; j<number; j++)
    		whitespace += " ";
    	
    	return whitespace;
    }
    
    private int getLongestMovieTitlteLen(List<Movie> movie)
    {
    	if (movie == null)
    	{
    		return 0;
    	}
    	
    	int len = 0;
    	for(int k=0; k<maxMovies; k++)
    	{
    	    if(movie.get(k).title.length() > len)
    	    	len = movie.get(k).title.length();
    	}
    	
    	return len;
    }
    

    // Implementation of AsyncTask used to download XML feed from Finnkino.
    private class DownloadXmlTask extends AsyncTask<String, Void, List<Movie>> 
    {
        @Override
        protected List<Movie> doInBackground(String... urls) 
        {
        	List<Movie> empty = null;
        	try 
            {
                return loadXmlFromNetwork(urls[0]);
            } 
            
            catch (IOException e) 
            {
            	Log.d(LOG_TAG, e.toString());
                return empty;
            } 
            catch (XmlPullParserException e) 
            {
            	Log.d(LOG_TAG, e.toString());
                return empty;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> result) 
        {
        	if (result != null)
        	{
        		movieInfoDownloaded = true;
        		fillMovieInfo(result);
        	}
        	else
        	{
        		Log.d(LOG_TAG, "Got error during movie data downloading!");
        	}
        	//Log.d(LOG_TAG, result);
        }
    }
    

    private List<Movie> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
    	InputStream stream = null;
        FinnkinoXmlParser finnkinoXmlParser = new FinnkinoXmlParser();
        List<Movie> movies = null;
        
        try 
        {
            stream = downloadUrl(urlString);
            movies = finnkinoXmlParser.parse(stream);
        }
        
        // Makes sure that the InputStream is closed after the app is finished using it.
        finally
        {
            if (stream != null)
                stream.close();
        }        
        return movies;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }


    private void createAuditoriumScreen()
    {
    	MeshComponent plaza = GLFactory.getInstance().newTexturedSquare("plaza", IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.plaza_1_smaller));
    	
    	serviceManager.setVisibilityToAllApplications(false);
    	infobubble.visible();
    	
    	serviceManager.getSetup().world.add(plaza);
 	    plaza.setPosition(new Vec(0.0f, -5.0f, 0.0f));
 	    plaza.setRotation(new Vec(45.0f, 180.0f, 0.0f));
 	    plaza.setScale(new Vec(15.0f, 10.0f, 15.0f));
    	
    }
    
    private void createLogInScreen()
    {
    	Handler mHandler = new Handler(Looper.getMainLooper());
    	
    	mHandler.post(new Runnable() {

			@Override
			public void run() 
			{
				LayoutInflater li = (LayoutInflater) serviceManager.getSetup().myTargetActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View v = li.inflate(R.layout.movielogin, null); 
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				root.addView(v);
				    	
//				MeshComponent loginScreen = GLFactory.getInstance().newTexturedSquare("loginscreen", IO.loadBitmapFromView(v));
//				
//				serviceManager.getSetup().world.add(loginScreen);
//		    	loginScreen.setPosition(new Vec(0.0f, 0.0f, 0.0f));
//		    	loginScreen.setRotation(new Vec(90.0f, 0.0f, 0.0f));
//		    	loginScreen.setScale(new Vec(15.0f, 10.0f, 15.0f));
				
			}

		});

    }
}
