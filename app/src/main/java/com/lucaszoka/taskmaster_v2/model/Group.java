package com.lucaszoka.taskmaster_v2.model;

import java.io.Serializable;
import java.util.Map;

public class Group implements Serializable {

    String id;
    String name;
    String creatorEmail;
    Map<String,Boolean> members;
    int gColor;

    public Group() {
    }

    public Group(String id, String name, String creatorEmail, Map<String, Boolean> members, int gColor) {
        this.id = id;
        this.name = name;
        this.creatorEmail = creatorEmail;
        this.members = members;
        this.gColor = gColor;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public int getgColor() {
        return gColor;
    }

    public void setgColor(int gColor) {
        this.gColor = gColor;
    }
}
