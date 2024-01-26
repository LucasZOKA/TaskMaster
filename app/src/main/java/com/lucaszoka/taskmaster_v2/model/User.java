package com.lucaszoka.taskmaster_v2.model;

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {
    int tasks_completed;
    String id, name, email;
    Map<String,Boolean> groups;
    Map<String,Integer> points;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(Map<String,Integer> points, String name, String email, int tasks_completed) {
        this.points = points;
        this.name = name;
        this.email = email;
        this.tasks_completed = tasks_completed;
    }

    public User(String id, Map<String,Integer> points, String name, String email, int tasks_completed) {
        this.id = id;
        this.points = points;
        this.name = name;
        this.email = email;
        this.tasks_completed = tasks_completed;
    }

    public User(String id, Map<String,Integer> points, int tasks_completed, String name, String email, Map<String, Boolean> groups) {
        this.id = id;
        this.points = points;
        this.tasks_completed = tasks_completed;
        this.name = name;
        this.email = email;
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,Integer> getPoints() {
        return points;
    }

    public void setPoints(Map<String,Integer> points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTasks_completed() {
        return tasks_completed;
    }

    public void setTasks_completed(int tasks_completed) {
        this.tasks_completed = tasks_completed;
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }
    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }
}
