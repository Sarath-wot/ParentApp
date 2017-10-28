package com.example.wot.parentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    CardView screencard,sitecard,batterycard,trackcard,usagecard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        screencard = (CardView)findViewById(R.id.cardscreenshot);
        sitecard = (CardView)findViewById(R.id.cardblock);
        batterycard = (CardView)findViewById(R.id.cardbattery);
        trackcard = (CardView)findViewById(R.id.cardplace);
        usagecard = (CardView)findViewById(R.id.cardusage);

        screencard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ScreenshotActivity.class));
            }
        });

        sitecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,BlockSitesActivity.class));
            }
        });

        batterycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,BatteryActivity.class));
            }
        });

        trackcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                            startActivity(new Intent(HomeActivity.this,MapsActivity.class));
            }
        });

        usagecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ChildAccess.class));
            }
        });
    }
}
