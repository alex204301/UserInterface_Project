package com.example.userinterface_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;

import java.util.ArrayList;
import java.util.List;

public class SubjectiveQuizFragment extends Fragment {

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_IS_TYPE_WORD = "isTypeWord";
    private static final String CURRENT_QUIZ = "currentQuiz";

    private ArrayList<Word> words; // 전체 단어 목록
    private List<Word> questions;
    private int currentQuiz = -1;
    private boolean isTypeWord;
    private final ArrayList<Word> wrong = new ArrayList<>();

    private TextView questionTextView;
    private EditText editText;
    private Button checkButton;
    private TextView correctAnswer;
    private Button nextButton;
    private WordDbHelper dbHelper;

    private int right = 0;

    public SubjectiveQuizFragment() {
    }

    public static SubjectiveQuizFragment newInstance(
            SubjectiveQuizActivity activity, List<Word> questions, boolean isTypeWord) {
        ArrayList<Word> words = activity.getWords();
        int[] questionIds = new int[questions.size()]; // bundle 저장용
        int i = 0;
        for (Word w : questions) {
            questionIds[i++] = (int) w.getId();
        }

        SubjectiveQuizFragment fragment = new SubjectiveQuizFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_QUESTIONS, questionIds);
        args.putBoolean(ARG_IS_TYPE_WORD, isTypeWord);
        fragment.setArguments(args);

        fragment.words = words;
        fragment.questions = questions;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SubjectiveQuizActivity activity = (SubjectiveQuizActivity) requireActivity();
        dbHelper = WordDbHelper.getInstance(activity);
        Bundle args = requireArguments();
        if (words == null) {
            words = activity.getWords();
            questions = activity.getWordsByIds(args.getIntArray(ARG_QUESTIONS));
        }
        isTypeWord = args.getBoolean(ARG_IS_TYPE_WORD);

        if (savedInstanceState != null) {
            currentQuiz = savedInstanceState.getInt(CURRENT_QUIZ, currentQuiz);
        } else {
            currentQuiz = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjective_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        questionTextView = view.findViewById(R.id.word_textview);
        editText = view.findViewById(R.id.edit_text);
        checkButton = view.findViewById(R.id.check_button);
        checkButton.setOnClickListener(v -> onCheckButtonClick());
        correctAnswer = view.findViewById(R.id.correct_answer);
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            if (currentQuiz + 1 >= questions.size()) {
                SubjectiveQuizActivity activity = (SubjectiveQuizActivity) requireActivity();
                activity.setQuizProgress(1, 1);
                activity.showResult(right, questions.size() - right, wrong);
            } else {
                currentQuiz++;
                showCurrentQuiz();
            }
        });
        currentQuiz = 0;
        showCurrentQuiz();
    }

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n"})
    private void onCheckButtonClick(){
        Word currentQuestion = questions.get(currentQuiz);
        editText.setClickable(false);
        editText.setEnabled(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        checkButton.setVisibility(View.GONE);
        Word word = (Word) questionTextView.getTag();
        if(isTypeWord) {// eqitText 수정 불가능하게하고 색칠하기, 틀렸으면 맞는 답 출력
            if(word.getWord().equals(editText.getText().toString())) {
                editText.setBackground(this.getResources().getDrawable(R.drawable.edit_text_correct));
                right++;
                dbHelper.incrementCountCorrect(currentQuestion.getId());
            }
            else {
                editText.setBackground(this.getResources().getDrawable(R.drawable.edit_text_wrong));
                correctAnswer.setVisibility(View.VISIBLE);
                correctAnswer.setText("답 : " + word.getWord());
                wrong.add(currentQuestion);
                dbHelper.incrementCountIncorrect(currentQuestion.getId());
            }
        }
        else {
            if(word.getMeaning().equals(editText.getText().toString())) {
                editText.setBackground(this.getResources().getDrawable(R.drawable.edit_text_correct));
                right++;
                dbHelper.incrementCountCorrect(currentQuestion.getId());
            }
            else {
                editText.setBackground(this.getResources().getDrawable(R.drawable.edit_text_wrong));
                correctAnswer.setVisibility(View.VISIBLE);
                correctAnswer.setText("답 : " + word.getMeaning());
                wrong.add(currentQuestion);
                dbHelper.incrementCountIncorrect(currentQuestion.getId());
            }
        }
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setEnabled(true);
    }

    @SuppressLint({"ResourceAsColor", "ResourceType", "UseCompatLoadingForDrawables"})
    private void showCurrentQuiz() {
        SubjectiveQuizActivity activity = (SubjectiveQuizActivity) requireActivity();
        activity.setQuizProgress(currentQuiz, questions.size());
        Word word = questions.get(currentQuiz);
        questionTextView.setText(isTypeWord ? word.getMeaning() : word.getWord());
        questionTextView.setTag(word);
        editText.setClickable(true);
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setBackground(this.getResources().getDrawable(R.drawable.edit_text_border));
        editText.setText(null);
        editText.setHint(isTypeWord ? "단어를 입력해주세요" : "뜻을 입력해주세요");
        checkButton.setVisibility(View.VISIBLE);
        correctAnswer.setVisibility(View.GONE);

        // 다음 버튼
        nextButton.setVisibility(View.GONE);
    }
}
