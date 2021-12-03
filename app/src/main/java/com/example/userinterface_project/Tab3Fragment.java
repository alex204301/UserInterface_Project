package com.example.userinterface_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Tab3Fragment extends Fragment {

    private Button goalSetting;
    private Button alarmSetting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab3, container, false);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("스케줄 메뉴");
        actionBar.setDisplayHomeAsUpEnabled(false);

        goalSetting = rootView.findViewById(R.id.goal_setting);
        alarmSetting = rootView.findViewById(R.id.alarm_setting);

//        goalSetting.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), ~Activity.class);
//            startActivity(intent);
//        });
//        alarmSetting.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), ~Activity.class);
//            startActivity(intent);
//        });

        return rootView;
    }
}