package com.example.userinterface_project;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.userinterface_project.db.Note;
import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;

public class AddWordActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "noteId";
    private final WordDbHelper dbHelper = WordDbHelper.getInstance(this);
    private EditText editWord;
    private EditText editMeaning;
    private RadioGroup radioGroup;
    private Button buttonAdd;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        editWord = findViewById(R.id.edit_word);
        editMeaning = findViewById(R.id.edit_meaning);
        radioGroup = findViewById(R.id.radio_group_difficulty);
        buttonAdd = findViewById(R.id.button_add);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtonEnabled();
            }
        };
        editWord.addTextChangedListener(textWatcher);
        editMeaning.addTextChangedListener(textWatcher);
        radioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> updateButtonEnabled()
        );
        updateButtonEnabled();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
            Note note = dbHelper.getNote(noteId);
            actionBar.setSubtitle(note.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buttonAdd.setOnClickListener(v -> onAddButtonClick());
    }

    /**
     * 단어, 뜻, 난이도 모두 입력되었으면 버튼 활성화시키고,
     * 하나라도 입력되지 않으면 비활성화시킴.
     */
    private void updateButtonEnabled() {
        boolean enabled = editWord.getText().length() != 0 &&
                editMeaning.getText().length() != 0 &&
                radioGroup.getCheckedRadioButtonId() != -1;

        buttonAdd.setEnabled(enabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (editWord.getText().length() != 0 || editMeaning.getText().length() != 0) {
            // 단어나 뜻이 입력된 채로 뒤로가기 클릭할 경우 다이얼로그 띄움
            new ExitDialogFragment().show(getSupportFragmentManager(), null);
        } else {
            super.onBackPressed();
        }
    }

    private void onAddButtonClick() {
        int difficulty;
        int checked = radioGroup.getCheckedRadioButtonId();

        if (checked == R.id.radio_easy)
            difficulty = Word.DIFFICULTY_EASY;
        else if (checked == R.id.radio_normal)
            difficulty = Word.DIFFICULTY_NORMAL;
        else
            difficulty = Word.DIFFICULTY_HARD;

        dbHelper.addWord(noteId, editWord.getText().toString(),
                editMeaning.getText().toString(),
                difficulty);

        setResult(RESULT_OK);
        finish();
    }

    public static class ExitDialogFragment extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AddWordActivity activity = (AddWordActivity) requireActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(R.string.confirm_message)
                    .setPositiveButton(R.string.exit,
                            (dialog, which) -> activity.finish())
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }
}