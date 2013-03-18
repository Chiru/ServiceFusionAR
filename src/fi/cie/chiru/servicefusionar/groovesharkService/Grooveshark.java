package fi.cie.chiru.servicefusionar.groovesharkService;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class Grooveshark implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener{

	private static final String key = "";
	private static final String secret = "";
	private static final String api_url = "https://api.grooveshark.com/ws3.php?sig=";
	
	private static final String LOG_TAG = "GroovesharkPlayer";
	private static String session_id = null;
	private JSONObject country = null;
	private JSONObject active_stream = new JSONObject();
	
	private MediaPlayer mediaPlayer = null;
	private Timer timer = null;

	private class Request extends AsyncTask<String, Void, String>
	{
		private String Signature(String s)
	    {
	        String sEncodedString = null;
	        try
	        {
	            SecretKeySpec key = new SecretKeySpec((secret).getBytes("UTF-8"), "HmacMD5");
	            Mac mac = Mac.getInstance("HmacMD5");
	            mac.init(key);

	            byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

	            StringBuffer hash = new StringBuffer();

	            for (int i=0; i<bytes.length; i++) {
	                String hex = Integer.toHexString(0xFF &  bytes[i]);
	                if (hex.length() == 1) {
	                    hash.append('0');
	                }
	                hash.append(hex);
	            }
	            sEncodedString = hash.toString();
	        }
	        catch (UnsupportedEncodingException e) {
	        	Log.d(LOG_TAG, e.toString());
	        }
	        catch(InvalidKeyException e){
	        	Log.d(LOG_TAG, e.toString());
	        }
	        catch (NoSuchAlgorithmException e) {
	        	Log.d(LOG_TAG, e.toString());
	        }
	        return sEncodedString ;
	    }
		
		private String sendRequest(String payload) throws IOException
		{
			String sig = Signature(payload);
			Log.i(LOG_TAG, "Payload: " + payload);
			URL url = new URL(api_url + sig);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.connect();

			OutputStream os = connection.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
			pw.write(payload);
			pw.close();

			InputStream is = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
			    sb.append(line);
			}
			is.close();
			String response = sb.toString();
			Log.i(LOG_TAG, "response: " + response);
			
			return response;
		}

		@Override
		protected String doInBackground(String... params) {
			String response = null;
			
			try {
				response = sendRequest(params[0]);
			} catch (IOException e) {
				Log.d(LOG_TAG, e.toString());
			}
			return response;
		}
		
	}
	
	/**
	Public constructor for Grooveshark API. This constructor initializes grooveshark connection, saves
	sessionId and country data for later use.
	 */
	public Grooveshark()
	{
		assert !key.isEmpty() : "Key is empty!";
		assert !secret.isEmpty() : "Secret Key is empty!";
		
		// Initialize Grooveshark session
		StartSession();
		
	}
	
	private void StartSession()
	{
		String payload = "{\"header\":{\"wsKey\": \"" + key + "\"},\"method\": \"startSession\",\"parameters\":\"\"}";
		
		new Request() { 
	        protected void onPostExecute(String response) {
	        	JSONObject responseObject = null;
	        	if (response != null)
	        	{
		    		try {
		    			responseObject = new JSONObject(response);
		    		} catch (JSONException e) {
		    			Log.e(LOG_TAG, e.toString());
		    		}
		    		
		    		try {
		    			if (responseObject.getJSONObject("result").getBoolean("success"))
		    				session_id = responseObject.getJSONObject("result").getString("sessionID");
		    		} catch (JSONException e) {
		    			Log.e(LOG_TAG, e.toString());
		    		}
		    		
		    		// Initialize Country data for Grooveshark session
		    		GetCountry();
	        	}
	        	else
	        		Log.e(LOG_TAG, "Couldn't get response from Grooveshark service!");
	        }
	    }.execute(payload); 

	}

    private void GetCountry()
    {
    	String payload = "{\"header\":{\"wsKey\": \"" + key + "\", \"sessionID\": \""+ session_id + "\"},\"method\": \"getCountry\",\"parameters\":\"\"}";

		new Request() { 
	        protected void onPostExecute(String response) {
	        	JSONObject responseObject = null;
	        	if (response != null)
	        	{
		    		try {
		    			responseObject = new JSONObject(response);
		    		} catch (JSONException e) {
		    			Log.e(LOG_TAG, e.toString());
		    		}
		    		
		        	try {
		    			country = responseObject.getJSONObject("result");
		    		} catch (JSONException e) {
		    			Log.e(LOG_TAG, e.toString());
		    		}
	        	}
	        	else
	        		Log.e(LOG_TAG, "Couldn't get response from Grooveshark service!");
	        }
	    }.execute(payload); 

    }

    public void SearchSong(String song_name, int limit)
    {
    	if (mediaPlayer != null)
    		StopPlaying();
    	
    	JSONObject header = new JSONObject();
    	JSONObject parameters = new JSONObject();
    	JSONObject payload = new JSONObject();

    	try {
    		header.put("wsKey", key);
    		header.put("sessionID", session_id);

    		parameters.put("query", song_name);
			parameters.put("country", country);
    		parameters.put("limit", limit);
    		
    		payload.put("method", "getSongSearchResults");
    		payload.put("header", header);
    		payload.put("parameters", parameters);
    		
    	} catch (JSONException e) {
    		Log.e(LOG_TAG, e.toString());
    	}
    	
		new Request() { 
	        protected void onPostExecute(String response) {
	    		SearchSongCallback(response);
	        }
	    }.execute(payload.toString()); 

    }
    
    private void SearchSongCallback(String s)
    {
    	JSONObject responseObject = null;
		try {
			responseObject = new JSONObject(s);
		} catch (JSONException e) {
			Log.e(LOG_TAG, e.toString());
		}
		
		try {
			// Get first song object from JSON response
			JSONObject song = responseObject.getJSONObject("result").getJSONArray("songs").getJSONObject(0);
			String songId = song.getString("SongID");
			
			// Get Streaming server for fetched song ID
			GetStreamServer(songId, true);
		} catch (JSONException e) {
			Log.d(LOG_TAG, responseObject.toString());
			Log.e(LOG_TAG, e.toString());
		}
    }

    public void GetStreamServer(final String song_id, boolean low_bitrate)
    {
    	JSONObject header = new JSONObject();
    	JSONObject parameters = new JSONObject();
    	JSONObject payload = new JSONObject();

    	try {
    		header.put("wsKey", key);
    		header.put("sessionID", session_id);

    		parameters.put("songID", song_id);
			parameters.put("country", country);
    		parameters.put("lowBitrate", low_bitrate);
    		
    		payload.put("method", "getStreamKeyStreamServer");
    		payload.put("header", header);
    		payload.put("parameters", parameters);

    	} catch (JSONException e) {
    		Log.e(LOG_TAG, e.toString());
    	}
    	
		new Request() { 
	        protected void onPostExecute(String response) {
	        	GetStreamServerCallback(response, song_id);
	        }
	    }.execute(payload.toString()); 
    	
    }
    
    private void GetStreamServerCallback(String response, String song_id)
    {
    	JSONObject result = null;    	
    	
    	if (response != null)
    	{
	    	try	{
	    		result = new JSONObject(response).getJSONObject("result");
	    		    		
	    		active_stream.put("songID", song_id);
	    		active_stream.put("StreamKey", result.getString("StreamKey"));
	    		active_stream.put("StreamServerID", result.getString("StreamServerID"));
	    		active_stream.put("url", result.getString("url"));
	    		active_stream.put("TimeElapsed", 0);
	    		active_stream.put("Acked", false);
	    		
	    	} catch (JSONException e) {
	    		Log.d(LOG_TAG, result.toString());
	    		Log.e(LOG_TAG, e.toString());
	    	}
	    	
	    	if (mediaPlayer == null)
	    		mediaPlayer = new MediaPlayer();
	    	
	    	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	    	mediaPlayer.setOnCompletionListener(this);
	    	mediaPlayer.setOnPreparedListener(this);
	    	
	    	try {
	    		mediaPlayer.setDataSource(active_stream.getString("url"));
	    		mediaPlayer.prepareAsync();
	    	} catch (IOException e) {
				Log.e(LOG_TAG, e.toString());
			} catch (JSONException e) {
				Log.e(LOG_TAG, e.toString());
			}
    	}
    	else
    		Log.e(LOG_TAG, "Couldn't get response from Grooveshark service!");
    }

    public void MarkStreamFinished()
    {
    	Log.i(LOG_TAG, "GroovesharkHandler: Marking stream finished");
    	//Log.d(LOG_TAG, active_stream.toString());
    	
    	JSONObject header = new JSONObject();
    	JSONObject parameters = new JSONObject();
    	JSONObject payload = new JSONObject();

    	try {
    		if (active_stream.getBoolean("Acked"))
    		{
	    		header.put("wsKey", key);
	    		header.put("sessionID", session_id);
	
	    		parameters.put("songID", active_stream.get("songID"));
				parameters.put("streamKey", active_stream.getString("StreamKey"));
	    		parameters.put("streamServerID", active_stream.get("StreamServerID"));
	    		
	    		payload.put("method", "markSongComplete");
	    		payload.put("header", header);
	    		payload.put("parameters", parameters);
	    		
				new Request() { 
					protected void onPostExecute(String response) {}
				}.execute(payload.toString()); 
    		}
    	} catch (JSONException e) {
    		Log.e(LOG_TAG, e.toString());
    	}
    	try {
    		active_stream.put("StreamIsPlaying", false);
    	} catch (JSONException e) {
    		Log.e(LOG_TAG, e.toString());
    	}
    }

    public void MarkStreamOver30s()
    {
        Log.i(LOG_TAG, "GroovesharkHandler: Marking stream duration >30s");
        //Log.d(LOG_TAG, active_stream.toString());
        
    	JSONObject header = new JSONObject();
    	JSONObject parameters = new JSONObject();
    	JSONObject payload = new JSONObject();
    	try {
            // Check if stream is not already acked and it is really active! If not don't mark its length!
    		if (!active_stream.getBoolean("Acked") && active_stream.getBoolean("StreamIsPlaying"))
    		{
	    		header.put("wsKey", key);
	    		header.put("sessionID", session_id);
	
				parameters.put("streamKey", active_stream.get("StreamKey"));
	    		parameters.put("streamServerID", active_stream.get("StreamServerID"));
	    		
	    		payload.put("method", "markStreamKeyOver30Secs");
	    		payload.put("header", header);
	    		payload.put("parameters", parameters);
	    		
	    		active_stream.put("Acked", true);

	    		new Request() { 
	    			protected void onPostExecute(String response) {
	    			}
	    		}.execute(payload.toString());
    		}
	    } catch (JSONException e) {
    		Log.e(LOG_TAG, e.toString());
    	}
    }

    
    public void StartPlaying(String song_name)
    {
		SearchSong(song_name, 1);
    }
    
    public void StopPlaying()
    {
    	if (mediaPlayer != null)
    	{
    		Log.d(LOG_TAG, "StopPlaying!");
    		mediaPlayer.stop();
    		mediaPlayer.release();
    		mediaPlayer = null;
    	}

    	MarkStreamFinished();
    }

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.d(LOG_TAG, "MediaPlayer signaled onCompletion");
		mediaPlayer.release();
		mediaPlayer = null;
		MarkStreamFinished();
	}
	
	@Override
	public void onPrepared(MediaPlayer mp)
	{
    	mediaPlayer.start();
    	try {
    		active_stream.put("StreamIsPlaying", true);
    	} catch (JSONException e) {
    		Log.e(LOG_TAG, e.toString());
    	}
    	
    	timer = new Timer();
    	timer.schedule(new TimerTask(){
    		@Override
    		public void run() {
    			MarkStreamOver30s();
    		}
    	}, 30*1000);
	}

}

