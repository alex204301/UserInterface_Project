package com.example.userinterface_project.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WordDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "word.db";
    private static WordDbHelper instance;

    private WordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static WordDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new WordDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DbContract.Notes.TABLE_NAME + " (" +
                DbContract.Notes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.Notes.COLUMN_NAME + " TEXT, " +
                DbContract.Notes.COLUMN_LAST_STUDIED + " INTEGER)"); // notes(단어장) 테이블

        db.execSQL("CREATE TABLE " + DbContract.Words.TABLE_NAME + " (" +
                DbContract.Words._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.Words.COLUMN_NOTE_ID + " INTEGER, " +
                DbContract.Words.COLUMN_WORD + " TEXT, " +
                DbContract.Words.COLUMN_MEANING + " TEXT, " +
                DbContract.Words.COLUMN_COUNT_CORRECT + " INTEGER DEFAULT 0, " +
                DbContract.Words.COLUMN_COUNT_INCORRECT + " INTEGER DEFAULT 0, " +
                DbContract.Words.COLUMN_DIFFICULTY + " INTEGER)"); // words(단어 목록) 테이블

        db.execSQL("CREATE TABLE " + DbContract.Alarms.TABLE_NAME + " (" +
                DbContract.Alarms._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.Alarms.COLUMN_HOUR + " INTEGER, " +
                DbContract.Alarms.COLUMN_MINUTE + " INTEGER)");  // alarms(알람) 테이블
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 새 알람 만들기
     */
    public long createAlarm(int hour, int minute) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Alarms.COLUMN_HOUR, hour);
        values.put(DbContract.Alarms.COLUMN_MINUTE, minute);

        return db.insert(DbContract.Alarms.TABLE_NAME, null, values);
    }

    public Alarm getAlarm(long alarmId) {
        return getAlarmList(alarmId).get(0);
    }

    /**
     * 알람 목록 불러오기
     */
    public List<Alarm> getAlarmList() {
        return getAlarmList(-1);
    }

    private List<Alarm> getAlarmList(long alarmId) {
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(DbContract.Alarms.TABLE_NAME);
        if (alarmId != -1) {
            sql.append(" WHERE ")
                    .append(DbContract.Alarms._ID)
                    .append("=?");
        }
        Cursor cursor = db.rawQuery(sql.toString(),
                alarmId == -1 ? null : new String[]{String.valueOf(alarmId)});

        ArrayList<Alarm> alarms = new ArrayList<>(cursor.getCount());

        int columnId = cursor.getColumnIndex(DbContract.Alarms._ID);
        int columnHour = cursor.getColumnIndex(DbContract.Alarms.COLUMN_HOUR);
        int columnMinute = cursor.getColumnIndex(DbContract.Alarms.COLUMN_MINUTE);
        while (cursor.moveToNext()) {
            alarms.add(new Alarm(
                    cursor.getLong(columnId),
                    cursor.getInt(columnHour),
                    cursor.getInt(columnMinute)
            ));
        }

        cursor.close();

        return alarms;
    }

    /**
     * 알람 수정
     */
    public void editAlarm(long alarmId, int hour, int minute) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Alarms.COLUMN_HOUR, hour);
        values.put(DbContract.Alarms.COLUMN_MINUTE, minute);

        db.update(DbContract.Alarms.TABLE_NAME, values, DbContract.Alarms._ID + "=?",
                new String[]{String.valueOf(alarmId)});
    }

    /**
     * 알람 삭제
     */
    public void deleteAlarm(long alarmId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DbContract.Alarms.TABLE_NAME, DbContract.Alarms._ID + "=?",
                new String[]{String.valueOf(alarmId)}); // alarm 삭제
    }

    /**
     * 새 단어장 만들기
     */
    public long createNote(String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Notes.COLUMN_NAME, name);

        return db.insert(DbContract.Notes.TABLE_NAME, null, values);
    }

    /**
     * 단어장 이름 변경
     */
    public void renameNote(long noteId, String newName) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Notes.COLUMN_NAME, newName);

        db.update(DbContract.Notes.TABLE_NAME, values,
                DbContract.Notes._ID + "=?",
                new String[]{String.valueOf(noteId)});
    }

    /**
     * 최근 학습일 변경
     */
    public void updateLastStudiedDate(long noteId, Date date) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Notes.COLUMN_LAST_STUDIED, date.getTime());

        db.update(DbContract.Notes.TABLE_NAME, values,
                DbContract.Notes._ID + "=?",
                new String[]{String.valueOf(noteId)});
    }

    /**
     * 단어장 삭제
     */
    public void deleteNote(long noteId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DbContract.Notes.TABLE_NAME, DbContract.Notes._ID + "=?",
                new String[]{String.valueOf(noteId)}); // note 삭제

        db.delete(DbContract.Words.TABLE_NAME,
                DbContract.Words.COLUMN_NOTE_ID + "=?",
                new String[]{String.valueOf(noteId)}); // word 삭제
    }

    public Note getNote(long noteId) {
        return getNoteList(noteId).get(0);
    }

    /**
     * 단어장 목록 불러오기
     */
    public List<Note> getNoteList() {
        return getNoteList(-1);
    }

    private List<Note> getNoteList(long noteId) {
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(DbContract.Notes.TABLE_NAME);
        if (noteId != -1) {
            sql.append(" WHERE ")
                    .append(DbContract.Notes._ID)
                    .append("=?");
        }
        Cursor cursor = db.rawQuery(sql.toString(),
                noteId == -1 ? null : new String[]{String.valueOf(noteId)});

        ArrayList<Note> notes = new ArrayList<>(cursor.getCount());

        int columnId = cursor.getColumnIndex(DbContract.Notes._ID);
        int columnTitle = cursor.getColumnIndex(DbContract.Notes.COLUMN_NAME);
        int columnLastStudied = cursor.getColumnIndex(DbContract.Notes.COLUMN_LAST_STUDIED);
        while (cursor.moveToNext()) {
            notes.add(new Note(
                    cursor.getLong(columnId),
                    cursor.getString(columnTitle),
                    cursor.isNull(columnLastStudied) ? null
                            : new Date(cursor.getLong(columnLastStudied))
            ));
        }

        cursor.close();

        return notes;
    }

    /**
     * 새 단어 추가
     */
    public long addWord(long noteId, String word, String meaning, int difficulty) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Words.COLUMN_NOTE_ID, noteId);
        values.put(DbContract.Words.COLUMN_WORD, word);
        values.put(DbContract.Words.COLUMN_MEANING, meaning);
        values.put(DbContract.Words.COLUMN_DIFFICULTY, difficulty);

        return db.insert(DbContract.Words.TABLE_NAME, null, values);
    }

    /**
     * 단어 수정
     */
    public void editWord(long wordId, String word, String meaning, int difficulty) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Words.COLUMN_WORD, word);
        values.put(DbContract.Words.COLUMN_MEANING, meaning);
        values.put(DbContract.Words.COLUMN_DIFFICULTY, difficulty);

        db.update(DbContract.Words.TABLE_NAME, values, DbContract.Words._ID + "=?",
                new String[]{String.valueOf(wordId)});
    }

    /**
     * 단어 맞은 횟수 변경
     */
    public void updateCountCorrect(long wordId, int count) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Words.COLUMN_COUNT_CORRECT, count);

        db.update(DbContract.Words.TABLE_NAME, values,
                DbContract.Words._ID + "=?",
                new String[]{String.valueOf(wordId)});
    }

    /**
     * 단어 틀린 횟수 변경
     */
    public void updateCountIncorrect(long wordId, int count) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Words.COLUMN_COUNT_INCORRECT, count);

        db.update(DbContract.Words.TABLE_NAME, values,
                DbContract.Words._ID + "=?",
                new String[]{String.valueOf(wordId)});
    }

    /**
     * 맞힌 횟수 1 증가
     */
    public void incrementCountCorrect(long wordId) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE " + DbContract.Words.TABLE_NAME +
                " SET " + DbContract.Words.COLUMN_COUNT_CORRECT + "=" +
                DbContract.Words.COLUMN_COUNT_CORRECT + "+1 " +
                "WHERE " + DbContract.Words._ID + "=?", new Object[]{wordId});
    }

    /**
     * 틀린 횟수 1 증가
     */
    public void incrementCountIncorrect(long wordId) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE " + DbContract.Words.TABLE_NAME +
                " SET " + DbContract.Words.COLUMN_COUNT_INCORRECT + "=" +
                DbContract.Words.COLUMN_COUNT_INCORRECT + "+1 " +
                "WHERE " + DbContract.Words._ID + "=?", new Object[]{wordId});
    }

    /**
     * 단어 하나 삭제
     */
    public void deleteWord(long wordId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DbContract.Words.TABLE_NAME,
                DbContract.Words._ID + "=?",
                new String[]{String.valueOf(wordId)});
    }

    /**
     * wordId로 단어 불러오기
     */
    public Word getWordById(long wordId) {
        return getWords(wordId, -1).get(0);
    }

    /**
     * 단어장에서 단어 목록 불러오기
     */
    public ArrayList<Word> getWordList(long noteId) {
        return getWords(-1, noteId);
    }

    private ArrayList<Word> getWords(long wordId, long noteId) {
        SQLiteDatabase db = getReadableDatabase();

        String selection;
        String[] selectionArgs;
        if (wordId != -1) {
            selection = DbContract.Words._ID + "=?";
            selectionArgs = new String[]{String.valueOf(wordId)};
        } else {
            selection = DbContract.Words.COLUMN_NOTE_ID + "=?";
            selectionArgs = new String[]{String.valueOf(noteId)};
        }

        Cursor cursor = db.query(
                DbContract.Words.TABLE_NAME, null, selection, selectionArgs,
                null, null, null
        );

        ArrayList<Word> words = new ArrayList<>(cursor.getCount());

        int columnId = cursor.getColumnIndex(DbContract.Words._ID);
        int columnWord = cursor.getColumnIndex(DbContract.Words.COLUMN_WORD);
        int columnMeaning = cursor.getColumnIndex(DbContract.Words.COLUMN_MEANING);
        int columnCountCorrect = cursor.getColumnIndex(DbContract.Words.COLUMN_COUNT_CORRECT);
        int columnCountIncorrect = cursor.getColumnIndex(DbContract.Words.COLUMN_COUNT_INCORRECT);
        int columnDifficulty = cursor.getColumnIndex(DbContract.Words.COLUMN_DIFFICULTY);
        int columnNoteId = cursor.getColumnIndex(DbContract.Words.COLUMN_NOTE_ID);

        while (cursor.moveToNext()) {
            words.add(new Word(
                    cursor.getLong(columnId),
                    cursor.getString(columnWord),
                    cursor.getString(columnMeaning),
                    cursor.getInt(columnCountCorrect),
                    cursor.getInt(columnCountIncorrect),
                    cursor.getInt(columnDifficulty),
                    cursor.getLong(columnNoteId)
            ));
        }

        cursor.close();
        return words;
    }
}
