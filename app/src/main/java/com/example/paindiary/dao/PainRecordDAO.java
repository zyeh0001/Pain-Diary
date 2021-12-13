package com.example.paindiary.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.paindiary.entity.DateConverter;
import com.example.paindiary.entity.PainRecord;

import java.util.Date;
import java.util.List;

@Dao
public interface PainRecordDAO {
    @Query("SELECT * FROM painrecord ORDER BY date ASC")
    LiveData<List<PainRecord>> getAll();

    @Query("SELECT * FROM painrecord WHERE uid = :uid LIMIT 1")
    PainRecord findByID(int uid);

    @TypeConverters(DateConverter.class)
    @Query("SELECT * FROM painrecord WHERE uEmail = :uEmail AND date = :date LIMIT 1")
    PainRecord checkDate(String uEmail, Date date);

    @TypeConverters(DateConverter.class)
    @Query("SELECT * FROM painrecord WHERE uEmail = :uEmail ORDER BY date ASC")
    LiveData<List<PainRecord>> checkUser(String uEmail);


    @TypeConverters(DateConverter.class)
    @Query("SELECT * FROM painrecord WHERE uEmail = :uEmail AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    LiveData<List<PainRecord>> getBetweenDate(String uEmail, Date startDate, Date endDate);

    @Insert
    void insert(PainRecord painRecord);

    @Delete
    void delete(PainRecord painRecord);

    @Update
    void updatePainRecord(PainRecord painRecord);

    @Query("DELETE FROM painrecord")
    void deleteAll();


}
