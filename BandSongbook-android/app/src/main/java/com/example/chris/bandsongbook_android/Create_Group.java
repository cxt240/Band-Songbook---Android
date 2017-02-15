package com.example.chris.bandsongbook_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class Create_Group extends AppCompatActivity {

    private EditText groupName;
    private EditText groupCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__group);

        groupName = (EditText) findViewById(R.id.group_name);
        groupCode = (EditText) findViewById(R.id.Code);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(getApplicationContext(), Group_Details.class);

                String group = groupName.getText().toString();
                String password = groupCode.getText().toString();

                nextScreen.putExtra("Group Name", group);
                nextScreen.putExtra("Admin", true);
                startActivity(nextScreen);
            }
        });
    }

}
