package fi.cie.chiru.servicefusionar.serviceApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import util.Log;

import android.os.AsyncTask;

public class XmlDownloader extends AsyncTask<String, Void, String>
{
	private static String LOG_TAG = "XmlDownloader";

    @Override
    protected String doInBackground(String... urls) 
    {
    	String xmldata = null;
    	try {
    		xmldata = loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
        	Log.d(LOG_TAG, e.toString());
        }
    	return xmldata;
    }
	
	private String loadXmlFromNetwork(String urlString) throws IOException
	{
    	InputStream stream = null;

        try 
        {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            stream = conn.getInputStream();
            
            InputStreamReader is = new InputStreamReader(stream);
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while(read != null) 
            {
                sb.append(read);
                read =br.readLine();
            }
            return sb.toString();
        }
        
        // Makes sure that the InputStream is closed after the app is finished using it.
        finally
        {
            if (stream != null)
            {
                stream.close();
                stream = null;
            }
        }
    }

}
