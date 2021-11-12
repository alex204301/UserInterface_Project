package com.example.userinterface_project;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface_project.db.Note;
import com.example.userinterface_project.db.WordDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class Tab1Fragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyText;

    public static Tab1Fragment newInstance() {
        return new Tab1Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab1, container, false);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("단어장");
        actionBar.setDisplayHomeAsUpEnabled(false);
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
                                WordDbHelper dbHelper = WordDbHelper.getInstance(getContext());
                                dbHelper.createNote(noteName);
                                refreshList();
                            }
                        })
                        .show();
            }
        });
        recyclerView = rootView.findViewById(R.id.recycler_view);
        emptyText = rootView.findViewById(R.id.empty_text);
        refreshList();
        return rootView;
    }

    /**
     * DB에서 목록을 읽은 후 화면 새로 고침
     */
    public void refreshList() {
        NoteListAdapter adapter = (NoteListAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new NoteListAdapter();
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

    private static class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {
        private final DateFormat dateFormat = DateFormat.getDateInstance();
        List<Note> list;

        private NoteListAdapter() {
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(
                    R.layout.note_list_item, parent, false
            );

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

        private static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1;
            TextView text2;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(R.id.text1);
                text2 = itemView.findViewById(R.id.text2);

                itemView.setOnClickListener(v -> {
                    MainActivity activity = (MainActivity) itemView.getContext();
                    activity.showWordList(getItemId());
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage("선택한 단어장을 삭제하시겠습니까?")
                                .setNegativeButton("취소", null)
                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        WordDbHelper dbHelper = WordDbHelper.getInstance(v.getContext());
                                        dbHelper.removeNote(getItemId());
                                        MainActivity activity = (MainActivity) itemView.getContext();
                                        activity.replaceFragment(new Tab1Fragment());
                                    }
                                })
                                .show();
                        return false;
                    }
                });
            }
        }
    }
}