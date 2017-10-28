package com.example.wot.parentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class SelectChildActivity extends AppCompatActivity {

    ListView listView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String user;
    SelectChildAdapter selectChildAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child);

        sharedPreferences = getSharedPreferences("PARENT",MODE_PRIVATE);
        listView = (ListView)findViewById(R.id.selectchildlist);
        user = sharedPreferences.getString("USER","no user");
        Log.i("user",user);
        new LoginTask().execute(user);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView idtxt = (TextView)view.findViewById(R.id.selectchildid);
                editor = sharedPreferences.edit();
                editor.putString("CHILD",idtxt.getText().toString());
                editor.apply();
              //  Toast.makeText(SelectChildActivity.this, idtxt.getText().toString(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SelectChildActivity.this,HomeActivity.class));
            }
        });
    }

    private class LoginTask extends AsyncTask<String,String,String>
    {
        ProgressDialog pd = new ProgressDialog(SelectChildActivity.this);
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

                url=new URL("https://app-1503993646.000webhostapp.com/parentchild/childlist.php");

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
                builder.appendQueryParameter("pid",strings[0]);



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
            Log.i("SelectChildResult",s);
            if(s.equals("0 results"))
            {
                Toast.makeText(SelectChildActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
            }
            else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(SelectChildActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            else {
                String[] name,id;
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.optJSONArray("child");
                    name = new String[jsonArray.length()];
                    id = new String[jsonArray.length()];
                    if(jsonArray==null)
                    {
                        Toast.makeText(SelectChildActivity.this, "No child added", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            name[i] = jsonObject1.getString("name");
                            id[i] = jsonObject1.getString("id");
                        }

                        selectChildAdapter = new SelectChildAdapter(SelectChildActivity.this, R.layout.selectchildlayout, name, id);
                        listView.setAdapter(selectChildAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
