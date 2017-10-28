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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class ChildAccess extends AppCompatActivity {

    RadioGroup radioGroup;
    Button button;
    String s,text,cid;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_access);

        sharedPreferences = getSharedPreferences("PARENT",MODE_PRIVATE);
        cid= sharedPreferences.getString("CHILD","no child");
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiog);
        button = (Button)findViewById(R.id.sbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id=radioGroup.getCheckedRadioButtonId();
                RadioButton rb=(RadioButton)findViewById(id);
                text = rb.getText().toString().trim();
                Log.i("rbText",text);
                if(text.equals("Allow Access"))
                {
                    s="true";
                }
                else
                {
                    s="false";
                }
                new SendAccess().execute(cid,s);
            }
        });
    }

    private class SendAccess extends AsyncTask<String,String,String>
    {
        ProgressDialog pd = new ProgressDialog(ChildAccess.this);
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

                url=new URL("https://app-1503993646.000webhostapp.com/parentchild/sendaccess.php");

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
                builder.appendQueryParameter("access",strings[1]);



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
                Toast.makeText(ChildAccess.this, "Invalid", Toast.LENGTH_SHORT).show();
            }
            else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(ChildAccess.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
            else if(s.equals("success"))
            {
                Toast.makeText(ChildAccess.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChildAccess.this,HomeActivity.class));
                finish();
            }


        }
    }
}
