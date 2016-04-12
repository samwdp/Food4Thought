package com.food4thought.test.constants;

import com.food4thought.test.databse.RestaurantDatabase;
import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantModel;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samwdp on 09/04/2016.
 */
public class Constants {

    public static List<RestaurantModel> restaurantModelList;
    public static String reference;
    public static List<RestaurantDataModel.Reviews> reviewsList;
    public static RestaurantDatabase database;
    public static Place myPlace;

}
