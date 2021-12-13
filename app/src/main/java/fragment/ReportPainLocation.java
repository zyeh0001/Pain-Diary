package fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.databinding.ReportPainLocationBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ReportPainLocation extends Fragment {
    private PainRecordViewModel painRecordViewModel;
    private ReportPainLocationBinding addBinding;
    private PieChart pieChart;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addBinding = ReportPainLocationBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        pieChart = addBinding.locationPiechart;
        auth = FirebaseAuth.getInstance();
        setupPieChart();
        loadPieChartData();
        return view;
    }


    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        //pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Pain Location Distribution!");
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
            painRecordViewModel.getUserData(auth.getCurrentUser().getEmail()).observe(requireActivity(), painRecords -> {
                HashMap<String, Integer> hmap = new HashMap<String, Integer>();
                for (PainRecord temp : painRecords) {
                    if (hmap.containsKey(temp.painLocation)) {
                        hmap.put(temp.painLocation, hmap.get(temp.painLocation) + 1);
                    } else {
                        hmap.put(temp.painLocation, 1);
                    }

                }

                Log.i("Hashmap", "" + hmap);
                ArrayList<PieEntry> entries = new ArrayList<>();
                for (Map.Entry me : hmap.entrySet()) {
                    System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
                    entries.add(new PieEntry((int) me.getValue(), me.getKey().toString()));
                }


                // entries.add(new PieEntry(remainingSteps, "Remaining Steps"));
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                    colors.add(color);
                }

                PieDataSet dataSet = new PieDataSet(entries, "Location Pie Chart");
                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                //data.setValueFormatter(new PercentFormatter(pieChart));
                data.setValueTextSize(12f);
                data.setValueTextColor(Color.BLACK);

                pieChart.setData(data);
                pieChart.invalidate();

                pieChart.animateY(1400, Easing.EaseInOutQuad);
            });
            return;
        }
        ;
    }


}
