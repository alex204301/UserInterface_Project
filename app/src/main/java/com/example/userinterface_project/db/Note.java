package com.example.userinterface_project.db;

import java.util.Date;

public final class Note {
    private final int id;
    private final String name;
    private final Date lastStudied;

    public Note(int id, String name, Date lastStudied) {
        this.id = id;
        this.name = name;
        this.lastStudied = lastStudied;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getLastStudied() {
        return lastStudied;
    }
}
