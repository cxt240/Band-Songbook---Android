package com.example.chris.bandsongbook_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Files extends Fragment{

    private ListView fileList;
    private List<String> filenames;
    private boolean bandleader = false;

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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, filenames);
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
        startActivityForResult(chooseFile, 42);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent resultData) {
        String path = resultData.getDataString();
        filenames.add(path);
        fileList.notify();
    }

    /**
     * getting the filepath that was chosen
     * taken from http://stackoverflow.com/questions/7856959/android-file-chooser
     * @param context current context
     * @param uri path to file
     * @return file path string
     * @throws URISyntaxException invalid path
     */
    public static String getPath(Context context, URI uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                android.net.Uri uriPath = android.net.Uri.parse(uri.toString());
                cursor = context.getContentResolver().query(uriPath, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
