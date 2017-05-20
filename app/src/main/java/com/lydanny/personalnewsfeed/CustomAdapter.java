package com.lydanny.personalnewsfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * ----------------------------------------------|
 * Project Name: Personal News Feed
 * File Name: CustomeAdapter.java
 * AUTHOR: Danny Ly | RedKlouds
 * Created On: 5/19/2017                         |
 * ----------------------------------------------|
 *
 * File Description:
 * ->
 * Assumptions:
 * ->
 **/

public class CustomAdapter extends BaseAdapter {
    ArrayList<String[]> arrayList;
    Context appContent;

    public CustomAdapter(Context cont, ArrayList<String[]> list){
        arrayList = list;
        this.appContent = cont;
    }

    @Override
    public int getCount(){
        return arrayList.size();
    }

    @Override
    public Object getItem(int position){
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = null;
        LayoutInflater inflater = (LayoutInflater) appContent.getSystemService(
                                                        Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            row = inflater.inflate(R.layout.list_item_reddit, parent, false);

        }else{
            row = convertView;
        }
        String[] detail = arrayList.get(position);

        RelativeLayout rl = (RelativeLayout) row.findViewById(R.id.list_item);
        /*
        rl.setBackgroundColor(Color.parseColor(detail[3]));
        TextView name = (TextView)row.findViewById(R.id.listing_title);
        name.setText
        */
        return null;


    }

}
