package com.example.userinterface_project;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QuizResultFragment extends Fragment {
    private static final String ARG_RIGHT = "right";
    private static final String ARG_WRONG = "wrong";

    public QuizResultFragment() {
    }

    /**
     * @param right 맞힌 문제 수
     * @param wrong 틀린 문제 수
     */
    public static QuizResultFragment newInstance(int right, int wrong) {
        QuizResultFragment fragment = new QuizResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RIGHT, right);
        args.putInt(ARG_WRONG, wrong);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = requireArguments();
        int right = args.getInt(ARG_RIGHT);
        int wrong = args.getInt(ARG_WRONG);

        TextView resultTextView = view.findViewById(R.id.result_textview);
        Button retryButton = view.findViewById(R.id.retry_button);
        Button retryButton2 = view.findViewById(R.id.retry_button_2);
        Button exitButton = view.findViewById(R.id.exit_button);

        resultTextView.setText(getString(R.string.quiz_result, right, right + wrong));

        OnResultButtonClickListener listener = (OnResultButtonClickListener) requireActivity();
        retryButton.setOnClickListener(v -> listener.onRetryButtonClick());
        if (wrong == 0)
            retryButton2.setEnabled(false);
        else
            retryButton2.setOnClickListener(v -> listener.onRetryButton2Click());
        exitButton.setOnClickListener(v -> listener.onExitButtonClick());
    }

    interface OnResultButtonClickListener {
        void onRetryButtonClick();

        void onRetryButton2Click();

        default void onExitButtonClick() {
            ((Activity) this).finish();
        }
    }
}