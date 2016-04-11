package com.food4thought.test.databse;

import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantDatabaseModel;
import com.food4thought.test.model.RestaurantModel;

import java.util.ArrayList;

/**
 * Created by samwdp on 09/04/2016.
 */
public interface RestaurantListener {

    public void addRestaurant(RestaurantDatabaseModel restaurantModel);
    public ArrayList<RestaurantDatabaseModel> getAllRestaurant();
    public int getRestaurantCount();
}
