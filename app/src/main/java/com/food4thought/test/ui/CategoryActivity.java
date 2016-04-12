package com.food4thought.test.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.food4thought.test.R;
import com.food4thought.test.constants.Constants;
import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantDatabaseModel;
import com.food4thought.test.model.RestaurantModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends DrawerActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ArrayList<String> list = new ArrayList<>();
    private Spinner spinner;
    private Button addButton;
    private ArrayAdapter<String> adapter;
    private ListView lvCategories;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLatitudeText;
    private double mLongitudeText;
    private String request;
    private String type;
    private static final String TAG = "MyActivity";
    private LocationManager locMan;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_category, null, false);
        frameLayout.addView(activityView);

        String[] array = getResources().getStringArray(R.array.category_array);
        for(String s : array){
            list.add(s);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        //Gets the location of the user
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }
        getLocation();

        //Set the spinner
        spinner = (Spinner) findViewById(R.id.spinner);

        //ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
                request = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + mLatitudeText
                        + ","
                        + mLongitudeText
                        + "&radius=5000&type="
                        + type.toLowerCase()
                        + "&key=AIzaSyDH4Jl_wgyCJeuI1pkPFRj9Q0He8ZR2IxE";
                Log.w(TAG, request);
                new GetSearchData().execute(request);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //button
        addButton = (Button) findViewById(R.id.btnAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText txtItem = (EditText) findViewById(R.id.txtItem);
                        list.add(txtItem.getText().toString());
                        txtItem.setText("");
                        adapter.notifyDataSetChanged();
                    }
                });
        //set the list view for items to be added
        lvCategories = (ListView) findViewById(R.id.lvCategoryList);

    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void getLocation() {
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
        mLatitudeText = lastLoc.getLatitude();
        mLongitudeText = lastLoc.getLongitude();
        Log.w(TAG, "Got location");
    }


    private class GetSearchData extends AsyncTask<String, Void, ArrayList<RestaurantDataModel>> {
        @Override
        protected ArrayList<RestaurantDataModel> doInBackground(String... placesURL) {
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

                    ArrayList<RestaurantDataModel> restaurantModels = new ArrayList<>();

                    Gson gson = new Gson();

                    for (int i = 0; i < restaurantArray.length(); i++) {
                        JSONObject object = restaurantArray.getJSONObject(i);

                        RestaurantDataModel restaurantModel = gson.fromJson(object.toString(), RestaurantDataModel.class);

                        restaurantModels.add(restaurantModel);

                    }
                    //Constants.restaurantModelList = restaurantModels;
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
        protected void onPostExecute(ArrayList<RestaurantDataModel> result) {

            lvCategories = (ListView) findViewById(R.id.lvCategoryList);
            CategoriesAdapter c = new CategoriesAdapter(getApplicationContext(), R.layout.favourite_row, result);
            //Log.w(TAG, result.get(0).getName());
            lvCategories.setAdapter(c);

        }
    }

    public class CategoriesAdapter extends ArrayAdapter {

        private ArrayList<RestaurantDataModel> restaurantDataModels;
        private int resource;
        private LayoutInflater inflater;

        public CategoriesAdapter(Context context, int resource, ArrayList<RestaurantDataModel> objects) {
            super(context, resource, objects);
            restaurantDataModels = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.text = (TextView) convertView.findViewById(R.id.tvRestTitle);
                holder.ibFavourite = (ImageButton) convertView.findViewById(R.id.ibButton);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final RestaurantDataModel r = restaurantDataModels.get(position);

            //setting the text of the row
            holder.text.setText(r.getName());

            final ViewHolder finalHolder = holder;
            Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, r.getPlaceId()).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                @Override
                public void onResult(PlacePhotoMetadataResult placePhotoMetadataResult) {
                    if (placePhotoMetadataResult.getStatus().isSuccess()) {
                        PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();
                        int photoCount = photoMetadata.getCount();

                        if (photoCount > 1) {
                            PlacePhotoMetadata placePhotoMetadata = photoMetadata.get(0);
                            final String photoDetail = placePhotoMetadata.toString();
                            placePhotoMetadata.getScaledPhoto(mGoogleApiClient, 500, 500).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                @Override
                                public void onResult(PlacePhotoResult placePhotoResult) {
                                    if (placePhotoResult.getStatus().isSuccess()) {
                                        finalHolder.ibFavourite.setImageBitmap(placePhotoResult.getBitmap());

                                        finalHolder.ibFavourite.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Constants.reference = r.getPlaceId();
                                                Intent intent = new Intent(CategoryActivity.this, RestaurantViewActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                        Log.w(TAG, "Photo " + photoDetail + " loaded");
                                    } else {
                                        Log.w(TAG, "Photo " + photoDetail + " failed to load");
                                    }
                                }
                            });

                            photoMetadata.release();
                        } else {
                            finalHolder.ibFavourite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Constants.reference = r.getPlaceId();
                                    Log.w("JSON", r.getName());
                                    Intent intent = new Intent(CategoryActivity.this, RestaurantViewActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                            Log.w(TAG, "No photos returned");
                        }
                    } else {
                        Log.w(TAG, "No photos returned");
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView text;
            private ImageButton ibFavourite;
        }
    }
}

