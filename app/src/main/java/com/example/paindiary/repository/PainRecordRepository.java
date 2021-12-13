package com.example.paindiary.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.paindiary.dao.PainRecordDAO;
import com.example.paindiary.database.PainRecordDatabase;
import com.example.paindiary.entity.PainRecord;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public class PainRecordRepository {
    private PainRecordDAO painRecordDAO;
    private LiveData<List<PainRecord>> allPainRecords;

    public PainRecordRepository(Application application) {
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        painRecordDAO = db.painRecordDAO();
        allPainRecords = painRecordDAO.getAll();
    }

    // Room executes this query on a separate thread
    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public void insert(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.insert(painRecord);
            }
        });
    }

    public void deleteAll() {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.deleteAll();
            }
        });
    }

    public void delete(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.delete(painRecord);
            }
        });
    }

    public void updatePainRecord(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.updatePainRecord(painRecord);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIDFuture(final int painRecordId) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
                public PainRecord get() {
                return painRecordDAO.findByID(painRecordId);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findDate(String uEmail, Date date) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return painRecordDAO.checkDate(uEmail, date);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    public LiveData<List<PainRecord>> getUserData(String uEmail) {
        return painRecordDAO.checkUser(uEmail);
    }

    public LiveData<List<PainRecord>> getBetweenDate(String uEmail, Date startDate, Date endDate) {
        return painRecordDAO.getBetweenDate(uEmail, startDate,endDate);
    }

}
