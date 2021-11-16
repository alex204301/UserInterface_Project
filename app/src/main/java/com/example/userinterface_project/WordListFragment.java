package com.example.userinterface_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WordListFragment extends Fragment {
    public static final String NOTE_ID = "noteId";

    private RecyclerView recyclerView;
    private TextView emptyText;

    private long noteId;

    private int sortBy; //0:등록순  1:최근 등록순  2:A-Z  3:Z-A  4:많이 틀린 순
    private boolean showEasy;
    private boolean showNormal;
    private boolean showHard;
    private boolean showWord;
    private boolean showMeaning;

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //다시 이 화면으로 돌아왔을 때 목록 새로 고침
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if(result.getData() != null) { //정렬필터 화면에서 데이터 받아옴
                        sortBy = result.getData().getIntExtra("sortBy", 0);
                        showEasy = result.getData().getBooleanExtra("showEasy", true);
                        showNormal = result.getData().getBooleanExtra("showNormal", true);
                        showHard = result.getData().getBooleanExtra("showHard", true);
                        showWord = result.getData().getBooleanExtra("showWord", true);
                        showMeaning = result.getData().getBooleanExtra("showMeaning", true);
                    }
                    refreshList();
                }
            }
    );

    public static WordListFragment newInstance(long noteId) {
        WordListFragment wordListFragment = new WordListFragment();
        Bundle args = new Bundle();
        args.putLong(NOTE_ID, noteId);
        wordListFragment.setArguments(args);
        return wordListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_word_list, container,false);
        WordDbHelper dbHelper = WordDbHelper.getInstance(getContext());

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        noteId = requireArguments().getLong(NOTE_ID);
        actionBar.setTitle(dbHelper.getNote(noteId).getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        sortBy = 0;
        showEasy = true;
        showNormal = true;
        showHard = true;
        showWord = true;
        showMeaning = true;

        FloatingActionButton fab = rootView.findViewById(R.id.tab1_plus_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((MainActivity)getActivity(), AddWordActivity.class);
                intent.putExtra(AddWordActivity.EXTRA_NOTE_ID, noteId); // note id 전달
                resultLauncher.launch(intent);
            }
        });

        recyclerView = rootView.findViewById(R.id.recycler_view);
        emptyText = rootView.findViewById(R.id.empty_text);

        // divider 추가
        recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        refreshList();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu1, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }else if(item.getItemId() == R.id.menu1) {
            Intent intent = new Intent((MainActivity)getActivity(), FilterActivity.class);
            intent.putExtra("sortBy", sortBy);
            intent.putExtra("showEasy", showEasy);
            intent.putExtra("showNormal", showNormal);
            intent.putExtra("showHard", showHard);
            intent.putExtra("showWord", showWord);
            intent.putExtra("showMeaning", showMeaning);
            resultLauncher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DB에서 목록을 읽은 후 화면 새로 고침
     */
    private void refreshList() {
        WordListAdapter adapter = (WordListAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new WordListAdapter();
            recyclerView.setAdapter(adapter);
        }
        List<Word> list = WordDbHelper.getInstance(getContext()).getWordList(noteId);

        //필터
        if(!showEasy)
            for(int i = list.size() - 1; i >= 0; i--)
                if(list.get(i).getDifficulty() == Word.DIFFICULTY_EASY) {
                    list.remove(i);
                }
        if(!showNormal)
            for(int i = list.size() - 1; i >= 0; i--)
                if(list.get(i).getDifficulty() == Word.DIFFICULTY_NORMAL) {
                    list.remove(i);
                }
        if(!showHard)
            for(int i = list.size() - 1; i >= 0; i--)
                if(list.get(i).getDifficulty() == Word.DIFFICULTY_HARD) {
                    list.remove(i);
                }
        //정렬
        switch (sortBy) {
            case 1 :
                Collections.reverse(list);
                break;
            case 2:
                Comparator<Word> wordAsc = new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o1.getWord().compareTo(o2.getWord());
                    }
                };
                Collections.sort(list, wordAsc);
                break;
            case 3:
                Comparator<Word> wordDsc = new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o2.getWord().compareTo(o1.getWord());
                    }
                };
                Collections.sort(list, wordDsc);
                break;
            case 4:
                Comparator<Word> countIncorrectDsc = new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o2.getCountIncorrect() - o1.getCountIncorrect();
                    }
                };
                Collections.sort(list, countIncorrectDsc);
                break;
        }


        adapter.changeList(list);

        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    // 리사이클러뷰 어댑터
    private class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {
        List<Word> list;
        private Context context;

        private WordListAdapter() {
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(
                    R.layout.word_list_item, parent, false
            );

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Word word = list.get(position);

            holder.text1.setText(word.getWord());
            holder.text2.setText(word.getMeaning());
            holder.text3.setText(context.getString(R.string.count,
                    word.getCountCorrect(), word.getCountIncorrect()));

            //단어,뜻 가리기
            if(!showWord)
                holder.text1.setVisibility(View.INVISIBLE);
            else
                holder.text1.setVisibility(View.VISIBLE);
            if(!showMeaning)
                holder.text2.setVisibility(View.INVISIBLE);
            else
                holder.text2.setVisibility(View.VISIBLE);

            int difficultyText = -1;
            int difficultyColor = -1;
            switch (word.getDifficulty()) {
                case Word.DIFFICULTY_EASY:
                    difficultyText = R.string.difficulty_easy;
                    difficultyColor = R.color.difficulty_easy;
                    break;
                case Word.DIFFICULTY_NORMAL:
                    difficultyText = R.string.difficulty_normal;
                    difficultyColor = R.color.difficulty_normal;
                    break;
                case Word.DIFFICULTY_HARD:
                    difficultyText = R.string.difficulty_hard;
                    difficultyColor = R.color.difficulty_hard;
                    break;
            }

            holder.badge.setText(context.getString(difficultyText));
            holder.badge.setBackgroundTintList(
                    AppCompatResources.getColorStateList(context, difficultyColor));

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent((MainActivity)getActivity(), ModifyWordActivity.class);
                    intent.putExtra(AddWordActivity.EXTRA_NOTE_ID, noteId); // note id 전달
                    intent.putExtra("wordId", list.get(holder.getAdapterPosition()).getId());
                    resultLauncher.launch(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).getId();
        }

        /**
         * 화면에 보이는 리스트 변경
         */
        public void changeList(@NonNull List<Word> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1;
            TextView text2;
            TextView text3;
            TextView badge;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(R.id.text1);
                text2 = itemView.findViewById(R.id.text2);
                text3 = itemView.findViewById(R.id.text3);
                badge = itemView.findViewById(R.id.badge);
            }
        }
    }
}