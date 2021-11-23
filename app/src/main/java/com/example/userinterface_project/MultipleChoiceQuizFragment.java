package com.example.userinterface_project;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.userinterface_project.db.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MultipleChoiceQuizFragment extends Fragment {
    public static final int NUMBER_OF_CHOICES = 4; // 선택지 개수

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_IS_TYPE_WORD = "isTypeWord";

    private final Random random = new Random();
    private final Button[] choiceButtons = new Button[NUMBER_OF_CHOICES];

    private ArrayList<Word> words; // 전체 단어 목록
    private ArrayList<Word> questions;
    private int currentQuiz = -1;
    private boolean isTypeWord;
    private ArrayList<Word> answers; // 선택지

    private TextView questionTextView;
    private Button nextButton;

    public MultipleChoiceQuizFragment() {
    }


    public static MultipleChoiceQuizFragment newInstance(
            MultipleChoiceQuizActivity activity, ArrayList<Word> questions, boolean isTypeWord) {
        ArrayList<Word> words = activity.getWords();
        int[] questionIds = new int[questions.size()]; // bundle 저장용
        int i = 0;
        for (Word w : questions) {
            questionIds[i++] = (int) w.getId();
        }

        MultipleChoiceQuizFragment fragment = new MultipleChoiceQuizFragment();
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
        MultipleChoiceQuizActivity activity = (MultipleChoiceQuizActivity) requireActivity();
        Bundle args = requireArguments();
        if (words == null) {
            words = activity.getWords();
            questions = activity.getWordsByIds(args.getIntArray(ARG_QUESTIONS));
        }
        isTypeWord = args.getBoolean(ARG_IS_TYPE_WORD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multiple_choice_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        questionTextView = view.findViewById(R.id.word_textview);
        LinearLayout layout = view.findViewById(R.id.choices_layout);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < NUMBER_OF_CHOICES; i++) {
            int index = i;
            Button b = (Button) inflater.inflate(R.layout.choice_button, layout, false);
            layout.addView(b);
            b.setOnClickListener(v -> onButtonClick(index));
            choiceButtons[i] = b;
        }
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            currentQuiz++;
            showCurrentQuiz();
        });
        currentQuiz = 0;
        showCurrentQuiz();
    }

    private void onButtonClick(int index) {
        Context context = requireContext();
        Word question = (Word) questionTextView.getTag();
        for (int i = 0; i < NUMBER_OF_CHOICES; i++) {
            Button button = choiceButtons[i];
            button.setClickable(false);
            Word word = (Word) button.getTag();
            if (word.getId() == question.getId()) {
                button.setBackgroundTintList(ContextCompat.getColorStateList(
                        context, R.color.choice_button_color_correct));
            } else if (index == i) {
                button.setBackgroundTintList(ContextCompat.getColorStateList(
                        context, R.color.choice_button_color_incorrect));
            }
        }

        nextButton.setEnabled(true);
    }

    private void showCurrentQuiz() {
        MultipleChoiceQuizActivity activity = (MultipleChoiceQuizActivity) requireActivity();
        activity.setQuizProgress(currentQuiz, questions.size());
        Word word = questions.get(currentQuiz);
        questionTextView.setText(isTypeWord ? word.getMeaning() : word.getWord());
        questionTextView.setTag(word);

        // 선택지 만들기
        answers = new ArrayList<>(NUMBER_OF_CHOICES);
        answers.add(word); // 선택지에 답 추가

        outer:
        // 나머지 랜덤으로 추가
        for (int i = 1; i < NUMBER_OF_CHOICES; i++) {
            Word add = words.get(random.nextInt(words.size()));
            for (Word w : answers) { // 이미 동일한 id의 word가 추가되었는지 검사
                if (w.getId() == add.getId()) {
                    i--;
                    continue outer;
                }
            }
            answers.add(add);
        }
        Collections.shuffle(answers); // 보기 순서 섞기

        // 버튼 설정
        ColorStateList choiceButtonColor =
                ContextCompat.getColorStateList(activity, R.color.choice_button_color);
        for (int i = 0; i < NUMBER_OF_CHOICES; i++) {
            Word item = answers.get(i);
            Button button = choiceButtons[i];
            if (isTypeWord) {
                button.setText(item.getWord());
            } else {
                button.setText(item.getMeaning());
            }
            button.setTag(item);

            button.setClickable(true);
            button.setBackgroundTintList(choiceButtonColor);
        }

        // 다음 버튼
        nextButton.setEnabled(false);
    }
}