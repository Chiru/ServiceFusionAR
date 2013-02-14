package fi.cie.chiru.servicefusionar.serviceApi;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.cie.chiru.servicefusionar.R;
import util.Log;
import util.Vec;


import fi.cie.chiru.servicefusionar.serviceApi.FinnkinoXmlParser.Movie;

import java.io.IOException;
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
	private TextPopUp movieInfo[];
	private boolean movieInfoFilled;
	private boolean movieInfoDownloaded;
	private int maxMovies = 5;
		
	
	public MovieManager(ServiceManager serviceManager)
	{
		movieInfoFilled = false;
		movieInfoDownloaded = false;
		
		String URL = "http://www.finnkino.fi/xml/Schedule/?area=1018";
		new DownloadXmlTask().execute(URL);
		
        this.serviceManager = serviceManager;
        movieInfo = new TextPopUp[maxMovies];
	}
	
    public void showMovieInfo()
    {
        if(!movieInfoFilled)
    	    return;
        
        for(int i=0; i<maxMovies; i++)
        {
            movieInfo[i].visible();
        }
        
    }
    
    public void seatSelection()
    {
    	
    }
    
    public void login()
    {

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
    	    TextPopUp movieInfoItem = new TextPopUp(this.serviceManager);
    	    String movieInfoString = new String();
    		String time = new String();
    		String movieTitle = new String();
    		String auditorium = new String();
    		
    		time = movielist.get(i).time;
    		movieTitle = movielist.get(i).title;
    		auditorium = movielist.get(i).auditorium;
    		
    		movieInfoString = time + " " + movieTitle + fillWhiteSpace(longestTitle - movieTitle.length()) + "    " + auditorium;
    		Log.d(LOG_TAG, movieInfoString);
    		movieInfoItem.setDragText(movieInfoString);
    		movieInfoItem.setPosition(new Vec(5.0f, 4.0f + i, -15.0f));
   		
    		movieInfo[i] = movieInfoItem;
    		movieInfoFilled = true;
    	}
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
}
