/**************************************************************************************************
 * Copyright (C) 2016 WickerLabs. inc - All Rights Reserved.                                      *
 *                                                                                                *
 * NOTICE:  All information contained herein is, and remains the property of WickerLabs,          *
 * The intellectual and technical concepts contained herein are proprietary to WickerLabs.        *
 * Dissemination of this information or reproduction of this material                             *
 * is strictly forbidden unless prior permission is obtained from WickerLabs. inc                 *
 *                                                                                                *
 **************************************************************************************************/
package com.example.calls;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calls.Adapter.WeatherInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.jar.Manifest;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.calls.Adapter.HttpUtil;

public class ContactDetailActivity extends AppCompatActivity {
    private MyDataBaseHelper dbHelper;
    String index;
    Integer id;
    WeatherInfo weatherInfo;
    private static final String TAG = "ContactDetailActivity";
    //定义异步更新UI，实现线程中更新UI
    public static final int UPDATE_LOACTION = 1;
    public static final int UPDATE_WEATHER = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact_detail);
        setSupportActionBar(toolbar);
        setTitle("Contacts");
        iniData();
        iniWeather();
        iniSweetMessageButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_contact_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.delete_contact_detail:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("delete from People where id = ?",new String[]{id.toString()});
                Toast.makeText(this, "Delete succeeded", Toast.LENGTH_SHORT).show();
                finish();
            case R.id.edit_contact_detail:
//                Intent intent = new Intent(this,AddEditContactActivity.class);
//                startActivity(intent);
                break;
            default:
        }
        return true;
    }

    private void iniData(){
        dbHelper = new MyDataBaseHelper(this,"PeopleStore.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        index = intent.getStringExtra("index");
        String firstFourChar = index;

        if(index.length()>=4) {
            firstFourChar = index.substring(0, 4);
        }

        if(firstFourChar.equals("TEL:")){
            String phoneNumber = index.substring(4);
            Cursor qcursor = db.rawQuery("select * from People where phoneNumber1 = ?",
                    new String[]{phoneNumber});
            //If there exist such phone number in the database.
            if(qcursor.moveToFirst()){
                String name = qcursor.getString(qcursor.getColumnIndex("name"));
                String phone = qcursor.getString(qcursor.getColumnIndex("phoneNumber1"));
                String relationship = qcursor.getString(qcursor.getColumnIndex("relationship"));

                ImageView avatorView = (ImageView) findViewById(R.id.contact_detail_avatar);
                TextView nameView = (TextView) findViewById(R.id.contact_detail_name);
                TextView phoneView = (TextView) findViewById(R.id.contact_detail_phone_number);
                TextView relationshipView = (TextView) findViewById(R.id.contact_detail_relationship);
                TextView locationView = (TextView) findViewById(R.id.contact_detail_phone_location);
                int resID = R.drawable.avatar_boy;//getResources().getIdentifier("avatar_boy", "drawable", "com.example.calls");
                avatorView.setImageResource(resID);
                nameView.setText(name);
                phoneView.setText(phone);
                relationshipView.setText(relationship);

                String location = qcursor.getString(qcursor.getColumnIndex("phoneLocation"));
                locationView.setText(location);
                if(location==null) {
                    findLocation(phone);
                }
                qcursor.close();
            }
            else{
                qcursor.close();
                Unknown();
            }

        }
        else {
            id = Integer.parseInt(index);
            Cursor qcursor = db.rawQuery("select * from People where id = ?",
                    new String[]{id.toString()});
            qcursor.moveToFirst();
            String name = qcursor.getString(qcursor.getColumnIndex("name"));
            String phone = qcursor.getString(qcursor.getColumnIndex("phoneNumber1"));
            String relationship = qcursor.getString(qcursor.getColumnIndex("relationship"));

            ImageView avatorView = (ImageView) findViewById(R.id.contact_detail_avatar);
            TextView nameView = (TextView) findViewById(R.id.contact_detail_name);
            TextView phoneView = (TextView) findViewById(R.id.contact_detail_phone_number);
            TextView relationshipView = (TextView) findViewById(R.id.contact_detail_relationship);
            TextView locationView = (TextView) findViewById(R.id.contact_detail_phone_location);
            int resID = R.drawable.avatar_boy;//getResources().getIdentifier("avatar_boy", "drawable", "com.example.calls");

            avatorView.setImageResource(resID);
            nameView.setText(name);
            phoneView.setText(phone);
            relationshipView.setText(relationship);
            String location = qcursor.getString(qcursor.getColumnIndex("phoneLocation"));
            locationView.setText(location);
            if(location==null) {
                findLocation(phone);
            }
            qcursor.close();
        }
    }

    private void Unknown(){
        ImageView avatorView = (ImageView) findViewById(R.id.contact_detail_avatar);
        TextView nameView = (TextView) findViewById(R.id.contact_detail_name);
        TextView phoneView = (TextView) findViewById(R.id.contact_detail_phone_number);
        TextView relationshipView = (TextView) findViewById(R.id.contact_detail_relationship);
        int resID = R.drawable.avatar_boy;//getResources().getIdentifier("avatar_boy", "drawable", "com.example.calls");
        avatorView.setImageResource(resID);
        nameView.setText("Unknown");
        phoneView.setText(index.substring(4));
        relationshipView.setText("");
        findLocation(index.substring(4));

    }

    private android.os.Handler handler = new android.os.Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_LOACTION:
                    TextView locationView = (TextView) findViewById(R.id.contact_detail_phone_location);
                    String location = (String) msg.obj;
                    locationView.setText(location);
                    iniWeather();
                    break;
                case UPDATE_WEATHER:
                    TextView weatherView = (TextView) findViewById(R.id.contact_detail_weather);
                    weatherView.setText(weatherInfo.getWeather());
                    break;
                default:
                    break;
            }
        }
    };
    private void iniSweetMessageButton(){
        FloatingActionButton sendMessage = (FloatingActionButton) findViewById(R.id.contact_detail_send_sweet_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView phoneNumView = (TextView)findViewById(R.id.contact_detail_phone_number);
                final String phoneNum = (String) phoneNumView.getText();
                TextView locationView = (TextView)findViewById(R.id.contact_detail_phone_location);
                final String location = (String)locationView.getText();
                TextView weatherView = (TextView)findViewById(R.id.contact_detail_weather);
                final String weather = (String)weatherView.getText();
                if(location.equals("")) {
                    Toast.makeText(ContactDetailActivity.this, "找不到号码归属地，天气查询失败", Toast.LENGTH_SHORT).show();
                    findLocation(phoneNum);
                    return;
                }else if(weather.equals("")){
                    Toast.makeText(ContactDetailActivity.this, "正在获取天气信息，请稍后再试", Toast.LENGTH_SHORT).show();
                    refreshWeather();
                    return;
                } else{
                    generateMessage(phoneNum, location);
                }

            }
        });
    }
    private  void iniWeather(){
        TextView locationView = (TextView)findViewById(R.id.contact_detail_phone_location);
        final String location = (String)locationView.getText();
        if(location.equals(""))
            return;
        weatherInfo = new WeatherInfo(location);
        //一段时间后刷新
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(400);
                    Message msg = new Message();
                    msg.what = UPDATE_WEATHER;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private  void refreshWeather(){
        if(weatherInfo == null){
            iniWeather();
            return;
        }
        Log.d(TAG, "iniWeahter: "+weatherInfo.getWeather());
        TextView weatherView = (TextView) findViewById(R.id.contact_detail_weather);
        weatherView.setText(weatherInfo.getWeather());
    }

    private void findLocation(final String phoneNum){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(ContextCompat.checkSelfPermission(ContactDetailActivity.this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ContactDetailActivity.this, new String[]{android.Manifest.permission.INTERNET}, 10);
                }else {
                    try {
                        //360API接口
                        String API = "http://cx.shouji.360.cn/phonearea.php?number=";
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(API+phoneNum)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        String location = parseLocation(responseData);

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("phoneLocation", location);
                        db.update("People", values, "phoneNumber1=?", new String[]{phoneNum});
                        Message msg = new Message();

                        msg.what = UPDATE_LOACTION;
                        msg.obj = location;
                        handler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private String  parseLocation(String jsonData) {
        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONObject data = jObj.getJSONObject("data");
            Log.d(TAG, "parseLocation: "+data.getString("city"));

            return data.getString("city");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private  void generateMessage(final String phoneNum, String location){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor qcursor = db.rawQuery("select * from People where phoneNumber1 = ?",
                        new String[]{phoneNum});
                //If there exist such phone number in the database.
                String name = null;
                String relationship = null;
                if(qcursor.moveToFirst()) {
                    name = qcursor.getString(qcursor.getColumnIndex("name"));
                    relationship = qcursor.getString(qcursor.getColumnIndex("relationship"));
                }
                final String finalName = name;
                final String finalRelationship = relationship;
                //联网查找天气并生成对应预报短信
                String sweetMessage = new String();
                if (finalRelationship!=null)
                    sweetMessage+=finalRelationship;
                else if(finalName!=null)
                    sweetMessage+=finalName;
                String weatherMessage = weatherInfo.getWeatherMessage();
                if (weatherMessage == null){
                    Toast.makeText(ContactDetailActivity.this, "未查找到相关天气信息，请重试",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                sweetMessage+= ","+weatherMessage;
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNum));
                intent.putExtra("sms_body", sweetMessage);
                startActivity(intent);
            }
        }).start();
    }



}
