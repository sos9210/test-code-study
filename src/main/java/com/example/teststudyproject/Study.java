package com.example.teststudyproject;

public class Study {
    public Study(int limit) {
        if(limit < 0){
            throw new IllegalArgumentException("limit은 0보다 작으면 IllegalArgumentException");
        }
        this.limit = limit;
    }

    private StudyStatus status = StudyStatus.DRAFT;

    private int limit;

    public int getLimit() {
        return limit;
    }

    public StudyStatus getStatus() {
        return this.status;
    }
}
