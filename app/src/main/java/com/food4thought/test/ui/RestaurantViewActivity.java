package com.food4thought.test.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.food4thought.test.R;
import com.food4thought.test.constants.Constants;
import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantModel;
import com.food4thought.test.ui.fragments.RestaurantDetailsFragment;
import com.food4thought.test.ui.fragments.ReviewFragment;
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

public class RestaurantViewActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ListView lvReview;
    private TextView nameText;
    private TextView formattedAddress;
    private TextView website;
    private TextView phone;
    private RatingBar restaurantRating;
    private JSONObject restaurantDataObject;
    private String reference;
    private String PLACES_DATA_REQUEST;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        lvReview = (ListView) findViewById(R.id.lvReview);
        reference = Constants.reference;
        //new DisplayData().execute("");
        PLACES_DATA_REQUEST = "https://maps.googleapis.com/maps/api/place/details/json?placeid="
                + reference
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
                Constants.reviewsList = reviewsList;

                //Text on the details fragment
                nameText = (TextView) findViewById(R.id.tvTitle);
                restaurantRating = (RatingBar) findViewById(R.id.rbRestauarant);
                formattedAddress = (TextView) findViewById(R.id.tvAddress);
                phone = (TextView) findViewById(R.id.tvPhone);
                website = (TextView) findViewById(R.id.tvQWebsite);

                nameText.setText(restaurantDataModel.getName());
                restaurantRating.setRating(restaurantDataModel.getRating());
                formattedAddress.setText(restaurantDataModel.getFormattedAddress());
                phone.setText(restaurantDataModel.getFormattedPhoneNumber());
                website.setText(restaurantDataModel.getWebsite());

                //Reviews
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
            Log.w("JSON", r.getAuthorName());
            Log.w("JSON", holder.tvReviewText.toString());

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
