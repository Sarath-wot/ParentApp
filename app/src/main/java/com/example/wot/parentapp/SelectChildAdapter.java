package com.example.wot.parentapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by WOT on 8/29/2017.
 */

public class SelectChildAdapter extends ArrayAdapter<String> {

    Activity activity;
    String[] name;
    String[] id;
    public SelectChildAdapter(@NonNull Activity context, @LayoutRes int resource, String[] name,String[] id) {
        super(context, resource, name);
        this.activity = context;
        this.name = name;
        this.id = id;
    }
    private class ViewHolder
    {
        TextView nametxt,idtxt;
        CardView cardView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v= convertView;
        ViewHolder viewHolder;
        if(v==null)
        {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.selectchildlayout,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.nametxt = (TextView)v.findViewById(R.id.selectchildname);
            viewHolder.idtxt = (TextView)v.findViewById(R.id.selectchildid);
            viewHolder.cardView = (CardView) v.findViewById(R.id.selectchildcard);
            viewHolder.nametxt.setText(name[position]);
            viewHolder.idtxt.setText(id[position]);

        }
        return v;
    }
}
