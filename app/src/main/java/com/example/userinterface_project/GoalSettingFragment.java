package com.example.userinterface_project;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface_project.db.GoalListItem;
import com.example.userinterface_project.db.WordDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalSettingFragment extends Fragment {

    private static final String ARG_DAY_OF_WEEK = "day_of_week";
    private static final String ARG_GOAL = "goal";
    private RecyclerView recyclerView;

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
            actionBar.setTitle("?????? ??????");
            actionBar.setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);
        }

        recyclerView = view.findViewById(R.id.goal_recycler_view);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );

        refreshList();

        if (savedInstanceState == null) {
            GoalListAdapter adapter = (GoalListAdapter) recyclerView.getAdapter();
            assert adapter != null;
            recyclerView.scrollToPosition(adapter.getTodayPosition()); // ?????? ????????? ?????????
        }

        getChildFragmentManager().setFragmentResultListener(
                "dialogResult", this, (requestKey, result) -> {
                    WordDbHelper dbHelper = WordDbHelper.getInstance(requireContext());
                    dbHelper.setGoal(
                            result.getInt(ARG_DAY_OF_WEEK), result.getInt(ARG_GOAL)
                    );
                    refreshList();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshList() {
        GoalListAdapter adapter = (GoalListAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new GoalListAdapter();
            recyclerView.setAdapter(adapter);
        }
        WordDbHelper dbHelper = WordDbHelper.getInstance(getContext());
        List<GoalListItem> items = dbHelper.getGoalList();
        adapter.changeList(items);
    }

    public static class GoalSettingDialogFragment extends DialogFragment {
        public static GoalSettingDialogFragment newInstance(int dayOfWeek, int goal) {
            Bundle args = new Bundle();
            args.putInt(ARG_DAY_OF_WEEK, dayOfWeek);
            args.putInt(ARG_GOAL, goal);
            GoalSettingDialogFragment fragment = new GoalSettingDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, requireArguments().getInt(ARG_DAY_OF_WEEK));
            String dayOfWeek = calendar.getDisplayName(
                    Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.goal_setting_dialog, null);
            EditText goalEdit = dialogView.findViewById(R.id.goal_edit);

            int goal = requireArguments().getInt(ARG_GOAL);
            if (goal != -1) {
                goalEdit.setText(String.valueOf(goal));
                goalEdit.selectAll();
            }
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle(dayOfWeek + " ?????? ??????")
                    .setView(dialogView)
                    .setPositiveButton("??????", (dialog1, which) -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt(ARG_DAY_OF_WEEK, requireArguments().getInt(ARG_DAY_OF_WEEK));
                        String s = goalEdit.getText().toString();
                        bundle.putInt(ARG_GOAL, s.isEmpty() ? -1 : Integer.parseInt(s));
                        getParentFragmentManager().setFragmentResult("dialogResult", bundle);
                    })
                    .create();
            goalEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    boolean enabled = true;
                    try {
                        if (Integer.parseInt(s.toString()) < 0) {
                            enabled = false;
                        }
                    } catch (NumberFormatException e) {
                        enabled = false;
                    }
                    button.setEnabled(enabled);
                }
            });
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            return dialog;
        }
    }

    private class GoalListAdapter extends RecyclerView.Adapter<GoalListAdapter.ViewHolder> {
        private final DateFormat dateFormat = new SimpleDateFormat("M/d (E)", Locale.getDefault());
        private final Date today;
        List<GoalListItem> items;

        GoalListAdapter() {
            setHasStableIds(true);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            today = calendar.getTime();
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
            GoalListItem item = items.get(position);

            if (item.getDate().equals(today)) {
                holder.dateText.setText(dateFormat.format(item.getDate()));
                holder.dateText.setTypeface(null, Typeface.BOLD);
            } else {
                holder.dateText.setText(dateFormat.format(item.getDate()));
                holder.dateText.setTypeface(null, Typeface.NORMAL);
            }

            if (item.getGoal() == -1) {
                holder.goalText.setText(resources.getString(R.string.goal_is_not_set));
            } else {
                holder.goalText.setText(resources.getString(R.string.goal, item.getGoal()));
            }

            holder.solvedText.setText(resources.getString(R.string.solved_quizzes,
                    item.getSolved()));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).getDate().getTime();
        }

        public void changeList(List<GoalListItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        public int getTodayPosition() {
            //noinspection ComparatorCombinators
            return Collections.binarySearch(items, new GoalListItem(today, 0, 0),
                    (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            TextView goalText;
            TextView solvedText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.date_text);
                goalText = itemView.findViewById(R.id.goal_text);
                solvedText = itemView.findViewById(R.id.solved_text);

                itemView.setOnClickListener(v -> {
                    GoalListItem item = items.get(getAdapterPosition());
                    if (item.getDate().before(today)) {
                        Toast.makeText(getContext(),
                                "?????? ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(item.getDate());
                    GoalSettingDialogFragment
                            .newInstance(calendar.get(Calendar.DAY_OF_WEEK), item.getGoal())
                            .show(getChildFragmentManager(), null);
                });
            }
        }
    }
}
