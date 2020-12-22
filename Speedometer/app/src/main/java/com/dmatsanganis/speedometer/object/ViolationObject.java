package com.dmatsanganis.speedometer.object;

import java.sql.Timestamp;

public class ViolationObject {

    private int id;
    private Double longitude;
    private Double latitude;
    private Double speed;
    private Timestamp timestamp;

    public ViolationObject() { }

    public ViolationObject(Double longitude, Double latitude, Double speed, Timestamp timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ViolationModel{" +
                "id=" + id +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", speed=" + speed +
                ", timestamp=" + timestamp +
                '}';
    }
}
