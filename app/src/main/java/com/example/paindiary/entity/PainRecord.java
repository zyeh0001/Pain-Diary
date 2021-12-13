package com.example.paindiary.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;


@Entity
@TypeConverters(DateConverter.class)
public class PainRecord {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "uEmail")
    @NonNull
    public String uEmail;
    @ColumnInfo(name = "date")
    @NonNull
    public Date date;

    public int painLevel;

    public String painLocation;

    public String moodLevel;

    public int steps;

    public int todaySteps;

    public String temp;

    public String humidity;

    public String pressure;

    public PainRecord( @NonNull String uEmail, @NonNull Date date, int painLevel, String painLocation, String moodLevel, int steps, int todaySteps, String temp, String humidity, String pressure) {
        this.uEmail = uEmail;
        this.date = date;
        this.painLevel = painLevel;
        this.painLocation = painLocation;
        this.moodLevel = moodLevel;
        this.steps = steps;
        this.todaySteps =  todaySteps;
        this.temp = temp;
        this.humidity = humidity;
        this.pressure = pressure;
    }
}