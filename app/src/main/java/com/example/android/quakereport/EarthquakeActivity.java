/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>>{

    public static final String USGS_REQUEST = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=5";
    public static final int EARTHQUAKE_LOADER_ID = 1;
    private EarthquakeAdapter adapter;
    private TextView emptyEarthquakeView;
    private ProgressBar loadingSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //Setting the loading spinner and displaying it
        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        //Create emptyView in case no search results are found
        emptyEarthquakeView = (TextView) findViewById(R.id.empty_view);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes the list of earthquakes as input
        adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setEmptyView(emptyEarthquakeView);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //If Device is connected to internet then query the API
        if(isNetworkConnected()){
            //Initialize loader manager
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID,null,this);

            //Log.d("initLoader","initLoader called");
        }

        //Device is not connected to internet
        else{
            //Disable the loading spinner
            loadingSpinner.setVisibility(View.GONE);
            //Set the message to be displayed
            emptyEarthquakeView.setText(R.string.no_internet_connection);
        }

    }

    public Loader<List<Earthquake>> onCreateLoader(int i,Bundle bundle){
        adapter.clear();
        //Log.d("CreateLoader","onCreateLoader called");
        return new EarthquakeLoader(this, USGS_REQUEST);
    }

    public void onLoadFinished(Loader<List<Earthquake>> loader,List<Earthquake> earthquakes){

        //Removing the loading spinner as the network request has completed
        loadingSpinner.setVisibility(View.GONE);
        //Log.d("onLoadFinished","onLoadFinished called");

        //Set the message to be displayed when no earthquake is found for the user query
        emptyEarthquakeView.setText(R.string.no_earthquake);
        adapter.clear();

        //Update UI with the list of earthquakes received
        if(earthquakes != null && !earthquakes.isEmpty())
            updateUI(earthquakes);
    }

    public void onLoaderReset(Loader<List<Earthquake>> loader){
        adapter.clear();
        //Log.d("onLodaerREset","onloaderReset called");
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.settings){
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    protected static URL createURL(String request){
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

    protected static String makeHttpRequest(URL url) throws IOException{

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

    protected static String readFromStream(InputStream inputStream) throws IOException {
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

    protected void updateUI(List<Earthquake> earthquakes){
        adapter.addAll(earthquakes);
    }

    //Method to check whether the device is connected to internet or not
    protected boolean isNetworkConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return(activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }
}