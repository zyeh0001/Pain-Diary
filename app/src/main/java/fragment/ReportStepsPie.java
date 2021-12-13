package fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.databinding.ReportStepsPieBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;


public class ReportStepsPie extends Fragment {
    private ReportStepsPieBinding addBinding;
    private PieChart pieChart;
    private PainRecordViewModel painRecordViewModel;
    private String mail;
    private Date date;
    private FirebaseAuth auth;
    int currentSteps = 0;
    int remainingSteps = 0;

    public ReportStepsPie() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addBinding = ReportStepsPieBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        pieChart = addBinding.stepsPiechart;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date = cal.getTime();
        auth = FirebaseAuth.getInstance();
        mail = auth.getCurrentUser().getEmail();


        setupPieChart();
        loadPieChartData();
        return view;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        //pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Go Reach your Goal Steps!");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        ((Legend) l).setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextColor(Color.parseColor("#FFFFFF"));
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        painRecordViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PainRecordViewModel.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<PainRecord> painRecordCompletableFuture = painRecordViewModel.findDate(mail, date);
            painRecordCompletableFuture.thenApply(painRecord -> {
                if (painRecord != null) {
                    Log.d("data", painRecord.date.toString());
                    currentSteps = painRecord.todaySteps;
                    Log.d("step", "" + currentSteps);
                    if (painRecord.todaySteps <= painRecord.steps)
                        remainingSteps = (painRecord.steps - painRecord.todaySteps);
                    else {
                        remainingSteps = 0;
                        pieChart.setCenterText("Congratulation!! You did it");
                    }
                    Log.d("step", "" + remainingSteps);
                    ArrayList<PieEntry> entries = new ArrayList<>();
                    entries.add(new PieEntry(currentSteps, "Current Steps"));
                    entries.add(new PieEntry(remainingSteps, "Remaining Steps"));
                    ArrayList<Integer> colors = new ArrayList<>();
                    for (int color : ColorTemplate.MATERIAL_COLORS) {
                        colors.add(color);
                    }

                    for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                        colors.add(color);
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "Steps Chart");
                    dataSet.setColors(colors);

                    PieData data = new PieData(dataSet);
                    data.setDrawValues(true);
                    data.setValueTextSize(12f);
                    data.setValueTextColor(Color.BLACK);

                    pieChart.setData(data);
                    pieChart.invalidate();

                    pieChart.animateY(1400, Easing.EaseInOutQuad);
                }
                return painRecord;
            });
        }


    }

}
