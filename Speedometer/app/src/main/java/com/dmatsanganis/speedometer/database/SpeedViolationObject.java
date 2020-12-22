package com.dmatsanganis.speedometer.database;

public class SpeedViolationObject {

    private SpeedViolationObject() { }

    public static class SpeedViolationEntity {

        public static final String TABLE_NAME = "Speed_Violations";
        public static final String ID = "id";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String SPEED = "speed";
        public static final String TIMESTAMP = "timestamp";

    }
}