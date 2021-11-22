package com.example.userinterface_project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.userinterface_project.db.Note;
import com.example.userinterface_project.db.WordDbHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class Tab2Fragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyText;

    public static Tab2Fragment newInstance() {
        return new Tab2Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab2, container, false);

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("단어장 선택");
        actionBar.setDisplayHomeAsUpEnabled(false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        emptyText = rootView.findViewById(R.id.empty_text);

        refreshList();
        return rootView;
    }

    /**
     * DB에서 목록을 읽은 후 화면 새로 고침
     */
    public void refreshList() {
        Tab2Fragment.NoteListAdapter adapter = (Tab2Fragment.NoteListAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new Tab2Fragment.NoteListAdapter();
            recyclerView.setAdapter(adapter);
        }
        List<Note> list = WordDbHelper.getInstance(getContext()).getNoteList();
        adapter.changeList(list);

        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    private class NoteListAdapter extends RecyclerView.Adapter<Tab2Fragment.NoteListAdapter.ViewHolder> {
        private final DateFormat dateFormat = DateFormat.getDateInstance();
        List<Note> list;

        private NoteListAdapter() {
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public Tab2Fragment.NoteListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(
                    R.layout.note_list_item, parent, false
            );

            return new Tab2Fragment.NoteListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Tab2Fragment.NoteListAdapter.ViewHolder holder, int position) {
            Note note = list.get(position);
            holder.text1.setText(note.getName());

            Date date = note.getLastStudied();
            holder.text2.setText(
                    holder.itemView.getContext().getString(R.string.last_studied,
                            date == null ? "" : dateFormat.format(date)));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).getId();
        }

        public void changeList(@NonNull List<Note> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1;
            TextView text2;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(R.id.text1);
                text2 = itemView.findViewById(R.id.text2);

                itemView.setOnClickListener(v -> {
                    MainActivity activity = (MainActivity) itemView.getContext();
                    activity.showQuizSelect(getItemId());
                });
            }
        }
    }
}