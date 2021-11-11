package com.example.userinterface_project.db;

public final class Word {
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;

    private final int id;
    private final String word;
    private final String meaning;
    private final int countCorrect;
    private final int countIncorrect;
    private final int difficulty;

    public Word(int id, String word, String meaning,
                int countCorrect, int countIncorrect, int difficulty) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.countCorrect = countCorrect;
        this.countIncorrect = countIncorrect;
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public int getCountCorrect() {
        return countCorrect;
    }

    public int getCountIncorrect() {
        return countIncorrect;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
