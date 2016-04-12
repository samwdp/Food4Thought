package com.food4thought.test.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.food4thought.test.constants.Constants;
import com.food4thought.test.model.RestaurantModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends DrawerActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locMan;
    private Marker userMarker;
    private Marker restaurantMarker;
    private ArrayList<Marker> markerArrayList;
    private double userLat;
    private double userLng;
    private String PLACES_SEARCH;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_maps, null, false);
        frameLayout.addView(activityView);
        markerArrayList = new ArrayList<Marker>();
        /*if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.the_map));
        }*/
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.the_map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        String s = Constants.reference;
        Constants.reference = null;
        updatePlaces();

        if (s != null) {
            getPlaceFromSearch(s);
            Constants.reference = null;
        } else {
            getPlaces();
            Constants.reference = null;
        }
        Constants.reference = null;
    }

    /**
     * Starts the async task to retrieve data from the places API
     */
    public void getPlaces() {
        PLACES_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?key=AIzaSyDH4Jl_wgyCJeuI1pkPFRj9Q0He8ZR2IxE" +
                "&location=" + userLat + "," + userLng +
                "&radius=2000&sensor=true" +
                "&types=bar|cafe|restaurant";

        Log.w(TAG, PLACES_SEARCH);

        GetPlaces placesTask = new GetPlaces();
        placesTask.execute(PLACES_SEARCH);
        Log.w("JSON", PLACES_SEARCH);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Gets the last known location of the user
     */
    private void updatePlaces() {
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

        if (userMarker != null) userMarker.remove();

        userMarker = map.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .snippet("Your last recorded location"));
        map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 150, null);


    }

    public void getPlaceFromSearch(String id) {
        new GetPlaceFromSearch().execute(id);

    }

    private class GetPlaces extends AsyncTask<String, Void, List<RestaurantModel>> {
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

                if (status == 200) {
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }


                    JSONObject restaurantObject = new JSONObject(buffer.toString());
                    JSONArray restaurantArray = restaurantObject.getJSONArray("results");

                    List<RestaurantModel> restaurantModels = new ArrayList<>();

                    Gson gson = new Gson();

                    for (int i = 0; i < restaurantArray.length(); i++) {
                        JSONObject object = restaurantArray.getJSONObject(i);

                        RestaurantModel restaurantModel = gson.fromJson(object.toString(), RestaurantModel.class);

                        restaurantModels.add(restaurantModel);

                    }
                    Constants.restaurantModelList = restaurantModels;
                    return restaurantModels;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<RestaurantModel> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (map != null) {
                    try {
                        for (final RestaurantModel restaurantModel : result) {
                            double lat = restaurantModel.getGeometry().getLocation().getLat();
                            double lng = restaurantModel.getGeometry().getLocation().getLng();
                            LatLng l = new LatLng(lat, lng);
                            restaurantMarker = map.addMarker(new MarkerOptions()
                                    .position(l).title(restaurantModel.getName())
                                    .snippet("Rating : " + Double.toString(restaurantModel.getRating()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_black_24dp)));
                            markerArrayList.add(restaurantMarker);
                            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    for (RestaurantModel r : result) {
                                        if (r.getName().equals(marker.getTitle())) {
                                            Constants.reference = r.getPlace_id();
                                            Intent intent = new Intent(MapsActivity.this, RestaurantViewActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                    return false;
                                }
                            });
                            /*map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                @Override
                                public void onMapLongClick(LatLng latLng) {
                                    ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
                                    for (Marker marker : markerArrayList) {
                                        if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
                                            Toast.makeText(MapsActivity.this, "got clicked", Toast.LENGTH_SHORT).show();
                                            for (RestaurantModel r : result) {
                                                double lat = r.getGeometry().getLocation().getLat();
                                                double lng = r.getGeometry().getLocation().getLng();
                                                LatLng l = new LatLng(lat, lng);
                                                if (marker.getPosition().equals(l)) {
                                                    Log.w(TAG, "Here");
                                                    Constants.reference = r.getPlace_id();
                                                    Intent intent = new Intent(MapsActivity.this, RestaurantViewActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            }
                                            *//*for(LatLng la : latLngArrayList){

                                            }*//*

                                            break;
                                        }
                                    }
                                }
                            });*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class GetPlaceFromSearch extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            Places.GeoDataApi.getPlaceById(mGoogleApiClient, params[0])
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                final Place myPlace = places.get(0);

                                if (map != null) {
                                    try {
                                        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_black_24dp);
                                        LatLng l = myPlace.getLatLng();
                                        CharSequence c = myPlace.getName();
                                        Log.w("JSON", myPlace.getId());
                                        restaurantMarker = map.addMarker(new MarkerOptions()
                                                .position(l)
                                                .title(c.toString())
                                                .snippet("Rating : " + Double.toString(myPlace.getRating()))
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_black_24dp)));
                                        map.animateCamera(CameraUpdateFactory.newLatLng(l), 150, null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.i(TAG, "Place found: " + myPlace.getName());
                            } else {
                                Log.e(TAG, "Place not found");
                            }
                            places.release();
                        }
                    });

            return null;
        }
    }
}
