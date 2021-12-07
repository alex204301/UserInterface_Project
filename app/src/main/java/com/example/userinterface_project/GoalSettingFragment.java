package com.example.userinterface_project;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GoalSettingFragment extends Fragment {

    public GoalSettingFragment() {
    }

    public static GoalSettingFragment newInstance() {
        GoalSettingFragment fragment = new GoalSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("목표 설정");
            actionBar.setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);
        }

        ArrayList<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, -7);

        for (int i = 0; i < 30; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }

        RecyclerView recyclerView = view.findViewById(R.id.goal_recycler_view);
        recyclerView.setAdapter(new GoalListAdapter(dates));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class GoalListAdapter extends RecyclerView.Adapter<GoalListAdapter.ViewHolder> {
        List<Date> dates;
        GoalListAdapter(List<Date> dates) {
            this.dates = dates;
        }
        @NonNull
        @Override
        public GoalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GoalListAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.goal_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull GoalListAdapter.ViewHolder holder, int position) {
            Resources resources = holder.itemView.getContext().getResources();
            DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
            holder.dateText.setText(format.format(dates.get(position)));
            holder.goalText.setText(resources.getString(R.string.goal_is_not_set));
            holder.solvedText.setText(resources.getString(R.string.solved_quizzes, 0));
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            TextView goalText;
            TextView solvedText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.date_text);
                goalText = itemView.findViewById(R.id.goal_text);
                solvedText = itemView.findViewById(R.id.solved_text);
            }
        }
    }
}
