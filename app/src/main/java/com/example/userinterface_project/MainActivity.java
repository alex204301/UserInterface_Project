package com.example.userinterface_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.userinterface_project.db.Alarm;
import com.example.userinterface_project.db.WordDbHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;
import java.util.List;

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, tab1Fragment).commit();
        }
        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.first_tab:
                        replaceFragment(tab1Fragment);
                        return true;
                    case R.id.second_tab:
                        replaceFragment(tab2Fragment);
                        return true;
                    case R.id.third_tab:
                        replaceFragment(tab3Fragment);
                        return true;
                }
                return false;
            }
        });
        initialAlarm();
    }

    public void initialAlarm(){
        List<Alarm> list = WordDbHelper.getInstance(this).getAlarmList();
        Alarm alarm;
        int position = 0;
        int hour, minute, REQUEST_CODE;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent;
        Calendar calendar;

        while (position < list.size()) {
            alarm = list.get(position++);
            hour = alarm.getHour();
            minute = alarm.getMinutes();
            REQUEST_CODE = hour*100 + minute;

            alarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();
    }

    public void showWordList(long noteId) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, WordListFragment.newInstance(noteId))
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();
    }

    public void showQuizSelect(long noteId) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, SelectQuizFragment.newInstance(noteId))
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();
    }

    public void showAlarmSetting() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, AlarmSettingFragment.newInstance())
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();
    }

    public void showGoalSetting() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container, GoalSettingFragment.newInstance())
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();
    }
}