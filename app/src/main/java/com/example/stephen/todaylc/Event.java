package com.example.stephen.todaylc;

import java.util.Arrays;
import java.util.Objects;

public class Event {
    private String title;
    private String description;
    private String time;
    private String imageURL;
    private String location;
    private String group;
    private String monthDay;
    private String startEnd;
    private String[] tags;
    private boolean firstOfDay;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(title, event.title) &&
                Objects.equals(time, event.time) &&
                Objects.equals(location, event.location);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, time, location);
    }

    @Override

    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", location='" + location + '\'' +
                ", group='" + group + '\'' +
                ", monthDay='" + monthDay + '\'' +
                ", startEnd='" + startEnd + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    public Event(String title, String description, String time, String imageURL, String location, String group, String monthDay, String startEnd, String[] tags, boolean firstOfDay) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageURL = imageURL;
        this.location = location;
        this.group = group;
        this.monthDay = monthDay;
        this.startEnd = startEnd;
        this.tags = tags;
        this.firstOfDay = firstOfDay;
    }

    public boolean isFirstOfDay() {
        return firstOfDay;
    }

    public void setFirstOfDay(boolean firstOfDay) {
        this.firstOfDay = firstOfDay;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

    public String getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(String startEnd) {
        this.startEnd = startEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() { return imageURL; }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
}
