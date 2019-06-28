package com.allstudio.askstack.StackExchange;

public enum AnswerSort implements ISortType {
    Activity("date"),
    Creation("date"),
    Votes("integer");

    public static final AnswerSort Default = Activity;

    private final String type;

    public boolean isInteger(){ return type == "integer"; }
    public boolean isDate(){ return type == "date"; }
    public boolean isString(){ return type == "string"; }
    public boolean isNone() { return type == "none"; }
    public boolean isBadgeType() { return false; }
    public boolean isBadgeRank() { return false; }

    AnswerSort(String type){
        this.type = type;
    }
}
