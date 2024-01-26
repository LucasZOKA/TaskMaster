package com.lucaszoka.taskmaster_v2.model;

import java.io.Serializable;

public class Reward implements Serializable {

    int points;
    String id;
    String groupID;
    String title,description,email,icon;
    int is_permanent;
    int iconRef, colorRef;

    public Reward(){
    }

    public Reward(String id, int pontos, String title, String description, String email, String icon, int is_permanent, String groupID, int iconRef, int colorRef) {
        this.id = id;
        this.points = pontos;
        this.title = title;
        this.description = description;
        this.email = email;
        this.icon = icon;
        this.is_permanent = is_permanent;
        this.groupID = groupID;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
    }

    public Reward(String id, int pontos, String title, String description, String email, String icon, int is_permanent, int iconRef, int colorRef) {
        this.id = id;
        this.points = pontos;
        this.title = title;
        this.description = description;
        this.email = email;
        this.icon = icon;
        this.is_permanent = is_permanent;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
    }
    public Reward(String id,int pontos, String title, String description, String email, int is_permanent, int iconRef, int colorRef) {
        this.id = id;
        this.points = pontos;
        this.title = title;
        this.description = description;
        this.email = email;
        this.is_permanent = is_permanent;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
    }

    public Reward(String id,String title, String description,int points, int is_permanent, String email, int iconRef, int colorRef){
        this.id = id;
        this.title = title;
        this.description = description;
        this.points = points;
        this.is_permanent = is_permanent;
        this.email = email;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
    }

    public Reward(String id,String title, String description,int points, int is_permanent, String email, String groupID, int iconRef, int colorRef){
        this.id = id;
        this.title = title;
        this.description = description;
        this.points = points;
        this.is_permanent = is_permanent;
        this.email = email;
        this.groupID = groupID;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
    }

    public Reward(String title, int iconRef, int colorRef) {
        this.title = title;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
    }

    public Reward(String id, String title, int iconRef, int colorRef) {
        this.id = id;
        this.title = title;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
    }

    public Reward(int points, String title, String description, int iconRef, int colorRef) {
        this.points = points;
        this.title = title;
        this.description = description;
        this.iconRef = iconRef;
        this.colorRef = colorRef;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIs_permanent() {
        return is_permanent;
    }

    public void setIs_permanent(int is_permanent) {
        this.is_permanent = is_permanent;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public int getIconRef(){return iconRef;}

    public void setIconRef(int iconRef) {
        this.iconRef = iconRef;
    }

    public int getColorRef() {
        return colorRef;
    }

    public void setColorRef(int colorRef) {
        this.colorRef = colorRef;
    }
}