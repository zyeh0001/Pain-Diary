package com.example.paindiary.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiary.databinding.RvLayoutBinding;
import com.example.paindiary.entity.PainRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ViewHolder> {
    List<PainRecord> painRecords= new ArrayList<>();
    //this method will be used by LiveData to keep updating the recyclerview
    public void setData(List<PainRecord> painRecords) {
        this.painRecords = painRecords;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvLayoutBinding binding =
                RvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    // this method binds the view holder created with Room livedata
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        PainRecord painRecord = painRecords.get(position);
        viewHolder.binding.painLevel.setText(Integer.toString(painRecord.painLevel));
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        viewHolder.binding.date.setText("Date: "+dateFormat.format(painRecord.date));
        viewHolder.binding.rvEmail.setText("Email: "+painRecord.uEmail);
        viewHolder.binding.rvLocation.setText("Location: "+painRecord.painLocation);
        viewHolder.binding.rvHumidity.setText("Humidity: "+painRecord.humidity);
        viewHolder.binding.rvMood.setText("Mood: "+painRecord.moodLevel);
        viewHolder.binding.rvPressure.setText("Pressure: "+painRecord.pressure);
        viewHolder.binding.rvTemp.setText("Temp: "+painRecord.temp);
        viewHolder.binding.rvGoalSteps.setText("Goal Steps: "+Integer.toString(painRecord.steps));
        viewHolder.binding.rvTodaySteps.setText("Today Steps: "+Integer.toString(painRecord.todaySteps));
        switch(painRecord.painLevel) {
            case 1:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#F39C9C"));
                break;
            case 2:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#F48585"));
                break;
            case 3:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#F47070"));
                break;
            case 4:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#F85D5D"));
                break;
            case 5:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#F14C4C"));
                break;
            case 6:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#DD3E3E"));
                break;
            case 7:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#C13030"));
                break;
            case 8:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#B62222"));
                break;
            case 9:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#B62223"));
                break;
            case 10:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#B30D0D"));
                break;
            default:
                viewHolder.binding.painLevel.setBackgroundColor(Color.parseColor("#FFAFAF"));
        }

    }
    @Override
    public int getItemCount() {
        return painRecords.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private RvLayoutBinding binding;
        public ViewHolder(RvLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}