package com.example.userinterface_project.db;

import android.provider.BaseColumns;

public class DbContract {
    public static final class Notes implements BaseColumns {
        public static final String TABLE_NAME = "notes";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAST_STUDIED = "last_studied";
    }

    public static final class Alarms implements  BaseColumns {
        public static final String TABLE_NAME = "alarms";

        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTE = "minute";
    }

    public static final class Words implements BaseColumns {
        public static final String TABLE_NAME = "words";

        public static final String COLUMN_NOTE_ID = "note_id";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_MEANING = "meaning";
        public static final String COLUMN_COUNT_CORRECT = "correct_count";
        public static final String COLUMN_COUNT_INCORRECT = "incorrect_count";
        public static final String COLUMN_DIFFICULTY = "difficulty";
    }
}
