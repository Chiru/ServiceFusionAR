package fi.cie.chiru.servicefusionar.serviceApi;

import java.util.List;

import util.Log;

public class MoviePayment 
{
	private ServiceManager serviceManager;
	
	public MoviePayment(ServiceManager serviceManager)
	{
		this.serviceManager = serviceManager;
	}
	
	public void paySelectedSeats(List<SeatNumber> seats)
	{
    	Log.d("MoviePayment","------------------selected seats:");
    	for(int i=0; i<seats.size(); i++)
    	    Log.d("MoviePayment","------------------row: " +  seats.get(i).getRow() + "   col: "   + seats.get(i).getCol());
	}

}
