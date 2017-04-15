package com.example.chris.bandsongbook_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Members extends Fragment{

    private ListView memberList;
    private ArrayList<String> members;
    public Members() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_member, container, false);

        Group_Details activity = (Group_Details) getActivity();
        members = activity.members;

        memberList = (ListView) rootView.findViewById(R.id.member_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, members);
        memberList.setAdapter(arrayAdapter);

        memberList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence[] instruments = {"Voice", "Guitar", "Drums"};
                final boolean[] parts = new boolean[instruments.length];
                for(int i = 0; i < parts.length; i++) {
                    parts[i] = false;
                }

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Choose Parts")
                        .setMultiChoiceItems(instruments, parts, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                parts[which] = isChecked;
                            }
                        })
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

        return rootView;
    }
}
