package com.food4thought.test.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.food4thought.test.R;
import com.food4thought.test.constants.Constants;
import com.food4thought.test.databse.RestaurantDatabase;
import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantModel;

import java.util.List;

public class FavouritesActivity extends DrawerActivity {

    private ListView listView;
    private ImageButton ibFavourite;


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

        listView = (ListView) findViewById(R.id.lvFavourites);

        ibFavourite = (ImageButton) findViewById(R.id.ibButton);
        new GetArrayFromDatabase().execute("");


    }

    public class GetArrayFromDatabase extends AsyncTask<String, Void, List<RestaurantDataModel>>{

        @Override
        protected List<RestaurantDataModel> doInBackground(String... params) {

            RestaurantDatabase r = Constants.database;
            List<RestaurantDataModel> list = r.getAllRestaurant();

            if(list == null){
                Log.w("JSON", "List is empty");
            }else {
                Log.w("JSON", Integer.toString(list.size()));
            }

            return null;
        }
    }
}
