package com.food4thought.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.food4thought.test.R;

public class MainActivity extends DrawerActivity implements View.OnClickListener{

    private Button searchBtn;
    private Button mapBtn;
    private Button categoriesBtn;
    private Button favouritesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_frame);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View activityView = layoutInflater.inflate(R.layout.activity_main, null,false);
        frameLayout.addView(activityView);

        searchBtn = (Button) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(this);
        mapBtn = (Button) findViewById(R.id.map_button);
        mapBtn.setOnClickListener(this);
        categoriesBtn = (Button) findViewById(R.id.category_button);
        categoriesBtn.setOnClickListener(this);
        favouritesBtn = (Button) findViewById( R.id.favourite_button);
        favouritesBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.search_button:
                buttonClick("com.food4thought.search");
                break;
            case R.id.map_button:
                buttonClick("com.food4thought.map");
                break;
            case R.id.category_button:
                buttonClick("com.food4thought.category");
                break;
            case R.id.favourite_button:
                buttonClick("com.food4thought.favourites");
                break;
        }
    }

    private void buttonClick(String s)
    {
        startActivity(new Intent(s));
    }
}
