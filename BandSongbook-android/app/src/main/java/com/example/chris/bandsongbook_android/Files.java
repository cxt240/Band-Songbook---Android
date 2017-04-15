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

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Files extends Fragment{

    private ListView fileList;
    private List<String> filenames;
    public ArrayAdapter<String> arrayAdapter;
    private boolean bandleader = false;
    private static final int FILE_SELECT_CODE = 0;

    public FloatingActionButton addFile;
    public Files() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_files, container, false);

        Group_Details activity = (Group_Details) getActivity();
        bandleader = activity.bandleader;

        filenames = activity.files;
        fileList = (ListView) rootView.findViewById(R.id.file_list);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, filenames);
        fileList.setAdapter(arrayAdapter);

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ArrayList parts = new ArrayList();
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Play settings")
                        .setMessage("Set the tempo and start measure")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(Intent.createChooser(chooseFile, "Select file"), FILE_SELECT_CODE);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent resultData) {
        Log.v(TAG, requestCode + " " + resultCode + " " + resultData.getData().toString());
        if(requestCode == FILE_SELECT_CODE) {
            Uri uri = resultData.getData();
            File file = new File(uri.toString());
            String actualPath = file.getAbsolutePath();
            Log.v(TAG, actualPath);
            if(!filenames.contains(actualPath)) {
                filenames.add(actualPath);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

}
