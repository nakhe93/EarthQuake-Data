package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by adhiraj on 8/24/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    private String earthquakeUrl;

    public EarthquakeLoader(Context context, String url){
        super(context);
        this.earthquakeUrl = url;
    }

    protected void onStartLoading(){
        forceLoad();
    }

    public List<Earthquake> loadInBackground(){
        if(earthquakeUrl == null)
            return null;

        String jsonResponse = "";
        //Create URL using the query string USGS_REQUEST
        URL url = EarthquakeActivity.createURL(earthquakeUrl);

        try{
            jsonResponse = EarthquakeActivity.makeHttpRequest(url);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //return list of earthquakes using the string jsonResponse
        return QueryUtils.extractEarthquakes(jsonResponse);
    }
/*
    protected URL createURL(String request){
        URL url = null;
        //Create URL
        try{
            url = new URL(request);
        }
        catch(MalformedURLException m){
            m.printStackTrace();
        }
        return url;
    }

    protected String makeHttpRequest(URL url) throws IOException{

        String jsonResponse = "";
        if(url == null)
            return jsonResponse;

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        //Make HTTP connection with the URL given in argument
        //and store the data received from server in a string
        try{
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.connect();
            inputStream = connection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        }
        finally {
            if(connection != null)
                connection.disconnect();
            if(inputStream != null)
                inputStream.close();
        }

        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

*/
}
