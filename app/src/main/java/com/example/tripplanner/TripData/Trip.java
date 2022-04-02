package com.example.tripplanner.TripData;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.io.Serializable;
import java.util.ArrayList;

/*room deal with @ annotation this class like column in Table*/

@Entity //this class name is name of room table if want to change table name (tableName = "Name")

@TypeConverters(DataConverter.class)
public class Trip implements Serializable {
    @NonNull
    private String userID;
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String tripName;
    @NonNull
    private String startPoint;
    @NonNull
    private double startPointLatitude;
    @NonNull
    private double startPointLongitude;
    @NonNull
    private String endPoint;
    @NonNull
    private double endPointLatitude;
    @NonNull
    private double endPointLongitude;
    @NonNull
    private String date;
    @NonNull
    private String time;
    @NonNull
    private String tripStatus;

    private ArrayList<String> notes;
    @NonNull
    private long calendar;

    public Trip(@NonNull String userID, @NonNull String tripName, @NonNull String startPoint,@NonNull double startPointLat, @NonNull double startPointLong,
                @NonNull String endPoint, @NonNull double endPointLat, @NonNull double endPointLong,
                @NonNull String date, @NonNull String time, String tripStatus, @NonNull long calendar,
                ArrayList<String> notes) {
        this.userID = userID;
        this.calendar = calendar;
     //   this.id = id;
        this.tripName = tripName;
        this.startPoint = startPoint;
        this.startPointLatitude=startPointLat;
        this.startPointLongitude=startPointLong;
        this.endPoint = endPoint;
        this.endPointLatitude = endPointLat;
        this.endPointLongitude = endPointLong;
        this.date = date;
        this.time = time;
        this.tripStatus = tripStatus;
        this.notes=notes;
    }

    public Trip() {
    }

    @NonNull
    public String getUserID() {
        return userID;
    }

    public void setUserID(@NonNull String userID) {
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTripName() {
        return tripName;
    }

    public void setTripName(@NonNull String tripName) {
        this.tripName = tripName;
    }

    @NonNull
    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(@NonNull String startPoint) {
        this.startPoint = startPoint;
    }

    public double getStartPointLatitude() {
        return startPointLatitude;
    }

    public void setStartPointLatitude(double startPointLatitude) {
        this.startPointLatitude = startPointLatitude;
    }

    public double getStartPointLongitude() {
        return startPointLongitude;
    }

    public void setStartPointLongitude(double startPointLongitude) {
        this.startPointLongitude = startPointLongitude;
    }

    @NonNull
    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(@NonNull String endPoint) {
        this.endPoint = endPoint;
    }

    public double getEndPointLatitude() {
        return endPointLatitude;
    }

    public void setEndPointLatitude(double endPointLatitude) {
        this.endPointLatitude = endPointLatitude;
    }

    public double getEndPointLongitude() {
        return endPointLongitude;
    }

    public void setEndPointLongitude(double endPointLongitude) {
        this.endPointLongitude = endPointLongitude;
    }

    @NonNull
    public String getDate() {
        return date;
    }
    public void setDate(@NonNull String date) {
        this.date = date;
    }
    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

    @NonNull
    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(@NonNull String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public long getCalendar() {
        return calendar;
    }

    public void setCalendar(long calendar) {
        this.calendar = calendar;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }
}
