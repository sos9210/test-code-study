package com.example.teststudyproject;

public class Study {
    public Study(int limit, String name){
        this.limit = limit;
        this.name = name;
    }
    public Study(int limit) {
        if(limit < 0){
            throw new IllegalArgumentException("limit은 0보다 작으면 IllegalArgumentException");
        }
        this.limit = limit;
    }

    private StudyStatus status = StudyStatus.DRAFT;

    private int limit;

    private String name;

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "Study{" +
                "status=" + status +
                ", limit=" + limit +
                ", name='" + name + '\'' +
                '}';
    }

    public StudyStatus getStatus() {
        return this.status;
    }
}
