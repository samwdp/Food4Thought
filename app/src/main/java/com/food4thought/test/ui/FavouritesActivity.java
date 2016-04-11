package com.food4thought.test.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.food4thought.test.R;
import com.food4thought.test.constants.Constants;
import com.food4thought.test.databse.RestaurantDatabase;
import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantDatabaseModel;
import com.food4thought.test.model.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends DrawerActivity {


    private ListView listView;


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



        new GetArrayFromDatabase().execute();


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
            RestaurantDatabaseModel r = restaurantDataModels.get(position);

            //setting the text of the row
            holder.text.setText(r.getName());
            return convertView;
        }

        class ViewHolder {
            private TextView text;
            private ImageButton ibFavourite;
        }
    }
}
