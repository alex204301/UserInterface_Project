package com.example.userinterface_project.db;

import java.util.Date;

public class GoalListItem {
    private final Date date;
    private final int solved;
    private final int goal;

    public GoalListItem(Date date, int solved, int goal) {
        this.date = date;
        this.solved = solved;
        this.goal = goal;
    }

    public Date getDate() {
        return date;
    }

    public int getSolved() {
        return solved;
    }

    public int getGoal() {
        return goal;
    }
}
