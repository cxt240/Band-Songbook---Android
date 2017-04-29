package com.example.chris.bandsongbook_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Fragment that displays the members in a list
 * @author Chris Tsuei
 */
public class Members extends Fragment{

    public ListView memberList;
    public ArrayList<String> members;
    ArrayAdapter<String> arrayAdapter;
    public boolean bandleader;

    /**
     * blank constructor for the class
     */
    public Members() {}

    /**
     * instantiating the elements of the fragment
     * @param inflater what the fragment is displayed in
     * @param container where the fragment is to be displayed
     * @param savedInstanceState current instance of the fragment
     * @return what the fragment should look like
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_member, container, false);

        Group_Details activity = (Group_Details) getActivity();
        members = activity.members;
        bandleader = activity.bandleader;

        memberList = (ListView) rootView.findViewById(R.id.member_list);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, members);
        memberList.setAdapter(arrayAdapter);

        return rootView;
    }

    /**
     * updates the member list (call from Group Details)
     * @param list list of members (possible repeats)
     */
    public void updateMembers(String[] list) {
        for(int i = 0; i < list.length; i++) {
            if(!members.contains(list[i])) {
                members.add(list[i]);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }
}
