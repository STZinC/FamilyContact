/**************************************************************************************************
 * Copyright (C) 2016 WickerLabs. inc - All Rights Reserved.                                      *
 *                                                                                                *
 * NOTICE:  All information contained herein is, and remains the property of WickerLabs,          *
 * The intellectual and technical concepts contained herein are proprietary to WickerLabs.        *
 * Dissemination of this information or reproduction of this material                             *
 * is strictly forbidden unless prior permission is obtained from WickerLabs. inc                 *
 *                                                                                                *
 **************************************************************************************************/
package com.example.calls.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calls.ContactDetailActivity;
import com.example.calls.People;
import com.example.calls.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yanyangma on 07/05/2017.
 */

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<People> mPeopleList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView peopleName;
        TextView peoplePhoneNumber;
        ImageView peopleAvator;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            peopleAvator = (ImageView) view.findViewById(R.id.people_avatar);
            peopleName = (TextView) view.findViewById(R.id.people_name);
            peoplePhoneNumber = (TextView) view.findViewById(R.id.people_phonenumber);

        }
    }

    public PeopleAdapter(ArrayList<People> peopleList){
        mPeopleList = peopleList;
        if(mPeopleList==null){
            mPeopleList = new ArrayList<People>();
        }
        Collections.sort(mPeopleList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.people_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                Context c = v.getContext();
                People people = mPeopleList.get(position);
                Integer id = people.getId();
//                Toast.makeText(c, "You click: "+id,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(),ContactDetailActivity.class);
                intent.putExtra("index",id.toString());
                c.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        People people = mPeopleList.get(position);
        holder.peopleName.setText(people.getName());
        holder.peoplePhoneNumber.setText(people.getPhoneNumber());
        holder.peopleAvator.setImageResource(R.drawable.avatar_boy);

    }

    @Override
    public int getItemCount(){
        return mPeopleList.size();
    }
}
