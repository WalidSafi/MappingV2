package com.example.mappingtest;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;

import java.net.*;
import java.io.*;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Arraylists to hold information (We can change some of these to an SQLitedatabase if needed
    // The serverResponse can be saved to a database, this will include names list aswell
    ArrayList<LatLng> markerLocations = new ArrayList<LatLng>();
    ArrayList<String> variables = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> serverResponse = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Clients variables to send to the sever, Changed this when testing with different emulators for testing
        variables.add("44.146334");
        variables.add("-72.541");
        variables.add("Location One");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Initalizing holding variables
        String latHolder = null;
        String longHolder = null;

        // Loops through the server response list and creates a marker object
        for (int i = 0; i < serverResponse.size(); i = i + 3) {

           latHolder = serverResponse.get(i);
           longHolder = serverResponse.get(i+1);
           names.add(serverResponse.get(i+2));

            double lt = Double.parseDouble(latHolder);
            double lg = Double.parseDouble(longHolder);
            LatLng marker = new LatLng(lt, lg);
            markerLocations.add(marker);
        }

        //System.out.println(names);

        // Loops though the marker objects array and creates a marker
        // names list matches the coordinates since they were added at the same indexes of both arrays
        for (int i=0; i<markerLocations.size(); i++) {
            // Adds marker
            mMap.addMarker(new MarkerOptions().position(markerLocations.get(i)).title(names.get(i)));
            // Moves camera to the area of its marker
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocations.get(i)));
        }

        System.out.println("Testing Data from server: " + serverResponse);
        // Clearing the arrays so it can get updated using the coonect and refresh buttons
        markerLocations.clear();
        names.clear();
    }

    public void refresh(View v) {

        // Calls the onMapReady callback to display the markers
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void buttonTest(View v) {
        // A thread to make a connection with the server and the information needed
        // We can call this function periodically without a button needed
        Thread conThread = new Thread(new connServerThread());
        conThread.start();
    }

    // The thread for getting input,
    // I dont think we could get a connection on the oncreate so i had to watch a youtube video
    class connServerThread implements Runnable
    {

        @Override
        public void run(){

            try
            {
                // 10.0.2.2 is localhost for an emulator
                Socket s = new Socket("10.0.2.2", 5556);

                // Creating input and output streams
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream inp = new ObjectInputStream(s.getInputStream());
                // sending the clients variables to the server
                oos.writeObject(variables);

                // Read the server response of all the known locations from past clients
                // We could most likely time stamp the markers just adding a date class and adding it to the names part of the marker
                Object list = inp.readObject();
                // Save the list to the global variable, and the device is ready to press refresh to get locations of the other clients
                serverResponse = (ArrayList<String>) list;

            } catch (ClassNotFoundException e){

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}