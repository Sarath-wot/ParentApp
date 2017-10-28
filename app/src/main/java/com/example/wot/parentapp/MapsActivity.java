package com.example.wot.parentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lon,lat;
    String longitude,latitude,cid;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPreferences = getSharedPreferences("PARENT",MODE_PRIVATE);
        cid= sharedPreferences.getString("CHILD","no child");
        new LocationTask().execute(cid);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        lon = Double.parseDouble(longitude);
        lat = Double.parseDouble(latitude);
        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(lat, lon);
        Log.i("Location",location.toString());
        mMap.addMarker(new MarkerOptions().position(location).title("Child Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10.0f));

    }

    private class LocationTask extends AsyncTask<String,String,String>
    {
        ProgressDialog pd = new ProgressDialog(MapsActivity.this);
        HttpURLConnection connection;
        URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                url=new URL("https://app-1503993646.000webhostapp.com/parentchild/getlocation.php");

            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            try {
                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder =new Uri.Builder();
                builder.appendQueryParameter("cid",strings[0]);



                String query = builder.build().getEncodedQuery();

                OutputStream os=connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                connection.connect();

                int rc = connection.getResponseCode();
                if(rc == HTTP_OK)
                {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null)
                    {
                        sb.append(line);
                    }
                    return sb.toString();
                }
                else{
                    Log.i("Error","Unsuccessfulcode"+rc);
                    return "unsuccessfull";
                }


            } catch (IOException e1) {
                e1.printStackTrace();
                return "Exception";
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            Log.i("LoginResult",s);
            if(s.equals("0 results"))
            {
                Toast.makeText(MapsActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(MapsActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                finish();

            }

            else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.optJSONArray("location");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jb = jsonArray.optJSONObject(i);
                        longitude = jb.getString("lon");
                        latitude = jb.getString("lat");
                    }
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    finish();
                }




            }

        }
    }
}
