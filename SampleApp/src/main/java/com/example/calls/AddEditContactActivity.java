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

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

public class AddEditContactActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editPhoneNumber;
    private EditText editRelationship;
    private MyDataBaseHelper dbHelper;

    Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_edit_contact);
        setSupportActionBar(toolbar);
        setTitle("Contacts");

        ImageView avatorView = (ImageView) findViewById(R.id.add_contact_avatar);
        avatorView.setImageResource(R.drawable.avatar_boy);
        editName = (EditText) findViewById(R.id.edit_name);
        editPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        editRelationship = (EditText) findViewById(R.id.edit_relationship);
        dbHelper = new MyDataBaseHelper(this,"PeopleStore.db",null,1);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_add_edit_contact,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.edit_commit:
                createNewContact();
                finish();
            case R.id.edit_cancel:
                finish();
            default:
        }
        return true;
    }

    private void createNewContact(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String Name = editName.getText().toString();
        String PhoneNumber = editPhoneNumber.getText().toString();
        String RelationShip = editRelationship.getText().toString();
        db.execSQL("insert into People (name, phoneNumber1, relationship) values(?,?,?)",
                new String[] {Name, PhoneNumber, RelationShip});

    }



}
