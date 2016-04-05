package com.food4thought.test.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import com.food4thought.test.R;
import com.food4thought.test.model.RestaurantModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends DrawerActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locMan;
    private Marker userMarker;
    private List<RestaurantModel> restaurantModelList;
    private double userLat;
    private double userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_maps, null, false);
        frameLayout.addView(activityView);
        if(mMap==null){
            mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.the_map)).getMap();
        }

        if(mMap != null){
            //ok - proceed
        }


        updatePlaces();
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+userLat+","+userLng+
                "&radius=2000&sensor=true" +
                "&types=food|bar|cafe"+
                "&key=AIzaSyDBpCptRbYGtwgp5u2atRWLU2d4J8adYl0";

        GetPlaces placesTask = new GetPlaces();
        placesTask.execute(placesSearchStr);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Gets the last known location of the user
     */
    private void updatePlaces(){
        locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        userLat = lastLoc.getLatitude();
        userLng = lastLoc.getLongitude();

        LatLng lastLatLng = new LatLng(userLat, userLng);

        if(userMarker!=null) userMarker.remove();

        userMarker = mMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .snippet("Your last recorded location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 150, null);



    }

    private class GetPlaces extends AsyncTask<String, Void, List<RestaurantModel>>
    {
        @Override
        protected List<RestaurantModel> doInBackground(String... placesURL) {
            HttpURLConnection con = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(placesURL[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                InputStream stream = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                int status = con.getResponseCode();

               if(status == 200){
                        while ((line = reader.readLine()) != null){
                            buffer.append(line);
                            //Log.w("JSON", line);
                        }

                        JSONObject restaurantObject = new JSONObject(buffer.toString());
                        JSONArray restaurantArray = restaurantObject.getJSONArray("results");

                        List<RestaurantModel> restaurantModels = new ArrayList<>();

                        Gson gson = new Gson();

                        for(int i = 0; i<restaurantArray.length(); i++){
                            JSONObject object = restaurantArray.getJSONObject(i);
                            //Log.w("JSON", object.toString());
                            RestaurantModel restaurantModel = gson.fromJson(object.toString(), RestaurantModel.class);

                            restaurantModels.add(restaurantModel);
                            //Log.w("JSON", Double.toString(restaurantModel.getGeometry().get(0).getLocations().get(0).getLat()));

                        }
                        Log.w("JSON", "finished modelling");
                        return restaurantModels;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if(con != null) {
                    con.disconnect();
                    Log.w("JSON", "con disconnect");
                }
                if(reader != null){
                    try {
                        reader.close();
                        Log.w("JSON", "reader disconnect");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<RestaurantModel> result){
            super.onPostExecute(result);
            Log.w("JSON", "on post execute");
            if(result!=null)
            {
                if(mMap != null) {
                    try {
                        Log.w("JSON", "result");
                        Log.w("JSON", Integer.toString(result.size()));
                        for (RestaurantModel restaurantModel : result) {
                            Log.w("JSON", restaurantModel.getName());
                            Log.w("JSON", restaurantModel.getName());

                            MarkerOptions markerOptions = new MarkerOptions();

                            double lat = restaurantModel.getGeometry().getLocation().getLat();
                            Log.w("JSON", Double.toString(lat));
                            double lng = restaurantModel.getGeometry().getLocation().getLng();
                            LatLng l = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(l).title(restaurantModel.getName()).snippet(Boolean.toString(restaurantModel.getOpeningHours().isOpen_now())));

                            Log.w("JSON", "added to map");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}