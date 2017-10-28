package com.example.wot.parentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class BlockSitesActivity extends AppCompatActivity {

    ListView listView;
    Button button;
    EditText editText;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    String cid,text;
    BlockSitesAdapter blockSitesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_sites);

        button = (Button)findViewById(R.id.blocksitebtn);
        editText = (EditText)findViewById(R.id.blocksiteet);
        listView = (ListView)findViewById(R.id.blocksitelist);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("PARENT",MODE_PRIVATE);
        cid= sharedPreferences.getString("CHILD","no child");
        new SitesTask().execute(cid);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             text = editText.getText().toString();
                new AddSitesTask().execute(cid,text);

            }
        });

    }

    private class SitesTask extends AsyncTask<String,String,String>
    {
        ProgressDialog pd = new ProgressDialog(BlockSitesActivity.this);
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

                url=new URL("https://app-1503993646.000webhostapp.com/parentchild/retrievesites.php");

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
                Toast.makeText(BlockSitesActivity.this, "No blocked sites", Toast.LENGTH_SHORT).show();
            }
            else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(BlockSitesActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            else {
                String[] sites,ids;
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.optJSONArray("sites");
                    sites = new String[jsonArray.length()];
                    ids = new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        sites[i] = jsonObject1.getString("site");
                        ids[i] = jsonObject1.getString("id");
                    }

                    blockSitesAdapter = new BlockSitesAdapter(BlockSitesActivity.this,R.layout.blocksiteslayout,sites,ids);
                    listView.deferNotifyDataSetChanged();
                    listView.setAdapter(blockSitesAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }


    private class AddSitesTask extends AsyncTask<String,String,String>
    {
        ProgressDialog pd = new ProgressDialog(BlockSitesActivity.this);
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

                url=new URL("https://app-1503993646.000webhostapp.com/parentchild/sendsites.php");

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
                builder.appendQueryParameter("site",strings[1]);



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
            if(s.equals("failed"))
            {
                Toast.makeText(BlockSitesActivity.this, "Failed to set", Toast.LENGTH_SHORT).show();
            }
            else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(BlockSitesActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            else if(s.equals("success")){
                editText.setText("");
                new SitesTask().execute(cid);
                Toast.makeText(BlockSitesActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
