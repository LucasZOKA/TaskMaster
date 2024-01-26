package com.lucaszoka.taskmaster_v2.model;

import java.io.Serializable;

public class Task implements Serializable {

    int points;
    String id;
    String title;
    String category;
    String description;
    String difficulty;
    String dateEnd;
    String dateInitial;
    String email;
    String groupID;

    public Task() {
    }

    public Task(int points, String id, String title, String category, String description, String difficulty, String dateEnd, String email) {
        this.points = points;
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.difficulty = difficulty;
        this.dateEnd = dateEnd;
        this.email = email;
    }

    public Task(int points, String id, String title, String category, String description, String difficulty, String dateEnd, String email, String groupID) {
        this.points = points;
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.difficulty = difficulty;
        this.dateEnd = dateEnd;
        this.email = email;
        this.groupID = groupID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDateInitial() {
        return dateInitial;
    }

    public void setDateInitial(String dateInitial) {
        this.dateInitial = dateInitial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}
