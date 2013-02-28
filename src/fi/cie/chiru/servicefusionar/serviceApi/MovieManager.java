package fi.cie.chiru.servicefusionar.serviceApi;


import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import util.Log;
import fi.cie.chiru.servicefusionar.Finnkino.Auditorium;
import fi.cie.chiru.servicefusionar.Finnkino.FinnkinoXmlParser;
import fi.cie.chiru.servicefusionar.Finnkino.LogInScreen;
import fi.cie.chiru.servicefusionar.Finnkino.MoviePayment;
import fi.cie.chiru.servicefusionar.Finnkino.MovieTicket;
import fi.cie.chiru.servicefusionar.Finnkino.SeatNumber;
import fi.cie.chiru.servicefusionar.Finnkino.FinnkinoXmlParser.Movie;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MovieManager
{
	private static final String LOG_TAG = "MovieManager";
	private ServiceManager serviceManager;
	private boolean movieInfoDownloaded;
	private int maxMovies = 5;
	private String selectedMovie;

	private InfoBubble infobubble;
	private LogInScreen loginscreen;
	private Auditorium auditorium;
	private MoviePayment moviePayment;
	public IdCard ic;
	public CreditCard cc;
	
	public MovieManager(ServiceManager serviceManager)
	{
		movieInfoDownloaded = false;
		
		String URL = "http://www.finnkino.fi/xml/Schedule/?area=1018";
		new DownloadXmlTask().execute(URL);
		loginscreen = new LogInScreen(serviceManager);
		auditorium = new Auditorium(serviceManager);
		moviePayment = new MoviePayment(serviceManager);
		
        this.serviceManager = serviceManager;
	}
	
    public void showMovieInfo(String requesterName)
    {
        if(!movieInfoDownloaded)
    	    return;
    	
    	infobubble.visible();   
    }
    
    public void login(String movieTitle)
    {
    	selectedMovie = movieTitle;
    	//if more auditoriums than Plaza1 is added later the auditorium number can be parsed from movieTitle
    	serviceManager.setVisibilityToAllApplications(false);
    	infobubble.visible();
    	//auditorium.createAuditoriumScreen("Sali1");
    	loginscreen.createLogInScreen(movieTitle);
    	moviePayment.setMovieName(movieTitle);
    	
    	if(ic==null)
    	    ic = new IdCard(serviceManager);
    }
    
    public void seatSelection()
    {
    	auditorium.createAuditoriumScreen("Sali1");
    }
    
    public void payment(List<SeatNumber> seats)
    {
    	moviePayment.paySelectedSeats(seats);
    	
    	if(cc==null)
    	    cc = new CreditCard(serviceManager);
    }
    
    public void createTickets(List<SeatNumber> seats)
    {
    	for(int i=0; i<seats.size(); i++)
    	{
    		MovieTicket ticket = new MovieTicket(serviceManager, selectedMovie, seats.get(i));
    	}
    		
    }
    
    public InfoBubble getInfoBubble()
    {
    	return infobubble;
    }
    
    private void fillMovieInfo(List<Movie> movielist)
    {
    	if (!movieInfoDownloaded || movielist == null)
    		return;
    	
    	int longestTitle = getLongestMovieTitlteLen(movielist);

    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	String currentTime = sdf.format(new Date());
    	
    	List<String> movieInfo = new ArrayList<String>();
    	
    	for(int i = 0; i < movielist.size(); i++)
    	{	
    	
    	    String movieInfoString = new String();
    		String time = new String();
    		String movieTitle = new String();
    		String auditorium = new String();
    		
    		time = movielist.get(i).time.split("[T]")[1];
    		time = time.substring(0,5);
    		
    		if (currentTime.compareTo(time) > 0)
    			continue;
    		
    		movieTitle = movielist.get(i).title;
    		auditorium = movielist.get(i).auditorium;
    		
    		movieInfoString = time + "  " + movieTitle + fillWhiteSpace(longestTitle - movieTitle.length()) + "    " + auditorium;

    		Log.d(LOG_TAG, movieInfoString);
    		
    		if (movieInfo.size() < maxMovies)
    			movieInfo.add(0, movieInfoString);
    		else
    			break;

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
    	for(int k=0; k<movie.size(); k++)
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
                
        if (movies != null)
        {
        	Collections.sort(movies);
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
