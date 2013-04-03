package fi.cie.chiru.servicefusionar.sensors;

import java.util.ArrayList;
import java.util.List;

import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

public class Sensors implements OrientationListener 
{
	private static final String LOG_TAG = "ServiceFusion sensors";
	private Location currentGeoLocation = null;
	private ServiceManager serviceManager = null;

	private Orientator orientator;
	private Handler myHandler;
	private SensorResults sensorResults = new SensorResults();
	private float currentAngle = 0.f;
	private boolean tilt;

	public Sensors(ServiceManager servicemanager)
	{
		this.serviceManager = servicemanager;

		orientator = new Orientator(this, serviceManager.getSetup().getActivity());
		myHandler = new Handler();

		orientator.Start();
	}


	/** change the text to the new updated one */
	@Override
	public void newStatus(int status, final String info)
	{
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				//textStatus.setText(info);
				//Log.i(LOG_TAG, "Info: " + info);
			}
		});
	}

	/** storage for sensors: Name:Reading */
	class SensorResult
	{
		public String sensorName;
		public String sensorReading;
		public SensorResult(String n, String r)
		{
			sensorName = n;
			sensorReading = r;
		}
	}

	/** interpret the data from sensor and translate it to readable format */
	class SensorResults
	{
		List<SensorResult> results = new ArrayList<SensorResult>();

		public void NewResult(String n, float[] r, String f)
		{
			String data = "";
			if(r == null || r.length == 0) {
				data = "null";
			} else {
				for(Float v : r) {
					data += (data.length() > 0 ? ", " : "");
					data += String.format(f, v);
				}
			}
			for(SensorResult cur : results) {
				if(cur.sensorName == n) {
					cur.sensorReading = data;
					post();
					return;
				}
			}
			results.add(new SensorResult(n,data));
		}
		public void NewResult(String n, float[] r)
		{
			NewResult(n,r,"%.2f");
		}

		private void post()
		{
			String angle = "";
			String location = "";
			
			for(SensorResult cur : results) {
				angle += (angle.length() > 0 ? "\n" : "");
				angle += (cur.sensorName.equals("Angle") ? cur.sensorReading : "");
				
				location += (angle.length() > 0 ? "\n" : "");
				location += (cur.sensorName.equals("Location") ? cur.sensorReading : "");
			}
			if (!angle.isEmpty())
			{
				if (currentAngle - Float.valueOf(angle) > 4 || currentAngle - Float.valueOf(angle) < -4 )
				{
					Log.i(LOG_TAG, "Sensor result: " + angle);
					currentAngle = Float.valueOf(angle);
				}
			}
			if (!location.isEmpty())
			{
				if (currentGeoLocation == null)
					currentGeoLocation = new Location("GpsLocation");
				
				if (location.split(",").length > 1)
				{
					//
					double latitude = Float.valueOf(location.split(",")[0]);
					double longitude = Float.valueOf(location.split(",")[1]);
					currentGeoLocation.setLatitude(latitude);
					currentGeoLocation.setLongitude(longitude);
					//Log.i(LOG_TAG, latitude + ", " + longitude);
					
					serviceManager.geoLocationEstablished(currentGeoLocation);
				}
			}
		}
	}
	
	/** return current bearing angle of the device */
	public float getCurrentAngle()
	{
		return currentAngle;
	}
	
	/** return current geological location if it is already available, otherwise return null */
	public Location getCurrentLocation()
	{
		return currentGeoLocation;
	}

	/** add new sensor result to the list */
	@Override
	public void newResult(final String sensor, final float[] result)
	{
		newResult(sensor,result,"%.2f");
	}
	@Override
	public void newResult(final String sensor, final float[] result, final String format)
	{
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				sensorResults.NewResult(sensor, result, format);
			}
		});
	}
	/** add implementation for the newLocation. This is how you get the location as Latitude, Longitude */
	@Override
	public void newLocation(double latitude, double longitude)
	{
		/** display the location */
		newResult("Location", new float[] {(float) latitude, (float) longitude},"%.6f");
	}
	/** add implementation for the newOrientation. This is how you get the orientation. As angle */
	@Override
	public void newOrientation(double angle)
	{
		newResult("Angle", new float[] {(float)angle});
	}
	/** add implementation for the isTilt. This is how we know if the device should stop showing the image when tilted */
	public void isTilt(Boolean bool)
	{
		if(bool) {
			newStatus(0, "Stopped due to tilting...");
		} else {
			newStatus(0, "Started as no tilting...");
		}
		
		tilt = bool;
	}
	/** add implementation for the isCalibrated. This is how we know if the device sensors are reliable */
	public void isCalibrated(final String sensor, Boolean bool)
	{
		if(!bool) {
			newStatus(0, "Sensor "+sensor+" need 8 figure calibration..."); // You need to draw an 8 in the air :)
		}
	}
	
	public boolean tilted()
	{
		return tilt;
	}
	
	public void onDestroy()
	{
		orientator.Stop();
	}

}
