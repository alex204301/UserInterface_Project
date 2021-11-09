package com.example.userinterface_project;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Tab2Fragment extends Fragment {

    public static Tab2Fragment newInstance() {
        return new Tab2Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("단어장 선택");
        actionBar.setDisplayHomeAsUpEnabled(false);
        return inflater.inflate(R.layout.fragment_tab2, container, false);
    }
}