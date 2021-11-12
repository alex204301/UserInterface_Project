package com.example.userinterface_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.userinterface_project.db.Note;
import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;

import java.util.List;

public class ModifyWordActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_NOTE_ID = "noteId";
    private long noteId;
    private long wordId;

    private EditText editWord;
    private EditText editMeaning;
    private RadioGroup radioGroup;
    private Button modifyBtn;
    private Word word;

    private WordDbHelper dbHelper = WordDbHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_word);

        ActionBar actionBar = getSupportActionBar();
        noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
        Note note = dbHelper.getNote(noteId);
        actionBar.setSubtitle(note.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);

        List<Word> list = WordDbHelper.getInstance(this).getWordList(noteId);
        int position = getIntent().getIntExtra("ItemPosition", -1);
        word = list.get(position);
        wordId = word.getId();

        editWord = findViewById(R.id.edit_word);
        editWord.setText(word.getWord());
        editMeaning= findViewById(R.id.edit_meaning);
        editMeaning.setText(word.getMeaning());
        radioGroup = findViewById(R.id.radio_group_difficulty);
        switch (word.getDifficulty()) {
            case Word.DIFFICULTY_EASY:
                radioGroup.check(R.id.radio_easy);
                break;
            case Word.DIFFICULTY_NORMAL:
                radioGroup.check(R.id.radio_normal);
                break;
            case Word.DIFFICULTY_HARD:
                radioGroup.check(R.id.radio_hard);
                break;
        }

        modifyBtn = findViewById(R.id.button_modify);
        modifyBtn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int difficulty;
        int checked = radioGroup.getCheckedRadioButtonId();

        if (checked == R.id.radio_easy)
            difficulty = Word.DIFFICULTY_EASY;
        else if (checked == R.id.radio_normal)
            difficulty = Word.DIFFICULTY_NORMAL;
        else
            difficulty = Word.DIFFICULTY_HARD;

        dbHelper.editWord(wordId, editWord.getText().toString(),
                editMeaning.getText().toString(),
                difficulty);

        setResult(RESULT_OK);

        finish();
    }

    @Override
    public void onBackPressed() {
        if (!editWord.getText().toString().equals(word.getWord()) || !editMeaning.getText().toString().equals(word.getMeaning()) || !difficulityModifyCheck()) {
            // 단어나 뜻이 수정된 채로 뒤로가기 클릭할 경우 다이얼로그 띄움
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm_message)
                    .setPositiveButton(R.string.exit,
                            (dialog, which) -> finish())
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    public boolean difficulityModifyCheck(){
        switch (word.getDifficulty()) {
            case Word.DIFFICULTY_EASY:
                if(radioGroup.getCheckedRadioButtonId() != R.id.radio_easy)
                    return false;
                break;
            case Word.DIFFICULTY_NORMAL:
                if(radioGroup.getCheckedRadioButtonId() != R.id.radio_normal)
                    return false;
                break;
            case Word.DIFFICULTY_HARD:
                if(radioGroup.getCheckedRadioButtonId() != R.id.radio_hard)
                    return false;
                break;
        }
        return true;
    }
}