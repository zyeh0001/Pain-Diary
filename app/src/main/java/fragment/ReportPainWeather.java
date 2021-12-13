package fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.databinding.ReportPainWeatherBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportPainWeather extends Fragment {
    private ReportPainWeatherBinding addBinding;
    private DatePickerDialog startDatePicker;
    private DatePickerDialog endDatePicker;
    private Date startDate;
    private Date endDate;
    private Button startDateButton;
    private Button endDateButton;
    private String weatherFeature;
    private PainRecordViewModel painRecordViewModel;
    Spinner partSpinner;
    private LineChart lineChart;
    private ArrayList<PainRecord> recordsBetween;
    private FirebaseAuth auth;
    private ArrayAdapter<String> spinnerAdapter;
    private int itemCount;


    public ReportPainWeather() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        addBinding = ReportPainWeatherBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        startDateButton = addBinding.startDateButton;
        endDateButton = addBinding.endDateButton;
        partSpinner = addBinding.lineGraphSpinner;
        lineChart = addBinding.lineChart;
        initDateStartPicker();
        initDateEndPicker();
        startDateButton.setText(getTodaysDate());
        endDateButton.setText(getTodaysDate());

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartDatePicker(v);
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEndDatePicker(v);
            }
        });

        addBinding.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDateButton.setText(getTodaysDate());
                endDateButton.setText(getTodaysDate());
                partSpinner.setSelection(0);

            }
        });

        List<String> weatherFeatureList = new ArrayList<>();
        String[] strs = {"Temperature", "Humidity", "Pressure"};
        for (int i = 0; i < strs.length; i++)
            weatherFeatureList.add(strs[i]);

        spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, weatherFeatureList);
        partSpinner.setAdapter(spinnerAdapter);
        partSpinner.setSelection(0);
        partSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String weatherFeatureSelect = parent.getItemAtPosition(position).toString();
                if (weatherFeatureSelect != null) {
                    Toast.makeText(requireActivity(), "Weather Feature Selected" + weatherFeatureSelect, Toast.LENGTH_LONG);
                    weatherFeature = weatherFeatureSelect;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addBinding.corrolationTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addBinding.corrolationTestBtn.setText(testCorrelation());
                testCorrelation();
            }
        });


        addBinding.generateGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //if start date  is after end date
                int result = startDate.compareTo(endDate);
                Log.i("between", "result " + result);
                if (result > 0) {
                    Toast.makeText(getActivity(), "Please select the right date!!", Toast.LENGTH_SHORT).show();
                    Log.i("between", "result123 " + result);
                    return;
                }

                painRecordViewModel =
                        ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PainRecordViewModel.class);
                painRecordViewModel.getBetweenDate(auth.getCurrentUser().getEmail(),startDate, endDate).observe(requireActivity(), new Observer<List<PainRecord>>() {
                    @Override
                    public void onChanged(List<PainRecord> painRecords) {
                        recordsBetween = new ArrayList<>();
                        for (PainRecord temp : painRecords) {
                            recordsBetween.add(temp);
                        }
                        itemCount = recordsBetween.size();
                        if (recordsBetween.size() > 0) {
                            initLineChart(weatherFeature);
                            Log.i("send", "" + recordsBetween.get(0).uEmail);
                        }
                        else {
                            Toast.makeText(getActivity(), "No value to show!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        return view;
    }


    private void initDateStartPicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                startDateButton.setText(date);
                String tmpDate = year + "." + month + "." + day;
                try {

                    startDate = new SimpleDateFormat("yyyy.MM.dd").parse(tmpDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //add code here
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        startDatePicker = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        startDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private void initDateEndPicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                endDateButton.setText(date);
                String tmpDate = year + "." + month + "." + day;
                try {

                    endDate = new SimpleDateFormat("yyyy.MM.dd").parse(tmpDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        endDatePicker = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        endDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String tmpDate = year + "." + month + "." + day;
        try {

            startDate = new SimpleDateFormat("yyyy.MM.dd").parse(tmpDate);
            endDate = new SimpleDateFormat("yyyy.MM.dd").parse(tmpDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openStartDatePicker(View view) {
        startDatePicker.show();
    }

    public void openEndDatePicker(View view) {
        endDatePicker.show();
    }

    private void initLineChart(String weatherOption) {
        lineChart = addBinding.lineChart;
        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> lineEntries1 = new ArrayList<Entry>();
        ArrayList<Entry> lineEntries2 = new ArrayList<Entry>();
        ArrayList<Double> painLevelList = new ArrayList<Double>();
        ArrayList<Double> weatherFeatureList = new ArrayList<Double>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        for (int i = 0; i < itemCount; i++) {

            if (weatherOption.equals("Temperature")) {
                lineEntries1.add(new Entry(i, recordsBetween.get(i).painLevel));
                lineEntries2.add(new Entry(i, Float.parseFloat(recordsBetween.get(i).temp)));
                painLevelList.add((double) recordsBetween.get(i).painLevel);
                weatherFeatureList.add(Double.parseDouble(recordsBetween.get(i).temp));
                labels.add(dateFormat.format(recordsBetween.get(i).date));
            } else if (weatherFeature.equals("Humidity")) {
                labels.add(dateFormat.format(recordsBetween.get(i).date));
                lineEntries1.add(new Entry(i, recordsBetween.get(i).painLevel));
                lineEntries2.add(new Entry(i, Float.parseFloat(recordsBetween.get(i).humidity)));
                painLevelList.add((double) recordsBetween.get(i).painLevel);
                weatherFeatureList.add(Double.parseDouble(recordsBetween.get(i).humidity));

            } else {
                labels.add(dateFormat.format(recordsBetween.get(i).date));
                lineEntries1.add(new Entry(i, recordsBetween.get(i).painLevel));
                lineEntries2.add(new Entry(i, Float.parseFloat(recordsBetween.get(i).pressure)));
                painLevelList.add((double) recordsBetween.get(i).painLevel);
                weatherFeatureList.add(Double.parseDouble(recordsBetween.get(i).pressure));

            }
        }
        setUpChart(lineEntries1,lineEntries2,labels);
    }

    public void setUpChart(ArrayList<Entry> lineEntries1, ArrayList<Entry> lineEntries2, ArrayList<String> xlabels)
    {
        LineDataSet lineDataSet1 = new LineDataSet(lineEntries1, "Pain Line");
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet1.setLineWidth(3);
        lineDataSet1.setHighlightEnabled(true);
        lineDataSet1.setCircleColor(Color.YELLOW);
        lineDataSet1.setCircleRadius(6);
        lineDataSet1.setCircleHoleRadius(3);
        lineDataSet1.setDrawHighlightIndicators(true);
        lineDataSet1.setHighLightColor(Color.YELLOW);
        lineDataSet1.setColor(Color.YELLOW);
        lineDataSet1.setLineWidth(2.5f);
        lineDataSet1.setCircleColor(Color.BLUE);
        lineDataSet1.setCircleRadius(5f);
        lineDataSet1.setFillColor(Color.YELLOW);
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet1.setDrawValues(true);
        lineDataSet1.setValueTextSize(10f);
        lineDataSet1.setValueTextColor(Color.YELLOW);


        LineDataSet lineDataSet2 = new LineDataSet(lineEntries2, weatherFeature + " Line");
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet2.setColors(Color.RED);
        lineDataSet1.setFillColor(Color.RED);
        lineDataSet2.setLineWidth(3);
        lineDataSet2.setHighlightEnabled(true);
        lineDataSet2.setCircleColor(Color.RED);
        lineDataSet2.setCircleRadius(6);
        lineDataSet2.setCircleHoleRadius(3);
        lineDataSet2.setDrawHighlightIndicators(true);
        lineDataSet2.setHighLightColor(Color.RED);
        lineDataSet2.setValueTextSize(8f);
        lineDataSet2.setValueTextColor(Color.rgb(89, 194, 230));
        lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        LineData data = new LineData(dataSets);

        lineChart.setData(data);
        lineChart.animateY(1000);
        lineChart.getDescription().setText("");

        Legend legend = lineChart.getLegend();
        legend.setStackSpace(5);
        legend.setTextColor(Color.YELLOW);

        // xAxis customization
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.YELLOW);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        // set x axis
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xlabels));

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.YELLOW);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTextColor(Color.YELLOW);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void testCorrelation() {
        //two column array: 1st column=first array, 1st column=second array
        painRecordViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PainRecordViewModel.class);
        painRecordViewModel.getBetweenDate(auth.getCurrentUser().getEmail(),startDate, endDate).observe(requireActivity(), painRecords -> {
            ArrayList<ArrayList<Double>> tmpdata = new ArrayList<ArrayList<Double>>();
            ArrayList<Double> tmpWeatherOption = new ArrayList<>();
            ArrayList<Double> tmpPainLevel = new ArrayList<>();
            if (painRecords.size() <= 2) {
                Toast.makeText(getActivity(), "Too less value to Analyze", Toast.LENGTH_SHORT).show();
                return;
            }
            for (PainRecord temp : painRecords) {
                tmpPainLevel.add(Double.valueOf(temp.painLevel));
                if (weatherFeature.equals("Temperature")) {
                    tmpWeatherOption.add(Double.parseDouble(temp.temp));
                } else if (weatherFeature.equals("Humidity")) {
                    tmpWeatherOption.add(Double.parseDouble(temp.humidity));
                } else {
                    tmpWeatherOption.add(Double.parseDouble(temp.pressure));
                }

            }
            tmpdata.add(tmpPainLevel);
            tmpdata.add(tmpWeatherOption);
            double[] PainLevel = tmpPainLevel.stream().mapToDouble(d -> d).toArray();
            double[] WeatherOption = tmpWeatherOption.stream().mapToDouble(d -> d).toArray();
            double[][] data = new double[painRecords.size()][2];
            Log.i("corr", "R: " + painRecords.size());
            Log.i("corr", "R: " + PainLevel.length);
            Log.i("corr", "R: " + WeatherOption.length);
            for (int j = 0; j < PainLevel.length; j++) {
                data[j][0] = PainLevel[j];
            }
            for (int i = 0; i < WeatherOption.length; i++) {
                data[i][1] = WeatherOption[i];
            }

            double[][] test = {{1, 1}, {-1, 0}, {11, 87}, {-6, 5}, {-6, 3},};

            // create a realmatrix
            RealMatrix m = MatrixUtils.createRealMatrix(data);
            // measure all correlation test: x-x, x-y, y-x, y-x
            for (int i = 0; i < m.getColumnDimension(); i++)
                for (int j = 0; j < m.getColumnDimension(); j++) {
                    PearsonsCorrelation pc = new PearsonsCorrelation();
                    double cor = pc.correlation(m.getColumn(i), m.getColumn(j));
                    System.out.println(i + "," + j + "=[" + String.format(".%2f", cor) + "," + "]");
                }
            // correlation test (another method): x-y
            PearsonsCorrelation pc = new PearsonsCorrelation(m);
            RealMatrix corM = pc.getCorrelationMatrix();
            // significant test of the correlation coefficient (p-value)
            RealMatrix pM = pc.getCorrelationPValues();
            addBinding.pValue.setText("P: " + pM.getEntry(0, 1));
            addBinding.rValue.setText("R: " + corM.getEntry(0, 1));
            Log.i("corr", "R: " + corM.getEntry(0, 1));
            Log.i("corr", "P: " + pM.getEntry(0, 1));

            //return("p value:" + pM.getEntry(0, 1)+ "\n" + " correlation: " + corM.getEntry(0, 1));
        });
    }
}
