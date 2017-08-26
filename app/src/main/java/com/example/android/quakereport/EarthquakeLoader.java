package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
        //Log.d("earthquakel constructor","earthquakel constructor");
    }

    protected void onStartLoading(){
        forceLoad();
        //Log.d("onstartloading","onstatrtloading cqalled");
    }

    public List<Earthquake> loadInBackground(){
        if(earthquakeUrl == null)
            return null;

        //Log.d("loadiinbackground ","loadibackground ");

        String jsonResponse = "";
        //Create URL using the query string USGS_REQUEST
        URL url = EarthquakeActivity.createURL(earthquakeUrl);

        try{
            //Query API with the created URL and store the result in string
            jsonResponse = EarthquakeActivity.makeHttpRequest(url);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //return list of earthquakes using the string jsonResponse
        return QueryUtils.extractEarthquakes(jsonResponse);
    }
}
