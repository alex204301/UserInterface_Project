package com.example.userinterface_project;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Tab1Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab1, container,false);
        FloatingActionButton fab = rootview.findViewById(R.id.tab1_plus_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View noteDialogView = inflater.inflate(R.layout.tab1_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(noteDialogView)
                        .setTitle("단어장 이름")
                        .setNegativeButton("취소", null)
                        .setPositiveButton("만들기", null)
                        .show();
            }
        });
        return rootview;
    }
}