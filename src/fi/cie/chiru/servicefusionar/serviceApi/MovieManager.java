package fi.cie.chiru.servicefusionar.serviceApi;

import util.Log;
import android.annotation.SuppressLint;
import fi.cie.chiru.servicefusionar.Finnkino.Auditorium;
import fi.cie.chiru.servicefusionar.Finnkino.FinnkinoXmlParser;
import fi.cie.chiru.servicefusionar.Finnkino.LogInScreen;
import fi.cie.chiru.servicefusionar.Finnkino.MoviePayment;
import fi.cie.chiru.servicefusionar.Finnkino.MovieTicket;
import fi.cie.chiru.servicefusionar.Finnkino.SeatNumber;
import fi.cie.chiru.servicefusionar.Finnkino.FinnkinoXmlParser.Movie;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;


public class MovieManager
{
	private static final String LOG_TAG = "MovieManager";
	private ServiceManager serviceManager;
	private boolean movieInfoDownloaded;
	private int maxMovies = 10;
	private String selectedMovie;

	private InfoBubble infobubble;
	private LogInScreen loginscreen;
	private Auditorium auditorium;
	private MoviePayment moviePayment;
	private List<MovieTicket> tickets = null;
	public IdCard ic;
	public CreditCard cc;
	
	public MovieManager(ServiceManager serviceManager)
	{
		movieInfoDownloaded = false;
		
		String URL = "http://www.finnkino.fi/xml/Schedule/?area=1018";
		new XmlDownloader() { 
	        protected void onPostExecute(String xmlData) {
	        	if (xmlData != null)
	        	{
	        		movieInfoDownloaded = true;
	        	
	        		FinnkinoXmlParser parser = new FinnkinoXmlParser();
	        		fillMovieInfo(parser.parse(xmlData));
	        	}
	        	else
	        		Log.e(LOG_TAG, "Couldn't download xml data!");
	        }
	    }.execute(URL); 
		
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
    	serviceManager.getSetup().camera.setUIMode(true);
    	selectedMovie = movieTitle;
    	//if more auditoriums than Plaza1 is added later the auditorium number can be parsed from movieTitle
    	serviceManager.setVisibilityToAllApplications(false);
    	infobubble.visible();
    	
    	if(serviceManager.getMusicManager().getInfoBubble() != null && serviceManager.getMusicManager().getInfoBubble().isVisible())
    		serviceManager.getMusicManager().getInfoBubble().visible();
    	
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
    	if(tickets ==null)
    	    tickets = new ArrayList<MovieTicket>();
    	
    	for(int i=0; i<seats.size(); i++)
    	{
    		MovieTicket ticket = new MovieTicket(serviceManager, selectedMovie, seats.get(i));
    		tickets.add(ticket);
    	}
    		
    }
    
    public void removeTickets()
    {
    	if(tickets == null)
    		return;
    	
    	for(int i=0; i<tickets.size(); i++)
    	{
    		tickets.get(i).removeTicket();
    	}
    	
    	tickets = null;	
    }
    
    public InfoBubble getInfoBubble()
    {
    	return infobubble;
    }
    
    @SuppressLint("SimpleDateFormat")
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
    			movieInfo.add(movieInfoString);
    		else
    			break;

    	}
    	
    	infobubble = new InfoBubble(serviceManager);

 		if(infobubble.setInfoBubbleApplication("MovieInfobubble"))
 		    infobubble.populateItems(movieInfo, "MovieManager");
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
}
