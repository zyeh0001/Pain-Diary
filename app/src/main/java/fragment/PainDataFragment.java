package fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.databinding.PainDataFragmentBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smileyrating.SmileyRating;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import fragment.alarmManager.AlertReceiver;
import viewmodel.SharedViewModel;

public class PainDataFragment extends Fragment {
    private PainDataFragmentBinding addBinding;
    SeekBar seekBar;
    TextView painLevel;
    Spinner partSpinner;
    TimePicker picker;
    Button setAlarm;
    TextView alartSetTime;
    private String uMood = "NO DATA";
    private String uPainPart = "";
    private String uTemp = "";
    private String uHumidity = "";
    private String uPressure = "";
    private Date date;

    private int uPainLevel = 5;
    private ArrayAdapter<String> spinnerAdapter;
    private PainRecordViewModel painRecordViewModel;
    private FirebaseAuth auth;

    public PainDataFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date = cal.getTime();
        Log.i("date no time", "" + date);

        addBinding = PainDataFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        picker = addBinding.timePicker;
        picker.setIs24HourView(true);
        setAlarm = addBinding.setAlarm;
        seekBar = addBinding.painSeekBar;
        painLevel = addBinding.prinLevelText;
        partSpinner = addBinding.partSpinner;
        List<String> partList = new ArrayList<>();
        String[] strs = {"Back", "Neck", "Head", "Knees", "Hips", "Abdomen", "elbows", "Shoulders", "Shins", "Jaw", "Facial"};
        for (int i = 0; i < strs.length; i++)
            partList.add(strs[i]);
        auth = FirebaseAuth.getInstance();
        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    String[] ss = s.split("\\s+");
                    uTemp = ss[0];
                    uHumidity = ss[1];
                    uPressure = ss[2];
                }

            }
        });
        //set alarm
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;
                String am_pm;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour = picker.getHour();
                    minute = picker.getMinute();
                } else {
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                if (hour > 12) {
                    am_pm = "PM";
                    hour = hour - 12;
                } else {
                    am_pm = "AM";
                }


                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                Log.i("alarm", "" + c);
                Log.i("alarm", "Selected Date: " + hour + ":" + minute + " " + am_pm);
                updateTimeText(c);
                startAlarm(c);
                //tvw.setText("Selected Date: "+ hour +":"+ minute+" "+am_pm);
            }

        });

        //add 10 data for report
        addBinding.textViewRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    add10Data();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "Pain Data added", Toast.LENGTH_SHORT).show();
            }
        });
        painRecordViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PainRecordViewModel.class);

        dateCheck(auth.getCurrentUser().getEmail(), date);


        //pain level seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                painLevel.setText("Pain Level: " + String.valueOf(progress));
                uPainLevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //mood level rating
        SmileyRating smileyRating = addBinding.smileRating;
        smileyRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                // You can compare it with rating Type
                if (SmileyRating.Type.GREAT == type) {
                    addBinding.mood.setText("User Mood: " + "Very Good");
                    uMood = "very good";
                }
                if (SmileyRating.Type.GOOD == type) {
                    addBinding.mood.setText("User Mood: " + "Good");
                    uMood = "good";
                }
                if (SmileyRating.Type.OKAY == type) {
                    addBinding.mood.setText("User Mood: " + "average");
                    uMood = "average";
                }
                if (SmileyRating.Type.BAD == type) {
                    addBinding.mood.setText("User Mood: " + "low");
                    uMood = "low";
                }
                if (SmileyRating.Type.TERRIBLE == type) {
                    addBinding.mood.setText("User Mood: " + "very low");
                    uMood = "very low";
                }
                int rating = type.getRating();
            }
        });


        //pain location spinner
        spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, partList);
        partSpinner.setAdapter(spinnerAdapter);
        partSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String painPart = parent.getItemAtPosition(position).toString();
                if (painPart != null) {
                    Toast.makeText(parent.getContext(), "Pain Part Selected" + painPart, Toast.LENGTH_LONG);
                    uPainPart = painPart;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addBinding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uEmail = auth.getCurrentUser().getEmail();

                int painLevel = uPainLevel;
                String painLocation = uPainPart;
                String moodLevel = uMood;
                String steps = addBinding.goalStep.getText().toString();
                String todaySteps = addBinding.todayStep.getText().toString();
                String temp = uTemp;
                String humidity = uHumidity;
                String pressure = uPressure;
                if (steps.isEmpty()) {
                    steps = "10000";
                }
                if (todaySteps.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your steps for today", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (moodLevel.equals("NO DATA")) {
                    Log.i("mood", "is empty " + moodLevel);
                    Log.i("mood", "is empty " + uMood);
                    Toast.makeText(getActivity(), "Please enter your mood", Toast.LENGTH_SHORT).show();
                    return;
                }
                //smileyRating.setRating(3, true);
                Log.i("mood", uMood);
                System.out.println(uEmail + "\n" + date + "\n" + painLevel + "\n" + painLocation + "\n" + moodLevel + "\n" + steps + "\n" + temp + "\n" + humidity + "\n" + pressure);
                assert uEmail != null;
                if (!uEmail.isEmpty()) {
                    int goal = Integer.parseInt(steps);
                    int current_steps = Integer.parseInt(todaySteps);
                    PainRecord nPainRecord = new PainRecord(uEmail, date, painLevel, painLocation, moodLevel, goal, current_steps, temp, humidity, pressure);
                    painRecordViewModel.insert(nPainRecord);
                    Toast.makeText(getActivity(), "Pain Record Saved", Toast.LENGTH_SHORT).show();

                }
                addBinding.saveButton.setVisibility(View.GONE);
            }
        });
        addBinding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uEmail = auth.getCurrentUser().getEmail();
                int painLevel = uPainLevel;
                String painLocation = uPainPart;
                String moodLevel = uMood;
                String goal_steps = addBinding.goalStep.getText().toString();
                String todaySteps = addBinding.todayStep.getText().toString();
                String temp = uTemp;
                String humidity = uHumidity;
                String pressure = uPressure;
                if (goal_steps.isEmpty()) {
                    goal_steps = "10000";
                }
                if (todaySteps.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your steps for today", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (moodLevel.equals("NO DATA")) {
                    Toast.makeText(getActivity(), "Please enter your mood", Toast.LENGTH_SHORT).show();
                    return;
                }

                System.out.println(uEmail + "\n" + date + "\n" + painLevel + "\n" + painLocation + "\n" + moodLevel + "\n" + goal_steps + "\n" + temp + "\n" + humidity + "\n" + pressure);
                assert uEmail != null;
                if (!uEmail.isEmpty()) {
                    int goal = Integer.parseInt(goal_steps);
                    int current_steps = Integer.parseInt(todaySteps);
                    PainRecord upPainRecord = new PainRecord(uEmail, date, painLevel, painLocation, moodLevel, goal, current_steps, temp, humidity, pressure);
                    painRecordViewModel.update(upPainRecord);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        CompletableFuture<PainRecord> customerCompletableFuture = painRecordViewModel.findDate(uEmail, date);
                        customerCompletableFuture.thenApply(painRecord -> {
                            if (painRecord != null) {
                                painRecord.painLevel = painLevel;
                                painRecord.painLocation = painLocation;
                                painRecord.moodLevel = moodLevel;
                                painRecord.temp = temp;
                                painRecord.humidity = humidity;
                                painRecord.pressure = pressure;
                                painRecord.steps = goal;
                                painRecord.todaySteps = current_steps;

                                painRecordViewModel.update(painRecord);
//                                Toast.makeText(getActivity(), "Pain Record updated", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getActivity(), "Update was successful for Date:" + date, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Record does not exist", Toast.LENGTH_SHORT).show();

                            }
                            return painRecord;
                        });
                        Toast.makeText(getActivity(), "Pain Record updated", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void dateCheck(String mail, Date date) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<PainRecord> painRecordCompletableFuture = painRecordViewModel.findDate(mail, date);
            painRecordCompletableFuture.thenApply(painRecord -> {
                if (painRecord != null) {
                    Log.d("data", painRecord.date.toString());
                    addBinding.saveButton.setVisibility(View.INVISIBLE);
                }
                Log.d("data", "This is main data----------------------suss");
                return painRecord;
            });
        }
    }

    public void add10Data() throws ParseException {

        String e = auth.getCurrentUser().getEmail();
        PainRecord PainRecord1 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.1"), 0, "Back", "very good", 10000, 2700, "16.7", "89", "1022");
        painRecordViewModel.insert(PainRecord1);
        PainRecord PainRecord2 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.2"), 1, "Neck", "average", 20000, 20000, "24.72", "89", "1022");
        painRecordViewModel.insert(PainRecord2);
        PainRecord PainRecord3 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.3"), 2, "Head", "very low", 17000, 2700, "14.44", "89", "1022");
        painRecordViewModel.insert(PainRecord3);
        PainRecord PainRecord4 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.4"), 3, "Knees", "good", 10560, 10000, "26.68", "89", "1022");
        painRecordViewModel.insert(PainRecord4);
        PainRecord PainRecord5 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.5"), 10, "elbows", "good", 9000, 7789, "15.52", "89", "1022");
        painRecordViewModel.insert(PainRecord5);
        PainRecord PainRecord6 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.6"), 9, "Back", "low", 10900, 8000, "18.59", "89", "1022");
        painRecordViewModel.insert(PainRecord6);
        PainRecord PainRecord7 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.7"), 3, "Back", "very good", 18000, 2400, "25.52", "89", "1022");
        painRecordViewModel.insert(PainRecord7);
        PainRecord PainRecord8 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.8"), 4, "Jaw", "very low", 9800, 2600, "24.52", "89", "1022");
        painRecordViewModel.insert(PainRecord8);
        PainRecord PainRecord9 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.9"), 7, "Facial", "low", 8000, 5000, "17.52", "89", "1022");
        painRecordViewModel.insert(PainRecord9);
        PainRecord PainRecord10 = new PainRecord(e, new SimpleDateFormat("yyyy.MM.dd").parse("2021.05.10"), 5, "Hips", "good", 7800, 8800, "12.92", "89", "1022");
        painRecordViewModel.insert(PainRecord10);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        Log.i("alarm", "" + timeText);
        setAlarm.setText(timeText);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        Log.i("alarm", "start alarm");
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 2 * 60 * 1000, pendingIntent);
    }
}

