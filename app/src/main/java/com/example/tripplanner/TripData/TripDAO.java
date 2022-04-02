package com.example.tripplanner.TripData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TripDAO {

    @Insert
    long insert(Trip trip);
    @Delete
    void delete(Trip trip);
    @Query("DELETE FROM Trip")
    void clear();
    @Query("DELETE FROM Trip where id=:id AND userID= :userId ")
    void deleteById(String userId, int id);

    @Query("SELECT * FROM Trip WHERE userID LIKE :userId")
    List<Trip> selectAll(String userId);

    @Query("SELECT * FROM Trip WHERE userID LIKE :userId")
    List<Trip> selectRoom(String userId);

    @Query("SELECT * FROM Trip WHERE id = :id ")
    Trip selectById(int id);

    @Query("SELECT * FROM Trip WHERE userId  = :userId And(tripStatus LIKE :cancleStatus Or tripStatus LIKE :finishedStatus Or tripStatus LIKE :missedStatus) ")
    List<Trip> selectHistoryTrip(String userId, String cancleStatus, String finishedStatus, String missedStatus);

    @Query("SELECT * FROM Trip WHERE userId  = :userId And tripStatus LIKE :status ")
    List<Trip> selectUpcomingTrip(String userId, String status);

    @Query("UPDATE Trip SET tripStatus = :tripStatus WHERE id = :id And userID= :userId")
    void updateTripStatus(String userId, int id, String tripStatus);

    @Query("UPDATE Trip SET tripName = :tripName , startPoint =:startPoint , endPoint =:endPoint , endPointLatitude=:endPointLat, endPointLongitude=:endPointLong," +
            " date =:date , time=:time, calendar=:calendar WHERE id = :id")
    void EditTrip(int id, String tripName,String startPoint,String endPoint,double endPointLat,double endPointLong,String date,String time,double calendar);

    @Query("UPDATE Trip SET notes = :notes WHERE id = :id")
    void EditNotes(int id, String notes);

    @Query("SELECT COUNT(*) FROM Trip WHERE userId  = :userId And tripStatus LIKE :status" )
    int getCountTripType(String userId ,String status);

    @Query("DELETE FROM Trip where userID= :userId AND tripStatus = :status ")
    void deleteUpcomingById(String userId,String status);

    @Query("DELETE FROM Trip where userID= :userId AND( tripStatus = :status OR tripStatus = :status1 OR tripStatus = :status2 ) ")
    void deleteHistoryById(String userId,String status,String status1,String status2);
}
