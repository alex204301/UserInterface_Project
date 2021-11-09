package com.example.userinterface_project;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Tab1Fragment extends Fragment {

    public static Tab1Fragment newInstance() {
        return new Tab1Fragment();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab1, container,false);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("단어장");
        actionBar.setDisplayHomeAsUpEnabled(false);
        Button testBtn = rootView.findViewById(R.id.test_btn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(WordListFragment.newInstance());
            }
        });
        FloatingActionButton fab = rootView.findViewById(R.id.tab1_plus_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View noteDialogView = inflater.inflate(R.layout.tab1_dialog, null);
                EditText edNoteName = noteDialogView.findViewById(R.id.note_name);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(noteDialogView)
                        .setTitle("단어장 이름")
                        .setNegativeButton("취소", null)
                        .setPositiveButton("만들기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String noteName = edNoteName.getText().toString();
                                Toast.makeText(getActivity(), noteName, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
        return rootView;
    }
}