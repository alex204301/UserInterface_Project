package com.example.userinterface_project;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddWordActivity extends AppCompatActivity {

    private EditText editWord;
    private EditText editMeaning;

    private RadioGroup radioGroup;

    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        editWord = findViewById(R.id.edit_word);
        editMeaning = findViewById(R.id.edit_meaning);

        radioGroup = findViewById(R.id.radio_group_difficulty);

        buttonAdd = findViewById(R.id.button_add);

        if (savedInstanceState == null) {
            radioGroup.check(R.id.radio_hard);
        } // 첫 실행 시 기본값으로 난이도 어려움 선택

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
    public void onBackPressed() {
        if (editWord.getText().length() != 0 || editMeaning.getText().length() != 0) {
            // 단어나 뜻이 입력된 채로 뒤로가기 클릭할 경우 다이얼로그 띄움
            new ExitDialogFragment().show(getSupportFragmentManager(), null);
        } else {
            super.onBackPressed();
        }
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