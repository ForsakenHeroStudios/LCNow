package com.example.stephen.todaylc;

public class Group {
    private String groupName;
    private boolean sub;

    @Override
    public String toString() {
        return "Group{" +
                "groupName='" + groupName + '\'' +
                ", subbed=" + sub +
                '}';
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSub() {
        return sub;
    }

    public void setSub(boolean sub) {
        this.sub = sub;
    }

    public Group(String groupName, boolean sub) {
        this.groupName = groupName;
        this.sub = sub;
    }
}
