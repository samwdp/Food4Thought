package com.food4thought.test.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.food4thought.test.R;
import com.food4thought.test.constants.Constants;
import com.food4thought.test.databse.RestaurantDatabase;
import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantDatabaseModel;
import com.food4thought.test.ui.fragments.restuarantdetails.RestaurantDetailsFragment;
import com.food4thought.test.ui.fragments.restuarantdetails.ReviewFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

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

public class RestaurantViewActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ListView lvReview;
    private TextView nameText;
    private TextView formattedAddress;
    private TextView website;
    private TextView phone;
    private TextView mText;
    private Button favourites;
    private Button removeFavourites;
    private Button share;
    private ImageView mImageView;
    private RatingBar restaurantRating;
    private JSONObject restaurantDataObject;
    private String placeID;
    private String PLACES_DATA_REQUEST;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        placeID = Constants.reference;
        //new DisplayData().execute("");
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        PLACES_DATA_REQUEST = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
                + placeID
                + "&key="
                + "AIzaSyDxKcc0v8ePBXGkknLrMiivHQJsrK6oo6g";


        new GetPlaceData().execute(PLACES_DATA_REQUEST);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantDetailsFragment(), "Details");
        adapter.addFragment(new ReviewFragment(), "Reviews");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Adds the fragments for the Tabs
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Background task to get the JSON data from the Plcases API
     */
    public class GetPlaceData extends AsyncTask<String, String, RestaurantDataModel> {

        @Override
        protected RestaurantDataModel doInBackground(String... params) {
            HttpURLConnection con = null;
            BufferedReader reader = null;

            try {
                //Connection to the Places API
                URL url = new URL(params[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                InputStream stream = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                //gets the json as a string
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                //makes the returned string a JSONObject
                restaurantDataObject = new JSONObject(buffer.toString());
                JSONObject resultObject = restaurantDataObject.getJSONObject("result");

                //Puts json data into the RestaurantDataModel class
                Gson gson = new Gson();
                RestaurantDataModel restaurantDataModel = gson.fromJson(resultObject.toString(), RestaurantDataModel.class);
                return restaurantDataModel;
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
        protected void onPostExecute(RestaurantDataModel restaurantDataModel) {
            super.onPostExecute(restaurantDataModel);
            if (restaurantDataModel != null) {
                List<RestaurantDataModel.Reviews> reviewsList = restaurantDataModel.getReviews();

                final RestaurantDatabaseModel restaurantDatabaseModel = new RestaurantDatabaseModel();
                restaurantDatabaseModel.setId(restaurantDataModel.getId());
                restaurantDatabaseModel.setPlaceId(restaurantDataModel.getPlaceId());
                restaurantDatabaseModel.setName(restaurantDataModel.getName());
                restaurantDatabaseModel.setRating(restaurantDataModel.getRating());
                restaurantDatabaseModel.setReference(restaurantDataModel.getReference());

                Constants.reviewsList = reviewsList;

                //Text on the details fragment
                nameText = (TextView) findViewById(R.id.tvTitle);
                restaurantRating = (RatingBar) findViewById(R.id.rbRestaurant);
                formattedAddress = (TextView) findViewById(R.id.tvAddress);
                phone = (TextView) findViewById(R.id.tvPhone);
                website = (TextView) findViewById(R.id.tvQWebsite);
                mImageView = (ImageView) findViewById(R.id.ivImage);
                mText = (TextView) findViewById(R.id.tvPhoto);
                favourites = (Button) findViewById(R.id.btnFavourites);
                removeFavourites = (Button) findViewById(R.id.btnRemoveFavourites);
                share = (Button) findViewById(R.id.btnShare);


                //Sets the text on the details fragment
                nameText.setText(restaurantDataModel.getName());
                restaurantRating.setRating(restaurantDataModel.getRating());
                formattedAddress.setText(restaurantDataModel.getFormattedAddress());
                phone.setText(restaurantDataModel.getFormattedPhoneNumber());

                //gets the photos from the Places Photo API

                Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeID).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
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
                                            mImageView.setImageBitmap(placePhotoResult.getBitmap());
                                            Log.i(TAG, "Photo " + photoDetail + " loaded");
                                        } else {
                                            Log.e(TAG, "Photo " + photoDetail + " failed to load");
                                        }
                                    }
                                });

                                photoMetadata.release();
                            } else {
                                mText.setText("No photos found");
                            }
                        } else {

                            Log.e(TAG, "No photos returned");
                        }
                    }
                });

                website.setText(restaurantDataModel.getWebsite());
                favourites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            RestaurantDatabase r = Constants.database;
                            r.addRestaurant(restaurantDatabaseModel);
                            Context context = getApplicationContext();
                            CharSequence text = "Added to favoutites";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                removeFavourites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RestaurantDatabase r = Constants.database;
                        r.removeRestuarant(restaurantDatabaseModel);
                        Context context = getApplicationContext();
                        CharSequence text = "Removed from favoutites";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Look at this restaurant: " + restaurantDatabaseModel.getName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                });

                //Sets the text on the Reviews fragment
                lvReview = (ListView) findViewById(R.id.lvReview);
                ReviewAdapter adapter = new ReviewAdapter(getApplicationContext(), R.layout.review_row, reviewsList);
                lvReview.setAdapter(adapter);
            }
        }
    }

    /**
     * Kills the activity on back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /**
     * Ends the activity when restarted
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Intent intent = new Intent(RestaurantViewActivity.this, RestaurantViewActivity.class);
        this.finish();
        startActivity(intent);
    }

    /**
     * Models the list for the reviews
     */
    public class ReviewAdapter extends ArrayAdapter {

        private List<RestaurantDataModel.Reviews> restaurantDataModels;
        private int resource;
        private LayoutInflater inflater;

        public ReviewAdapter(Context context, int resource, List<RestaurantDataModel.Reviews> objects) {
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
                holder.tvUser = (TextView) convertView.findViewById(R.id.tvUser);
                holder.rating = (RatingBar) convertView.findViewById(R.id.rbRest);
                holder.tvReviewText = (TextView) convertView.findViewById(R.id.tvReviewText);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            RestaurantDataModel.Reviews r = restaurantDataModels.get(position);

            //setting the text of the row
            holder.tvUser.setText(r.getAuthorName());
            holder.rating.setRating(r.getRating());
            holder.tvReviewText.setText(r.getText());
            return convertView;
        }

        class ViewHolder {
            private TextView tvUser;
            private TextView tvReviewText;
            private RatingBar rating;
        }
    }
}
