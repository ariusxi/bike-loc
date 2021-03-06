package br.com.felix.bikeloc.model;

import android.util.Log;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Place implements Serializable {

    private String id;
    private String user;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private Date createdAt;

    public Place(
            String id,
            String user,
            String name,
            String description,
            double latitude,
            double longitude
    ) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = Calendar.getInstance().getTime();
    }

    public Place(
            String user,
            String name,
            String description,
            double latitude,
            double longitude
    ) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = Calendar.getInstance().getTime();
    }

    public Place() {}

    public void setUser(String user) { this.user = user; }

    public String getUser() { return user; }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}