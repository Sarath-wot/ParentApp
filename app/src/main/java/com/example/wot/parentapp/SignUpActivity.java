package com.example.wot.parentapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class SignUpActivity extends AppCompatActivity {

    EditText ename,eaddr,ephone,email;
    RadioGroup radioGroup;
    String name,addr,gender,phone,mail;
    Button button;
    TextInputLayout tname,taddr,tphone,tmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ename = (EditText)findViewById(R.id.signupname);
        eaddr = (EditText)findViewById(R.id.signupaddress);
        ephone = (EditText)findViewById(R.id.signupphone);
        email = (EditText)findViewById(R.id.signupmail);
        button = (Button)findViewById(R.id.signupbtn);
        radioGroup = (RadioGroup)findViewById(R.id.signuprg);
        tname = (TextInputLayout)findViewById(R.id.namelayout);
        taddr = (TextInputLayout)findViewById(R.id.addresslayout);
        tphone = (TextInputLayout)findViewById(R.id.phonelayout);
        tmail = (TextInputLayout)findViewById(R.id.maillayout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = ename.getText().toString().trim();
                addr = eaddr.getText().toString().trim();
                phone = ephone.getText().toString().trim();
                mail = email.getText().toString().trim();
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)findViewById(id);
                gender = radioButton.getText().toString();

                if(name.isEmpty())
                {
                    tname.setErrorEnabled(true);
                    tname.setError("Enter your name");
                    return;
                }
                else
                {
                    tname.setErrorEnabled(false);
                }
                if(addr.isEmpty())
                {
                    taddr.setErrorEnabled(true);
                    taddr.setError("Enter Address");
                    return;
                }
                else
                {
                    taddr.setErrorEnabled(false);
                }
                if(phone.isEmpty())
                {
                    tphone.setErrorEnabled(true);
                    tphone.setError("Enter your phone number");
                    return;
                }
                else
                {
                    tphone.setErrorEnabled(false);
                }
                if(mail.isEmpty()|| !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches())
                {
                    tmail.setErrorEnabled(true);
                    tmail.setError("Enter valid email");
                    return;
                }
                else {
                    tmail.setErrorEnabled(false);
                }

                    new RegisterTask().execute(name,addr,gender,phone,mail);


            }
        });

    }

    private class RegisterTask extends AsyncTask<String,String,String>
    {
        ProgressDialog pd = new ProgressDialog(SignUpActivity.this);
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

                url=new URL("https://app-1503993646.000webhostapp.com/parentchild/signup.php");

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
                builder.appendQueryParameter("name",strings[0]);
                builder.appendQueryParameter("addr",strings[1]);
                builder.appendQueryParameter("gender",strings[2]);
                builder.appendQueryParameter("phone",strings[3]);
                builder.appendQueryParameter("mail",strings[4]);



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
                else
                    return "unsuccessfull";

            } catch (IOException e1) {
                e1.printStackTrace();
                return "Exception";
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if(s.equalsIgnoreCase("Success"))
            {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }

            else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(SignUpActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
            else if(s.equals("Already registered"))
            {
                Toast.makeText(SignUpActivity.this,"You are already registered",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(SignUpActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
