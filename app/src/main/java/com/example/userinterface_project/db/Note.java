package com.example.userinterface_project.db;

import java.util.Date;

public final class Note {
    private final long id;
    private final String name;
    private final Date lastStudied;

    public Note(long id, String name, Date lastStudied) {
        this.id = id;
        this.name = name;
        this.lastStudied = lastStudied;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getLastStudied() {
        return lastStudied;
    }
}
