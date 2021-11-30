package com.example.userinterface_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.userinterface_project.db.WordDbHelper;


public class SelectQuizFragment extends Fragment implements View.OnClickListener {

    public static final String NOTE_ID = "noteId";
    public static String Difficulty;
    public static  String WordMeaning;
    private long noteId;

    Button btnType1;
    Button btnType2;
    Button btnType3;
    RadioGroup radioGroupDifficulty;
    RadioGroup radioGroupWord;

    public static SelectQuizFragment newInstance(long noteId) {
        SelectQuizFragment selectQuizFragment = new SelectQuizFragment();
        Bundle args = new Bundle();
        args.putLong(NOTE_ID, noteId);
        selectQuizFragment.setArguments(args);
        return selectQuizFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_select_quiz, container,false);
        WordDbHelper dbHelper = WordDbHelper.getInstance(getContext());

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        noteId = requireArguments().getLong(NOTE_ID);
        actionBar.setTitle(dbHelper.getNote(noteId).getName() + " - 퀴즈 설정");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        btnType1 = rootView.findViewById(R.id.button_type1);
        btnType2 = rootView.findViewById(R.id.button_type2);
        btnType3 = rootView.findViewById(R.id.button_type3);
        radioGroupDifficulty = rootView.findViewById(R.id.radio_group_difficulty);
        radioGroupWord = rootView.findViewById(R.id.radio_group_word);

        // 퀴즈 유형에 따른 클릭 이벤트
        btnType1.setOnClickListener(this);
        btnType2.setOnClickListener(this);
        btnType3.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int checkedDifficulty = radioGroupDifficulty.getCheckedRadioButtonId();
        int checkedWord = radioGroupWord.getCheckedRadioButtonId();

        switch (checkedDifficulty){
            case R.id.radio_easy:
                Difficulty = "easy";
                break;
            case R.id.radio_normal:
                Difficulty = "normal";
                break;
            case R.id.radio_hard:
                Difficulty = "hard";
                break;
            case R.id.radio_all:
                Difficulty = "all";
                break;
        }

        if(checkedWord == R.id.radio_word)
            WordMeaning = "word";
        else if (checkedWord == R.id.radio_meaning)
            WordMeaning = "meaning";

        if (v == btnType1) {
            Intent intent = new Intent(getActivity(), SubjectiveQuizActivity.class);
            intent.putExtra(SubjectiveQuizActivity.EXTRA_NOTE_ID, noteId);
            intent.putExtra("Difficulty", Difficulty);
            intent.putExtra("WordMeaning", WordMeaning);
            startActivity(intent);
        } else if (v == btnType2) {
            Intent intent = new Intent(getActivity(), MultipleChoiceQuizActivity.class);
            intent.putExtra(MultipleChoiceQuizActivity.EXTRA_NOTE_ID, noteId);
            intent.putExtra("Difficulty", Difficulty);
            intent.putExtra("WordMeaning", WordMeaning);
            startActivity(intent);
        } else if (v == btnType3) {
//            Intent intent = new Intent((MainActivity) getActivity(), 실행시킬 액티비티 이름);
//            intent.putExtra("Difficulty", Difficulty);
//            intent.putExtra("WordMeaning", WordMeaning);
//            startActivity(intent);
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