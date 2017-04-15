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
import java.util.HashMap;


public class Members extends Fragment{

    public ListView memberList;
    public ArrayList<String> members;
    public HashMap<String, boolean[]> instruments;
    public boolean bandleader;

    public Members() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_member, container, false);

        Group_Details activity = (Group_Details) getActivity();
        instruments = new HashMap<String, boolean[]>();
        members = activity.members;
        bandleader = activity.bandleader;

        for(String name : members) {
            boolean[] parts = new boolean[3];
            Arrays.fill(parts, false);
            instruments.put(name, parts);
        }

        memberList = (ListView) rootView.findViewById(R.id.member_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, members);
        memberList.setAdapter(arrayAdapter);

        memberList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] parts = {"Voice", "Guitar", "Drums"};
                final AdapterView<?> list = parent;
                final boolean[] select = instruments.get((String)parent.getItemAtPosition(position));

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Choose Parts")
                        .setMultiChoiceItems(parts, select, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                select[which] = isChecked;
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(bandleader) {
                                    instruments.put((String) list.getItemAtPosition(position), select);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

        });

        return rootView;
    }
}
