package com.example.wot.parentapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

/**
 * Created by WOT on 9/9/2017.
 */

public class BlockSitesAdapter extends ArrayAdapter<String> {

    Activity activity;
    String[] sites,ids;
    public BlockSitesAdapter( Activity activity,  int resource,  String[] sites,String[] ids) {
        super(activity, resource, sites);
        this.activity = activity;
        this.sites=sites;
        this.ids = ids;
    }

    private class ViewHolder{
        TextView sitetxt,idtxt;
        ImageButton editimg,deleteim;

    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        View view;
        view = convertView;
        final ViewHolder viewHolder;
        if(convertView ==null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.blocksiteslayout,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.sitetxt = (TextView)view.findViewById(R.id.blocksitetxt);
            viewHolder.idtxt = (TextView)view.findViewById(R.id.blocksiteidtxt);
            viewHolder.editimg = (ImageButton) view.findViewById(R.id.siteeditim);
            viewHolder.deleteim = (ImageButton)view.findViewById(R.id.sitedeleteim);
            viewHolder.sitetxt.setText(sites[position]);
            viewHolder.idtxt.setText(ids[position]);



            viewHolder.deleteim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = viewHolder.idtxt.getText().toString();


                    new DeleteSitesTask().execute(id);
                }
            });

        }
        return view;
    }

    private class DeleteSitesTask extends AsyncTask<String,String,String>
    {
        ProgressDialog pd = new ProgressDialog(activity);
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

                url=new URL("https://app-1503993646.000webhostapp.com/parentchild/deletesites.php");

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
                builder.appendQueryParameter("id",strings[0]);



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
                Toast.makeText(activity, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
            else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(activity, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }

            else if(s.equals("success")){
                activity.finish();
                Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
