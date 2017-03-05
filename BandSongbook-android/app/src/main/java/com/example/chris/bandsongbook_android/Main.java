package com.example.chris.bandsongbook_android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends Activity {

    private EditText name;
    private EditText groupCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.Name);
        groupCode = (EditText) findViewById(R.id.Code);

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextScreen = new Intent(getApplicationContext(), Create_Group.class);
                startActivity(nextScreen);
            }
        });

        Button group = (Button) findViewById(R.id.join);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String member = name.getText().toString();
                // code to enter via server if password is correct

                String group = null; //get group name from server
                String password = groupCode.getText().toString();

                Intent nextScreen = new Intent(getApplicationContext(), Group_Details.class);

                nextScreen.putExtra("Group Name", group);

                startActivity(nextScreen);
            }
        });

    }

}
