package com.example.chris.bandsongbook_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Files extends Fragment{

    private ListView fileList;
    public List<String> filenames;
    public ArrayAdapter<String> arrayAdapter;
    public boolean bandleader = false;
    private static final int READ_REQUEST_CODE = 42;

    public FloatingActionButton addFile;
    public Files() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_files, container, false);

        Group_Details activity = (Group_Details) getActivity();
        bandleader = activity.bandleader;

        filenames = activity.songs;
        fileList = (ListView) rootView.findViewById(R.id.file_list);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, filenames);
        fileList.setAdapter(arrayAdapter);

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final ArrayList parts = new ArrayList();
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Play settings")
                        .setMessage("Set the tempo and start measure")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent play = new Intent(getContext(), Play.class);
                                play.putExtra("Songs", new ArrayList<String>(filenames));
                                play.putExtra("Bandleader", bandleader);
                                play.putExtra("Play", filenames.get(position));
                                startActivity(play);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

        });

        addFile = (FloatingActionButton) rootView.findViewById(R.id.file);
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFile();
            }
        });
        if(!bandleader) {
            addFile.setVisibility(View.GONE);
        }
        return rootView;
    }

    /**
     * fileChooser for the device. Starts an intent that opens the device's file manager
     */
    public void addFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.v(TAG, requestCode + " " + resultCode + " " + resultData.getData().toString());
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if(resultData!= null) {
                try {
                    String result = readTextFromUri(resultData.getData());
                    Log.v(TAG, result);
                }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }


    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
}
