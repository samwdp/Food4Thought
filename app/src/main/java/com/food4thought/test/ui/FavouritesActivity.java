package com.food4thought.test.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.carrier.CarrierMessagingService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.food4thought.test.R;
import com.food4thought.test.constants.Constants;
import com.food4thought.test.databse.RestaurantDatabase;
import com.food4thought.test.model.RestaurantDatabaseModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

public class FavouritesActivity extends DrawerActivity implements GoogleApiClient.OnConnectionFailedListener {


    private ListView listView;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // don’t set any content view here, since its already set in DrawerActivity
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_frame);
        // inflate the custom activity layout
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_favourites, null,false);
        // add the custom layout of this activity to frame layout.
        frameLayout.addView(activityView);
        // now you can do all your other stuffs


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        new GetArrayFromDatabase().execute();


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class GetArrayFromDatabase extends AsyncTask<Void, Void, ArrayList<RestaurantDatabaseModel>>{

        @Override
        protected ArrayList<RestaurantDatabaseModel> doInBackground(Void... params) {
            RestaurantDatabase r = Constants.database;
            ArrayList<RestaurantDatabaseModel> list = r.getAllRestaurant();
            if(list == null){
                Log.w("JSON", "nothing in list");
            }else{
                return list;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<RestaurantDatabaseModel> list){
            super.onPostExecute(list);
            listView = (ListView) findViewById(R.id.lvFavourites);
            FavouritesAdapter f = new FavouritesAdapter(getApplicationContext(), R.layout.favourite_row, list);
            listView.setAdapter(f);
        }
    }

    public class FavouritesAdapter extends ArrayAdapter {

        private ArrayList<RestaurantDatabaseModel> restaurantDataModels;
        private int resource;
        private LayoutInflater inflater;

        public FavouritesAdapter(Context context, int resource, ArrayList<RestaurantDatabaseModel> objects) {
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
            final RestaurantDatabaseModel r = restaurantDataModels.get(position);

            //setting the text of the row
            holder.text.setText(r.getName());

            final ViewHolder finalHolder = holder;
            Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, r.getPlaceId()).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                @Override
                public void onResult(PlacePhotoMetadataResult placePhotoMetadataResult) {
                    if (placePhotoMetadataResult.getStatus().isSuccess()) {
                        PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();
                        int photoCount = photoMetadata.getCount();

                        if(photoCount > 1){
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
                                                Intent intent = new Intent(FavouritesActivity.this, RestaurantViewActivity.class);
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
                        } else{
                            finalHolder.ibFavourite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Constants.reference = r.getPlaceId();
                                    Log.w("JSON", r.getName());
                                    Intent intent = new Intent(FavouritesActivity.this, RestaurantViewActivity.class);
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
