/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.Finnkino;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class FinnkinoXmlParser {
	private static final String ns = null;
	private static final String LOG_TAG = "FinnkinoXmlParser";

    public List<Movie> parse(String input){
    	List<Movie> movies = null;
    	try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(input));
            parser.nextTag();
            movies = readFeed(parser);
            
        } catch (XmlPullParserException e) {
        	Log.e(LOG_TAG, e.toString());
        } catch (IOException e) {
        	Log.e(LOG_TAG, e.toString());
		}
        return movies;
    }

    private List<Movie> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Movie> entries = null;
        parser.require(XmlPullParser.START_TAG, ns, "Schedule");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the show tag
            if (name.equals("Shows")) {
                entries = readShows(parser);
                //Log.d(LOG_TAG, name);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // This class represents a single entry (movie) in the XML feed.
    // It includes the data members "time," "title," and "auditorium."
	public static class Movie implements Comparable<Movie>
	{
        public final String time;
        public final String title;
        public final String auditorium;

        private Movie(String time, String title, String auditorium)
        {
            this.time = time;
            this.title = title;
            this.auditorium = auditorium;
            //Log.d(LOG_TAG, "time: " + time + " title: " + title + " auditorium: " + auditorium);
        }

		@Override
		public int compareTo(Movie another) {
			return this.time.compareTo(another.time);
		}
    }

    // Parses the contents of an entry. If it encounters a time, title, or auditorium tag, hands them off
    // to their respective read methods for processing. Otherwise, skips the tag.
    private List<Movie> readShows(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Shows");
        List<Movie> entries = new ArrayList<Movie>();
        
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Show"))
            {
            	entries.add(readShow(parser));
            }            
             else {
                skip(parser);
            }
        }
        return entries;
    }
    
    private Movie readShow(XmlPullParser parser) throws IOException, XmlPullParserException 
    {
        String time = null;
        String title = null;
        String auditorium = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
	    	if (name.equals("dttmShowStart")) {
	            time = readTime(parser);
	        } else if (name.equals("Title")) {
	            title = readTitle(parser);
	        } else if (name.equals("TheatreAuditorium")) {
	            auditorium = readAuditorium(parser);
	        }
            else
            {
                skip(parser);
            }
        }
    	return new Movie(time, title, auditorium);    	
    }

    // Processes time tags in the feed.
    private String readTime(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "dttmShowStart");
        String time = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "dttmShowStart");
        return time;
    }

    // Processes auditorium tags in the feed.
    private String readAuditorium(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "TheatreAuditorium");
        String auditorium = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "TheatreAuditorium");
        return auditorium;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Title");
        return title;
    }

    // For the tags title and auditorium, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
