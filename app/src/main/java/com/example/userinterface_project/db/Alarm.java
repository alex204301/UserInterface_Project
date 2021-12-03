package com.example.userinterface_project.db;

public final class Alarm {
    private final long id;
    private final int hour;
    private final int minutes;

    public Alarm(long id, int hour, int minutes) {
        this.id = id;
        this.hour = hour;
        this.minutes = minutes;
    }

    public long getId() {
        return id;
    }

    public int getHour() {return hour;}

    public int getMinutes() { return minutes; }
}
