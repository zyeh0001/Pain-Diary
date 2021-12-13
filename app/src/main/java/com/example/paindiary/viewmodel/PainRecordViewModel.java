package com.example.paindiary.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.repository.PainRecordRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainRecordViewModel extends AndroidViewModel {
    private PainRecordRepository pRepository;
    private LiveData<List<PainRecord>> allPainRecords;

    public PainRecordViewModel(Application application) {
        super(application);
        pRepository = new PainRecordRepository(application);
        allPainRecords = pRepository.getAllPainRecords();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIDFuture(final int painRecordId) {
        return pRepository.findByIDFuture(painRecordId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findDate(final String uEmail, final Date date) {
        return pRepository.findDate(uEmail,date );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<List<PainRecord>> getBetweenDate(final String uEmail, final Date startDate, final Date endDate) {
        return pRepository.getBetweenDate(uEmail, startDate,endDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<List<PainRecord>> getUserData(final String uEmail) {
        return pRepository.getUserData(uEmail);
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public void insert(PainRecord painRecord) {
        pRepository.insert(painRecord);
    }

    public void deleteAll() {
        pRepository.deleteAll();
    }

    public void update(PainRecord painRecord) {
        pRepository.updatePainRecord(painRecord);
    }
}
