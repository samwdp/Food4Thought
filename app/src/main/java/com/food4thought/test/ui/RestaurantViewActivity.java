package com.food4thought.test.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.food4thought.test.R;
import com.food4thought.test.constants.Constants;
import com.food4thought.test.model.RestaurantModel;
import com.food4thought.test.ui.fragments.RestaurantDetailsFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RestaurantViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView nameText;
    private ImageView image;

    public RestaurantViewActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        new DisplayData().execute("");

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantDetailsFragment(), "Details");
        viewPager.setAdapter(adapter);
    }

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

    public class DisplayData extends AsyncTask<String, String, List<RestaurantModel>> {

        @Override
        protected List<RestaurantModel> doInBackground(String... params) {
            List<RestaurantModel> list = Constants.restaurantModelList;
            return list;
        }

        @Override
        protected void onPostExecute(List<RestaurantModel> list){
            super.onPostExecute(list);
            nameText = (TextView) findViewById(R.id.name);


            String s = list.get(0).getName();
            nameText.setText(s);
            /*Log.w("JSON", list.get(0).getPhotos().get(0).getPhotoReference());
            image.setImageBitmap(bMap);*/
            String images = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+list.get(0).getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDxKcc0v8ePBXGkknLrMiivHQJsrK6oo6g";
            new GetImage().execute(images);
        }

    }

    public class GetImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.connect();
                InputStream is = con.getInputStream();
                Bitmap bMap = BitmapFactory.decodeStream(is);
                return bMap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute (Bitmap bMap){
            super.onPostExecute(bMap);
            image = (ImageView) findViewById(R.id.image);
            //image.setImageBitmap(bMap);
        }
    }
}
