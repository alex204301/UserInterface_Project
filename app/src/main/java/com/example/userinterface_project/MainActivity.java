package com.example.userinterface_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity{

    Tab1Fragment tab1Fragment;
    Tab2Fragment tab2Fragment;
    Tab3Fragment tab3Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tab1Fragment = new Tab1Fragment();
        tab2Fragment = new Tab2Fragment();
        tab3Fragment = new Tab3Fragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, tab1Fragment).commit();
        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.first_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, tab1Fragment).commit();
                        return true;
                    case R.id.second_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, tab2Fragment).commit();
                        return true;
                    case R.id.third_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, tab3Fragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}