package com.example.userinterface_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.userinterface_project.db.Alarm;
import com.example.userinterface_project.db.Note;
import com.example.userinterface_project.db.WordDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmSettingFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyText;

    public static AlarmSettingFragment newInstance() { return new AlarmSettingFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.alarm_recycler_view);
        emptyText = view.findViewById(R.id.alarm_empty_text);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("알림 설정");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        FloatingActionButton fab = view.findViewById(R.id.alarm_plus_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 시간으로 Dialog를 띄우기 위해 시간을 구함
                Calendar c = Calendar.getInstance(); // java.util.Calendar
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timeDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        WordDbHelper dbHelper = WordDbHelper.getInstance(getContext());
                        dbHelper.createAlarm(hourOfDay, minute);

                        int requestCode = hourOfDay * 100 + minute;
                        Intent intent = new Intent(v.getContext(), AlarmReceiver.class);
                        PendingIntent alarmIndent = PendingIntent.getBroadcast(v.getContext(),requestCode,intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager manager = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        manager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIndent);
                        refreshList();
                    }
                }, hour, minute, false);
                timeDialog.show();
            }
        });

        // divider 추가
        recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        refreshList();
    }

    /**
     * DB에서 목록을 읽은 후 화면 새로 고침
     */
    public void refreshList() {
        AlarmListAdapter adapter = (AlarmListAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new AlarmSettingFragment.AlarmListAdapter();
            recyclerView.setAdapter(adapter);
        }
        List<Alarm> list = WordDbHelper.getInstance(getContext()).getAlarmList();
        adapter.changeList(list);

        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    private static class AlarmListAdapter extends RecyclerView.Adapter<AlarmSettingFragment.AlarmListAdapter.ViewHolder> {
        List<Alarm> list;
        private Context context;

        private AlarmListAdapter() {
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(
                    R.layout.alarm_list_item, parent, false
            );

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AlarmSettingFragment.AlarmListAdapter.ViewHolder holder, int position) {
            Alarm alarm = list.get(position);
            int hour = alarm.getHour();
            int minute = alarm.getMinutes();

            StringBuilder sbl = new StringBuilder();

            if(hour > 11) {
                sbl.append("오후 ");
            } else{
                sbl.append("오전 ");
            }
            if(hour % 12 == 0) {
                if(minute < 10)
                    sbl.append("12:0" + minute);
                else
                    sbl.append("12:" + minute);
            }
            else {
                if (minute < 10)
                    sbl.append(hour % 12 + ":0" + minute);
                else
                    sbl.append(hour % 12 + ":" + minute);
            }

            holder.timeView.setText(sbl.toString());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).getId();
        }

        public void changeList(@NonNull List<Alarm> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {
            TextView timeView;
            ImageView deleteAlarm;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                timeView = itemView.findViewById(R.id.time_view);
                deleteAlarm = itemView.findViewById(R.id.delete_alarm);

                deleteAlarm.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("선택한 알림을 삭제하시겠습니까?")
                            .setNegativeButton("취소", null)
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    WordDbHelper dbHelper = WordDbHelper.getInstance(v.getContext());
                                    Alarm alarm = dbHelper.getAlarm(getItemId());
                                    dbHelper.deleteAlarm(getItemId());

                                    int requestCode = alarm.getHour()*100 + alarm.getMinutes();
                                    Intent intent = new Intent(v.getContext(), AlarmReceiver.class);
                                    PendingIntent alarmIndent = PendingIntent.getBroadcast(v.getContext(),requestCode,intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager manager = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);
                                    if(alarmIndent != null && manager != null)
                                        manager.cancel(alarmIndent);

                                    MainActivity activity = (MainActivity) itemView.getContext();
                                    activity.replaceFragment(new AlarmSettingFragment());
                                }
                            })
                            .show();
                });

                itemView.setOnClickListener(v -> {
                    WordDbHelper dbHelper = WordDbHelper.getInstance(v.getContext());
                    Alarm alarm = dbHelper.getAlarm(getItemId());
                    int hour = alarm.getHour();
                    int minute = alarm.getMinutes();
                    TimePickerDialog timeDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Dialog,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    dbHelper.editAlarm(getItemId(), hourOfDay, minute);
                                    MainActivity activity = (MainActivity) itemView.getContext();
                                    activity.replaceFragment(new AlarmSettingFragment());
                                }
                            }, hour, minute, false);
                    timeDialog.show();
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}