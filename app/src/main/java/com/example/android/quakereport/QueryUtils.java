package com.example.android.quakereport;

/**
 * Created by adhiraj on 7/28/2017.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /*
     * Create a private constructor because no one should ever create a QueryUtils object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of  Earthquake objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractEarthquakes(String jsonResponse) {
        /*
        try{
            Thread.sleep(2000);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }*/

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        if(jsonResponse == null || jsonResponse == null){
            Log.d(QueryUtils.class.getName(),"Empty jsonREsponse");
            return earthquakes;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
   
        try {

            // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            // Extract the JSONArray associated with the key called "features",
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < earthquakeArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties",
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract the value for the key called "mag"
                double magnitude = properties.getDouble("mag");

                // Extract the value for the key called "place"
                String location = properties.getString("place");

                // Extract the value for the key called "time"
                long time = properties.getLong("time");

                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // Create a new  Earthquake object with the magnitude, location, time,
                // and url from the JSON response.
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);

                // Add the new  Earthquake to the list of earthquakes.
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            return earthquakes;
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}