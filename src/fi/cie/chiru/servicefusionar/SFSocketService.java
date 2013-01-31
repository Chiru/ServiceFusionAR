package fi.cie.chiru.servicefusionar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SFSocketService implements Runnable 
{
	private static final String TAG = "SFSocketService";

    // designate a port
    public static final int SERVERPORT = 4242;

    private Handler handler;
    private ServerSocket serverSocket;

	int serverPort = 4242;
	String serverIp = "localhost";
	
	public SFSocketService(Handler handler, ServerSocket socket)
	{
		serverSocket = socket;
		this.handler = handler;
	}
	
	public void run() 
	{
		try 
		{
            //serverSocket = new ServerSocket(serverPort);
            while (true) {
                // listen for incoming clients
                Socket client = serverSocket.accept();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //serverStatus.setText("Connected.");
                    }
                });

                String line = null;
                try 
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    
                    while ((line = in.readLine()) != null)
                    {
                    	final String strReceived = line;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                            	// do whatever you want to the front end
                                // this is where you can be creative

                            	JSONObject jsonObj = null;
								try {
									//jsonObj = new JSONArray(strReceived);
									jsonObj = new JSONObject(strReceived);
									Log.d(TAG, "jsonObj: " + jsonObj.toString());
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								JSONArray entries = null;
                                try {
                                	
                                	entries = jsonObj.getJSONArray("entries");
									Log.d(TAG, "Entries: " + entries.toString());
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

                            }
                        });

                    }
                } 
                catch (Exception e) 
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                        }
                    });
                    e.printStackTrace();
                }

            }
             
			
			
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //serverStatus.setText("Error");
                }
            });
            e.printStackTrace();
        }
    
    }
	
	public void stopSocket()
	{
		try
		{
			Log.d(TAG, "Closing server socket!");
			serverSocket.close();
		}
		catch (IOException e)
		{
			Log.d(TAG, e.toString());
		}
	}

}

